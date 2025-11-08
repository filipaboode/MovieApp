package com.example.movieapp.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.data.db.DatabaseProvider
import com.example.movieapp.data.tmdb.Movie
import com.example.movieapp.data.tmdb.TmdbApi
import com.example.movieapp.data.db.FavoriteEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject


/**
 * Represents the complete UI state for the Home screen.
 * Stores search queries, loading state, movie results, favorites and genre filters
 */
data class HomeUiState(
    val query: String = "",
    val isLoading: Boolean = true,
    val error: String? = null,
    val movies: List<Movie> = emptyList(),
    val favoriteIds: Set<Int> = emptySet(),
    val favorites: List<FavoriteEntity> = emptyList(),
    val importedFavorites: List<Movie> = emptyList(),
    val selectedGenreId: Int? = null
)

/**
 * ViewModel is responsible for managing data and logic for the Home Screen
 * Handles the communication between the UI, the API and the local Room database
 *
 */
class HomeViewModel(app: Application) : AndroidViewModel(app) {
    private val api = TmdbApi(app)
    private val favDao = DatabaseProvider.get(app).favoriteDao()

    private var userId: Long? = null
    private val _ui = MutableStateFlow(HomeUiState())
    val ui = _ui.asStateFlow()

    init {
        loadPopular()
    }

    /**
     * Sets the active user and begins observing the users favorites in real time
     * Updates the UI state automatically whenever database changes occur
     */
    fun setActiveUser(id: Long) {
        if (userId == id) return
        userId = id

        viewModelScope.launch {

            launch {
                favDao.favoriteIdsFlow(id).collectLatest { ids ->
                    _ui.value = _ui.value.copy(favoriteIds = ids.toSet())
                }
            }
            launch {
                favDao.favoritesFlow(id).collectLatest { list ->
                    _ui.value = _ui.value.copy(favorites = list)
                }
            }
        }
    }

    // Updates the current search query in state.
    fun updateQuery(q: String) {
        _ui.value = _ui.value.copy(query = q)
    }

    /**
     * Executes a search request through the TMDB API
     * Falls back to popular movies if the search query is empty
     */
    fun search() {
        val q = _ui.value.query.trim()
        if (q.isBlank()) {
            loadPopular(); return
        }
        _ui.value = _ui.value.copy(isLoading = true, error = null)
        api.searchMovies(
            q,
            onSuccess = { list -> _ui.value = _ui.value.copy(isLoading = false, movies = list) },
            onError = { e -> _ui.value = _ui.value.copy(isLoading = false, error = e.message) }
        )

    }

    // Loads pouplar movies using the API and updates the UI state
    private fun loadPopular() {
        _ui.value = _ui.value.copy(isLoading = true, error = null)
        api.getPopular(
            onSuccess = { list -> _ui.value = _ui.value.copy(isLoading = false, movies = list) },
            onError = { e -> _ui.value = _ui.value.copy(isLoading = false, error = e.message) }
        )
    }

    /**
     * Toggles the favorite state of a movie for the current user
     * If already a favorite, removes it, otherwise adds it to the database
     */
    fun toggleFavorite(movie: Movie) {
        val uid = userId ?: return
        viewModelScope.launch {
            val isFav = _ui.value.favoriteIds.contains(movie.id)
            if (isFav) {
                favDao.delete(uid, movie.id)
            } else {
                favDao.insert(
                    FavoriteEntity(
                        userId = uid,
                        movieId = movie.id,
                        title = movie.title,
                        posterPath = movie.posterPath,
                        voteAverage = movie.voteAverage
                    )
                )

            }
        }

    }

    fun toggleFavorite(movieId: Int) {
        _ui.value.movies.firstOrNull { it.id == movieId }?.let { toggleFavorite(it) }
    }

    fun favoritesFlowFor(userId: Long): Flow<List<FavoriteEntity>> =
        favDao.favoritesFlow(userId)

    /**
     * Builds a JSON payload representing the current user's favorite movies
     * This payload is later used for QR code generation and sharing
     */
    fun buildFavoritesPayload(): String {
        val favs = _ui.value.favorites
        val arr = JSONArray()
        favs.forEach { fav -> arr.put(fav.movieId) }

        val obj = JSONObject()
        obj.put("type", "favorites")
        obj.put("ids", arr)
        return obj.toString()
    }

    /**
     * Loads a list of movies by their IDs and stores them as imported favorites
     * Used after scanning a friend's QR code to show their shared list
     */
    fun loadImportedFavorites(ids: List<Int>) {

        _ui.update { it.copy(importedFavorites = emptyList()) }

        viewModelScope.launch {
            ids.forEach { id ->
                api.getMovieById(
                    id = id,
                    onSuccess = { movie ->
                        _ui.update { current ->
                            current.copy(
                                importedFavorites = current.importedFavorites + movie
                            )
                        }
                    },
                    onError = {  }
                )
            }
        }
    }
    // Updates the currently selected genre for filtering the movie list
    fun selectGenre(id: Int?) {
        _ui.update { it.copy(selectedGenreId = id)}
    }
}