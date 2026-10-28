package com.ekistamp.app.data.remote

import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class EkiStampRemoteDataSource(
    private val client: OkHttpClient = OkHttpClient()
) {

    suspend fun fetchStamps(): List<EkiStampDto> = withContext(Dispatchers.IO) {
        val request = Request.Builder().url(STAMPS_URL).build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("HTTP ${response.code} from $STAMPS_URL")
            StampJsonParser.parse(response.body?.string() ?: throw IOException("Empty response"))
        }
    }

    companion object {
        const val BASE_URL = "https://japan-ekistamp.com"
        const val STAMPS_URL = "$BASE_URL/stamps.json"

        fun imageUrl(fileName: String): String =
            "$BASE_URL/stamps/" + java.net.URLEncoder.encode(fileName, "UTF-8").replace("+", "%20")
    }
}
