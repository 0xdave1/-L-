package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
    darkColorScheme(
        primary = DeepGreenPrimaryDark,
        onPrimary = DeepGreenOnPrimaryDark,
        primaryContainer = DeepGreenContainerDark,
        onPrimaryContainer = DeepGreenOnContainerDark,
        secondary = SoftOrangeAccent,
        onSecondary = MainText,
        secondaryContainer = SoftOrangeContainer,
        onSecondaryContainer = SoftOrangeOnContainer,
        tertiary = StatusSuccess,
        background = DarkBackground,
        onBackground = DarkMainText,
        surface = DarkSurface,
        onSurface = DarkMainText,
        surfaceVariant = DarkSurfaceVariant,
        onSurfaceVariant = DarkSecondaryText,
        outline = DarkBorder,
        error = StatusError,
        errorContainer = StatusErrorContainer
    )

private val LightColorScheme =
    lightColorScheme(
        primary = DeepGreenPrimary,
        onPrimary = DeepGreenOnPrimary,
        primaryContainer = DeepGreenContainer,
        onPrimaryContainer = DeepGreenOnContainer,
        secondary = SoftOrangeAccent,
        onSecondary = MainText,
        secondaryContainer = SoftOrangeContainer,
        onSecondaryContainer = SoftOrangeOnContainer,
        tertiary = StatusSuccess,
        background = WarmCreamSecondary,
        onBackground = MainText,
        surface = WarmCreamSurface,
        onSurface = MainText,
        surfaceVariant = CardBorder,
        onSurfaceVariant = SecondaryText,
        outline = CardBorder,
        error = StatusError,
        errorContainer = StatusErrorContainer
    )

@Composable
fun SmartGroceryListTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme =
        when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }
            darkTheme -> DarkColorScheme
            else -> LightColorScheme
        }

    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
