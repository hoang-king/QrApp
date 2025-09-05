package com.example.qrgrenertor.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import com.example.qrgrenertor.domain.repository.QRCodeRepository
import com.example.qrgrenertor.presentation.state.QRGeneratorUiState
import kotlinx.coroutines.flow.update


class QRGeneratorViewModel( private  val qrCodeRepository : QRCodeRepository = QRCodeRepository()): ViewModel() {
        private val _uiState = MutableStateFlow(QRGeneratorUiState())
    val uiState: StateFlow<QRGeneratorUiState> = _uiState.asStateFlow()

    fun updateInputText(text: String){
        _uiState.value = _uiState.value.copy(inputText = text)

    }

    fun clearInputText(){
        _uiState.value = _uiState.value.copy(
            inputText = "",
            qrBitmap = null
        )

    }
    fun generateQRCode() {
        if (_uiState.value.inputText.isEmpty()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val bitmap = qrCodeRepository.generateQRCode(_uiState.value.inputText)
                _uiState.value = _uiState.value.copy(
                    qrBitmap = bitmap,
                    isLoading = false,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Lỗi tạo mã QR: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun clearQRCode() {
        _uiState.update { it.copy(qrBitmap = null) }
    }







}