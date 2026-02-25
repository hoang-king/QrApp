package com.example.qrgrenertor.data.mapper

import com.example.qrgrenertor.data.local.entity.QRCodeEntity
import com.example.qrgrenertor.data.remote.dto.QRCodeDto
import com.example.qrgrenertor.domain.model.ErrorCorrectionLevel
import com.example.qrgrenertor.domain.model.QRCode
import com.example.qrgrenertor.domain.model.QRDesign
import com.example.qrgrenertor.domain.model.QRSourceType

fun QRCodeEntity.toDomain() = QRCode(
    id = id,
    name = name,
    content = content,
    sourceType = QRSourceType.valueOf(sourceType),
    designSettings = QRDesign(
        backgroundColor = backgroundColor,
        codeColor = codeColor,
        size = size,
        style = com.example.qrgrenertor.domain.model.QRStyle.valueOf(style),
        errorCorrectionLevel = ErrorCorrectionLevel.valueOf(errorCorrectionLevel)
    ),
    imageUrl = imageUrl,
    createdAt = createdAt,
    isSynced = isSynced
)

fun QRCodeDto.toDomain() = QRCode(
    id = id,
    name = name,
    content = content,
    sourceType = QRSourceType.valueOf(sourceType),
    designSettings = QRDesign(
        backgroundColor = backgroundColor,
        codeColor = codeColor,
        size = size,
        style = com.example.qrgrenertor.domain.model.QRStyle.valueOf(style),
        errorCorrectionLevel = ErrorCorrectionLevel.valueOf(errorCorrectionLevel)
    ),
    imageUrl = imageUrl,
    createdAt = createdAt,
    isSynced = isSynced
)

fun QRCode.toEntity() = QRCodeEntity(
    id = id,
    name = name,
    content = content,
    sourceType = sourceType.name,
    backgroundColor = designSettings.backgroundColor,
    codeColor = designSettings.codeColor,
    size = designSettings.size,
    style = designSettings.style.name,
    errorCorrectionLevel = designSettings.errorCorrectionLevel.name,
    imageUrl = imageUrl,
    createdAt = createdAt,
    isSynced = isSynced
)

fun QRCode.toDto() = QRCodeDto(
    id = id,
    name = name,
    content = content,
    sourceType = sourceType.name,
    backgroundColor = designSettings.backgroundColor,
    codeColor = designSettings.codeColor,
    size = designSettings.size,
    style = designSettings.style.name,
    errorCorrectionLevel = designSettings.errorCorrectionLevel.name,
    imageUrl = imageUrl,
    createdAt = createdAt,
    isSynced = isSynced
)
