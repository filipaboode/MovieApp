package com.example.movieapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import com.example.movieapp.ui.auth.AuthScreen
import com.example.movieapp.ui.auth.AuthViewModel
import com.example.movieapp.ui.home.FavoritesScreen
import com.example.movieapp.ui.home.HomeScreen
import com.example.movieapp.ui.home.HomeViewModel
import com.example.movieapp.ui.home.MovieDetailsScreen
import com.example.movieapp.ui.home.QrScreen
import com.example.movieapp.ui.home.ScanQrScreen
import com.example.movieapp.ui.theme.MovieAppTheme

// Since its the first time using kotlin and implementing stuff like QR code, I used AI (chatgpt)
// For explaining code and giving tips. But also I have a friend working with kotlin, that has helped me a little

/**
 * Main entry point of the application
 * It controls all navigation and screen transitions using simple Compose state variables
 * this app uses a single-activity, state-driven navigation pattern
 */
class MainActivity : ComponentActivity() {
    private val authVm: AuthViewModel by viewModels()
    private val homeVm: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MovieAppTheme {
                val auth by authVm.ui.collectAsState()
                val home by homeVm.ui.collectAsState()


                var showFavorites by rememberSaveable { mutableStateOf(false) }
                var showMyQr by rememberSaveable { mutableStateOf(false) }
                var showScanQr by rememberSaveable { mutableStateOf(false) }
                var selectedMovie by rememberSaveable {mutableStateOf<com.example.movieapp.data.tmdb.Movie?>(null)}
                var showingImported by rememberSaveable { mutableStateOf(false) }

                if (auth.isLoggedIn) {
                    LaunchedEffect(auth.userId) {
                        auth.userId?.let { homeVm.setActiveUser(it) }
                    }
                    when {
                        // Movie details screen, opened when a user taps a specific movie
                        selectedMovie !=null -> {
                            MovieDetailsScreen(
                                movie = selectedMovie!!,
                                onBack = { selectedMovie = null }
                            )
                        }
                        // My QR screen, shows a QR code with the users favorite movies
                        showMyQr -> {

                            val payload = remember(home.favorites) {
                                homeVm.buildFavoritesPayload()
                            }
                            QrScreen(
                                payload = payload,
                                onBack = { showMyQr = false }
                            )
                        }
                        //Scan QR screen, uses the camera to import antoher users favorites
                        showScanQr -> {
                            ScanQrScreen(
                                onBack = { showScanQr = false },
                                onFavoritesScanned = { ids ->

                                    homeVm.loadImportedFavorites(ids)
                                    showScanQr = false
                                    showFavorites = true
                                    showingImported = true
                                }
                            )
                        }
                        // Favorites screen shgowing imported favorites from QR
                        showFavorites && showingImported -> {
                            val imported = home.importedFavorites
                            FavoritesScreen(
                                items = imported.map {

                                    com.example.movieapp.data.db.FavoriteEntity(
                                        id = 0,
                                        userId = 0,
                                        movieId = it.id,
                                        title = it.title,
                                        posterPath = it.posterPath,
                                        voteAverage = it.voteAverage
                                    )
                                },
                                onBack = {
                                    showFavorites = false
                                    showingImported = false
                                },
                                onToggleFavorite = {

                                }
                            )
                        }
                        showFavorites -> { //Favorite screen for the logged in users own saved movies.
                            FavoritesScreen(
                                items = home.favorites,
                                onBack = { showFavorites = false },
                                onToggleFavorite = { id -> homeVm.toggleFavorite(id) }
                            )
                        }
                        else -> {  //Default home scren with movie list, filters and menu.
                            HomeScreen(
                                state = home,
                                onQueryChange = homeVm::updateQuery,
                                onSearch = homeVm::search,
                                onToggleFavorite = homeVm::toggleFavorite,
                                onOpenFavorites = {

                                    showingImported = false
                                    showFavorites = true
                                },
                                onOpenMyQr = { showMyQr = true },
                                onOpenScanQr = { showScanQr = true },
                                onMovieClick = { movie -> selectedMovie = movie },
                                onLogout = authVm::logout,
                                onSelectGenre = homeVm::selectGenre
                            )
                        }
                    }
                } else { // The auth screen that is shown when the user is not logged in
                    AuthScreen(
                        state = auth,
                        onUsername = authVm::updateUsername,
                        onEmail = authVm::updateEmail,
                        onPassword = authVm::updatePassword,
                        onSubmit = authVm::submit,
                        onToggleMode = authVm::switchMode
                    )
                }
            }
        }
    }
}