package com.example.movieapp.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.example.movieapp.ui.qr.generateQrBitmap
import com.example.movieapp.ui.theme.Night
import com.example.movieapp.ui.theme.PureWhite


@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun QrScreen(
    payload: String,
    onBack: () -> Unit
) {
    Surface(color = Night) {
        Scaffold(
            containerColor = Night,
            topBar = {
                TopAppBar(
                    title = { Text("My QR", color = PureWhite)},
                    navigationIcon = {
                        TextButton(onClick = onBack) { Text("Back") }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Night)
                )
            }
        ) { padding ->
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                val bmp = remember(payload) { generateQrBitmap(payload, size = 900) }
                Image(
                    bitmap = bmp.asImageBitmap(),
                    contentDescription = "My QR",
                    modifier = Modifier.size(280.dp)
                )
            }
        }
    }
}