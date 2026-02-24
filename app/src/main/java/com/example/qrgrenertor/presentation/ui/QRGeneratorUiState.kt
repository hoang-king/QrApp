package com.example.qrgrenertor.presentation.ui

import com.example.qrgrenertor.domain.model.QRCode
import com.example.qrgrenertor.domain.model.QRDesign
import com.example.qrgrenertor.domain.model.QRSourceType

sealed class QRGeneratorUiState {
    object Idle : QRGeneratorUiState()
    object Loading : QRGeneratorUiState()
    data class StepTypeSelection(
        val selectedType: QRSourceType? = null
    ) : QRGeneratorUiState()
    data class StepContentInput(
        val selectedType: QRSourceType,
        val content: String = ""
    ) : QRGeneratorUiState()
    data class StepDesignCustomization(
        val selectedType: QRSourceType,
        val content: String,
        val design: QRDesign = QRDesign()
    ) : QRGeneratorUiState()
    data class StepQRGeneration(
        val qrCode: QRCode,
        val design: QRDesign
    ) : QRGeneratorUiState()
    data class Success(val qrCode: QRCode) : QRGeneratorUiState()
    data class Error(val message: String) : QRGeneratorUiState()
}

sealed class QRGeneratorEvent {
    data class SelectQRType(val type: QRSourceType) : QRGeneratorEvent()
    data class EnterContent(val content: String) : QRGeneratorEvent()
    data class UpdateDesign(val design: QRDesign) : QRGeneratorEvent()
    object GoToNextStep : QRGeneratorEvent()
    object GoToPreviousStep : QRGeneratorEvent()
    object GenerateQR : QRGeneratorEvent()
    object SaveQR : QRGeneratorEvent()
    object Reset : QRGeneratorEvent()
}
