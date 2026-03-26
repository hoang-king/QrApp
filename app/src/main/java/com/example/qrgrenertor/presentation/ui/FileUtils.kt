package com.example.qrgrenertor.presentation.ui

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File

object FileUtils {
    fun getFileName(context: Context, uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex >= 0) {
                        result = cursor.getString(nameIndex)
                    }
                }
            } finally {
                cursor?.close()
            }
        } else if (uri.scheme == "file") {
            result = uri.lastPathSegment
        }
        
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != null && cut != -1) {
                result = result?.substring(cut + 1)
            }
        }
        return result
    }

    fun getFileSize(context: Context, uri: Uri): Long {
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                    if (sizeIndex >= 0) {
                        return cursor.getLong(sizeIndex)
                    }
                }
            } finally {
                cursor?.close()
            }
        } else if (uri.scheme == "file") {
            val path = uri.path
            if (path != null) {
                return File(path).length()
            }
        }
        return 0
    }
}
