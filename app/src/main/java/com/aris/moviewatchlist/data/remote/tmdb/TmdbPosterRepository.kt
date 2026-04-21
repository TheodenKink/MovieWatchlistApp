package com.aris.moviewatchlist.data.remote.tmdb

import com.aris.moviewatchlist.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class TmdbPosterRepository(
    private val apiKey: String = BuildConfig.TMDB_API_KEY
) {

    suspend fun findPosterUrl(movieTitle: String): String? = withContext(Dispatchers.IO) {
        if (apiKey.isBlank() || movieTitle.isBlank()) return@withContext null

        val encodedTitle = URLEncoder.encode(movieTitle, "UTF-8")
        val url = URL("$SEARCH_URL?api_key=$apiKey&query=$encodedTitle")
        val connection = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 8000
            readTimeout = 8000
        }

        try {
            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                return@withContext null
            }

            val response = connection.inputStream.bufferedReader().use { it.readText() }
            val results = JSONObject(response).optJSONArray("results") ?: return@withContext null
            val normalizedInputTitle = movieTitle.normalizeTitle()

            for (index in 0 until results.length()) {
                val result = results.optJSONObject(index) ?: continue
                val resultTitle = result.optString("title").normalizeTitle()
                if (resultTitle != normalizedInputTitle) continue

                val posterPath = result
                    .optString("poster_path")
                    ?.takeIf { it.isNotBlank() && it != "null" }

                if (posterPath != null) {
                    return@withContext "$IMAGE_BASE_URL$posterPath"
                }
            }

            null
        } catch (_: Exception) {
            null
        } finally {
            connection.disconnect()
        }
    }

    private companion object {
        const val SEARCH_URL = "https://api.themoviedb.org/3/search/movie"
        const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500"
    }
}

private fun String.normalizeTitle(): String {
    return trim()
        .replace(Regex("\\s+"), " ")
        .lowercase()
}
