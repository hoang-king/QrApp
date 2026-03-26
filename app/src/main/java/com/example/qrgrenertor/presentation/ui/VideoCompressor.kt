package com.example.qrgrenertor.presentation.ui

import android.content.Context
import android.net.Uri
import androidx.media3.common.Effect
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import androidx.media3.effect.Presentation
import androidx.media3.transformer.Composition
import androidx.media3.transformer.EditedMediaItem
import androidx.media3.transformer.Effects
import androidx.media3.transformer.ExportException
import androidx.media3.transformer.ExportResult
import androidx.media3.transformer.TransformationRequest
import androidx.media3.transformer.Transformer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@OptIn(markerClass = [UnstableApi::class])
object VideoCompressor {

    /**
     * Nén video với độ phân giải thích ứng dựa vào kích thước.
     * - < 100MB: 480p (nhanh nhất)
     * - 100-500MB: 720p (cân bằng)
     * - > 500MB: 1080p (chất lượng)
     */
    suspend fun compressVideo(
        context: Context,
        inputUri: Uri,
        targetHeight: Int = 0
    ): Result<File> = withContext(Dispatchers.IO) {

        val outputFile = File.createTempFile(
            "compressed_",
            ".mp4",
            context.cacheDir
        )

        try {
            // Tính toán độ phân giải thích ứng nếu không chỉ định
            val height = if (targetHeight > 0) {
                targetHeight
            } else {
                val fileSize = FileUtils.getFileSize(context, inputUri)
                when {
                    fileSize < 100 * 1024 * 1024L -> 480  // < 100MB: 480p
                    fileSize < 500 * 1024 * 1024L -> 720  // 100-500MB: 720p
                    else -> 1080  // > 500MB: 1080p
                }
            }

            // cấu hình codec
            val transformer = Transformer.Builder(context)
                .setVideoMimeType(MimeTypes.VIDEO_H264)
                .build()

            // effect resize thích ứng
            val videoEffects = mutableListOf<Effect>()
            videoEffects.add(Presentation.createForHeight(height))

            val effects = Effects(
                emptyList(),
                videoEffects
            )

            val mediaItem = MediaItem.fromUri(inputUri)

            val editedMediaItem = EditedMediaItem.Builder(mediaItem)
                .setRemoveAudio(false)
                .setEffects(effects)
                .build()

            val resultFile = suspendCancellableCoroutine<File> { continuation ->

                val listener = object : Transformer.Listener {

                    override fun onCompleted(
                        composition: Composition,
                        exportResult: ExportResult
                    ) {
                        if (continuation.isActive) {
                            continuation.resume(outputFile)
                        }
                    }

                    override fun onError(
                        composition: Composition,
                        exportResult: ExportResult,
                        exportException: ExportException
                    ) {
                        if (continuation.isActive) {
                            if (outputFile.exists()) outputFile.delete()
                            continuation.resumeWithException(exportException)
                        }
                    }
                }

                transformer.addListener(listener)

                try {
                    transformer.start(
                        editedMediaItem,
                        outputFile.absolutePath
                    )
                } catch (e: Exception) {

                    if (outputFile.exists()) outputFile.delete()

                    if (continuation.isActive) {
                        continuation.resumeWithException(e)
                    }
                }

                continuation.invokeOnCancellation {
                    transformer.cancel()
                    if (outputFile.exists()) outputFile.delete()
                }
            }

            Result.success(resultFile)

        } catch (e: Exception) {

            if (outputFile.exists()) outputFile.delete()
            Result.failure(e)
        }
    }
}