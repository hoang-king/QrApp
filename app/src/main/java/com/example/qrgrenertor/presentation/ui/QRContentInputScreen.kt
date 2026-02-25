package com.example.qrgrenertor.presentation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.qrgrenertor.domain.model.QRSourceType
import com.example.qrgrenertor.presentation.ui.components.GlassCard
import com.example.qrgrenertor.presentation.ui.components.GradientButton
import com.example.qrgrenertor.presentation.ui.components.StepProgressBar
import com.example.qrgrenertor.presentation.ui.theme.QRAppColors

@Composable
fun QRContentInputScreen(
    selectedType: QRSourceType,
    name: String,
    onNameEntered: (String) -> Unit,
    onContentEntered: (String) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit
) {
    var content by remember { mutableStateOf("") }
    
    // Initial sync with ViewModel state if needed, but using the passed parameters is better
    // For local state management within this composable:
    var qrName by remember { mutableStateOf(name) }
    var qrContent by remember { mutableStateOf("") }
    
    // Update local state when parent state changes
    LaunchedEffect(name) {
        qrName = name
    }

    val placeholders = mapOf(
        QRSourceType.URL to "https://example.com",
        QRSourceType.WIFI to "Tên mạng WiFi",
        QRSourceType.EMAIL to "email@example.com",
        QRSourceType.PHONE to "+84123456789",
        QRSourceType.SMS to "Nhập nội dung tin nhắn",
        QRSourceType.CONTACT to "Tên người liên hệ",
        QRSourceType.MUSIC to "https://music.example.com",
        QRSourceType.PDF to "https://pdf.example.com",
        QRSourceType.IMAGE to "https://image.example.com",
        QRSourceType.FACEBOOK to "https://facebook.com/username",
        QRSourceType.INSTAGRAM to "https://instagram.com/username",
        QRSourceType.VCARD to "Họ và tên đầy đủ"
    )

    val typeIcons: Map<QRSourceType, ImageVector> = mapOf(
        QRSourceType.URL to Icons.Outlined.Link,
        QRSourceType.WIFI to Icons.Outlined.Wifi,
        QRSourceType.EMAIL to Icons.Outlined.Email,
        QRSourceType.PHONE to Icons.Outlined.Phone,
        QRSourceType.SMS to Icons.Outlined.Sms,
        QRSourceType.CONTACT to Icons.Outlined.Contacts,
        QRSourceType.MUSIC to Icons.Outlined.MusicNote,
        QRSourceType.PDF to Icons.Outlined.PictureAsPdf,
        QRSourceType.IMAGE to Icons.Outlined.PhotoLibrary,
        QRSourceType.FACEBOOK to Icons.Outlined.Facebook,
        QRSourceType.INSTAGRAM to Icons.Outlined.CameraAlt,
        QRSourceType.VCARD to Icons.Outlined.Person
    )

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(QRAppColors.DarkBackground)
            .padding(horizontal = 16.dp)
            .statusBarsPadding()
    ) {
        // Step Progress
        StepProgressBar(currentStep = 2)

        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
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
                    text = "Nhập nội dung",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = selectedType.displayName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = QRAppColors.PrimaryStart
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Input Card
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn() + slideInVertically { 50 }
        ) {
            GlassCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Type badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(QRAppColors.PrimaryStart.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = typeIcons[selectedType] ?: Icons.Outlined.QrCode,
                                contentDescription = null,
                                tint = QRAppColors.PrimaryStart,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Nhập nội dung cho mã QR",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                    }

                    Text(
                        text = "Tên mã QR",
                        style = MaterialTheme.typography.labelMedium,
                        color = QRAppColors.TextSecondary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = qrName,
                        onValueChange = {
                            qrName = it
                            onNameEntered(it)
                        },
                        placeholder = {
                            Text(
                                "Ví dụ: Mã QR của tôi",
                                color = QRAppColors.TextTertiary
                            )
                        },
                        trailingIcon = {
                            if (qrName.isNotEmpty()) {
                                IconButton(onClick = { 
                                    qrName = ""
                                    onNameEntered("")
                                }) {
                                    Icon(
                                        imageVector = Icons.Outlined.Close,
                                        contentDescription = "Xóa",
                                        tint = QRAppColors.TextTertiary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = QRAppColors.PrimaryStart,
                            unfocusedBorderColor = QRAppColors.DarkCardElevated,
                            cursorColor = QRAppColors.PrimaryStart,
                            focusedContainerColor = QRAppColors.DarkSurface.copy(alpha = 0.5f),
                            unfocusedContainerColor = QRAppColors.DarkSurface.copy(alpha = 0.3f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Text(
                        text = "Nội dung",
                        style = MaterialTheme.typography.labelMedium,
                        color = QRAppColors.TextSecondary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Text Field
                    OutlinedTextField(
                        value = content,
                        onValueChange = {
                            content = it
                            onContentEntered(it)
                        },
                        placeholder = {
                            Text(
                                placeholders[selectedType] ?: "Nhập nội dung",
                                color = QRAppColors.TextTertiary
                            )
                        },
                        trailingIcon = {
                            if (content.isNotEmpty()) {
                                IconButton(onClick = { 
                                    content = ""
                                    onContentEntered("")
                                }) {
                                    Icon(
                                        imageVector = Icons.Outlined.Close,
                                        contentDescription = "Xóa",
                                        tint = QRAppColors.TextTertiary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = QRAppColors.PrimaryStart,
                            unfocusedBorderColor = QRAppColors.DarkCardElevated,
                            cursorColor = QRAppColors.PrimaryStart,
                            focusedContainerColor = QRAppColors.DarkSurface.copy(alpha = 0.5f),
                            unfocusedContainerColor = QRAppColors.DarkSurface.copy(alpha = 0.3f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    // Character count
                    if (content.isNotEmpty()) {
                        Text(
                            text = "${content.length} ký tự",
                            style = MaterialTheme.typography.labelSmall,
                            color = QRAppColors.TextTertiary,
                            modifier = Modifier
                                .align(Alignment.End)
                                .padding(top = 6.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
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
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Quay lại")
            }

            GradientButton(
                text = "Tiếp theo",
                onClick = { if (content.isNotEmpty()) onNext() },
                enabled = content.isNotEmpty(),
                icon = Icons.Filled.ArrowForward,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
