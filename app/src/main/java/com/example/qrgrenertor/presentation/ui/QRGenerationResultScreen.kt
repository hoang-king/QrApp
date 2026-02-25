package com.example.qrgrenertor.presentation.ui

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.qrgrenertor.domain.model.QRCode
import com.example.qrgrenertor.domain.model.QRDesign
import com.example.qrgrenertor.presentation.ui.components.GlassCard
import com.example.qrgrenertor.presentation.ui.components.GradientButton
import com.example.qrgrenertor.presentation.ui.components.StepProgressBar
import com.example.qrgrenertor.presentation.ui.theme.QRAppColors
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QRGenerationResultScreen(
    qrCode: QRCode,
    design: QRDesign,
    onSave: () -> Unit,
    onShare: () -> Unit,
    onBack: () -> Unit,
    onNewQR: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

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

    // Modal Bottom Sheet for Sharing
    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState,
            containerColor = QRAppColors.DarkCard,
            dragHandle = { BottomSheetDefaults.DragHandle(color = Color.White.copy(alpha = 0.2f)) },
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 40.dp, start = 24.dp, end = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Chia sẻ mã QR",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ShareOptionItem(
                        icon = Icons.Outlined.Share,
                        label = "Gửi ảnh",
                        color = Color(0xFF4285F4)
                    ) {
                        scope.launch {
                            sheetState.hide()
                            showSheet = false
                            ShareUtils.shareQRCode(context, qrBitmap, qrCode.name)
                        }
                    }
                    
                    ShareOptionItem(
                        icon = Icons.Outlined.ContentCopy,
                        label = "Sao chép",
                        color = Color(0xFF34A853)
                    ) {
                        scope.launch {
                            sheetState.hide()
                            showSheet = false
                            ShareUtils.copyImageToClipboard(context, qrBitmap)
                            Toast.makeText(context, "Đã sao chép ảnh mã QR", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(QRAppColors.DarkBackground)
            .statusBarsPadding()
    ) {
        StepProgressBar(currentStep = 4, modifier = Modifier.padding(horizontal = 16.dp))

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

            // QR Preview
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .scale(scaleAnim.value),
                contentAlignment = Alignment.Center
            ) {
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

                GlassCard(modifier = Modifier.wrapContentSize()) {
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

            // Details Card
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp)) {
                    InfoRow(icon = Icons.Outlined.Category, label = "Loại", value = qrCode.sourceType.displayName)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Color.White.copy(alpha = 0.05f))
                    InfoRow(icon = Icons.Outlined.Title, label = "Tên", value = qrCode.name.ifEmpty { "Không có tên" })
                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Color.White.copy(alpha = 0.05f))
                    InfoRow(
                        icon = Icons.Outlined.TextFields, 
                        label = "Nội dung", 
                        value = qrCode.content.take(100) + if (qrCode.content.length > 100) "..." else ""
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Action Buttons Row
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(QRAppColors.DarkBackground)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
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
                    onClick = { showSheet = true },
                    icon = Icons.Filled.Share,
                    gradient = QRAppColors.AccentGradient,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.weight(1f).height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = QRAppColors.TextSecondary),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                ) {
                    Text("Quay lại")
                }

                OutlinedButton(
                    onClick = onNewQR,
                    modifier = Modifier.weight(1f).height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = QRAppColors.PrimaryStart),
                    border = androidx.compose.foundation.BorderStroke(1.dp, QRAppColors.PrimaryStart.copy(alpha = 0.3f))
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Tạo mới")
                }
            }
        }
    }
}

@Composable
private fun ShareOptionItem(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(color.copy(alpha = 0.12f), CircleShape)
                .border(1.dp, color.copy(alpha = 0.2f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon, 
                contentDescription = label, 
                tint = color, 
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label, 
            style = MaterialTheme.typography.labelMedium, 
            color = Color.White.copy(alpha = 0.8f),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(), 
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon, 
            contentDescription = null, 
            tint = QRAppColors.PrimaryStart, 
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
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
            modifier = Modifier.weight(1f),
            maxLines = 2,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
        )
    }
}
