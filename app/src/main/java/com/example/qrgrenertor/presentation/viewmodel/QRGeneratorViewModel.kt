package com.example.qrgrenertor.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qrgrenertor.domain.model.QRCode
import com.example.qrgrenertor.domain.model.QRDesign
import com.example.qrgrenertor.domain.model.QRSourceType
import com.example.qrgrenertor.domain.model.Result
import com.example.qrgrenertor.domain.usecase.GenerateQRUseCase
import com.example.qrgrenertor.domain.usecase.GetQRHistoryUseCase
import com.example.qrgrenertor.domain.usecase.SaveQRUseCase
import com.example.qrgrenertor.domain.repository.QRCodeRepository
import com.example.qrgrenertor.presentation.ui.QRGeneratorEvent
import com.example.qrgrenertor.presentation.ui.QRGeneratorUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QRGeneratorViewModel @Inject constructor(
    private val generateQRUseCase: GenerateQRUseCase,
    private val saveQRUseCase: SaveQRUseCase,
    private val getQRHistoryUseCase: GetQRHistoryUseCase,
    private val repository: QRCodeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<QRGeneratorUiState>(QRGeneratorUiState.StepTypeSelection())
    val uiState: StateFlow<QRGeneratorUiState> = _uiState

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage = _toastMessage.asSharedFlow()

    private var selectedType: QRSourceType? = null
    private var currentName: String = ""
    private var currentContent: String = ""
    private var currentDesign: QRDesign = QRDesign()
    private var currentQRCode: QRCode? = null
    private var isCurrentSaved: Boolean = false

    fun onEvent(event: QRGeneratorEvent) {
        when (event) {
            is QRGeneratorEvent.SelectQRType -> handleSelectType(event.type)
            is QRGeneratorEvent.EnterName -> handleEnterName(event.name)
            is QRGeneratorEvent.EnterContent -> handleEnterContent(event.content)
            is QRGeneratorEvent.UpdateDesign -> handleUpdateDesign(event.design)
            QRGeneratorEvent.GoToNextStep -> handleGoToNextStep()
            QRGeneratorEvent.GoToPreviousStep -> handleGoToPreviousStep()
            QRGeneratorEvent.GenerateQR -> handleGenerateQR()
            QRGeneratorEvent.SaveQR -> handleSaveQR()
            QRGeneratorEvent.Reset -> handleReset()
            QRGeneratorEvent.NavigateToHistory -> handleNavigateToHistory()
            QRGeneratorEvent.NavigateToSettings -> _uiState.value = QRGeneratorUiState.Settings
            is QRGeneratorEvent.ViewHistoryDetail -> handleViewHistoryDetail(event.qrCode)
            QRGeneratorEvent.DismissHistoryDetail -> handleDismissHistoryDetail()
            is QRGeneratorEvent.DeleteHistoryItem -> handleDeleteHistoryItem(event.id)
        }
    }

    private fun handleSelectType(type: QRSourceType) {
        selectedType = type
        _uiState.value = QRGeneratorUiState.StepTypeSelection(selectedType)
    }

    private fun handleEnterName(name: String) {
        currentName = name
        if (selectedType != null) {
            _uiState.value = QRGeneratorUiState.StepContentInput(selectedType!!, name, currentContent)
        }
    }

    private fun handleEnterContent(content: String) {
        currentContent = content
        if (selectedType != null) {
            _uiState.value = QRGeneratorUiState.StepContentInput(selectedType!!, currentName, content)
        }
    }

    private fun handleUpdateDesign(design: QRDesign) {
        currentDesign = design
        if (selectedType != null) {
            _uiState.value = QRGeneratorUiState.StepDesignCustomization(
                selectedType!!,
                currentName,
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
                    QRGeneratorUiState.StepContentInput(currentState.selectedType, currentName, currentContent)
                } else {
                    return
                }
            }
            is QRGeneratorUiState.StepContentInput -> {
                if (currentContent.isNotEmpty()) {
                    QRGeneratorUiState.StepDesignCustomization(
                        currentState.selectedType,
                        currentName,
                        currentContent,
                        currentDesign
                    )
                } else {
                    return
                }
            }
            is QRGeneratorUiState.StepDesignCustomization -> {
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
                QRGeneratorUiState.StepContentInput(currentState.selectedType, currentName, currentState.content)
            }
            is QRGeneratorUiState.StepQRGeneration -> {
                QRGeneratorUiState.StepDesignCustomization(
                    selectedType ?: QRSourceType.URL,
                    currentName,
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
            isCurrentSaved = false
            val result = generateQRUseCase(currentName, currentContent, selectedType!!, currentDesign)
            when (result) {
                is Result.Success -> {
                    currentQRCode = result.data.copy(name = currentName.ifEmpty { "QR Code" })
                    _uiState.value = QRGeneratorUiState.StepQRGeneration(currentQRCode!!, currentDesign)
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
            if (isCurrentSaved) {
                _toastMessage.emit("Bạn đã lưu vào trong lịch sử")
                return@launch
            }

            _uiState.value = QRGeneratorUiState.Loading
            val result = saveQRUseCase(qrCode)
            when (result) {
                is Result.Success -> {
                    isCurrentSaved = true
                    _toastMessage.emit("Đã lưu vào lịch sử")
                    _uiState.value = QRGeneratorUiState.Success(qrCode)
                }
                is Result.Error -> {
                    _uiState.value = QRGeneratorUiState.Error(result.exception.message ?: "Save failed")
                }
                is Result.Loading -> {}
            }
        }
    }

    private fun handleNavigateToHistory() {
        _uiState.value = QRGeneratorUiState.HistoryList(isLoading = true)
        viewModelScope.launch {
            val result = getQRHistoryUseCase()
            when (result) {
                is Result.Success -> {
                    _uiState.value = QRGeneratorUiState.HistoryList(
                        items = result.data,
                        isLoading = false
                    )
                }
                is Result.Error -> {
                    _uiState.value = QRGeneratorUiState.HistoryList(
                        items = emptyList(),
                        isLoading = false
                    )
                }
                is Result.Loading -> {}
            }
        }
    }

    private fun handleViewHistoryDetail(qrCode: QRCode) {
        val currentState = _uiState.value
        if (currentState is QRGeneratorUiState.HistoryList) {
            _uiState.value = currentState.copy(selectedDetail = qrCode)
        }
    }

    private fun handleDismissHistoryDetail() {
        val currentState = _uiState.value
        if (currentState is QRGeneratorUiState.HistoryList) {
            _uiState.value = currentState.copy(selectedDetail = null)
        }
    }

    private fun handleDeleteHistoryItem(id: String) {
        viewModelScope.launch {
            val result = repository.deleteQR(id)
            if (result is Result.Success) {
                val historyResult = getQRHistoryUseCase()
                if (historyResult is Result.Success) {
                    _uiState.value = QRGeneratorUiState.HistoryList(
                        items = historyResult.data,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun handleReset() {
        selectedType = null
        currentName = ""
        currentContent = ""
        currentDesign = QRDesign()
        currentQRCode = null
        isCurrentSaved = false
        _uiState.value = QRGeneratorUiState.StepTypeSelection()
    }
}
