package com.ekistamp.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EkiStampDao {

    @Query("SELECT * FROM eki_stamps ORDER BY stationNameEn")
    fun observeAll(): Flow<List<EkiStampEntity>>

    @Query(
        """
        SELECT * FROM eki_stamps
        WHERE stationNameEn LIKE '%' || :query || '%'
           OR lineNameEn LIKE '%' || :query || '%'
           OR prefectureEn LIKE '%' || :query || '%'
        ORDER BY stationNameEn
        """
    )
    fun search(query: String): Flow<List<EkiStampEntity>>

    @Query("SELECT * FROM eki_stamps WHERE id = :id")
    fun observeById(id: String): Flow<EkiStampEntity?>

    @Query("SELECT id FROM eki_stamps WHERE isTranslated = 1")
    suspend fun getTranslatedIds(): List<String>

    @Query("SELECT id FROM eki_stamps WHERE isCollected = 1")
    suspend fun getCollectedIds(): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(stamps: List<EkiStampEntity>)
}
