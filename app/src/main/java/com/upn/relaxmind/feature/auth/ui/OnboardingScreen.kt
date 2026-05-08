package com.upn.relaxmind.feature.auth.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.outlined.Mood
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.foundation.Image
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upn.relaxmind.core.ui.theme.RelaxGreen
import kotlinx.coroutines.delay

private data class WelcomeSlide(
    val title: String,
    val subtitle: String,
    val imageRes: Int
)

@Composable
fun OnboardingScreen(
    modifier: Modifier = Modifier,
    onFinish: () -> Unit,
    onSkipToLogin: () -> Unit
) {
    val context = LocalContext.current
    val finishAction = {
        com.upn.relaxmind.core.data.preferences.AppPreferences.setSeenOnboarding(context, true)
        onFinish()
    }
    val skipAction = {
        com.upn.relaxmind.core.data.preferences.AppPreferences.setSeenOnboarding(context, true)
        onSkipToLogin()
    }
    var showContent by remember { mutableStateOf(false) }
    var introCompleted by rememberSaveable { mutableStateOf(false) }
    var pageIndex by rememberSaveable { mutableIntStateOf(0) }
    val slides = remember {
        listOf(
            WelcomeSlide(
                title = "Cuida tu mente",
                subtitle = "Descubre ejercicios, respiración guiada y herramientas para cada día.",
                imageRes = com.upn.relaxmind.R.drawable.screen1
            ),
            WelcomeSlide(
                title = "Expresa tus emociones",
                subtitle = "Registra cómo te sientes y recibe recomendaciones con apoyo de IA.",
                imageRes = com.upn.relaxmind.R.drawable.screen2
            ),
            WelcomeSlide(
                title = "Acompaña tu progreso",
                subtitle = "Visualiza avances y sigue un plan de bienestar personalizado.",
                imageRes = com.upn.relaxmind.R.drawable.screen3
            )
        )
    }
    val isLastPage = pageIndex == slides.lastIndex

    LaunchedEffect(Unit) { showContent = true }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color.White, Color(0xFFF8FAFC), Color(0xFFF1F5F9))
                )
            )
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            
            if (!introCompleted) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.foundation.Image(
                            painter = androidx.compose.ui.res.painterResource(id = com.upn.relaxmind.R.drawable.bienvenida),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp)
                                .clip(RoundedCornerShape(32.dp))
                        )
                    }
                    
                    AnimatedVisibility(
                        visible = showContent,
                        enter = fadeIn(tween(600)) + slideInVertically(tween(600)) { it / 4 }
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Bienvenido a\nRelaxMind",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 32.sp,
                                    lineHeight = 38.sp
                                ),
                                color = Color(0xFF1E293B),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Tu compañero de salud mental personalizado para apoyar tu bienestar.",
                                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                                color = Color(0xFF64748B),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    HighlightCard(
                        icon = Icons.Filled.Security,
                        iconTint = Color(0xFF10B981),
                        title = "Privacidad y seguridad",
                        subtitle = "Tus datos están cifrados y protegidos."
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    HighlightCard(
                        icon = Icons.Filled.AutoAwesome,
                        iconTint = Color(0xFF8B5CF6),
                        title = "Soporte con IA",
                        subtitle = "Recibe recomendaciones personalizadas."
                    )
                }
            } else {
                androidx.compose.animation.AnimatedContent(
                    targetState = pageIndex,
                    transitionSpec = {
                        (slideInHorizontally { it } + fadeIn()).togetherWith(slideOutHorizontally { -it } + fadeOut())
                    },
                    label = "featureCarousel"
                ) { idx ->
                    val slide = slides[idx]
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(280.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            androidx.compose.foundation.Image(
                                painter = androidx.compose.ui.res.painterResource(id = slide.imageRes),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                                    .clip(RoundedCornerShape(32.dp))
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        Text(
                            text = slide.title,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 28.sp
                            ),
                            color = Color(0xFF1E293B),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = slide.subtitle,
                            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                            color = Color(0xFF64748B),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                }
            }

                Spacer(modifier = Modifier.height(36.dp))

                AnimatedVisibility(
                    visible = showContent,
                    enter = fadeIn(tween(520)) + slideInVertically(tween(520)) { it / 3 }
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Button(
                            onClick = {
                                if (!introCompleted) {
                                    introCompleted = true
                                    pageIndex = 0
                                } else if (isLastPage) {
                                    finishAction()
                                } else {
                                    pageIndex++
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = RelaxGreen)
                        ) {
                            Text(
                                text = if (!introCompleted) {
                                    "SIGUIENTE"
                                } else if (isLastPage) {
                                    "COMENZAR"
                                } else {
                                    "SIGUIENTE"
                                },
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(18.dp))
                        if (introCompleted) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                repeat(3) { dotIndex ->
                                    Box(
                                        modifier = Modifier
                                            .size(if (dotIndex == pageIndex) 18.dp else 8.dp, 8.dp)
                                            .background(
                                                color = if (dotIndex == pageIndex) RelaxGreen else Color(0xFFD1D9E0),
                                                shape = RoundedCornerShape(16.dp)
                                            )
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        TextButton(onClick = skipAction) {
                            Text(
                                text = "Skip Intro",
                                style = MaterialTheme.typography.labelLarge,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                }
        }
    }
}

@Composable
private fun HighlightCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFFF8FBFD),
        border = BorderStroke(1.dp, Color(0xFFE5ECF1)),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(14.dp),
                color = iconTint.copy(alpha = 0.14f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1F2937)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF64748B)
                )
            }
        }
    }
}
