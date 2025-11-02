package com.example.movieapp.ui.home

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import com.example.movieapp.ui.theme.Night
import com.example.movieapp.ui.theme.PureWhite
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanQrScreen(
    onBack: () -> Unit,
    onFavoritesScanned: (List<Int>) -> Unit
) {
    val launcher = rememberLauncherForActivityResult(ScanContract()) { result ->
        val text = result?.contents.orEmpty()


        val ids: List<Int>? = try {
            val obj = JSONObject(text)
            if (obj.optString("type") == "favorites") {
                val arr = obj.getJSONArray("ids")
                List(arr.length()) { i -> arr.getInt(i) }
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }

        if (ids != null) {
            onFavoritesScanned(ids)
        } else {
            onBack()
        }
    }

    LaunchedEffect(Unit) {
        val options = ScanOptions()
            .setDesiredBarcodeFormats(ScanOptions.QR_CODE)
            .setPrompt("Scan a friend's favorites")
            .setBeepEnabled(true)
            .setBarcodeImageEnabled(false)
        launcher.launch(options)
    }

    Surface(color = Night) {
        Scaffold(
            containerColor = Night,
            topBar = {
                TopAppBar(
                    title = { Text("Scan QR", color = PureWhite) },
                    navigationIcon = { TextButton(onClick = onBack) { Text("Back") } },
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
                Text("Opening camera...", color = PureWhite)
            }
        }
    }
}

