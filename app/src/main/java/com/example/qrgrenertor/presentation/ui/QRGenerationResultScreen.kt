package com.example.qrgrenertor.presentation.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.qrgrenertor.domain.model.QRCode
import com.example.qrgrenertor.domain.model.QRDesign
import com.example.qrgrenertor.presentation.ui.components.GlassCard
import com.example.qrgrenertor.presentation.ui.components.GradientButton
import com.example.qrgrenertor.presentation.ui.components.StepProgressBar
import com.example.qrgrenertor.presentation.ui.theme.QRAppColors

@Composable
fun QRGenerationResultScreen(
    qrCode: QRCode,
    design: QRDesign,
    onSave: () -> Unit,
    onShare: () -> Unit,
    onBack: () -> Unit,
    onNewQR: () -> Unit
) {
    val scrollState = rememberScrollState()

    val qrBitmap = remember(qrCode.content, design) {
        QRGeneratorUtils.generateQRCode(qrCode.content, design)
    }

    // Celebration animation
    val scaleAnim = remember { Animatable(0.5f) }
    LaunchedEffect(Unit) {
        scaleAnim.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(QRAppColors.DarkBackground)
            .statusBarsPadding()
    ) {
        // Step Progress
        StepProgressBar(
            currentStep = 4,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = null,
                    tint = QRAppColors.AccentEnd,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Mã QR đã sẵn sàng!",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Lưu hoặc chia sẻ mã QR của bạn",
                        style = MaterialTheme.typography.bodyMedium,
                        color = QRAppColors.TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // QR Code Preview with glow
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .scale(scaleAnim.value),
                contentAlignment = Alignment.Center
            ) {
                // Glow
                Box(
                    modifier = Modifier
                        .size(280.dp)
                        .shadow(
                            elevation = 24.dp,
                            shape = RoundedCornerShape(24.dp),
                            ambientColor = QRAppColors.PrimaryStart.copy(alpha = 0.3f),
                            spotColor = QRAppColors.PrimaryStart.copy(alpha = 0.3f)
                        )
                )

                GlassCard(
                    modifier = Modifier.wrapContentSize()
                ) {
                    Box(
                        modifier = Modifier
                            .padding(20.dp)
                            .size(240.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(design.backgroundColor)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            bitmap = qrBitmap.asImageBitmap(),
                            contentDescription = "QR Code",
                            modifier = Modifier.size(220.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // QR Code Info
            GlassCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    InfoRow(
                        icon = Icons.Outlined.Category,
                        label = "Loại",
                        value = qrCode.sourceType.displayName
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 10.dp),
                        color = QRAppColors.DarkCardElevated
                    )
                    InfoRow(
                        icon = Icons.Outlined.Title,
                        label = "Tên",
                        value = qrCode.name
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 10.dp),
                        color = QRAppColors.DarkCardElevated
                    )
                    InfoRow(
                        icon = Icons.Outlined.TextFields,
                        label = "Nội dung",
                        value = qrCode.content.take(40) + if (qrCode.content.length > 40) "..." else ""
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Action Buttons
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(QRAppColors.DarkBackground)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Save & Share row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                GradientButton(
                    text = "Lưu",
                    onClick = onSave,
                    icon = Icons.Filled.Download,
                    gradient = QRAppColors.PrimaryGradient,
                    modifier = Modifier.weight(1f)
                )

                GradientButton(
                    text = "Chia sẻ",
                    onClick = onShare,
                    icon = Icons.Filled.Share,
                    gradient = QRAppColors.AccentGradient,
                    modifier = Modifier.weight(1f)
                )
            }

            // Back & New QR row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = QRAppColors.TextSecondary
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp, QRAppColors.DarkCardElevated
                    )
                ) {
                    Text("Quay lại", style = MaterialTheme.typography.labelLarge)
                }

                OutlinedButton(
                    onClick = onNewQR,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = QRAppColors.PrimaryStart
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp, QRAppColors.PrimaryStart.copy(alpha = 0.5f)
                    )
                ) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Tạo mới", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

@Composable
private fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = QRAppColors.PrimaryStart,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = QRAppColors.TextTertiary,
            modifier = Modifier.width(80.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
            modifier = Modifier.weight(1f)
        )
    }
}
