package com.ekistamp.app.data.remote

/**
 * Raw record from https://japan-ekistamp.com/stamps.json.
 * The feed carries Japanese fields plus optional English ones (`*En`); blank means "not provided".
 */
data class EkiStampDto(
    val nameJa: String,
    val nameEn: String,
    val type: String,
    val lineJa: String,
    val lineEn: String,
    val prefectureJa: String,
    val imageFile: String,
    val memoJa: String,
    val memoEn: String,
    val storyJa: String,
    val storyEn: String,
    val latitude: Double?,
    val longitude: Double?
)
