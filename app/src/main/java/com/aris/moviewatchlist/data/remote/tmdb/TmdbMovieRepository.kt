package com.aris.moviewatchlist.data.remote.tmdb

import com.aris.moviewatchlist.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

data class TmdbMovieMetadata(
    val title: String,
    val year: Int,
    val genre: String,
    val duration: Int,
    val posterUrl: String?
)

data class TmdbMovieSearchCandidate(
    val id: Int,
    val title: String,
    val year: Int
)

class TmdbMovieRepository(
    private val apiKey: String = BuildConfig.TMDB_API_KEY
) {

    suspend fun searchMovies(movieTitle: String): List<TmdbMovieSearchCandidate> = withContext(Dispatchers.IO) {
        if (apiKey.isBlank() || movieTitle.isBlank()) return@withContext emptyList()

        searchMoviesInternal(movieTitle).map { result ->
            TmdbMovieSearchCandidate(
                id = result.id,
                title = result.title,
                year = result.year
            )
        }
    }

    suspend fun findMovieMetadata(movieId: Int): TmdbMovieMetadata? = withContext(Dispatchers.IO) {
        if (apiKey.isBlank() || movieId <= 0) return@withContext null

        val details = fetchMovieDetails(movieId) ?: return@withContext null

        TmdbMovieMetadata(
            title = details.title,
            year = details.year,
            genre = details.genre,
            duration = details.duration,
            posterUrl = details.posterPath?.let { "$IMAGE_BASE_URL$it" }
        )
    }

    private fun searchMoviesInternal(movieTitle: String): List<TmdbSearchResult> {
        val encodedTitle = URLEncoder.encode(movieTitle, "UTF-8")
        val response = getJson("$SEARCH_URL?api_key=$apiKey&query=$encodedTitle") ?: return emptyList()
        val results = response.optJSONArray("results") ?: return emptyList()
        val searchResults = mutableListOf<TmdbSearchResult>()

        for (index in 0 until results.length()) {
            val result = results.optJSONObject(index) ?: continue
            val resultTitle = result.optString("title")
            if (resultTitle.isBlank()) continue

            searchResults.add(
                TmdbSearchResult(
                    id = result.optInt("id"),
                    title = resultTitle,
                    year = result.optString("release_date").toYear(),
                    posterPath = result.optString("poster_path")
                        .takeIf { it.isNotBlank() && it != "null" }
                )
            )
        }

        return searchResults
    }

    private fun fetchMovieDetails(movieId: Int): TmdbDetailsResult? {
        if (movieId <= 0) return null

        val response = getJson("$DETAILS_URL/$movieId?api_key=$apiKey") ?: return null
        val genres = response.optJSONArray("genres")
        val genreNames = mutableListOf<String>()
        if (genres != null) {
            for (index in 0 until genres.length()) {
                val genreName = genres.optJSONObject(index)?.optString("name").orEmpty()
                if (genreName.isNotBlank()) {
                    genreNames.add(genreName)
                }
            }
        }

        return TmdbDetailsResult(
            title = response.optString("title"),
            year = response.optString("release_date").toYear(),
            genre = genreNames.joinToString(", "),
            duration = response.optInt("runtime"),
            posterPath = response.optString("poster_path")
                .takeIf { it.isNotBlank() && it != "null" }
        )
    }

    private fun getJson(urlValue: String): JSONObject? {
        val connection = (URL(urlValue).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 8000
            readTimeout = 8000
        }

        return try {
            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                null
            } else {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                JSONObject(response)
            }
        } catch (_: Exception) {
            null
        } finally {
            connection.disconnect()
        }
    }

    private data class TmdbSearchResult(
        val id: Int,
        val title: String,
        val year: Int,
        val posterPath: String?
    )

    private data class TmdbDetailsResult(
        val title: String,
        val year: Int,
        val genre: String,
        val duration: Int,
        val posterPath: String?
    )

    private companion object {
        const val SEARCH_URL = "https://api.themoviedb.org/3/search/movie"
        const val DETAILS_URL = "https://api.themoviedb.org/3/movie"
        const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500"
    }
}

private fun String.toYear(): Int {
    return takeIf { it.length >= 4 }
        ?.take(4)
        ?.toIntOrNull()
        ?: 0
}
