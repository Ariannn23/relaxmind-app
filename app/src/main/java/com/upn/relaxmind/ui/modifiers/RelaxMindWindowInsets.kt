package com.upn.relaxmind.ui.modifiers

import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.ui.Modifier

/**
 * Respeta las barras del sistema ([systemBarsPadding]) y la zona segura de dibujo
 * ([safeDrawingPadding]: notch, isla, bordes curvos) con el modo edge-to-edge activo.
 */
fun Modifier.relaxMindScreenInsets(): Modifier =
    systemBarsPadding()
        .safeDrawingPadding()
