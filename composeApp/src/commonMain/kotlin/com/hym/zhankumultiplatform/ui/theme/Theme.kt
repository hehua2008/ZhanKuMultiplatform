package com.hym.zhankumultiplatform.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val DarkColorScheme = darkColorScheme(
    error = Color(0xFFF2B8B5),
    errorContainer = Color(0xFF8C1D18),
    inverseOnSurface = Color(0xFF2A3136),
    inversePrimary = Color(0xFF00668B),
    inverseSurface = Color(0xFFDCE3E9),
    onBackground = Color(0xFFDCE3E9),
    onError = Color(0xFF601410),
    onErrorContainer = Color(0xFFF9DEDC),
    onPrimary = Color(0xFF003549),
    onPrimaryContainer = Color(0xFFC1E8FF),
    onSecondary = Color(0xFF20333D),
    onSecondaryContainer = Color(0xFFD1E5F4),
    onSurface = Color(0xFFDCE3E9),
    onSurfaceVariant = Color(0xFFC0C7CD),
    onTertiary = Color(0xFF322C4C),
    onTertiaryContainer = Color(0xFFE6DEFF),
    outline = Color(0xFF8A9297),
    outlineVariant = Color(0xFF40484D),
    primary = Color(0xFF76D1FF),
    primaryContainer = Color(0xFF004C69),
    scrim = Color(0xFF000000),
    secondary = Color(0xFFB5CAD7),
    secondaryContainer = Color(0xFF374955),
    surface = Color(0xFF0D1419),
    surfaceBright = Color(0xFF343A40),
    surfaceContainer = Color(0xFF1A2025),
    surfaceContainerHigh = Color(0xFF242B30),
    surfaceContainerHighest = Color(0xFF2F363B),
    surfaceContainerLow = Color(0xFF161C20),
    surfaceContainerLowest = Color(0xFF060F15),
    surfaceDim = Color(0xFF0D1419),
    surfaceTint = Color(0xFF76D1FF),
    surfaceVariant = Color(0xFF40484D),
    tertiary = Color(0xFFCAC1EA),
    tertiaryContainer = Color(0xFF484264)
)

val LightColorScheme = lightColorScheme(
    error = Color(0xFFB3261E),
    errorContainer = Color(0xFFF9DEDC),
    inverseOnSurface = Color(0xFFEBF1F8),
    inversePrimary = Color(0xFF76D1FF),
    inverseSurface = Color(0xFF2A3136),
    onBackground = Color(0xFF161C20),
    onError = Color(0xFFFFFFFF),
    onErrorContainer = Color(0xFF410E0B),
    onPrimary = Color(0xFFFFFFFF),
    onPrimaryContainer = Color(0xFF001E2C),
    onSecondary = Color(0xFFFFFFFF),
    onSecondaryContainer = Color(0xFF091E28),
    onSurface = Color(0xFF161C20),
    onSurfaceVariant = Color(0xFF40484D),
    onTertiary = Color(0xFFFFFFFF),
    onTertiaryContainer = Color(0xFF1D1736),
    outline = Color(0xFF70777C),
    outlineVariant = Color(0xFFC0C7CD),
    primary = Color(0xFF00668B),
    primaryContainer = Color(0xFFC1E8FF),
    scrim = Color(0xFF000000),
    secondary = Color(0xFF4E616C),
    secondaryContainer = Color(0xFFD1E5F4),
    surface = Color(0xFFF2FBFF),
    surfaceBright = Color(0xFFF2FBFF),
    surfaceContainer = Color(0xFFE7EFF6),
    surfaceContainerHigh = Color(0xFFE1E9F0),
    surfaceContainerHighest = Color(0xFFDCE3E9),
    surfaceContainerLow = Color(0xFFEDF5FC),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceDim = Color(0xFFD3DBE2),
    surfaceTint = Color(0xFF00668B),
    surfaceVariant = Color(0xFFDCE3E9),
    tertiary = Color(0xFF605A7C),
    tertiaryContainer = Color(0xFFE6DEFF)
)

@Composable
fun ComposeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        /*dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }*/

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    updateStatusBarColor(darkTheme)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
expect fun updateStatusBarColor(darkTheme: Boolean, isInit: Boolean = true)
