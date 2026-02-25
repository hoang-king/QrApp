package com.example.qrgrenertor.presentation.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.qrgrenertor.presentation.ui.components.GlassCard
import com.example.qrgrenertor.presentation.ui.components.GradientButton
import com.example.qrgrenertor.presentation.ui.theme.QRAppColors

@Composable
fun QRGeneratorNavigation(
    uiState: QRGeneratorUiState,
    onEvent: (QRGeneratorEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        when (uiState) {
            is QRGeneratorUiState.StepTypeSelection -> {
                QRTypeSelectionScreen(
                    onTypeSelected = { type -> onEvent(QRGeneratorEvent.SelectQRType(type)) },
                    onNext = { onEvent(QRGeneratorEvent.GoToNextStep) },
                    onNavigateToHistory = { onEvent(QRGeneratorEvent.NavigateToHistory) },
                    onNavigateToSettings = { onEvent(QRGeneratorEvent.NavigateToSettings) }
                )
            }
            is QRGeneratorUiState.HistoryList -> {
                QRHistoryScreen(
                    items = uiState.items,
                    isLoading = uiState.isLoading,
                    selectedDetail = uiState.selectedDetail,
                    onItemClick = { qrCode -> onEvent(QRGeneratorEvent.ViewHistoryDetail(qrCode)) },
                    onDeleteItem = { id -> onEvent(QRGeneratorEvent.DeleteHistoryItem(id)) },
                    onDismissDetail = { onEvent(QRGeneratorEvent.DismissHistoryDetail) },
                    onBack = { onEvent(QRGeneratorEvent.Reset) }
                )
            }
            is QRGeneratorUiState.Settings -> {
                SettingsScreen(onBack = { onEvent(QRGeneratorEvent.Reset) })
            }
            is QRGeneratorUiState.StepContentInput -> {
                QRContentInputScreen(
                    selectedType = uiState.selectedType,
                    name = uiState.name,
                    onNameEntered = { name -> onEvent(QRGeneratorEvent.EnterName(name)) },
                    onContentEntered = { content -> onEvent(QRGeneratorEvent.EnterContent(content)) },
                    onNext = { onEvent(QRGeneratorEvent.GoToNextStep) },
                    onPrevious = { onEvent(QRGeneratorEvent.GoToPreviousStep) }
                )
            }
            is QRGeneratorUiState.StepDesignCustomization -> {
                QRDesignCustomizationScreen(
                    content = uiState.content,
                    name = uiState.name,
                    currentDesign = uiState.design,
                    onDesignUpdated = { design -> onEvent(QRGeneratorEvent.UpdateDesign(design)) },
                    onGenerateQR = { onEvent(QRGeneratorEvent.GenerateQR) },
                    onPrevious = { onEvent(QRGeneratorEvent.GoToPreviousStep) }
                )
            }
            is QRGeneratorUiState.StepQRGeneration -> {
                QRGenerationResultScreen(
                    qrCode = uiState.qrCode,
                    design = uiState.design,
                    onSave = { onEvent(QRGeneratorEvent.SaveQR) },
                    onShare = { /* Share functionality to be implemented */ },
                    onBack = { onEvent(QRGeneratorEvent.GoToPreviousStep) },
                    onNewQR = { onEvent(QRGeneratorEvent.Reset) }
                )
            }
            is QRGeneratorUiState.Success -> {
                QRGenerationResultScreen(
                    qrCode = uiState.qrCode,
                    design = uiState.qrCode.designSettings,
                    onSave = { onEvent(QRGeneratorEvent.SaveQR) },
                    onShare = { /* Share functionality */ },
                    onBack = { onEvent(QRGeneratorEvent.GoToPreviousStep) },
                    onNewQR = { onEvent(QRGeneratorEvent.Reset) }
                )
            }
            is QRGeneratorUiState.Error -> {
                ErrorScreen(
                    message = uiState.message,
                    onRetry = { onEvent(QRGeneratorEvent.Reset) }
                )
            }
            is QRGeneratorUiState.Loading -> {
                LoadingScreen()
            }
            is QRGeneratorUiState.Idle -> {
                IdleScreen()
            }
        }
    }
}

@Composable
private fun ErrorScreen(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(QRAppColors.DarkBackground)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Error icon with glow
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(QRAppColors.Error.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.ErrorOutline,
                contentDescription = "Lỗi",
                tint = QRAppColors.Error,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Đã xảy ra lỗi",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = QRAppColors.TextSecondary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))

        GradientButton(
            text = "Thử lại",
            onClick = onRetry,
            icon = Icons.Outlined.Refresh,
            modifier = Modifier.fillMaxWidth(0.6f)
        )
    }
}

@Composable
private fun LoadingScreen() {
    val infiniteTransition = rememberInfiniteTransition(label = "loading")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(QRAppColors.DarkBackground),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Pulsating QR icon
        Box(
            modifier = Modifier
                .scale(scale)
                .size(80.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(QRAppColors.PrimaryStart, QRAppColors.PrimaryEnd)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.QrCode,
                contentDescription = "Đang tạo",
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Đang tạo mã QR...",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Vui lòng chờ trong giây lát",
            style = MaterialTheme.typography.bodyMedium,
            color = QRAppColors.TextSecondary
        )
    }
}

@Composable
private fun IdleScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(QRAppColors.DarkBackground),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.QrCode2,
            contentDescription = "QR",
            tint = QRAppColors.PrimaryStart,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Đang khởi tạo...",
            style = MaterialTheme.typography.titleMedium,
            color = QRAppColors.TextSecondary
        )
    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cài đặt", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = QRAppColors.DarkBackground)
            )
        },
        containerColor = QRAppColors.DarkBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Outlined.Settings, contentDescription = null, modifier = Modifier.size(64.dp), tint = QRAppColors.TextSecondary)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Tính năng cài đặt đang phát triển", color = QRAppColors.TextSecondary)
        }
    }
}