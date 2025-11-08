package com.example.movieapp.data.tmdb

/**
 * Data class representing a single movie from the API
 * Contains essential fields such as title, overview, rating, and genres
 * This model is used throughout the app for displaying movie lists and details
 */
data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val voteAverage: Double,
    val releaseDate: String?,
    val genreIds: List<Int> = emptyList()
) {
    val posterUrl: String?
        get() = posterPath?.let { "https://image.tmdb.org/t/p/w500$it" }
}