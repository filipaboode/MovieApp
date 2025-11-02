package com.example.movieapp.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.movieapp.data.db.FavoriteEntity
import com.example.movieapp.ui.theme.*
import androidx.compose.material.icons.filled.Favorite

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun FavoritesScreen (
    items: List<FavoriteEntity>,
    onBack: () -> Unit,
    onToggleFavorite: (Int) -> Unit
) {
    Surface(color = Night) {
        Scaffold(
            containerColor = Night,
            topBar = {
                TopAppBar(
                    title = { Text("Favorites", color = PureWhite) },
                    navigationIcon = {
                        TextButton(onClick = onBack) { Text("Back") }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Night)
                )
            }
        ) { padding ->
            if (items.isEmpty()) {
                Box(
                    Modifier.fillMaxSize().padding(padding),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {  Text("No favorites yet", color = Silver) }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    modifier = Modifier.padding(padding)
                ) {

                    items(items, key = { it.movieId }) { fav ->
                        FavoriteRow(
                            item = fav,
                            onToggle = { onToggleFavorite(fav.movieId) }
                        )
                        Spacer(Modifier.height(10.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun FavoriteRow(
    item: FavoriteEntity,
    onToggle: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(ExtraBlack)
            .padding(12.dp)
    ) {
        AsyncImage(
            model = item.posterPath?.let { "https://image.tmdb.org/t/p/w500$it" },
            contentDescription = item.title,
            modifier = Modifier.size(width = 90.dp, height = 120.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Night)
        )
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(item.title, color = PureWhite, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(6.dp))
            Text("★ %.1f".format(item.voteAverage), color = Gold)
        }
        Spacer(Modifier.width(8.dp))
        IconButton(onClick = onToggle) {
            Icon(
                imageVector = androidx.compose.material.icons.Icons.Filled.Favorite,
                contentDescription = "Remove favorite",
                tint = RedPrimary
            )
        }
    }
}