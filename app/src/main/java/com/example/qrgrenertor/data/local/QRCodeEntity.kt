package com.example.qrgrenertor.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "qr_codes")
data class QRCodeEntity(
    @PrimaryKey val id: String,
    val name: String,
    val content: String,
    val sourceType: String,
    val backgroundColor: Long,
    val codeColor: Long,
    val size: Int,
    val style: String,
    val errorCorrectionLevel: String,
    val imageUrl: String?,
    val createdAt: Long,
    val isSynced: Boolean = false
)
