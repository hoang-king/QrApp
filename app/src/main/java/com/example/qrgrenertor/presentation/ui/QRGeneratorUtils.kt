package com.example.qrgrenertor.presentation.ui

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.example.qrgrenertor.domain.model.QRDesign
import com.example.qrgrenertor.domain.model.QRStyle
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel

object QRGeneratorUtils {
    fun generateQRCode(
        content: String,
        design: QRDesign
    ): Bitmap {
        val size = design.size
        val hints = HashMap<EncodeHintType, Any>()
        hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
        hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H // Use High for better custom shape resilience
        hints[EncodeHintType.MARGIN] = 1

        val writer = QRCodeWriter()
        // Request minimal bitMatrix by passing small dimensions
        val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 0, 0, hints)
        
        val width = bitMatrix.width
        val height = bitMatrix.height
        
        // Calculate the scale factor to fit the desired size
        val scale = size.toFloat() / width
        
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        
        // Fill background
        canvas.drawColor(design.backgroundColor.toInt())
        
        paint.color = design.codeColor.toInt()
        paint.style = Paint.Style.FILL
        
        for (x in 0 until width) {
            for (y in 0 until height) {
                if (bitMatrix[x, y]) {
                    val left = x * scale
                    val top = y * scale
                    val right = left + scale
                    val bottom = top + scale
                    
                    val margin = scale * 0.05f // Add a tiny margin between modules for style clarity
                    
                    when (design.style) {
                        QRStyle.SQUARE -> {
                            canvas.drawRect(left, top, right, bottom, paint)
                        }
                        QRStyle.ROUNDED -> {
                            val rect = RectF(left + margin, top + margin, right - margin, bottom - margin)
                            canvas.drawRoundRect(rect, scale / 2, scale / 2, paint)
                        }
                        QRStyle.DOT -> {
                            val centerX = left + scale / 2
                            val centerY = top + scale / 2
                            canvas.drawCircle(centerX, centerY, (scale / 2) - margin, paint)
                        }
                        QRStyle.DIAMOND -> {
                            val path = Path()
                            val centerX = left + scale / 2
                            val centerY = top + scale / 2
                            val innerMargin = scale * 0.05f
                            
                            path.moveTo(centerX, top + innerMargin) // Top
                            path.lineTo(right - innerMargin, centerY) // Right
                            path.lineTo(centerX, bottom - innerMargin) // Bottom
                            path.lineTo(left + innerMargin, centerY) // Left
                            path.close()
                            canvas.drawPath(path, paint)
                        }
                        QRStyle.HEART -> {
                            val path = Path()
                            val centerX = left + scale / 2
                            val centerY = top + scale / 2
                            val s = scale * 0.8f // Scale down a bit to fit
                            val offsetX = (scale - s) / 2
                            val offsetY = (scale - s) / 2
                            
                            val l = left + offsetX
                            val t = top + offsetY
                            val r = right - offsetX
                            val b = bottom - offsetY
                            val m = l + s/2
                            
                            path.moveTo(m, t + s/4)
                            path.cubicTo(l, t, l, t + s/2, m, b)
                            path.cubicTo(r, t + s/2, r, t, m, t + s/4)
                            path.close()
                            canvas.drawPath(path, paint)
                        }
                        QRStyle.CLASSY -> {
                            val rect = RectF(left + margin, top + margin, right - margin, bottom - margin)
                            canvas.drawRoundRect(rect, scale / 4, scale / 4, paint)
                        }
                    }
                }
            }
        }
        return bitmap
    }
}
