package com.ekistamp.app.data.translation

import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import com.google.mlkit.common.model.DownloadConditions
import kotlinx.coroutines.tasks.await

/** On-device Japanese -> English translation. Rules in CLAUDE.md section 3. */
class TranslationManager {

    private val translator: Translator = Translation.getClient(
        TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.JAPANESE)
            .setTargetLanguage(TranslateLanguage.ENGLISH)
            .build()
    )

    /** Downloads the ja->en model if needed; call from a background coroutine. */
    suspend fun ensureModelDownloaded() {
        translator.downloadModelIfNeeded(DownloadConditions.Builder().build()).await()
    }

    suspend fun translate(text: String): String {
        if (text.isBlank()) return text
        ensureModelDownloaded()
        return translator.translate(text).await()
    }

    fun close() = translator.close()
}
