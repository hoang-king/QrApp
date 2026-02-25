package com.example.qrgrenertor.data.sync

interface SyncManager {
    suspend fun markForSync(id: String)
    suspend fun syncAll()
}
