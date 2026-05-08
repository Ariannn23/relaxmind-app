package com.upn.relaxmind.core.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.dp

// ── Color schemes ─────────────────────────────────────────────────────────────

private val LightColorScheme = lightColorScheme(
    primary          = RelaxGreen,
    secondary        = RelaxGreenSoft,
    tertiary         = RelaxGreen,
    background       = RelaxBackground,
    surface          = RelaxSurface,
    onPrimary        = RelaxOnPrimary,
    onBackground     = RelaxOnBackground,
    onSurface        = RelaxOnSurface,
    outline          = RelaxOutline,
    surfaceVariant   = RelaxBackground,
    onSurfaceVariant = RelaxMutedText
)

private val DarkColorScheme = darkColorScheme(
    primary          = RelaxDarkGreen,
    secondary        = RelaxDarkGreenSoft,
    tertiary         = RelaxDarkGreen,
    background       = RelaxDarkBackground,
    surface          = RelaxDarkSurface,
    onPrimary        = RelaxDarkOnPrimary,
    onBackground     = RelaxDarkOnBackground,
    onSurface        = RelaxDarkOnSurface,
    outline          = RelaxDarkOutline,
    surfaceVariant   = RelaxDarkSurfaceVar,
    onSurfaceVariant = RelaxDarkMutedText
)

// ── CompositionLocal for dark mode — accessible anywhere in the tree ──────────

/**
 * Provides the current dark-mode state. Use [LocalIsDarkTheme].current to read,
 * and call [onToggleDarkTheme] (passed from MainActivity) to toggle.
 */
val LocalIsDarkTheme = compositionLocalOf { false }

// ── Shapes ────────────────────────────────────────────────────────────────────

private val RelaxMindShapes = Shapes(
    extraSmall = RoundedCornerShape(16.dp),
    small      = RoundedCornerShape(18.dp),
    medium     = RoundedCornerShape(24.dp),
    large      = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

// ── Theme entry point ─────────────────────────────────────────────────────────

@Composable
fun RelaxMindTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(LocalIsDarkTheme provides darkTheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography  = Typography,
            shapes      = RelaxMindShapes,
            content     = content
        )
    }
}