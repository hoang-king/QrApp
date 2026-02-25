package com.example.qrgrenertor.domain.model

data class QRCode(
    val id: String,
    val name: String,
    val content: String,
    val sourceType: QRSourceType,
    val designSettings: QRDesign,
    val imageUrl: String?,
    val createdAt: Long,
    val isSynced: Boolean = false
)

enum class QRSourceType(val displayName: String) {
    URL("URL"),
    WIFI("WiFi"),
    CONTACT("Contact"),
    EMAIL("Email"),
    PHONE("Phone"),
    SMS("SMS"),
    MUSIC("Music"),
    PDF("PDF"),
    IMAGE("Image"),
    FACEBOOK("Facebook"),
    INSTAGRAM("Instagram"),
    VCARD("vCard"),
    LOCATION("Vị trí")
}

data class QRSource(
    val type: QRSourceType,
    val value: String,
    val metadata: Map<String, String> = emptyMap()
)

data class QRDesign(
    val backgroundColor: Long = 0xFFFFFFFF,
    val codeColor: Long = 0xFF000000,
    val size: Int = 512,
    val style: QRStyle = QRStyle.SQUARE,
    val errorCorrectionLevel: ErrorCorrectionLevel = ErrorCorrectionLevel.MEDIUM
)

enum class QRStyle(val displayName: String) {
    SQUARE("Vuông"),
    ROUNDED("Bo góc"),
    DOT("Chấm tròn"),
    DIAMOND("Kim cương"),
    HEART("Trái tim"),
    CLASSY("Cổ điển")
}

enum class ErrorCorrectionLevel(val displayName: String, val value: String) {
    LOW("Low (7%)", "L"),
    MEDIUM("Medium (15%)", "M"),
    QUARTILE("Quartile (25%)", "Q"),
    HIGH("High (30%)", "H")
}

data class QRHistory(
    val id: String,
    val qrCode: QRCode,
    val lastAccessedAt: Long
)

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    object Loading : Result<Nothing>()
}
