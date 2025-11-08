package com.example.movieapp.ui.home

import android.icu.number.NumberFormatter
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.movieapp.data.tmdb.Movie
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.ui.semantics.contentDescription
import com.example.movieapp.ui.theme.*
import java.nio.file.DirectoryStream


@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun HomeScreen(
    state: HomeUiState,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onLogout: () -> Unit,
    onOpenFavorites: () -> Unit,
    onToggleFavorite: (Movie) -> Unit,
    onOpenMyQr: () -> Unit,
    onOpenScanQr: () -> Unit,
    onMovieClick: (Movie) -> Unit,
    onSelectGenre: (Int?) -> Unit
) {
    var menuOpen by remember { mutableStateOf(false) }  // Controls whether the top-right menu is open

    // Filter the movie list based on the selected genre
    val visibleMovies by remember(state.movies, state.selectedGenreId) {
        mutableStateOf(
            if (state.selectedGenreId == null || state.selectedGenreId == 0) {
                state.movies
            } else {
                state.movies.filter { movie ->
                    movie.genreIds.contains(state.selectedGenreId)
                }
            }
        )
    }
    Surface(color=Night) {
        Scaffold(
            containerColor = Night,
            topBar = {
                TopAppBar(
                    title = { Text("Home", color = PureWhite) },
                    navigationIcon = {
                        IconButton(onClick = { menuOpen = true }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = PureWhite)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Night,
                        titleContentColor = PureWhite
                    )
                )
            }
        ) { padding ->
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Search bar
                OutlinedTextField(
                    value = state.query,
                    onValueChange = onQueryChange,
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = Silver) },
                    placeholder = { Text("Search for movies", color = Silver) },
                    textStyle = LocalTextStyle.current.copy(color = PureWhite),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = ExtraBlack,
                        unfocusedContainerColor = ExtraBlack,
                        disabledContainerColor = ExtraBlack,
                        focusedBorderColor = RedPrimary,
                        unfocusedBorderColor = Color.Transparent,
                        cursorColor = PureWhite
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { onSearch() })
                )

                Spacer(Modifier.height(12.dp))
                // Genre chips row (All, Action, Comedy and more)
                GenreRow(
                    selectedId = state.selectedGenreId,
                    onSelect = onSelectGenre
                )

                when {
                    state.isLoading -> Box(
                        Modifier.fillMaxWidth().padding(top = 24.dp),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator(color = PureWhite) }

                    state.error != null -> Text(
                        state.error ?: "",
                        color = MaterialTheme.colorScheme.error
                    )
                }

                // Movie list
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(visibleMovies, key = { it.id }) { movie ->
                        MovieListItem(
                            movie = movie,
                            isFavorite = state.favoriteIds.contains(movie.id),
                            onToggleFavorite = { onToggleFavorite(movie) },
                            onClick = { onMovieClick(movie) }
                        )
                    }
                }

            }

            DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                DropdownMenuItem(
                    text = { Text("Log out") },
                    onClick = { menuOpen = false; onLogout() }
                )
                DropdownMenuItem(
                    text = { Text("Favorites") },
                    onClick = { menuOpen = false; onOpenFavorites() }
                )
                DropdownMenuItem(
                    text = { Text("My QR") }, onClick = { menuOpen = false; onOpenMyQr() }
                )
                DropdownMenuItem(
                    text = { Text("Scan QR") },     onClick = { menuOpen = false; onOpenScanQr() }
                )
            }
        }
        }
    }

/**
 * This lets the user quickly filter the movie list.
 */
@Composable
private fun GenreRow(
    selectedId: Int?,
    onSelect: (Int?) -> Unit
) {
    // These are hardcoded to the most popular genres
    val genres = listOf(
        0 to "All",
        28 to "Action",
        35 to "Comedy",
        18 to "Drama",
        10749 to "Romance",
        27 to "Horror",
        16 to "Animation"
    )

    Row(
        Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
    ) {
        genres.forEach { (id, label) ->
            val isSelected = (selectedId ?: 0 ) == id
            FilterChip(
                selected = isSelected,
                onClick = { onSelect(if (id == 0) null else id) },
                label = { Text(label) },
                modifier = Modifier.padding(end = 8.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = RedPrimary,
                    selectedLabelColor = PureWhite,
                    containerColor = ExtraBlack,
                    labelColor = PureWhite
                )
            )
        }
    }
}

/**
 * Single movie row showing poster, title, rating and a favorite button.
 */
@Composable
private fun MovieListItem(
    movie: Movie,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onClick: () -> Unit

) {
Row(
    Modifier
        .fillMaxWidth()
        .padding(vertical = 10.dp)
        .clip(RoundedCornerShape(16.dp))
        .background(ExtraBlack)
        .clickable { onClick () }
        .padding(12.dp),
    verticalAlignment = Alignment.CenterVertically
) {
    AsyncImage(
        model = movie.posterUrl,
        contentDescription = movie.title,
        modifier = Modifier
            .size(width = 90.dp, height = 120.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Night)
    )
    Spacer(Modifier.width(12.dp))

    Column (Modifier.weight(1f)) {
        Text(movie.title, color = PureWhite, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("★", color = Gold, style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.width(6.dp))
            Text("%.1f".format(movie.voteAverage), color = PureWhite, style = MaterialTheme.typography.titleSmall)
        }
        Spacer(Modifier.height(6.dp))
        Text(movie.overview, maxLines = 2, color = Silver, style = MaterialTheme.typography.bodySmall)
    }
    Spacer(Modifier.width(8.dp))

    // Heart button to toggle favorites
    IconToggleButton(
        checked = isFavorite,
        onCheckedChange = { onToggleFavorite() },
        modifier = Modifier.semantics {
            contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites"
        }
    ) {
        Icon(
            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = null,
            tint = if (isFavorite) RedPrimary else Silver
        )
    }
}
}