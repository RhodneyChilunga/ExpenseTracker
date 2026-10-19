package com.example.expensetracker.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = WalletGreenLight,
    onPrimary = Color.Black,
    primaryContainer = WalletGreenDark,
    onPrimaryContainer = Color.White,
    secondary = WalletGreenSecondary,
    tertiary = WalletGreenTertiary,
    background = BackgroundDark,
    surface = SurfaceDark,
    onBackground = Color.White,
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = WalletGreen,
    onPrimary = Color.White,
    primaryContainer = WalletGreenContainer,
    onPrimaryContainer = Color(0xFF064E3B),
    secondary = WalletGreenSecondary,
    tertiary = WalletGreenTertiary,
    background = BackgroundLight,
    surface = SurfaceLight,
    onBackground = Color(0xFF1F2937),
    onSurface = Color(0xFF1F2937)
)

@Composable
fun ExpenseTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Default to false to ensure Wallet Green brand theme is respected
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
