package com.upn.relaxmind.feature.meditation.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.*
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upn.relaxmind.core.ui.theme.RelaxBackground
import com.upn.relaxmind.core.ui.theme.RelaxMutedText
import kotlinx.coroutines.delay

// ─────────────────────────────────────────────
// Data model
// ─────────────────────────────────────────────

private data class BreathTechnique(
    val id: Int,
    val name: String,
    val description: String,
    val durationMin: Int,
    val icon: ImageVector,
    val accentColor: Color,
    val steps: List<BreathStep>
)

private data class BreathStep(
    val label: String,   // "INHALA", "RETÉN", "EXHALA"
    val seconds: Int
)

private val MeditationTeal  = Color(0xFF0D9488)
private val MeditationMist  = Color(0xFFCCFBF1)
private val MeditationBlue  = Color(0xFF2563EB)
private val MeditationPurple = Color(0xFF7C3AED)
private val MeditationGreen = Color(0xFF10B981)
private val MeditationAmber = Color(0xFFF59E0B)
private val MeditationRose  = Color(0xFFE11D48)

private val techniques = listOf(
    BreathTechnique(
        id = 1, name = "4-7-8", description = "Reduce ansiedad y promueve el sueño",
        durationMin = 5, icon = Icons.Outlined.Air, accentColor = MeditationTeal,
        steps = listOf(
            BreathStep("INHALA", 4), BreathStep("RETÉN", 7), BreathStep("EXHALA", 8)
        )
    ),
    BreathTechnique(
        id = 2, name = "4-4-6", description = "Promueve la relajación profunda",
        durationMin = 5, icon = Icons.Outlined.Spa, accentColor = MeditationPurple,
        steps = listOf(
            BreathStep("INHALA", 4), BreathStep("RETÉN", 4), BreathStep("EXHALA", 6)
        )
    ),
    BreathTechnique(
        id = 3, name = "Box Breathing", description = "Equilibra el sistema nervioso",
        durationMin = 4, icon = Icons.Outlined.SelfImprovement, accentColor = MeditationBlue,
        steps = listOf(
            BreathStep("INHALA", 4), BreathStep("RETÉN", 4),
            BreathStep("EXHALA", 4), BreathStep("RETÉN", 4)
        )
    ),
    BreathTechnique(
        id = 4, name = "5-5-5", description = "Técnica de respiración coherente",
        durationMin = 5, icon = Icons.Outlined.Favorite, accentColor = MeditationRose,
        steps = listOf(
            BreathStep("INHALA", 5), BreathStep("RETÉN", 1), BreathStep("EXHALA", 5)
        )
    ),
    BreathTechnique(
        id = 5, name = "Relajación Muscular", description = "Libera tensión corporal progresivamente",
        durationMin = 10, icon = Icons.Outlined.FitnessCenter, accentColor = MeditationAmber,
        steps = listOf(
            BreathStep("TENSA", 5), BreathStep("MANTÉN", 5), BreathStep("RELAJA", 10)
        )
    )
)

// ─────────────────────────────────────────────
// Entry screen — Meditation grid
// ─────────────────────────────────────────────

@Composable
fun MeditationScreen(modifier: Modifier = Modifier) {
    var selectedTechnique by remember { mutableStateOf<BreathTechnique?>(null) }

    AnimatedContent(
        targetState = selectedTechnique,
        transitionSpec = {
            fadeIn(tween(340)) + slideInVertically(tween(340)) { it / 8 } togetherWith
            fadeOut(tween(220))
        },
        label = "meditationContent"
    ) { selected ->
        if (selected == null) {
            MeditationGridScreen(
                modifier = modifier,
                onTechniqueSelected = { selectedTechnique = it }
            )
        } else {
            val context = androidx.compose.ui.platform.LocalContext.current
            BreathingSessionScreen(
                technique = selected,
                modifier = modifier,
                onBack = {
                    com.upn.relaxmind.feature.gamification.data.GamificationManager.updateActivity(context)
                    selectedTechnique = null
                }
            )
        }
    }
}

@Composable
private fun MeditationGridScreen(
    modifier: Modifier,
    onTechniqueSelected: (BreathTechnique) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = "Meditar",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Elige una técnica de respiración",
                style = MaterialTheme.typography.bodyMedium,
                color = RelaxMutedText
            )
        }
        Spacer(modifier = Modifier.height(20.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(techniques) { technique ->
                TechniqueCard(
                    technique = technique,
                    onClick = { onTechniqueSelected(technique) }
                )
            }
        }
    }
}

@Composable
private fun TechniqueCard(technique: BreathTechnique, onClick: () -> Unit) {
    val accent = technique.accentColor
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(10.dp, RoundedCornerShape(22.dp), spotColor = accent.copy(0.2f))
            .clip(RoundedCornerShape(22.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(18.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = technique.icon,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = technique.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = technique.description,
                style = MaterialTheme.typography.bodySmall,
                color = RelaxMutedText,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "${technique.durationMin} min",
                style = MaterialTheme.typography.labelSmall,
                color = accent,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// ─────────────────────────────────────────────
// Full-screen breathing session
// ─────────────────────────────────────────────

@Composable
private fun BreathingSessionScreen(
    technique: BreathTechnique,
    modifier: Modifier,
    onBack: () -> Unit
) {
    var isRunning by remember { mutableStateOf(false) }
    var currentStepIndex by remember { mutableIntStateOf(0) }
    var secondsLeft by remember { mutableIntStateOf(technique.steps[0].seconds) }
    val currentStep = technique.steps[currentStepIndex]
    val accent = technique.accentColor

    // Timer coroutine
    LaunchedEffect(isRunning, currentStepIndex) {
        if (!isRunning) return@LaunchedEffect
        secondsLeft = technique.steps[currentStepIndex].seconds
        while (secondsLeft > 0) {
            delay(1000)
            secondsLeft--
        }
        val nextIndex = (currentStepIndex + 1) % technique.steps.size
        currentStepIndex = nextIndex
        secondsLeft = technique.steps[nextIndex].seconds
    }

    // Breathing animation — scale synced to step progress
    val infinite = rememberInfiniteTransition(label = "breathCircle")
    val circleScale by infinite.animateFloat(
        initialValue = 0.72f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (currentStep.seconds * 1000).coerceAtLeast(800),
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val bgGradient = Brush.verticalGradient(
        listOf(accent.copy(alpha = 0.12f), MaterialTheme.colorScheme.background)
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgGradient)
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    onClick = onBack,
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 2.dp,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("←", fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = technique.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.size(40.dp))
            }

            // Breathing Animation Area
            Box(
                modifier = Modifier
                    .weight(3f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {

                // Core Breathing Visualizer
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Title Area: Time and Step Label on the SAME line
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = secondsLeft.toString(),
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Black,
                                color = accent
                            )
                            
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            Text(
                                text = currentStep.label,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = accent,
                                letterSpacing = 2.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Dynamic Image
                        val stepImg = when (currentStep.label.uppercase()) {
                            "INHALA" -> com.upn.relaxmind.R.drawable.inhalar
                            "RETÉN" -> com.upn.relaxmind.R.drawable.retener
                            "EXHALA" -> com.upn.relaxmind.R.drawable.exahalar
                            else -> null
                        }

                        Box(
                            modifier = Modifier
                                .size(380.dp)
                                .graphicsLayer { 
                                    scaleX = circleScale
                                    scaleY = circleScale
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            androidx.compose.animation.Crossfade(
                                targetState = stepImg,
                                animationSpec = tween(1000),
                                label = "imageFade"
                            ) { imgRes ->
                                if (imgRes != null) {
                                    Image(
                                        painter = painterResource(id = imgRes),
                                        contentDescription = currentStep.label,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    // Fallback
                                    val stepIcon = when (currentStep.label.uppercase()) {
                                        "INHALA" -> Icons.Outlined.Air
                                        "RETÉN" -> Icons.Outlined.SelfImprovement
                                        "EXHALA" -> Icons.Outlined.Spa
                                        else -> technique.icon
                                    }
                                    Icon(
                                        imageVector = stepIcon,
                                        contentDescription = null,
                                        tint = accent,
                                        modifier = Modifier.size(80.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Play / Pause button
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .shadow(12.dp, CircleShape, spotColor = accent.copy(0.35f))
                    .clip(CircleShape)
                    .background(accent)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { isRunning = !isRunning },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isRunning) Icons.Outlined.Pause else Icons.Outlined.PlayArrow,
                    contentDescription = if (isRunning) "Pausar" else "Iniciar",
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Technique description card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(10.dp, RoundedCornerShape(20.dp), spotColor = Color(0x12000000))
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(20.dp)
            ) {
                Column {
                    Text(
                        text = technique.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = technique.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = RelaxMutedText
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    // Steps row
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        technique.steps.forEachIndexed { idx, step ->
                            val isActive = idx == currentStepIndex && isRunning
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (isActive) accent.copy(alpha = 0.12f)
                                        else Color(0xFFF1F5F9)
                                    )
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "${step.label} ${step.seconds}s",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isActive) accent else RelaxMutedText,
                                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
