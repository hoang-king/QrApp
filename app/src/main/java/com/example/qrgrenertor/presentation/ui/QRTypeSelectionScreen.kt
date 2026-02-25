package com.example.qrgrenertor.presentation.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.qrgrenertor.domain.model.QRSourceType
import com.example.qrgrenertor.presentation.ui.components.GradientButton
import com.example.qrgrenertor.presentation.ui.components.StepProgressBar
import com.example.qrgrenertor.presentation.ui.theme.QRAppColors

@Composable
fun QRTypeSelectionScreen(
    onTypeSelected: (QRSourceType) -> Unit,
    onNext: () -> Unit,
    onNavigateToHistory: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    val qrTypes = listOf(
        QRSourceType.URL to Icons.Outlined.Link,
        QRSourceType.WIFI to Icons.Outlined.Wifi,
        QRSourceType.CONTACT to Icons.Outlined.Contacts,
        QRSourceType.EMAIL to Icons.Outlined.Email,
        QRSourceType.PHONE to Icons.Outlined.Phone,
        QRSourceType.SMS to Icons.Outlined.Sms,
        QRSourceType.MUSIC to Icons.Outlined.MusicNote,
        QRSourceType.PDF to Icons.Outlined.PictureAsPdf,
        QRSourceType.IMAGE to Icons.Outlined.PhotoLibrary,
        QRSourceType.FACEBOOK to Icons.Outlined.Facebook,
        QRSourceType.INSTAGRAM to Icons.Outlined.CameraAlt,
        QRSourceType.VCARD to Icons.Outlined.Person,
        QRSourceType.LOCATION to Icons.Outlined.Map
    )

    val typeColors = mapOf(
        QRSourceType.URL to Color(0xFF448AFF),
        QRSourceType.WIFI to Color(0xFF00E5FF),
        QRSourceType.CONTACT to Color(0xFF7C4DFF),
        QRSourceType.EMAIL to Color(0xFFFF6E40),
        QRSourceType.PHONE to Color(0xFF69F0AE),
        QRSourceType.SMS to Color(0xFFFFAB40),
        QRSourceType.MUSIC to Color(0xFFFF4081),
        QRSourceType.PDF to Color(0xFFEF5350),
        QRSourceType.IMAGE to Color(0xFF26C6DA),
        QRSourceType.FACEBOOK to Color(0xFF1E88E5),
        QRSourceType.INSTAGRAM to Color(0xFFE040FB),
        QRSourceType.VCARD to Color(0xFF66BB6A),
        QRSourceType.LOCATION to Color(0xFF3F51B5)
    )

    var selectedType: QRSourceType? by remember { mutableStateOf(null) }
    var showMenu by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(QRAppColors.DarkBackground)
            .padding(horizontal = 16.dp)
            .statusBarsPadding()
    ) {
        StepProgressBar(currentStep = 1)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Chọn loại QR Code", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Color.White)
            Box {
                IconButton(onClick = { showMenu = true }) { Icon(imageVector = Icons.Default.MoreVert, contentDescription = null, tint = Color.White) }
                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }, modifier = Modifier.background(QRAppColors.DarkCard)) {
                    DropdownMenuItem(text = { Text("Lịch sử", color = Color.White) }, leadingIcon = { Icon(Icons.Default.History, contentDescription = null, tint = QRAppColors.PrimaryStart) }, onClick = { showMenu = false; onNavigateToHistory() })
                    DropdownMenuItem(text = { Text("Cài đặt", color = Color.White) }, leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null, tint = QRAppColors.PrimaryStart) }, onClick = { showMenu = false; onNavigateToSettings() })
                }
            }
        }
        
        Text(text = "Chọn loại nội dung bạn muốn tạo mã QR", style = MaterialTheme.typography.bodyMedium, color = QRAppColors.TextSecondary, modifier = Modifier.padding(bottom = 20.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(qrTypes) { (type, icon) ->
                QRTypeCard(type = type, icon = icon, accentColor = typeColors[type] ?: QRAppColors.PrimaryStart, isSelected = selectedType == type, onSelect = { selectedType = it; onTypeSelected(it) })
            }
        }

        GradientButton(text = "Tiếp theo", onClick = { if (selectedType != null) onNext() }, enabled = selectedType != null, icon = Icons.Filled.ArrowForward, modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp))
    }
}

@Composable
private fun QRTypeCard(type: QRSourceType, icon: ImageVector, accentColor: Color, isSelected: Boolean, onSelect: (QRSourceType) -> Unit) {
    val animatedScale by animateFloatAsState(targetValue = if (isSelected) 1.05f else 1f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow), label = "cardScale")
    val interactionSource = remember { MutableInteractionSource() }
    Card(modifier = Modifier.scale(animatedScale).fillMaxWidth().aspectRatio(0.9f).then(if (isSelected) Modifier.shadow(elevation = 12.dp, shape = RoundedCornerShape(16.dp), ambientColor = accentColor.copy(alpha = 0.3f), spotColor = accentColor.copy(alpha = 0.3f)) else Modifier).clickable(interactionSource = interactionSource, indication = ripple(color = accentColor.copy(alpha = 0.3f))) { onSelect(type) }, shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = if (isSelected) accentColor.copy(alpha = 0.15f) else QRAppColors.DarkCard), border = if (isSelected) BorderStroke(1.5.dp, Brush.linearGradient(colors = listOf(accentColor, accentColor.copy(alpha = 0.3f)))) else BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxSize().padding(8.dp)) {
            Box(modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(if (isSelected) accentColor.copy(alpha = 0.2f) else QRAppColors.DarkCardElevated), contentAlignment = Alignment.Center) { Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(24.dp), tint = if (isSelected) accentColor else QRAppColors.TextSecondary) }
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = type.displayName, style = MaterialTheme.typography.labelSmall, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium, color = if (isSelected) accentColor else QRAppColors.TextSecondary, textAlign = TextAlign.Center, maxLines = 1)
        }
    }
}
