package com.example.qrgrenertor.presentation.ui


import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.material3.ripple
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
import com.example.qrgrenertor.domain.model.ErrorCorrectionLevel
import com.example.qrgrenertor.domain.model.QRDesign
import com.example.qrgrenertor.domain.model.QRStyle
import com.example.qrgrenertor.presentation.ui.components.GlassCard
import com.example.qrgrenertor.presentation.ui.components.GradientButton
import com.example.qrgrenertor.presentation.ui.components.StepProgressBar
import com.example.qrgrenertor.presentation.ui.theme.QRAppColors

@Composable
fun QRDesignCustomizationScreen(
    content: String,
    name: String,
    currentDesign: QRDesign,
    onDesignUpdated: (QRDesign) -> Unit,
    onGenerateQR: () -> Unit,
    onPrevious: () -> Unit
) {
    var backgroundColor by remember { mutableStateOf(currentDesign.backgroundColor) }
    var codeColor by remember { mutableStateOf(currentDesign.codeColor) }
    var qrStyle by remember { mutableStateOf(currentDesign.style) }

    val qrBitmap = remember(backgroundColor, codeColor, qrStyle) {
        QRGeneratorUtils.generateQRCode(
            "PREVIEW_ONLY",
            QRDesign(backgroundColor, codeColor, 512, qrStyle)
        )
    }

    val backgroundColors = listOf(
        0xFFFFFFFF to "Trắng",
        0xFFF5F5F5 to "Xám nhạt",
        0xFF000000 to "Đen",
        0xFFE8F4F8 to "Xanh nhạt",
        0xFFF0E8FF to "Tím nhạt",
        0xFFFFF3E0 to "Cam nhạt",
        0xFFE8F5E9 to "Xanh lá nhạt"
    )

    val codeColors = listOf(
        0xFF000000 to "Đen",
        0xFF1976D2 to "Xanh",
        0xFFD32F2F to "Đỏ",
        0xFF388E3C to "Xanh lá",
        0xFF7B1FA2 to "Tím",
        0xFFE65100 to "Cam",
        0xFF00695C to "Teal"
    )

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(QRAppColors.DarkBackground)
            .statusBarsPadding()
    ) {
        // Step Progress
        StepProgressBar(
            currentStep = 3,
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                IconButton(
                    onClick = onPrevious,
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = QRAppColors.TextSecondary
                    )
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Quay lại"
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Thiết kế mã QR",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Tuỳ chỉnh giao diện theo ý bạn",
                        style = MaterialTheme.typography.bodyMedium,
                        color = QRAppColors.TextSecondary
                    )
                }
            }

            // QR Preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp),
                contentAlignment = Alignment.Center
            ) {
                // Khung viền cố định bên ngoài (Khung giữ nguyên)
                Box(
                    modifier = Modifier
                        .size(240.dp)
                        .border(
                            width = 1.dp,
                            color = Color.White.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .background(
                            color = Color.White.copy(alpha = 0.03f),
                            shape = RoundedCornerShape(24.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // QR preview at fixed size
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .shadow(
                                elevation = 12.dp,
                                shape = RoundedCornerShape(12.dp),
                                ambientColor = QRAppColors.PrimaryStart.copy(alpha = 0.2f),
                                spotColor = QRAppColors.PrimaryStart.copy(alpha = 0.2f)
                            )
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(backgroundColor)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            bitmap = qrBitmap.asImageBitmap(),
                            contentDescription = "Bản xem trước QR",
                            modifier = Modifier.size(140.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Background Color Section
            SectionHeader(title = "Màu nền")
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(backgroundColors.size) { index ->
                    val (color, name) = backgroundColors[index]
                    ColorCircle(
                        color = Color(color),
                        isSelected = backgroundColor == color,
                        onClick = {
                            backgroundColor = color
                            onDesignUpdated(QRDesign(color, codeColor, 512, qrStyle))
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Code Color Section
            SectionHeader(title = "Màu mã QR")
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(codeColors.size) { index ->
                    val (color, name) = codeColors[index]
                    ColorCircle(
                        color = Color(color),
                        isSelected = codeColor == color,
                        onClick = {
                            codeColor = color
                            onDesignUpdated(QRDesign(backgroundColor, color, 512, qrStyle))
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // QR Style Section
            SectionHeader(title = "Kiểu mẫu")
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(QRStyle.entries.size) { index ->
                    val style = QRStyle.entries[index]
                    QRStyleCard(
                        style = style,
                        isSelected = qrStyle == style,
                        codeColor = codeColor,
                        onClick = {
                            qrStyle = style
                            onDesignUpdated(QRDesign(backgroundColor, codeColor, 512, style))
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Bottom Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(QRAppColors.DarkBackground)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onPrevious,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = QRAppColors.TextSecondary
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp, QRAppColors.DarkCardElevated
                )
            ) {
                Text("Quay lại")
            }

            GradientButton(
                text = "Tạo mã QR",
                onClick = onGenerateQR,
                icon = Icons.Filled.AutoFixHigh,
                gradient = QRAppColors.AccentGradient,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.SemiBold,
        color = QRAppColors.TextSecondary
    )
}

@Composable
private fun ColorCircle(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .size(44.dp)
            .then(
                if (isSelected) {
                    Modifier.shadow(
                        elevation = 8.dp,
                        shape = CircleShape,
                        ambientColor = color.copy(alpha = 0.5f),
                        spotColor = color.copy(alpha = 0.5f)
                    )
                } else Modifier
            )
            .clip(CircleShape)
            .background(color)
            .then(
                if (isSelected) {
                    Modifier.border(
                        width = 2.5.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(QRAppColors.PrimaryStart, QRAppColors.AccentStart)
                        ),
                        shape = CircleShape
                    )
                } else {
                    Modifier.border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.1f),
                        shape = CircleShape
                    )
                }
            )
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true, color = Color.White)
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "Đã chọn",
                tint = if (color == Color.White || color == Color(0xFFF5F5F5) || color == Color(0xFFF0E8FF) || color == Color(0xFFE8F4F8) || color == Color(0xFFFFF3E0) || color == Color(0xFFE8F5E9))
                    QRAppColors.PrimaryStart
                else
                    Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun QRStyleCard(
    style: QRStyle,
    isSelected: Boolean,
    codeColor: Long,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    // Generate mini QR thumbnail for this style
    val thumbnailBitmap = remember(style, codeColor) {
        QRGeneratorUtils.generateQRCode(
            "STYLE",
            QRDesign(
                backgroundColor = 0xFFFFFFFF,
                codeColor = codeColor,
                size = 128,
                style = style
            )
        )
    }

    val animatedScale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "styleCardScale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .scale(animatedScale)
            .width(80.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true, color = QRAppColors.PrimaryStart)
            ) { onClick() }
    ) {
        // Thumbnail container
        Box(
            modifier = Modifier
                .size(72.dp)
                .then(
                    if (isSelected) {
                        Modifier.shadow(
                            elevation = 12.dp,
                            shape = RoundedCornerShape(16.dp),
                            ambientColor = QRAppColors.PrimaryStart.copy(alpha = 0.4f),
                            spotColor = QRAppColors.PrimaryStart.copy(alpha = 0.4f)
                        )
                    } else Modifier
                )
                .clip(RoundedCornerShape(16.dp))
                .background(
                    if (isSelected) QRAppColors.DarkCardElevated
                    else QRAppColors.DarkCard
                )
                .then(
                    if (isSelected) {
                        Modifier.border(
                            width = 2.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(QRAppColors.PrimaryStart, QRAppColors.AccentStart)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                    } else {
                        Modifier.border(
                            width = 1.dp,
                            color = Color.White.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            // Mini QR preview
            Image(
                bitmap = thumbnailBitmap.asImageBitmap(),
                contentDescription = style.displayName,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(6.dp))
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Style name
        Text(
            text = style.displayName,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) QRAppColors.PrimaryStart else QRAppColors.TextSecondary,
            maxLines = 1
        )
    }
}
