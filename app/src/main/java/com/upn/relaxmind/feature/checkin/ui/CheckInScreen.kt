package com.upn.relaxmind.feature.checkin.ui

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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upn.relaxmind.core.ui.components.RelaxBackButton
import com.upn.relaxmind.core.ui.theme.*
import kotlinx.coroutines.launch
import com.upn.relaxmind.feature.gamification.data.GamificationManager

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
    
    var hoursSlept by remember { mutableStateOf(8) }
    
    val currentBgColor = getSoftMint()

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
            // 3D Styled Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 20.dp)
                    .height(10.dp)
                    .clip(CircleShape)
                    .background(if (isDark) Color.White.copy(0.05f) else Color.Black.copy(0.05f))
            ) {
                val progress = (pagerState.currentPage + 1).toFloat() / totalSteps
                val progressWidth = animateFloatAsState(progress).value
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progressWidth)
                        .fillMaxHeight()
                        .clip(CircleShape)
                        .background(
                            Brush.horizontalGradient(
                                listOf(RelaxGreen.copy(0.7f), RelaxGreen)
                            )
                        )
                        .shadow(4.dp, CircleShape, spotColor = RelaxGreen)
                )
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
                        1 -> SleepStep(
                            selected = sleepQuality,
                            hoursSlept = hoursSlept,
                            onSelect = { sleepQuality = it },
                            onHoursChange = { hoursSlept = it }
                        )
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
    val labels = listOf("Muy Mal", "Mal", "Neutral", "Bien", "Excelente")
    
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "¿Cómo brilla tu energía hoy?",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(40.dp))
        
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            labels.forEachIndexed { index, label ->
                val isSelected = selected == index + 1
                val scale by animateFloatAsState(if (isSelected) 1.05f else 1f)
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .scale(scale)
                        .shadow(if (isSelected) 12.dp else 2.dp, RoundedCornerShape(24.dp), spotColor = RelaxGreen.copy(0.3f))
                        .background(
                            color = if (isSelected) Color.White else Color(0xFFF7FBF7), // Solid bone-white
                            shape = RoundedCornerShape(24.dp)
                        )
                        .border(1.dp, if (isSelected) RelaxGreen.copy(0.5f) else Color.Black.copy(0.05f), RoundedCornerShape(24.dp))
                        .clickable { onSelect(index + 1) }
                ) {
                    Row(
                        modifier = Modifier.background(Color.Transparent).padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
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
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            label,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            modifier = Modifier.background(Color.Transparent)
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        if (isSelected) {
                            Icon(Icons.Default.CheckCircle, null, tint = RelaxGreen, modifier = Modifier.size(24.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SleepStep(selected: Int, hoursSlept: Int, onSelect: (Int) -> Unit, onHoursChange: (Int) -> Unit) {
    val options = listOf("Poco", "Regular", "Muy Bien")
    val icons = listOf(Icons.Default.Cloud, Icons.Default.NightsStay, Icons.Default.Bedtime)
    
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "¿Qué tal descansaste?",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(40.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            options.forEachIndexed { index, label ->
                val isSelected = selected == index + 1
                val scale by animateFloatAsState(if (isSelected) 1.05f else 1f)
                val accentColor = when(index) {
                    0 -> Color(0xFFFF6B6B)
                    1 -> Color(0xFFF59E0B)
                    else -> Color(0xFF6366F1)
                }
                
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(130.dp)
                        .scale(scale)
                        .shadow(if (isSelected) 16.dp else 0.dp, RoundedCornerShape(32.dp), spotColor = accentColor.copy(0.2f))
                        .background(
                            color = if (isSelected) Color.White else Color.Black.copy(0.04f),
                            shape = RoundedCornerShape(32.dp)
                        )
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            brush = if (isSelected) Brush.verticalGradient(listOf(accentColor, accentColor.copy(0.6f))) else SolidColor(Color.Black.copy(0.03f)),
                            shape = RoundedCornerShape(32.dp)
                        )
                        .clickable { onSelect(index + 1) },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier.background(Color.Transparent),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            icons[index], 
                            null, 
                            modifier = Modifier.size(36.dp).background(Color.Transparent),
                            tint = if (isSelected) accentColor else MaterialTheme.colorScheme.onSurface.copy(0.3f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            label, 
                            fontWeight = FontWeight.Bold, 
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.background(Color.Transparent)
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text("¿Cuántas horas lograste dormir?", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        
        // Horizontal Hour Picker (Simple Wheel)
        androidx.compose.foundation.lazy.LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 20.dp)
        ) {
            items(12) { i ->
                val hours = i + 1
                val isSelected = hours == hoursSlept
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .shadow(if (isSelected) 8.dp else 0.dp, CircleShape, spotColor = Color(0xFF6366F1).copy(0.3f))
                        .background(
                            color = if (isSelected) Color(0xFF6366F1) else Color(0xFFF7FBF7),
                            shape = CircleShape
                        )
                        .border(1.dp, if (isSelected) Color.Transparent else Color.Black.copy(0.05f), CircleShape)
                        .clickable { onHoursChange(hours) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "${hours}h", 
                        fontWeight = FontWeight.Bold, 
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.background(Color.Transparent)
                    )
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
        Spacer(modifier = Modifier.height(56.dp))
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(6.dp, RoundedCornerShape(32.dp), spotColor = Color.Black.copy(0.05f))
                .background(
                    color = if (hasDiscomfort) MaterialTheme.colorScheme.surface else Color(0xFFF7FBF7),
                    shape = RoundedCornerShape(32.dp)
                )
                .border(
                    1.dp, 
                    if (hasDiscomfort) RelaxGreen.copy(0.3f) else Color.Black.copy(0.05f), 
                    RoundedCornerShape(32.dp)
                )
                .clickable { onToggle(!hasDiscomfort) }
        ) {
            Row(
                modifier = Modifier.background(Color.Transparent).padding(horizontal = 24.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(44.dp).clip(CircleShape).background(if (hasDiscomfort) Color(0xFFFF6B6B).copy(0.1f) else Color.Transparent),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Healing, null, tint = if (hasDiscomfort) Color(0xFFFF6B6B) else Color.Gray, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        "Tengo alguna molestia", 
                        fontWeight = FontWeight.Bold, 
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.background(Color.Transparent).weight(1f, fill = false)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Switch(
                    checked = hasDiscomfort, 
                    onCheckedChange = onToggle,
                    modifier = Modifier.scale(0.75f)
                )
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
                Text("Intensidad", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    (1..5).forEach { i ->
                        val isSelected = intensity.toInt() == i
                        val stepColor = when(i) {
                            1, 2 -> Color(0xFF10B981)
                            3 -> Color(0xFFF59E0B)
                            else -> Color(0xFFFF6B6B)
                        }
                        
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .shadow(if (isSelected) 8.dp else 0.dp, CircleShape, spotColor = stepColor)
                                .background(
                                    color = if (isSelected) stepColor else Color(0xFFF7FBF7),
                                    shape = CircleShape
                                )
                                .border(1.dp, if (isSelected) Color.Transparent else Color.Black.copy(0.05f), CircleShape)
                                .clickable { onIntensityChange(i.toFloat()) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "$i",
                                fontWeight = FontWeight.Black,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface.copy(0.4f),
                                modifier = Modifier.background(Color.Transparent)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(0.05f))
                ) {
                    val progress = (intensity - 1) / 4f
                    val trackColor = when {
                        intensity <= 2 -> Color(0xFF10B981)
                        intensity <= 3 -> Color(0xFFF59E0B)
                        else -> Color(0xFFFF6B6B)
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .fillMaxHeight()
                            .background(trackColor, CircleShape)
                    )
                }
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
        
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
                listOf("Sí", "No", "Pendiente").forEach { opt ->
                    val isSelected = selected == opt
                    val scale by animateFloatAsState(if (isSelected) 1.03f else 1f)
                    val color = when(opt) {
                        "Sí" -> RelaxGreen
                        "No" -> Color(0xFFFF6B6B)
                        else -> Color(0xFFF59E0B)
                    }
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(72.dp)
                            .scale(scale)
                            .shadow(if (isSelected) 12.dp else 0.dp, RoundedCornerShape(24.dp), spotColor = color.copy(0.2f))
                            .background(
                                color = if (isSelected) Color.White else Color.Black.copy(0.04f),
                                shape = RoundedCornerShape(24.dp)
                            )
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                brush = if (isSelected) SolidColor(color) else SolidColor(Color.Black.copy(0.03f)),
                                shape = RoundedCornerShape(24.dp)
                            )
                            .clickable { onSelect(opt) }
                    ) {
                        Row(
                            modifier = Modifier.background(Color.Transparent).padding(horizontal = 24.dp).fillMaxHeight(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                if (opt == "Sí") Icons.Default.CheckCircle else if (opt == "No") Icons.Default.Cancel else Icons.Default.Schedule,
                                null,
                                tint = if (isSelected) color else Color.Gray.copy(0.4f),
                                modifier = Modifier.background(Color.Transparent)
                            )
                            Spacer(modifier = Modifier.width(20.dp))
                            Text(
                                if (opt == "Sí") "He tomado mis aliados" else if (opt == "No") "Aún no los tomo" else "Pendiente por tomar",
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) color else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.background(Color.Transparent)
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            if (isSelected) {
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
                            }
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
    val message = when {
        finalScore > 85 -> "¡Estás radiante! Hoy nada puede apagar tu luz interna."
        finalScore > 70 -> "Vas por muy buen camino. Mantén esa energía positiva."
        finalScore > 50 -> "Un día equilibrado. Recuerda darte pequeños momentos de paz."
        finalScore > 30 -> "Está bien no estar al 100%. Estamos aquí para acompañarte."
        else -> "Mañana será una nueva oportunidad. Respira profundo, estamos contigo."
    }
    
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "Tu Estado de Paz",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        
        Box(contentAlignment = Alignment.Center) {
            // Animated background glow
            val infinite = rememberInfiniteTransition(label = "scoreGlow")
            val glowScale by infinite.animateFloat(1f, 1.3f, infiniteRepeatable(tween(2000), RepeatMode.Reverse))
            
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .scale(glowScale)
                    .background(
                        Brush.radialGradient(listOf(RelaxGreen.copy(0.15f), Color.Transparent)),
                        CircleShape
                    )
            )

            Surface(
                modifier = Modifier.size(180.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 16.dp,
                border = BorderStroke(4.dp, Brush.sweepGradient(listOf(RelaxGreen.copy(0.2f), RelaxGreen, RelaxGreen.copy(0.2f))))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "$finalScore",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Black,
                        color = RelaxGreen
                    )
                    Text("NIVEL RELAX", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = RelaxMutedText)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(40.dp))
        
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            color = Color.White.copy(0.2f),
            border = BorderStroke(1.dp, Color.White.copy(0.3f))
        ) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                androidx.compose.foundation.Image(
                    painter = painterResource(id = com.upn.relaxmind.R.drawable.lumi),
                    contentDescription = "Lumi",
                    modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    message,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )
            }
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        val context = LocalContext.current
        Button(
            onClick = {
                GamificationManager.updateActivity(context)
                onNavigateToHistory()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .shadow(8.dp, RoundedCornerShape(24.dp), spotColor = RelaxGreen.copy(0.2f)),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = RelaxGreen)
        ) {
            Text("Ver Detalles", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}
