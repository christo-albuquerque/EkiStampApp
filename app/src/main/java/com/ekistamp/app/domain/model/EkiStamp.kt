package com.ekistamp.app.domain.model

/** UI-facing model; every user-visible field is English. */
data class EkiStamp(
    val id: String,
    val stationName: String,
    val stationNameJa: String,
    val lineName: String,
    val prefecture: String,
    val imageUrl: String,
    val description: String,
    val latitude: Double?,
    val longitude: Double?,
    val isCollected: Boolean
)
