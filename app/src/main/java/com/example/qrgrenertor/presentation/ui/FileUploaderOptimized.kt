package com.example.qrgrenertor.presentation.ui

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okio.BufferedSink
import okio.source
import java.io.File
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

object FileUploaderOptimized {
    private const val CHUNK_SIZE = 10 * 1024 * 1024L  // 10MB chunks
    private const val MAX_PARALLEL_CHUNKS = 4  // 4 parallel connections

    private val client = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.MINUTES)
        .writeTimeout(10, TimeUnit.MINUTES)
        .readTimeout(5, TimeUnit.MINUTES)
        .connectionPool(okhttp3.ConnectionPool(10, 5, TimeUnit.MINUTES))
        .build()

    /**
     * Tải file lên với hỗ trợ nén và song song.
     * Tự động chọn Catbox (< 200MB) hoặc Litterbox (> 200MB).
     * 
     * @param context App context
     * @param uri File URI
     * @param onProgress Callback tiến độ (0-100)
     */
    suspend fun uploadFile(
        context: Context,
        uri: Uri,
        onProgress: ((Int) -> Unit)? = null
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val fileName = FileUtils.getFileName(context, uri) ?: "file_${System.currentTimeMillis()}"
            val fileSize = FileUtils.getFileSize(context, uri)
            
            val isLargeFile = fileSize > 200 * 1024 * 1024L
            val apiUrl = if (isLargeFile) "https://litterbox.catbox.moe/resources/internals/api.php" 
                         else "https://catbox.moe/user/api.php"

            // Nếu file nhỏ, dùng upload đơn giản
            if (fileSize < 100 * 1024 * 1024L) {
                return@withContext uploadSingleStream(context, uri, apiUrl, isLargeFile, fileName)
            }

            // Nếu file lớn, dùng upload theo chunks song song
            return@withContext uploadChunked(context, uri, apiUrl, isLargeFile, fileName, fileSize, onProgress)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun uploadSingleStream(
        context: Context,
        uri: Uri,
        apiUrl: String,
        isLargeFile: Boolean,
        fileName: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val mediaType = "application/octet-stream".toMediaTypeOrNull()
            val requestBodyBuilder = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("reqtype", "fileupload")

            if (isLargeFile) {
                requestBodyBuilder.addFormDataPart("time", "72h")
            }

            requestBodyBuilder.addFormDataPart(
                "fileToUpload",
                fileName,
                createRequestBodyFromUri(context, uri, mediaType)
            )

            val request = Request.Builder()
                .url(apiUrl)
                .post(requestBodyBuilder.build())
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (response.isSuccessful && responseBody != null) {
                val responseText = responseBody.trim()
                if (responseText.startsWith("https://")) {
                    val finalUrl = if (isLargeFile) "$responseText?expires=72h" else responseText
                    Result.success(finalUrl)
                } else {
                    Result.failure(Exception("Server error: $responseText"))
                }
            } else {
                Result.failure(Exception("Upload failed: ${response.code} ${response.message}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun uploadChunked(
        context: Context,
        uri: Uri,
        apiUrl: String,
        isLargeFile: Boolean,
        fileName: String,
        fileSize: Long,
        onProgress: ((Int) -> Unit)?
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val chunks = mutableListOf<Chunk>()
            var offset = 0L
            var chunkIndex = 0

            // Chia file thành chunks
            while (offset < fileSize) {
                val chunkSize = minOf(CHUNK_SIZE, fileSize - offset)
                chunks.add(Chunk(chunkIndex, offset, chunkSize))
                offset += chunkSize
                chunkIndex++
            }

            val chunkResults = mutableMapOf<Int, Boolean>()
            val uploadedBytes = AtomicLong(0L)

            // Upload chunks song song
            val uploadTasks = chunks.chunked(MAX_PARALLEL_CHUNKS).flatMap { batchChunks ->
                batchChunks.map { chunk ->
                    async {
                        try {
                            val chunkResult = uploadFileChunk(
                                context, uri, apiUrl, fileName, 
                                chunk, chunks.size, isLargeFile
                            )
                            if (chunkResult.isSuccess) {
                                synchronized(chunkResults) {
                                    chunkResults[chunk.index] = true
                                    uploadedBytes.addAndGet(chunk.size)
                                    onProgress?.invoke((uploadedBytes.get() * 100 / fileSize).toInt())
                                }
                                true
                            } else {
                                false
                            }
                        } catch (e: Exception) {
                            false
                        }
                    }
                }
            }

            val results = uploadTasks.awaitAll()
            
            if (results.all { it }) {
                // Gửi yêu cầu hoàn thành
                finalizeUpload(apiUrl, fileName, isLargeFile)
            } else {
                Result.failure(Exception("Some chunks failed to upload"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun uploadFileChunk(
        context: Context,
        uri: Uri,
        apiUrl: String,
        fileName: String,
        chunk: Chunk,
        totalChunks: Int,
        isLargeFile: Boolean
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val requestBody = createChunkRequestBody(context, uri, chunk)
            
            val requestBodyBuilder = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("reqtype", "fileupload")
                .addFormDataPart("chunk_index", chunk.index.toString())
                .addFormDataPart("total_chunks", totalChunks.toString())
                .addFormDataPart("filename", fileName)

            if (isLargeFile) {
                requestBodyBuilder.addFormDataPart("time", "72h")
            }

            requestBodyBuilder.addFormDataPart("fileToUpload", fileName, requestBody)

            val request = Request.Builder()
                .url(apiUrl)
                .post(requestBodyBuilder.build())
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (response.isSuccessful && responseBody != null) {
                val responseText = responseBody.trim()
                if (responseText.startsWith("https://") || responseText == "OK") {
                    Result.success(responseText)
                } else {
                    Result.failure(Exception("Chunk upload error: $responseText"))
                }
            } else {
                Result.failure(Exception("Chunk upload failed: ${response.code}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun finalizeUpload(
        apiUrl: String,
        fileName: String,
        isLargeFile: Boolean
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val requestBodyBuilder = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("reqtype", "finalize")
                .addFormDataPart("filename", fileName)

            if (isLargeFile) {
                requestBodyBuilder.addFormDataPart("time", "72h")
            }

            val request = Request.Builder()
                .url(apiUrl)
                .post(requestBodyBuilder.build())
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (response.isSuccessful && responseBody != null) {
                val responseText = responseBody.trim()
                if (responseText.startsWith("https://")) {
                    val finalUrl = if (isLargeFile) "$responseText?expires=72h" else responseText
                    Result.success(finalUrl)
                } else {
                    Result.failure(Exception("Finalize error: $responseText"))
                }
            } else {
                Result.failure(Exception("Finalize failed: ${response.code}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun createChunkRequestBody(
        context: Context,
        uri: Uri,
        chunk: Chunk
    ): RequestBody {
        return object : RequestBody() {
            override fun contentType() = "application/octet-stream".toMediaTypeOrNull()

            override fun contentLength(): Long = chunk.size

            override fun writeTo(sink: BufferedSink) {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    inputStream.skip(chunk.offset)
                    val buffer = ByteArray(8192)
                    var remaining = chunk.size
                    while (remaining > 0) {
                        val read = inputStream.read(buffer, 0, minOf(buffer.size, remaining.toInt()))
                        if (read <= 0) break
                        sink.write(buffer, 0, read)
                        remaining -= read
                    }
                } ?: throw Exception("Cannot open InputStream from Uri")
            }
        }
    }

    private fun createRequestBodyFromUri(context: Context, uri: Uri, contentType: okhttp3.MediaType?): RequestBody {
        return object : RequestBody() {
            override fun contentType() = contentType

            override fun contentLength(): Long {
                return FileUtils.getFileSize(context, uri)
            }

            override fun writeTo(sink: BufferedSink) {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    sink.writeAll(inputStream.source())
                } ?: throw Exception("Cannot open InputStream from Uri")
            }
        }
    }

    private data class Chunk(
        val index: Int,
        val offset: Long,
        val size: Long
    )
}
