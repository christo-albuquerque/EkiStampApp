package com.ekistamp.app.data.remote

import org.json.JSONArray
import org.json.JSONObject

object StampJsonParser {

    fun parse(json: String): List<EkiStampDto> {
        val array = JSONArray(json)
        return (0 until array.length())
            .map { array.getJSONObject(it) }
            .filter { it.optBoolean("public", true) }
            .mapNotNull { it.toDto() }
    }

    private fun JSONObject.toDto(): EkiStampDto? {
        val image = str("img")
        val name = str("name")
        if (name.isEmpty() || image.isEmpty()) return null
        return EkiStampDto(
            nameJa = name,
            nameEn = str("name_en"),
            type = str("type"),
            lineJa = str("line"),
            lineEn = str("line_en"),
            prefectureJa = str("pref"),
            imageFile = image,
            memoJa = str("memo"),
            memoEn = str("memo_en"),
            storyJa = str("story_ja"),
            storyEn = str("story_en"),
            latitude = if (has("lat") && !isNull("lat")) optDouble("lat") else null,
            longitude = if (has("lng") && !isNull("lng")) optDouble("lng") else null
        )
    }

    private fun JSONObject.str(key: String): String =
        if (isNull(key)) "" else optString(key, "").trim()
}
