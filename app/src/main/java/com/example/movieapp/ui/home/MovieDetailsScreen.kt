package com.example.movieapp.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.movieapp.data.tmdb.Movie
import com.example.movieapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun MovieDetailsScreen(
    movie: Movie,  // the movie the user tapped on in HomeScreen
    onBack: () -> Unit  // callback to go back to the previous screen
) {
    Surface(color = Night) {
        Scaffold(
            containerColor = Night,
            topBar = {
                TopAppBar(
                    title = { Text(movie.title, color = PureWhite, maxLines = 1) },
                    navigationIcon = {
                        TextButton(onClick = onBack) { Text("Back") }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Night)
                )
            }
        ) { padding ->
            Column(
                Modifier
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                AsyncImage(
                    model = movie.posterUrl,
                    contentDescription = movie.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
                Spacer(Modifier.height(16.dp))
                Text("★ %.1f".format(movie.voteAverage), color = Gold)
                Spacer(Modifier.height(8.dp))
                Text(
                    text = movie.overview.ifBlank { "No description available." },
                    color = PureWhite
                )
            }
        }
    }
}