package com.ekistamp.app.data.repository

import com.ekistamp.app.data.local.EkiStampDao
import com.ekistamp.app.data.local.EkiStampEntity
import com.ekistamp.app.data.remote.EkiStampDto
import com.ekistamp.app.data.remote.EkiStampRemoteDataSource
import com.ekistamp.app.data.translation.PrefectureTranslator
import com.ekistamp.app.data.translation.TranslationManager
import com.ekistamp.app.domain.model.EkiStamp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class EkiStampRepositoryImpl(
    private val dao: EkiStampDao,
    private val remote: EkiStampRemoteDataSource,
    private val translator: TranslationManager
) : EkiStampRepository {

    override fun observeStamps(query: String): Flow<List<EkiStamp>> =
        (if (query.isBlank()) dao.observeAll() else dao.search(query.trim()))
            .map { list -> list.map { it.toDomain() } }

    override fun observeStamp(id: String): Flow<EkiStamp?> =
        dao.observeById(id).map { it?.toDomain() }

    override suspend fun refresh(): Result<Unit> = runCatching {
        // Fully translated rows are left alone; new rows and rows whose translation failed are (re)built.
        val done = dao.getTranslatedIds().toSet()
        val collected = dao.getCollectedIds().toSet()
        val entities = remote.fetchStamps()
            .filter { idFor(it) !in done }
            .map { dto ->
                val id = idFor(dto)
                dto.toEntity(id, isCollected = id in collected)
            }
        dao.upsertAll(entities)
    }

    private suspend fun EkiStampDto.toEntity(id: String, isCollected: Boolean): EkiStampEntity {
        var fullyTranslated = true
        suspend fun english(provided: String, ja: String, fallback: (String) -> String? = { null }): String {
            if (provided.isNotBlank()) return provided
            if (ja.isBlank()) return ""
            fallback(ja)?.let { return it }
            return runCatching { translator.translate(ja) }.getOrElse {
                fullyTranslated = false
                ja
            }
        }

        val story = english(storyEn, storyJa)
        val description = if (story.isNotBlank()) story else english(memoEn, memoJa)

        return EkiStampEntity(
            id = id,
            stationNameJa = nameJa,
            stationNameEn = english(nameEn, nameJa),
            lineNameJa = lineJa,
            lineNameEn = english(lineEn, lineJa),
            prefectureEn = english("", prefectureJa, PrefectureTranslator::toEnglish),
            imageUrl = EkiStampRemoteDataSource.imageUrl(imageFile),
            stampDescriptionEn = description,
            latitude = latitude,
            longitude = longitude,
            isCollected = isCollected,
            isTranslated = fullyTranslated
        )
    }

    /** Stable id from the image file name (unique per stamp in the feed). */
    private fun idFor(dto: EkiStampDto): String = dto.imageFile.substringBeforeLast('.')

    private fun EkiStampEntity.toDomain() = EkiStamp(
        id = id,
        stationName = stationNameEn,
        stationNameJa = stationNameJa,
        lineName = lineNameEn,
        prefecture = prefectureEn,
        imageUrl = imageUrl,
        description = stampDescriptionEn,
        latitude = latitude,
        longitude = longitude,
        isCollected = isCollected
    )
}
