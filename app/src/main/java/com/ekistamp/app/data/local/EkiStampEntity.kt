package com.ekistamp.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "eki_stamps")
data class EkiStampEntity(
    @PrimaryKey val id: String,              // e.g. "JR_NAMBU_YAHO"
    val stationNameJa: String,
    val stationNameEn: String,
    val lineNameJa: String,
    val lineNameEn: String,
    val prefectureEn: String,
    val imageUrl: String,
    val stampDescriptionEn: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val isCollected: Boolean = false,
    val isTranslated: Boolean = true
)
