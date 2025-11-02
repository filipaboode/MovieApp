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

class HomeViewModel(app: Application) : AndroidViewModel(app) {
    private val api = TmdbApi(app)
    private val favDao = DatabaseProvider.get(app).favoriteDao()

    private var userId: Long? = null
    private val _ui = MutableStateFlow(HomeUiState())
    val ui = _ui.asStateFlow()

    init {
        loadPopular()
    }


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

    fun updateQuery(q: String) {
        _ui.value = _ui.value.copy(query = q)
    }

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

    private fun loadPopular() {
        _ui.value = _ui.value.copy(isLoading = true, error = null)
        api.getPopular(
            onSuccess = { list -> _ui.value = _ui.value.copy(isLoading = false, movies = list) },
            onError = { e -> _ui.value = _ui.value.copy(isLoading = false, error = e.message) }
        )
    }

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

    fun buildFavoritesPayload(): String {
        val favs = _ui.value.favorites
        val arr = JSONArray()
        favs.forEach { fav -> arr.put(fav.movieId) }

        val obj = JSONObject()
        obj.put("type", "favorites")
        obj.put("ids", arr)
        return obj.toString()
    }

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
    fun selectGenre(id: Int?) {
        _ui.update { it.copy(selectedGenreId = id)}
    }
}