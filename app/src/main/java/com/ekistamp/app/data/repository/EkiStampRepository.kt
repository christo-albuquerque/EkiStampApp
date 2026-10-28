package com.ekistamp.app.data.repository

import com.ekistamp.app.domain.model.EkiStamp
import kotlinx.coroutines.flow.Flow

interface EkiStampRepository {
    fun observeStamps(query: String = ""): Flow<List<EkiStamp>>
    fun observeStamp(id: String): Flow<EkiStamp?>

    /** Fetches the remote catalog, translates anything missing English, and stores it in Room. */
    suspend fun refresh(): Result<Unit>
}
