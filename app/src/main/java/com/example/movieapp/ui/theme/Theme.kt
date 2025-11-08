package com.example.movieapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

//Defines the dark color palette used throughout the app.

private val DarkColors = darkColorScheme(
    primary = RedPrimary,
    onPrimary = PureWhite,
    secondary = Silver,
    onSecondary = PureWhite,
    background = Night,
    onBackground = PureWhite,
    surface = ExtraBlack,
    onSurface = PureWhite,
)
// Composable that applies the apps Material 3 theme settings globally
@Composable
fun MovieAppTheme(
    useDarkTheme: Boolean = true,
    content: @Composable () -> Unit
) {


    MaterialTheme(
        colorScheme = DarkColors,
        typography = Typography,
        content = content
    )
}