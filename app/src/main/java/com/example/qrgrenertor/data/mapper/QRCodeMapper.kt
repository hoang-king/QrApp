package com.example.qrgrenertor.data.mapper

import com.example.qrgrenertor.data.local.entity.QRCodeEntity
import com.example.qrgrenertor.data.remote.dto.QRCodeDto
import com.example.qrgrenertor.domain.model.ErrorCorrectionLevel
import com.example.qrgrenertor.domain.model.QRCode
import com.example.qrgrenertor.domain.model.QRDesign
import com.example.qrgrenertor.domain.model.QRSourceType

fun QRCodeEntity.toDomain() = QRCode(
    id = id,
    content = content,
    sourceType = QRSourceType.valueOf(sourceType),
    designSettings = QRDesign(
        backgroundColor = backgroundColor,
        codeColor = codeColor,
        size = size,
        errorCorrectionLevel = ErrorCorrectionLevel.valueOf(errorCorrectionLevel)
    ),
    imageUrl = imageUrl,
    createdAt = createdAt,
    isSynced = isSynced
)

fun QRCodeDto.toDomain() = QRCode(
    id = id,
    content = content,
    sourceType = QRSourceType.valueOf(sourceType),
    designSettings = QRDesign(
        backgroundColor = backgroundColor,
        codeColor = codeColor,
        size = size,
        errorCorrectionLevel = ErrorCorrectionLevel.valueOf(errorCorrectionLevel)
    ),
    imageUrl = imageUrl,
    createdAt = createdAt,
    isSynced = isSynced
)

fun QRCode.toEntity() = QRCodeEntity(
    id = id,
    content = content,
    sourceType = sourceType.name,
    backgroundColor = designSettings.backgroundColor,
    codeColor = designSettings.codeColor,
    size = designSettings.size,
    errorCorrectionLevel = designSettings.errorCorrectionLevel.name,
    imageUrl = imageUrl,
    createdAt = createdAt,
    isSynced = isSynced
)

fun QRCode.toDto() = QRCodeDto(
    id = id,
    content = content,
    sourceType = sourceType.name,
    backgroundColor = designSettings.backgroundColor,
    codeColor = designSettings.codeColor,
    size = designSettings.size,
    errorCorrectionLevel = designSettings.errorCorrectionLevel.name,
    imageUrl = imageUrl,
    createdAt = createdAt,
    isSynced = isSynced
)
