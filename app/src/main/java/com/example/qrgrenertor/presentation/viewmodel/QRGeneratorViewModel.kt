package com.example.qrgrenertor.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qrgrenertor.domain.model.QRCode
import com.example.qrgrenertor.domain.model.QRDesign
import com.example.qrgrenertor.domain.model.QRSourceType
import com.example.qrgrenertor.domain.model.Result
import com.example.qrgrenertor.domain.usecase.GenerateQRUseCase
import com.example.qrgrenertor.domain.usecase.SaveQRUseCase
import com.example.qrgrenertor.presentation.ui.QRGeneratorEvent
import com.example.qrgrenertor.presentation.ui.QRGeneratorUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QRGeneratorViewModel @Inject constructor(
    private val generateQRUseCase: GenerateQRUseCase,
    private val saveQRUseCase: SaveQRUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<QRGeneratorUiState>(QRGeneratorUiState.StepTypeSelection())
    val uiState: StateFlow<QRGeneratorUiState> = _uiState

    private var selectedType: QRSourceType? = null
    private var currentContent: String = ""
    private var currentDesign: QRDesign = QRDesign()
    private var currentQRCode: QRCode? = null

    fun onEvent(event: QRGeneratorEvent) {
        when (event) {
            is QRGeneratorEvent.SelectQRType -> handleSelectType(event.type)
            is QRGeneratorEvent.EnterContent -> handleEnterContent(event.content)
            is QRGeneratorEvent.UpdateDesign -> handleUpdateDesign(event.design)
            QRGeneratorEvent.GoToNextStep -> handleGoToNextStep()
            QRGeneratorEvent.GoToPreviousStep -> handleGoToPreviousStep()
            QRGeneratorEvent.GenerateQR -> handleGenerateQR()
            QRGeneratorEvent.SaveQR -> handleSaveQR()
            QRGeneratorEvent.Reset -> handleReset()
        }
    }

    private fun handleSelectType(type: QRSourceType) {
        selectedType = type
        _uiState.value = QRGeneratorUiState.StepTypeSelection(selectedType)
    }

    private fun handleEnterContent(content: String) {
        currentContent = content
        if (selectedType != null) {
            _uiState.value = QRGeneratorUiState.StepContentInput(selectedType!!, content)
        }
    }

    private fun handleUpdateDesign(design: QRDesign) {
        currentDesign = design
        if (selectedType != null) {
            _uiState.value = QRGeneratorUiState.StepDesignCustomization(
                selectedType!!,
                currentContent,
                design
            )
        }
    }

    private fun handleGoToNextStep() {
        val currentState = _uiState.value
        val newState = when (currentState) {
            is QRGeneratorUiState.StepTypeSelection -> {
                if (currentState.selectedType != null) {
                    selectedType = currentState.selectedType
                    QRGeneratorUiState.StepContentInput(currentState.selectedType)
                } else {
                    return
                }
            }
            is QRGeneratorUiState.StepContentInput -> {
                if (currentContent.isNotEmpty()) {
                    QRGeneratorUiState.StepDesignCustomization(
                        currentState.selectedType,
                        currentContent,
                        currentDesign
                    )
                } else {
                    return
                }
            }
            is QRGeneratorUiState.StepDesignCustomization -> {
                // Generate QR on next step
                return
            }
            else -> return
        }
        _uiState.value = newState
    }

    private fun handleGoToPreviousStep() {
        val currentState = _uiState.value
        val newState = when (currentState) {
            is QRGeneratorUiState.StepContentInput -> {
                QRGeneratorUiState.StepTypeSelection(currentState.selectedType)
            }
            is QRGeneratorUiState.StepDesignCustomization -> {
                QRGeneratorUiState.StepContentInput(currentState.selectedType, currentState.content)
            }
            is QRGeneratorUiState.StepQRGeneration -> {
                QRGeneratorUiState.StepDesignCustomization(
                    currentState.design.errorCorrectionLevel.toString().let {
                        selectedType ?: QRSourceType.URL
                    },
                    currentContent,
                    currentState.design
                )
            }
            else -> return
        }
        _uiState.value = newState
    }

    private fun handleGenerateQR() {
        if (selectedType == null || currentContent.isEmpty()) {
            _uiState.value = QRGeneratorUiState.Error("Please fill all fields")
            return
        }

        viewModelScope.launch {
            _uiState.value = QRGeneratorUiState.Loading
            val result = generateQRUseCase(currentContent, currentDesign)
            when (result) {
                is Result.Success -> {
                    currentQRCode = result.data
                    _uiState.value = QRGeneratorUiState.StepQRGeneration(result.data, currentDesign)
                }
                is Result.Error -> {
                    _uiState.value = QRGeneratorUiState.Error(result.exception.message ?: "Unknown error")
                }
                is Result.Loading -> {}
            }
        }
    }

    private fun handleSaveQR() {
        val qrCode = currentQRCode ?: return
        viewModelScope.launch {
            _uiState.value = QRGeneratorUiState.Loading
            val result = saveQRUseCase(qrCode)
            when (result) {
                is Result.Success -> {
                    _uiState.value = QRGeneratorUiState.Success(qrCode)
                }
                is Result.Error -> {
                    _uiState.value = QRGeneratorUiState.Error(result.exception.message ?: "Save failed")
                }
                is Result.Loading -> {}
            }
        }
    }

    private fun handleReset() {
        selectedType = null
        currentContent = ""
        currentDesign = QRDesign()
        currentQRCode = null
        _uiState.value = QRGeneratorUiState.StepTypeSelection()
    }
}
