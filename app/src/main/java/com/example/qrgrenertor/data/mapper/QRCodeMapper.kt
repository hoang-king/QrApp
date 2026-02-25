package com.example.qrgrenertor.data.mapper

import com.example.qrgrenertor.data.local.entity.QRCodeEntity
import com.example.qrgrenertor.data.remote.dto.QRCodeDto
import com.example.qrgrenertor.domain.model.ErrorCorrectionLevel
import com.example.qrgrenertor.domain.model.QRCode
import com.example.qrgrenertor.domain.model.QRDesign
import com.example.qrgrenertor.domain.model.QRSourceType
import com.example.qrgrenertor.domain.model.QRStyle

fun QRCodeEntity.toDomain() = QRCode(
    id = id,
    name = name,
    content = content,
    sourceType = try { QRSourceType.valueOf(sourceType) } catch (e: Exception) { QRSourceType.URL },
    designSettings = QRDesign(
        backgroundColor = backgroundColor,
        codeColor = codeColor,
        size = size,
        style = try { QRStyle.valueOf(style) } catch (e: Exception) { QRStyle.SQUARE },
        errorCorrectionLevel = try { ErrorCorrectionLevel.valueOf(errorCorrectionLevel) } catch (e: Exception) { ErrorCorrectionLevel.MEDIUM }
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
