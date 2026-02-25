package com.example.qrgrenertor.data.remote.dto

import com.google.gson.annotations.SerializedName

data class QRCodeDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("sourceType")
    val sourceType: String,
    @SerializedName("backgroundColor")
    val backgroundColor: Long,
    @SerializedName("codeColor")
    val codeColor: Long,
    @SerializedName("size")
    val size: Int,
    @SerializedName("style")
    val style: String,
    @SerializedName("errorCorrectionLevel")
    val errorCorrectionLevel: String,
    @SerializedName("imageUrl")
    val imageUrl: String?,
    @SerializedName("createdAt")
    val createdAt: Long,
    @SerializedName("isSynced")
    val isSynced: Boolean = false
)
