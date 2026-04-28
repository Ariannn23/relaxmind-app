package com.upn.relaxmind.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Bedtime
import androidx.compose.material.icons.outlined.SentimentDissatisfied
import androidx.compose.material.icons.outlined.SentimentNeutral
import androidx.compose.material.icons.outlined.SentimentSatisfied
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upn.relaxmind.ui.theme.RelaxBackground
import com.upn.relaxmind.ui.theme.RelaxGreen
import com.upn.relaxmind.ui.theme.LocalIsDarkTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckInScreen(
    onBack: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    val totalSteps = 5
    val pagerState = rememberPagerState(pageCount = { totalSteps })
    val scope = rememberCoroutineScope()
    val isDark = LocalIsDarkTheme.current
    
    // State for questions
    var moodScore by rememberSaveable { mutableIntStateOf(3) }
    var sleepQuality by rememberSaveable { mutableIntStateOf(2) }
    var hasDiscomfort by rememberSaveable { mutableStateOf(false) }
    var discomfortType by rememberSaveable { mutableStateOf("Ansiedad") }
    var discomfortIntensity by rememberSaveable { mutableFloatStateOf(3f) }
    var tookMedication by rememberSaveable { mutableStateOf("Sí") }
    
    // Dynamic background colors based on page
    val bgColors = listOf(
        Color(0xFFFEF3C7), // Page 0: Mood (Sunny)
        Color(0xFFE0E7FF), // Page 1: Sleep (Night/Calm)
        Color(0xFFFFEDD5), // Page 2: Discomfort (Warm)
        Color(0xFFDCFCE7), // Page 3: Medication (Fresh)
        Color(0xFFF3E8FF)  // Page 4: Results (Magic)
    )
    
    val currentBgColor = if (isDark) {
        MaterialTheme.colorScheme.background
    } else {
        bgColors[pagerState.currentPage]
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "Mi Estado Actual", 
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold)
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Volver")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = currentBgColor
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .animateContentSize()
        ) {
            // High fidelity progress indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(totalSteps) { i ->
                    val isCompleted = i <= pagerState.currentPage
                    val color = if (isCompleted) RelaxGreen else if (isDark) Color.White.copy(0.1f) else Color.White.copy(0.4f)
                    val width by animateDpAsState(if (i == pagerState.currentPage) 32.dp else 12.dp)
                    
                    Box(
                        modifier = Modifier
                            .height(6.dp)
                            .width(width)
                            .clip(CircleShape)
                            .background(color)
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
                userScrollEnabled = false,
                beyondViewportPageCount = 1
            ) { page ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    when (page) {
                        0 -> MoodStep(moodScore) { moodScore = it }
                        1 -> SleepStep(sleepQuality) { sleepQuality = it }
                        2 -> DiscomfortStep(
                            hasDiscomfort, discomfortType, discomfortIntensity,
                            onToggle = { hasDiscomfort = it },
                            onTypeChange = { discomfortType = it },
                            onIntensityChange = { discomfortIntensity = it }
                        )
                        3 -> MedicationStep(tookMedication) { tookMedication = it }
                        4 -> FinalStep(
                            moodScore, sleepQuality, hasDiscomfort, discomfortIntensity,
                            onNavigateToHistory
                        )
                    }
                }
            }

            // Navigation Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .navigationBarsPadding(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (pagerState.currentPage > 0 && pagerState.currentPage < totalSteps - 1) {
                    TextButton(onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) } }) {
                        Text("Atrás", color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                if (pagerState.currentPage < totalSteps - 1) {
                    Button(
                        onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) } },
                        modifier = Modifier
                            .height(64.dp)
                            .width(180.dp)
                            .shadow(12.dp, RoundedCornerShape(24.dp), spotColor = RelaxGreen),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RelaxGreen)
                    ) {
                        Text("Siguiente", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.ArrowForward, null)
                    }
                }
            }
        }
    }
}

@Composable
private fun MoodStep(selected: Int, onSelect: (Int) -> Unit) {
    val moods = listOf("emoji1", "emoji2", "emoji3", "emoji4", "emoji5")
    val labels = listOf("Muy Mal", "Mal", "Neutral", "Bien", "Excelente")
    
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "¿Cómo brilla tu energía hoy?",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(60.dp))
        
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            moods.forEachIndexed { index, emoji ->
                val isSelected = selected == index + 1
                val scale by animateFloatAsState(if (isSelected) 1.15f else 1f)
                val alpha by animateFloatAsState(if (isSelected) 1f else 0.5f)
                
                Surface(
                    onClick = { onSelect(index + 1) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .scale(scale)
                        .graphicsLayer { this.alpha = alpha },
                    shape = RoundedCornerShape(24.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent,
                    border = if (isSelected) BorderStroke(2.dp, RelaxGreen) else null,
                    shadowElevation = if (isSelected) 8.dp else 0.dp
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) RelaxGreen.copy(0.1f) else Color.Transparent),
                            contentAlignment = Alignment.Center
                        ) {
                            androidx.compose.foundation.Image(
                                painter = painterResource(
                                    id = when(index) {
                                        0 -> com.upn.relaxmind.R.drawable.emoji1
                                        1 -> com.upn.relaxmind.R.drawable.emoji2
                                        2 -> com.upn.relaxmind.R.drawable.emoji3
                                        3 -> com.upn.relaxmind.R.drawable.emoji4
                                        else -> com.upn.relaxmind.R.drawable.emoji5
                                    }
                                ),
                                contentDescription = null,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(20.dp))
                        Text(
                            labels[index],
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        if (isSelected) {
                            Icon(Icons.Default.CheckCircle, null, tint = RelaxGreen)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SleepStep(selected: Int, onSelect: (Int) -> Unit) {
    val options = listOf("Poco", "Regular", "Muy Bien")
    val icons = listOf(Icons.Outlined.Bedtime, Icons.Outlined.SentimentNeutral, Icons.Default.NightsStay)
    
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "¿Qué tal descansaste?",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(48.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            options.forEachIndexed { index, label ->
                val isSelected = selected == index + 1
                Surface(
                    onClick = { onSelect(index + 1) },
                    modifier = Modifier.weight(1f).height(160.dp),
                    shape = RoundedCornerShape(32.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surface.copy(0.4f),
                    border = if (isSelected) BorderStroke(3.dp, Color(0xFF6366F1)) else null,
                    shadowElevation = if (isSelected) 12.dp else 2.dp
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            icons[index], 
                            null, 
                            modifier = Modifier.size(40.dp),
                            tint = if (isSelected) Color(0xFF6366F1) else MaterialTheme.colorScheme.onSurface.copy(0.4f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(label, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun DiscomfortStep(
    hasDiscomfort: Boolean, type: String, intensity: Float,
    onToggle: (Boolean) -> Unit, onTypeChange: (String) -> Unit, onIntensityChange: (Float) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "Sintoniza con tu cuerpo",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(40.dp))
        
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(32.dp),
            color = if (hasDiscomfort) Color.White else Color.White.copy(0.6f),
            onClick = { onToggle(!hasDiscomfort) }
        ) {
            Row(
                modifier = Modifier.padding(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(48.dp).clip(CircleShape).background(if (hasDiscomfort) Color(0xFFFF6B6B).copy(0.1f) else Color.Gray.copy(0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Healing, null, tint = if (hasDiscomfort) Color(0xFFFF6B6B) else Color.Gray)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text("Tengo alguna molestia", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.weight(1f))
                Switch(checked = hasDiscomfort, onCheckedChange = onToggle)
            }
        }

        AnimatedVisibility(visible = hasDiscomfort) {
            Column(modifier = Modifier.padding(top = 24.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Ansiedad", "Dolor", "Cansancio").forEach { t ->
                        FilterChip(
                            selected = type == t,
                            onClick = { onTypeChange(t) },
                            label = { Text(t) },
                            shape = CircleShape
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text("Intensidad", fontWeight = FontWeight.Bold)
                Slider(value = intensity, onValueChange = onIntensityChange, valueRange = 1f..5f, steps = 3)
            }
        }
    }
}

@Composable
private fun MedicationStep(selected: String, onSelect: (String) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "Tus aliados de salud",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(60.dp))
        
        Box(contentAlignment = Alignment.Center) {
            // Pill illustration logic here
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                listOf("Sí", "No", "Pendiente").forEach { opt ->
                    val isSelected = selected == opt
                    Surface(
                        onClick = { onSelect(opt) },
                        modifier = Modifier.fillMaxWidth().height(80.dp),
                        shape = RoundedCornerShape(40.dp),
                        color = if (isSelected) RelaxGreen else Color.White,
                        shadowElevation = 4.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                "Tomé mi medicación: $opt",
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else Color.Black
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FinalStep(
    mood: Int, sleep: Int, discomfort: Boolean, intensity: Float,
    onNavigateToHistory: () -> Unit
) {
    val finalScore = ((mood * 20) + (sleep * 5) - (if (discomfort) intensity * 10 else 0f)).toInt().coerceIn(0, 100)
    
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "¡Brillas con luz propia!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(40.dp))
        
        Box(contentAlignment = Alignment.Center) {
            val infinite = rememberInfiniteTransition(label = "sun")
            val rotate by infinite.animateFloat(0f, 360f, infiniteRepeatable(tween(10000, easing = LinearEasing)))
            
            Icon(
                Icons.Default.LightMode, 
                null, 
                modifier = Modifier.size(240.dp).rotate(rotate).graphicsLayer { alpha = 0.1f },
                tint = Color(0xFFFBBF24)
            )
            
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "$finalScore",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Black,
                    color = RelaxGreen
                )
                Text("Bienestar Hoy", fontWeight = FontWeight.Bold, color = RelaxGreen.copy(0.7f))
            }
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Button(
            onClick = onNavigateToHistory,
            modifier = Modifier.fillMaxWidth().height(64.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface, contentColor = RelaxGreen)
        ) {
            Text("Ver Historial Completo", fontWeight = FontWeight.Bold)
        }
    }
}
