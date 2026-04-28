package com.upn.relaxmind.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.upn.relaxmind.ui.theme.RelaxGreen
import com.upn.relaxmind.ui.theme.RelaxMutedText

/**
 * Texto "Omitir" con área de toque amplia y animación de entrada suave.
 * Si la pantalla padre **no** aplica insets superiores, pasa [applyStatusBarPadding] = true
 * para añadir [statusBarsPadding] y evitar solaparse con la barra de estado.
 */
@Composable
fun OmitirSkipButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    useGreenTint: Boolean = false,
    applyStatusBarPadding: Boolean = false
) {
    var targetAlpha by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        targetAlpha = 1f
    }
    val alpha by animateFloatAsState(
        targetValue = targetAlpha,
        animationSpec = tween(durationMillis = 520, delayMillis = 120),
        label = "omitirFade"
    )
    val color = if (useGreenTint) RelaxGreen.copy(alpha = 0.72f) else RelaxMutedText
    val insetModifier = if (applyStatusBarPadding) Modifier.statusBarsPadding() else Modifier
    Box(
        modifier = modifier
            .then(insetModifier)
            .alpha(alpha)
            .widthIn(min = 48.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 14.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Omitir",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = color
        )
    }
}
