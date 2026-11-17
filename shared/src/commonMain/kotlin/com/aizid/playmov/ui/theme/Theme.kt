package com.aizid.playmov.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = PlayMovRed,
    onPrimary = SurfacePage,
    primaryContainer = PlayMovRedLight,
    onPrimaryContainer = PlayMovRed,
    
    secondary = TextHeading,
    onSecondary = SurfacePage,
    
    surface = SurfacePage,
    onSurface = TextHeading,
    onSurfaceVariant = TextDescription,
    
    outline = BorderDefault,
    outlineVariant = DividerColor,
    
    error = ErrorRed,
    onError = SurfacePage,
    errorContainer = ErrorRedLight,
    onErrorContainer = ErrorRed
)

@Composable
fun PlayMovTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // For now, we only support Light Theme as per design specs
    val colorScheme = LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = playmovTypography(),
        content = content
    )
}
