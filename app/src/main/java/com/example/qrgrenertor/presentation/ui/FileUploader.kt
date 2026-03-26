package com.example.qrgrenertor.presentation.ui

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okio.BufferedSink
import okio.source
import java.io.File
import java.util.concurrent.TimeUnit

object FileUploader {
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.MINUTES)
        .writeTimeout(15, TimeUnit.MINUTES)
        .readTimeout(15, TimeUnit.MINUTES)
        .build()

    /**
     * Tải file lên Catbox.moe hoặc Litterbox tùy kích thước.
     * - Dưới 200MB: Dùng Catbox (Lưu trữ vĩnh viễn)
     * - Trên 200MB (đến 1GB): Dùng Litterbox (Lưu trữ trong 72 giờ)
     */
    suspend fun uploadFile(context: Context, uri: Uri): Result<String> = withContext(Dispatchers.IO) {
        try {
            val fileName = FileUtils.getFileName(context, uri) ?: "file_${System.currentTimeMillis()}"
            val fileSize = FileUtils.getFileSize(context, uri)
            val mediaType = "application/octet-stream".toMediaTypeOrNull()
            
            // Nếu file > 200MB, sử dụng Litterbox (hỗ trợ đến 1GB)
            val isLargeFile = fileSize > 200 * 1024 * 1024L
            val apiUrl = if (isLargeFile) "https://litterbox.catbox.moe/resources/internals/api.php" 
                         else "https://catbox.moe/user/api.php"
            
            val requestBodyBuilder = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("reqtype", "fileupload")
                
            // Litterbox yêu cầu tham số 'time' (1h, 12h, 24h, 72h)
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
                    Result.failure(Exception("Server trả về lỗi: $responseText"))
                }
            } else {
                Result.failure(Exception("Upload thất bại: ${response.code} ${response.message}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun createRequestBodyFromUri(context: Context, uri: Uri, contentType: MediaType?): RequestBody {
        return object : RequestBody() {
            override fun contentType(): MediaType? = contentType

            override fun contentLength(): Long {
                return FileUtils.getFileSize(context, uri)
            }

            override fun writeTo(sink: BufferedSink) {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    sink.writeAll(inputStream.source())
                } ?: throw Exception("Không thể mở InputStream từ Uri")
            }
        }
    }
}
