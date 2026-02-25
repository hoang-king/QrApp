package com.example.qrgrenertor.data.sync

import com.example.qrgrenertor.data.local.QRCodeDao
import com.example.qrgrenertor.data.remote.QRCodeApi
import javax.inject.Inject

class SyncManagerImpl @Inject constructor(
    private val localDao: QRCodeDao,
    private val remoteApi: QRCodeApi
) : SyncManager {

    override suspend fun markForSync(id: String) {
        // Mark record as pending sync
    }

    override suspend fun syncAll() {
        try {
            val unsyncedCodes = localDao.getAllQRCodes().filter { !it.isSynced }

            for (entity in unsyncedCodes) {
                try {
                    localDao.markAsSynced(entity.id)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
