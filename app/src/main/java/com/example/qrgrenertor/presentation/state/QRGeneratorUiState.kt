package com.example.qrgrenertor.presentation.state

import android.graphics.Bitmap

data class QRGeneratorUiState(
    val inputText: String = "",
    val qrBitmap: Bitmap? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
