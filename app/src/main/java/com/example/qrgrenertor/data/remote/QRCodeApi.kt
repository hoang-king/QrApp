package com.example.qrgrenertor.data.remote

import retrofit2.http.*
import com.example.qrgrenertor.data.remote.dto.QRCodeDto

interface QRCodeApi {
    @POST("/api/qr/generate")
    suspend fun generateQRCode(@Body request: GenerateQRRequest): QRCodeDto

    @GET("/api/qr/{id}")
    suspend fun getQRCode(@Path("id") id: String): QRCodeDto

    @POST("/api/qr/save")
    suspend fun saveQRCode(@Body qrCode: QRCodeDto): QRCodeDto

    @GET("/api/qr/sync")
    suspend fun syncQRCodes(): List<QRCodeDto>

    @DELETE("/api/qr/{id}")
    suspend fun deleteQRCode(@Path("id") id: String)
}

data class GenerateQRRequest(
    val content: String,
    val sourceType: String
)
