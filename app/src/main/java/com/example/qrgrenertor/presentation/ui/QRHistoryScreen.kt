package com.example.qrgrenertor.presentation.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.ripple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.qrgrenertor.domain.model.QRCode
import com.example.qrgrenertor.domain.model.QRHistory
import com.example.qrgrenertor.domain.model.QRSourceType
import com.example.qrgrenertor.presentation.ui.components.GlassCard
import com.example.qrgrenertor.presentation.ui.components.GradientButton
import com.example.qrgrenertor.presentation.ui.theme.QRAppColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun QRHistoryScreen(
    items: List<QRHistory>,
    isLoading: Boolean,
    selectedDetail: QRCode?,
    onItemClick: (QRCode) -> Unit,
    onDeleteItem: (String) -> Unit,
    onDismissDetail: () -> Unit,
    onBack: () -> Unit
) {
    if (selectedDetail != null) {
        QRDetailDialog(
            qrCode = selectedDetail,
            onDismiss = onDismissDetail
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(QRAppColors.DarkBackground)
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
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
                    text = "Lịch sử",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "${items.size} mã QR đã lưu",
                    style = MaterialTheme.typography.bodySmall,
                    color = QRAppColors.TextSecondary
                )
            }
        }

        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = QRAppColors.PrimaryStart)
                }
            }
            items.isEmpty() -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier.size(80.dp).clip(RoundedCornerShape(20.dp)).background(QRAppColors.PrimaryStart.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Outlined.History, contentDescription = null, tint = QRAppColors.PrimaryStart.copy(alpha = 0.6f), modifier = Modifier.size(40.dp))
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(text = "Chưa có mã QR nào", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(text = "Tạo và lưu mã QR để xem ở đây", style = MaterialTheme.typography.bodyMedium, color = QRAppColors.TextSecondary)
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    itemsIndexed(items) { _, historyItem ->
                        HistoryCard(
                            historyItem = historyItem,
                            onClick = { onItemClick(historyItem.qrCode) },
                            onDelete = { onDeleteItem(historyItem.id) }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
private fun HistoryCard(
    historyItem: QRHistory,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val qrCode = historyItem.qrCode
    val interactionSource = remember { MutableInteractionSource() }

    val typeIcon = when (qrCode.sourceType) {
        QRSourceType.URL -> Icons.Outlined.Link
        QRSourceType.WIFI -> Icons.Outlined.Wifi
        QRSourceType.CONTACT -> Icons.Outlined.Person
        QRSourceType.EMAIL -> Icons.Outlined.Email
        QRSourceType.PHONE -> Icons.Outlined.Phone
        QRSourceType.SMS -> Icons.Outlined.Sms
        QRSourceType.MUSIC -> Icons.Outlined.MusicNote
        QRSourceType.PDF -> Icons.Outlined.PictureAsPdf
        QRSourceType.IMAGE -> Icons.Outlined.Image
        QRSourceType.FACEBOOK -> Icons.Outlined.Facebook
        QRSourceType.INSTAGRAM -> Icons.Outlined.CameraAlt
        QRSourceType.VCARD -> Icons.Outlined.ContactPage
        QRSourceType.LOCATION -> Icons.Outlined.Place
        QRSourceType.VIDEO -> Icons.Outlined.VideoLibrary
    }

    val typeColor = when (qrCode.sourceType) {
        QRSourceType.URL -> Color(0xFF4FC3F7)
        QRSourceType.WIFI -> Color(0xFF81C784)
        QRSourceType.CONTACT -> Color(0xFFBA68C8)
        QRSourceType.EMAIL -> Color(0xFFFFB74D)
        QRSourceType.PHONE -> Color(0xFF4DB6AC)
        QRSourceType.SMS -> Color(0xFF7986CB)
        QRSourceType.MUSIC -> Color(0xFFE57373)
        QRSourceType.PDF -> Color(0xFFFF8A65)
        QRSourceType.IMAGE -> Color(0xFFAED581)
        QRSourceType.FACEBOOK -> Color(0xFF64B5F6)
        QRSourceType.INSTAGRAM -> Color(0xFFF06292)
        QRSourceType.VCARD -> Color(0xFF9575CD)
        QRSourceType.LOCATION -> Color(0xFF3F51B5)
        QRSourceType.VIDEO -> Color(0xFFEF5350)
    }

    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }
    val dateString = remember(qrCode.createdAt) { dateFormat.format(Date(qrCode.createdAt)) }

    GlassCard(
        modifier = Modifier.fillMaxWidth().clickable(interactionSource = interactionSource, indication = ripple(color = QRAppColors.PrimaryStart)) { onClick() }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(14.dp)).background(typeColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = typeIcon, contentDescription = null, tint = typeColor, modifier = Modifier.size(24.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(text = qrCode.name.ifEmpty { "QR Code" }, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = qrCode.sourceType.displayName, style = MaterialTheme.typography.labelSmall, color = typeColor, fontWeight = FontWeight.Medium)
                    Text(text = "·", color = QRAppColors.TextTertiary)
                    Text(text = dateString, style = MaterialTheme.typography.labelSmall, color = QRAppColors.TextTertiary)
                }
            }

            IconButton(onClick = onDelete, modifier = Modifier.size(36.dp), colors = IconButtonDefaults.iconButtonColors(contentColor = QRAppColors.TextTertiary)) {
                Icon(imageVector = Icons.Outlined.Delete, contentDescription = "Xóa", modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
private fun QRDetailDialog(
    qrCode: QRCode,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val qrBitmap = remember(qrCode.content, qrCode.designSettings) {
        QRGeneratorUtils.generateQRCode(qrCode.content, qrCode.designSettings)
    }

    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }
    val dateString = remember(qrCode.createdAt) { dateFormat.format(Date(qrCode.createdAt)) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(0.92f).clip(RoundedCornerShape(24.dp)).background(QRAppColors.DarkCard)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Chi tiết mã QR", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Filled.Close, contentDescription = "Đóng", tint = QRAppColors.TextSecondary, modifier = Modifier.size(20.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier.size(200.dp).shadow(elevation = 16.dp, shape = RoundedCornerShape(16.dp), ambientColor = QRAppColors.PrimaryStart.copy(alpha = 0.2f), spotColor = QRAppColors.PrimaryStart.copy(alpha = 0.2f)).clip(RoundedCornerShape(16.dp)).background(Color(qrCode.designSettings.backgroundColor)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(bitmap = qrBitmap.asImageBitmap(), contentDescription = "QR Code", modifier = Modifier.size(180.dp))
                }

                Spacer(modifier = Modifier.height(20.dp))

                Column(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(QRAppColors.DarkBackground.copy(alpha = 0.5f)).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DetailRow(label = "Tên", value = qrCode.name.ifEmpty { "QR Code" })
                    HorizontalDivider(color = QRAppColors.DarkCardElevated)
                    DetailRow(label = "Loại", value = qrCode.sourceType.displayName)
                    HorizontalDivider(color = QRAppColors.DarkCardElevated)
                    DetailRow(label = "Nội dung", value = qrCode.content)
                    HorizontalDivider(color = QRAppColors.DarkCardElevated)
                    DetailRow(label = "Ngày tạo", value = dateString)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = onDismiss, modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp), border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                    ) { Text("Đóng") }

                    GradientButton(
                        text = "Chia sẻ",
                        onClick = { ShareUtils.shareQRCode(context, qrBitmap, qrCode.name) },
                        icon = Icons.Outlined.Share,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = QRAppColors.TextTertiary, modifier = Modifier.width(80.dp), fontWeight = FontWeight.Medium)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, color = Color.White, modifier = Modifier.weight(1f))
    }
}
