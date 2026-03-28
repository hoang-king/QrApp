package com.example.qrgrenertor.presentation.ui

import com.example.qrgrenertor.domain.model.QRCode
import com.example.qrgrenertor.domain.model.QRDesign
import com.example.qrgrenertor.domain.model.QRHistory
import com.example.qrgrenertor.domain.model.QRSourceType

sealed class QRGeneratorUiState {
    object Idle : QRGeneratorUiState()
    object Loading : QRGeneratorUiState()
    data class StepTypeSelection(
        val selectedType: QRSourceType? = null
    ) : QRGeneratorUiState()
    data class StepContentInput(
        val selectedType: QRSourceType,
        val name: String = "",
        val content: String = ""
    ) : QRGeneratorUiState()
    data class StepDesignCustomization(
        val selectedType: QRSourceType,
        val name: String,
        val content: String,
        val design: QRDesign = QRDesign()
    ) : QRGeneratorUiState()
    data class StepQRGeneration(
        val qrCode: QRCode,
        val design: QRDesign
    ) : QRGeneratorUiState()
    data class Success(val qrCode: QRCode) : QRGeneratorUiState()
    data class Error(val message: String) : QRGeneratorUiState()
    data class HistoryList(
        val items: List<QRHistory> = emptyList(),
        val searchQuery: String = "",
        val selectedDetail: QRCode? = null,
        val isLoading: Boolean = false
    ) : QRGeneratorUiState()
    object Settings : QRGeneratorUiState()
}

sealed class QRGeneratorEvent {
    data class SelectQRType(val type: QRSourceType) : QRGeneratorEvent()
    data class EnterName(val name: String) : QRGeneratorEvent()
    data class EnterContent(val content: String) : QRGeneratorEvent()
    data class UpdateDesign(val design: QRDesign) : QRGeneratorEvent()
    object GoToNextStep : QRGeneratorEvent()
    object GoToPreviousStep : QRGeneratorEvent()
    object GenerateQR : QRGeneratorEvent()
    object SaveQR : QRGeneratorEvent()
    object Reset : QRGeneratorEvent()
    object NavigateToHistory : QRGeneratorEvent()
    data class SearchHistory(val query: String) : QRGeneratorEvent()
    object NavigateToSettings : QRGeneratorEvent()
    data class ViewHistoryDetail(val qrCode: QRCode) : QRGeneratorEvent()
    object DismissHistoryDetail : QRGeneratorEvent()
    data class DeleteHistoryItem(val id: String) : QRGeneratorEvent()
}

