package com.example.qrgrenertor.presentation.ui

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.qrgrenertor.domain.model.QRSourceType
import com.example.qrgrenertor.presentation.ui.components.GlassCard
import com.example.qrgrenertor.presentation.ui.components.GradientButton
import com.example.qrgrenertor.presentation.ui.components.StepProgressBar
import com.example.qrgrenertor.presentation.ui.theme.QRAppColors
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun QRContentInputScreen(
    selectedType: QRSourceType,
    name: String,
    onNameEntered: (String) -> Unit,
    onContentEntered: (String) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var qrName by remember { mutableStateOf(name) }
    val scrollState = rememberScrollState()

    var url by remember { mutableStateOf("") }
    var isUploading by remember { mutableStateOf(false) }
    var formattedContentState by remember { mutableStateOf("") }
    
    var ssid by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var security by remember { mutableStateOf("WPA") }
    
    var firstName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var emailAddr by remember { mutableStateOf("") }
    var company by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    
    var emailSubject by remember { mutableStateOf("") }
    var emailBody by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }

    var latitude by remember { mutableStateOf("21.0285") }
    var longitude by remember { mutableStateOf("105.8542") }
    val markerState = rememberMarkerState(position = LatLng(21.0285, 105.8542))
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(21.0285, 105.8542), 15f)
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.values.all { it }
        if (granted) {
            scope.launch {
                LocationManager.getCurrentLocation(context)?.let { location ->
                    latitude = String.format(Locale.US, "%.6f", location.latitude)
                    longitude = String.format(Locale.US, "%.6f", location.longitude)
                    val newLatLng = LatLng(location.latitude, location.longitude)
                    markerState.position = newLatLng
                    cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(newLatLng, 15f))
                }
            }
        } else {
            Toast.makeText(context, "Cần quyền truy cập vị trí", Toast.LENGTH_SHORT).show()
        }
    }

    var selectedFileName by remember { mutableStateOf("") }
    var selectedFileSize by remember { mutableStateOf(0L) }

    var isCompressing by remember { mutableStateOf(false) }

    val filePickerLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val fileName = FileUtils.getFileName(context, it) ?: "Unknown"
            val fileSize = FileUtils.getFileSize(context, it)
            val maxSizeBytes = when(selectedType) {
                QRSourceType.PDF -> 100 * 1024 * 1024L
                QRSourceType.VIDEO -> 1024 * 1024 * 1024L // Allow up to 1GB now with compression
                else -> 10 * 1024 * 1024L
            }
            if (fileSize > maxSizeBytes) {
                Toast.makeText(context, "File quá lớn!", Toast.LENGTH_LONG).show()
            } else {
                selectedFileName = fileName
                selectedFileSize = fileSize
                scope.launch {
                    var uploadUri = it
                    
                    // Nén nếu là Video
                    if (selectedType == QRSourceType.VIDEO) {
                        isCompressing = true
                        Toast.makeText(context, "Đang tối ưu dung lượng video...", Toast.LENGTH_SHORT).show()
                        val compressionResult = VideoCompressor.compressVideo(context, it)
                        isCompressing = false
                        
                        compressionResult.onSuccess { compressedFile ->
                            uploadUri = Uri.fromFile(compressedFile)
                            val newSize = compressedFile.length() / (1024 * 1024)
                            Toast.makeText(context, "Đã nén xuống còn ${newSize}MB", Toast.LENGTH_SHORT).show()
                        }.onFailure { e ->
                            Toast.makeText(context, "Không thể nén video, sẽ tải bản gốc: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    isUploading = true
                    val result = FileUploader.uploadFile(context, uploadUri)
                    isUploading = false
                    result.onSuccess { 
                        url = it
                        Toast.makeText(context, "Tải lên thành công!", Toast.LENGTH_SHORT).show()
                    }
                    result.onFailure { error ->
                        Toast.makeText(context, "Lỗi tải lên: ${error.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    LaunchedEffect(firstName, phone, emailAddr, company, address) {
        if (selectedType == QRSourceType.VCARD && firstName.isNotEmpty() && phone.isNotEmpty()) {
            delay(1500)
            val contactInfo = "Tên: $firstName\nSĐT: $phone\nEmail: $emailAddr\nCty: $company\nĐC: $address"
            isUploading = true
            val tempFile = java.io.File(context.cacheDir, "vcard.txt")
            tempFile.writeText(contactInfo)
            val result = FileUploader.uploadFile(context, Uri.fromFile(tempFile))
            isUploading = false
            result.onSuccess { url = it }
        }
    }

    LaunchedEffect(url, ssid, password, security, firstName, phone, emailAddr, company, address, emailSubject, emailBody, phoneNumber, latitude, longitude) {
        val formattedContent = when (selectedType) {
            QRSourceType.URL, QRSourceType.FACEBOOK, QRSourceType.INSTAGRAM, QRSourceType.MUSIC, QRSourceType.IMAGE, QRSourceType.VIDEO -> {
                if (url.isEmpty()) "" else if (!url.startsWith("http")) "https://$url" else url
            }
            QRSourceType.PDF -> if (url.isEmpty()) "" else "https://docs.google.com/viewer?url=${Uri.encode(url)}"
            QRSourceType.WIFI -> if (ssid.isEmpty()) "" else "WIFI:T:$security;S:$ssid;P:$password;;"
            QRSourceType.CONTACT -> if (firstName.isEmpty() || phone.isEmpty()) "" else "MECARD:N:$firstName;TEL:$phone;;"
            QRSourceType.VCARD -> url
            QRSourceType.EMAIL -> if (emailAddr.isEmpty()) "" else "mailto:$emailAddr?subject=${Uri.encode(emailSubject)}&body=${Uri.encode(emailBody)}"
            QRSourceType.PHONE -> {
                val clean = phoneNumber.replace(Regex("[^0-9+]"), "")
                if (clean.isEmpty()) "" else "TEL:$clean"
            }
            QRSourceType.SMS -> {
                val clean = phoneNumber.replace(Regex("[^0-9+]"), "")
                if (clean.isEmpty()) "" else "sms:$clean?body=${Uri.encode(emailBody)}"
            }
            QRSourceType.LOCATION -> {
                val cleanLat = latitude.replace(",", ".")
                val cleanLng = longitude.replace(",", ".")
                if (cleanLat.isNotEmpty() && cleanLng.isNotEmpty()) "https://www.google.com/maps/search/?api=1&query=$cleanLat,$cleanLng" else ""
            }
            else -> url
        }
        formattedContentState = formattedContent
        onContentEntered(formattedContent)
    }

    Column(modifier = Modifier.fillMaxSize().background(QRAppColors.DarkBackground).statusBarsPadding()) {
        StepProgressBar(currentStep = 2, modifier = Modifier.padding(horizontal = 16.dp))
        val contentModifier = if (selectedType == QRSourceType.LOCATION) Modifier.weight(1f).padding(horizontal = 16.dp) else Modifier.weight(1f).verticalScroll(scrollState).padding(horizontal = 16.dp)
        Column(modifier = contentModifier) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 16.dp)) {
                Column {
                    Text("Nhập nội dung", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(selectedType.displayName, style = MaterialTheme.typography.bodyMedium, color = QRAppColors.PrimaryStart)
                }
            }
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp)) {
                    CustomTextField(label = "Tên gợi nhớ", value = qrName, onValueChange = { qrName = it; onNameEntered(it) }, placeholder = "Ví dụ: Shop")
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                    Spacer(modifier = Modifier.height(16.dp))
                    when (selectedType) {
                        QRSourceType.LOCATION -> {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Box(modifier = Modifier.weight(1f)) { CustomTextField(label = "Vĩ độ", value = latitude, onValueChange = { latitude = it }, placeholder = "21.0285", keyboardType = KeyboardType.Number) }
                                Box(modifier = Modifier.weight(1f)) { CustomTextField(label = "Kinh độ", value = longitude, onValueChange = { longitude = it }, placeholder = "105.8542", keyboardType = KeyboardType.Number) }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Chạm bản đồ để chọn vị trí", style = MaterialTheme.typography.labelSmall, color = QRAppColors.TextSecondary)
                            Box(modifier = Modifier.fillMaxWidth().height(260.dp).clip(RoundedCornerShape(12.dp)).border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))) {
                                GoogleMap(modifier = Modifier.fillMaxSize(), cameraPositionState = cameraPositionState, onMapClick = { latLng ->
                                    latitude = String.format(Locale.US, "%.6f", latLng.latitude)
                                    longitude = String.format(Locale.US, "%.6f", latLng.longitude)
                                    markerState.position = latLng
                                }) { Marker(state = markerState, title = "Vị trí") }
                                
                                // Nút lấy vị trí hiện tại
                                FloatingActionButton(
                                    onClick = {
                                        locationPermissionLauncher.launch(
                                            arrayOf(
                                                android.Manifest.permission.ACCESS_FINE_LOCATION,
                                                android.Manifest.permission.ACCESS_COARSE_LOCATION
                                            )
                                        )
                                    },
                                    modifier = Modifier.align(Alignment.BottomEnd).padding(12.dp).size(44.dp),
                                    containerColor = QRAppColors.PrimaryStart,
                                    contentColor = Color.White,
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Outlined.MyLocation, contentDescription = "Vị trí của tôi", modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                        QRSourceType.IMAGE, QRSourceType.MUSIC, QRSourceType.PDF, QRSourceType.VIDEO -> {
                            val mime = when(selectedType) {
                                QRSourceType.IMAGE -> "image/*"
                                QRSourceType.MUSIC -> "audio/*"
                                QRSourceType.VIDEO -> "video/*"
                                else -> "application/pdf"
                            }
                            Box(modifier = Modifier.fillMaxWidth().height(120.dp).clip(RoundedCornerShape(12.dp)).background(Color.White.copy(alpha = 0.05f)).border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp)).clickable { 
                                if (!isUploading && !isCompressing) filePickerLauncher.launch(mime) 
                            }, contentAlignment = Alignment.Center) {
                                if (isCompressing) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        CircularProgressIndicator(color = QRAppColors.PrimaryStart, modifier = Modifier.size(32.dp))
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("Đang tối ưu video...", color = QRAppColors.PrimaryStart, style = MaterialTheme.typography.labelSmall)
                                    }
                                } else if (isUploading) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        CircularProgressIndicator(color = QRAppColors.PrimaryStart, modifier = Modifier.size(32.dp))
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("Đang tải lên...", color = QRAppColors.PrimaryStart, style = MaterialTheme.typography.labelSmall)
                                    }
                                } else if (selectedFileName.isEmpty()) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) { 
                                        Icon(Icons.Outlined.CloudUpload, contentDescription = null, tint = QRAppColors.PrimaryStart)
                                        Text("Tải lên", color = QRAppColors.TextTertiary, style = MaterialTheme.typography.bodySmall) 
                                    }
                                } else {
                                    Text(selectedFileName, color = Color.White)
                                }
                            }
                        }
                        QRSourceType.WIFI -> {
                            CustomTextField(label = "SSID", value = ssid, onValueChange = { ssid = it }, placeholder = "Tên Wifi")
                            CustomTextField(label = "Mật khẩu", value = password, onValueChange = { password = it }, placeholder = "Mật khẩu", isPassword = true)
                        }
                        QRSourceType.CONTACT -> {
                            CustomTextField(label = "Họ tên", value = firstName, onValueChange = { firstName = it }, placeholder = "Tên")
                            CustomTextField(label = "SĐT", value = phone, onValueChange = { phone = it }, placeholder = "0123", keyboardType = KeyboardType.Phone)
                        }
                        QRSourceType.VCARD -> {
                            CustomTextField(label = "Họ tên", value = firstName, onValueChange = { firstName = it }, placeholder = "Tên")
                            CustomTextField(label = "SĐT", value = phone, onValueChange = { phone = it }, placeholder = "0123")
                            CustomTextField(label = "Địa chỉ", value = address, onValueChange = { address = it }, placeholder = "Hà Nội")
                        }
                        QRSourceType.PHONE -> CustomTextField(label = "SĐT", value = phoneNumber, onValueChange = { phoneNumber = it }, placeholder = "0123")
                        QRSourceType.SMS -> {
                            CustomTextField(label = "SĐT", value = phoneNumber, onValueChange = { phoneNumber = it }, placeholder = "0123")
                            CustomTextField(label = "Tin nhắn", value = emailBody, onValueChange = { emailBody = it }, placeholder = "Nội dung...", singleLine = false)
                        }
                        else -> CustomTextField(label = "URL", value = url, onValueChange = { url = it }, placeholder = "https://...")
                    }
                }
            }
        }
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = onPrevious, modifier = Modifier.weight(1f).height(52.dp), shape = RoundedCornerShape(16.dp), border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))) { Text("Quay lại", color = Color.White) }
            GradientButton(text = "Tiếp theo", onClick = onNext, enabled = formattedContentState.isNotEmpty() && !isUploading, icon = Icons.AutoMirrored.Filled.ArrowForward, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun CustomTextField(label: String, value: String, onValueChange: (String) -> Unit, placeholder: String, keyboardType: KeyboardType = KeyboardType.Text, isPassword: Boolean = false, singleLine: Boolean = true) {
    Column {
        if (label.isNotEmpty()) Text(text = label, style = MaterialTheme.typography.labelMedium, color = QRAppColors.TextSecondary, modifier = Modifier.padding(bottom = 6.dp))
        OutlinedTextField(value = value, onValueChange = onValueChange, placeholder = { Text(placeholder, color = QRAppColors.TextTertiary) }, modifier = Modifier.fillMaxWidth(), singleLine = singleLine, shape = RoundedCornerShape(12.dp), visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None, keyboardOptions = KeyboardOptions(keyboardType = keyboardType), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = QRAppColors.PrimaryStart, unfocusedBorderColor = Color.White.copy(alpha = 0.1f), focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedContainerColor = Color.White.copy(alpha = 0.05f), unfocusedContainerColor = Color.White.copy(alpha = 0.02f)))
    }
}
