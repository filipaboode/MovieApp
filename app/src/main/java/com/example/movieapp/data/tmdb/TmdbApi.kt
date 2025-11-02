package com.example.movieapp.data.tmdb

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONException


class TmdbApi (private val ctx: Context) {
    private val API_KEY = "a57229e1c8e4536b6e8b9e1bf365324c"

    fun searchMovies(
        query: String,
        onSuccess: (List<Movie>) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val url = "https://api.themoviedb.org/3/search/movie" +
                "?api_key=$API_KEY&language=en-US&query=${query.trim()}&include_adult=false&page=1"

        val req = JsonObjectRequest(
            Request.Method.GET, url, null,
            {
                json ->
                try {
                    val arr = json.getJSONArray("results")
                    val list = ArrayList<Movie>(arr.length())
                    for (i in 0 until arr.length()) {
                        val o = arr.getJSONObject(i)
                        val genreArr = o.optJSONArray("genre_ids")
                        val genres = mutableListOf<Int>()
                        if (genreArr != null) {
                            for (g in 0 until genreArr.length()) {
                                genres.add(genreArr.getInt(g))
                            }
                        }

                        list.add(
                            Movie(
                                id = o.getInt("id"),
                                title = o.optString("title"),
                                overview = o.optString("overview"),
                                posterPath = o.optString("poster_path", null),
                                voteAverage = o.optDouble("vote_average", 0.0),
                                releaseDate = o.optString("release_date", null),
                                genreIds = genres
                            )
                        )
                    }
                    onSuccess(list)
                } catch (e: JSONException) {
                    onError(e)
                }
            },
            { err -> onError(err)}
        )

        VolleyClient.add(ctx, req)

    }

    fun getPopular(
        onSuccess: (List<Movie>) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val url = "https://api.themoviedb.org/3/movie/popular" +
                "?api_key=$API_KEY&language=en-US&page=1"

        val req = JsonObjectRequest(
            Request.Method.GET, url, null, {
                json ->
                try {
                    val arr = json.getJSONArray("results")
                    val list = ArrayList<Movie>(arr.length())
                    for (i in 0 until arr.length()) {
                        val o = arr.getJSONObject(i)
                        val genreArr = o.optJSONArray("genre_ids")
                        val genres = mutableListOf<Int>()

                        if (genreArr != null) {
                            for (g in 0 until genreArr.length()) {
                                genres.add(genreArr.getInt(g))
                            }
                        }

                        list.add(
                            Movie(
                                id = o.getInt("id"),
                                title = o.optString("title"),
                                overview = o.optString("overview"),
                                posterPath = o.optString("poster_path", null),
                                voteAverage = o.optDouble("vote_average", 0.0),
                                releaseDate = o.optString("release_date", null),
                                genreIds = genres
                            )
                        )
                    }
                    onSuccess(list)
                } catch (e: JSONException) {
                    onError(e)
                }
            },
            {
                err -> onError(err)
            }
        )
        VolleyClient.add(ctx, req)
    }

    fun getMovieById(
        id: Int,
        onSuccess: (Movie) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val url = "https://api.themoviedb.org/3/movie/$id" +
                "?api_key=$API_KEY&language=en-US"

        val req = JsonObjectRequest(
            Request.Method.GET, url, null,
            { json ->
                try {
                    val genreArr = json.optJSONArray("genres")
                    val genres = mutableListOf<Int>()
                    if (genreArr != null) {
                        for (g in 0 until genreArr.length()) {
                            val gObj = genreArr.getJSONObject(g)
                            genres.add(gObj.getInt("id"))
                        }
                    }
                    val movie = Movie(
                        id = json.getInt("id"),
                        title = json.optString("title"),
                        overview = json.optString("overview"),
                        posterPath = json.optString("poster_path", null),
                        voteAverage = json.optDouble("vote_average", 0.0),
                        releaseDate = json.optString("release_date", null),
                        genreIds = genres
                    )
                    onSuccess(movie)
                } catch (e: JSONException) {
                    onError(e)
                }
            },
            { err -> onError(err) }
        )

        VolleyClient.add(ctx, req)
    }
}