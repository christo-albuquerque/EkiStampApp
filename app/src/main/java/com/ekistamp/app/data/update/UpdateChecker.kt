package com.ekistamp.app.data.update

import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

data class ReleaseInfo(
    val version: String,
    val notes: String,
    val apkUrl: String,
    val apkSizeBytes: Long
)

sealed interface UpdateCheckResult {
    data object UpToDate : UpdateCheckResult
    data class Available(val release: ReleaseInfo) : UpdateCheckResult
}

/** Reads the latest GitHub release of [repo] ("owner/name"); needs a public repo. */
class UpdateChecker(
    private val repo: String,
    private val currentVersion: String,
    private val client: OkHttpClient
) {

    suspend fun check(): UpdateCheckResult = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("https://api.github.com/repos/$repo/releases/latest")
            .header("Accept", "application/vnd.github+json")
            .build()
        val body = client.newCall(request).execute().use { response ->
            when {
                response.code == 404 -> throw IOException("No releases published yet.")
                !response.isSuccessful -> throw IOException("GitHub returned HTTP ${response.code}.")
                else -> response.body?.string() ?: throw IOException("Empty response from GitHub.")
            }
        }
        val json = JSONObject(body)
        val version = json.getString("tag_name").removePrefix("v")
        if (!isNewer(version, currentVersion)) return@withContext UpdateCheckResult.UpToDate

        val assets = json.getJSONArray("assets")
        val apk = (0 until assets.length()).map { assets.getJSONObject(it) }
            .firstOrNull { it.getString("name").endsWith(".apk") }
            ?: throw IOException("Release v$version has no APK attached.")
        UpdateCheckResult.Available(
            ReleaseInfo(
                version = version,
                notes = json.optString("body", "").trim(),
                apkUrl = apk.getString("browser_download_url"),
                apkSizeBytes = apk.optLong("size", -1)
            )
        )
    }

    companion object {
        /** Compares dotted numeric versions; ignores any "-suffix". */
        fun isNewer(candidate: String, current: String): Boolean {
            fun parts(v: String) = v.substringBefore('-').split('.').map { it.toIntOrNull() ?: 0 }
            val a = parts(candidate)
            val b = parts(current)
            for (i in 0 until maxOf(a.size, b.size)) {
                val x = a.getOrElse(i) { 0 }
                val y = b.getOrElse(i) { 0 }
                if (x != y) return x > y
            }
            return false
        }
    }
}
