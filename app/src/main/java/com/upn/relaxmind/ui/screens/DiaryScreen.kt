package com.upn.relaxmind.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upn.relaxmind.ui.theme.RelaxMutedText
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.upn.relaxmind.data.LocalDataRepository
import com.upn.relaxmind.data.models.DiaryEntry
import java.util.UUID
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.upn.relaxmind.ui.theme.LocalIsDarkTheme

private val LavenderMid = Color(0xFFC4B5FD)
private val LavenderDark = Color(0xFF7C3AED)
private val DeepViolet = Color(0xFF4C1D95)

@Composable
fun DiaryScreen(modifier: Modifier = Modifier, onBack: () -> Unit = {}) {
    var isEditing by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val diaryEntries = remember { mutableStateListOf<DiaryEntry>() }
    
    LaunchedEffect(Unit) {
        diaryEntries.clear()
        diaryEntries.addAll(LocalDataRepository.getDiaryEntries(context))
    }

    AnimatedContent(
        targetState = isEditing,
        transitionSpec = {
            fadeIn(tween(500)) + scaleIn(initialScale = 0.92f) togetherWith fadeOut(tween(400))
        },
        label = "diary_transition"
    ) { editing ->
        if (editing) {
            DiaryEditorScreen(
                onSave = { entry ->
                    LocalDataRepository.saveDiaryEntry(context, entry)
                    val index = diaryEntries.indexOfFirst { it.id == entry.id }
                    if (index != -1) diaryEntries[index] = entry else diaryEntries.add(0, entry)
                    isEditing = false
                },
                onCancel = { isEditing = false }
            )
        } else {
            DiaryListScreen(
                entries = diaryEntries,
                onBack = onBack,
                onNewEntry = { isEditing = true }
            )
        }
    }
}

@Composable
private fun DiaryListScreen(entries: List<DiaryEntry>, onBack: () -> Unit, onNewEntry: () -> Unit) {
    val isDark = LocalIsDarkTheme.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(20.dp))
            
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    onClick = onBack,
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 2.dp,
                    shadowElevation = 2.dp,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, null, modifier = Modifier.size(20.dp))
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Mi Diario",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isDark) LavenderMid else LavenderDark
                    )
                    Text(
                        text = "Reflexiona y libera tu mente",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            
            if (entries.isEmpty()) {
                EmptyDiaryView()
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 120.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    items(entries) { entry ->
                        ModernDiaryCard(entry)
                    }
                }
            }
        }

        // Floating Action Button
        ExtendedFloatingActionButton(
            onClick = onNewEntry,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .shadow(16.dp, CircleShape, spotColor = LavenderDark),
            containerColor = LavenderDark,
            contentColor = Color.White,
            shape = CircleShape,
        ) {
            Icon(Icons.Outlined.Create, null)
            Spacer(modifier = Modifier.width(12.dp))
            Text("Nueva Entrada", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ModernDiaryCard(entry: DiaryEntry) {
    val date = LocalDate.parse(entry.dateIso)
    val isDark = LocalIsDarkTheme.current
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        shadowElevation = 4.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(0.05f))
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(LavenderDark)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = date.format(DateTimeFormatter.ofPattern("EEEE, d MMMM")),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                Surface(
                    color = LavenderDark.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = entry.emotion,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isDark) LavenderMid else LavenderDark,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = entry.text,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(0.85f),
                lineHeight = 28.sp,
                maxLines = 4
            )
        }
    }
}

@Composable
private fun EmptyDiaryView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Outlined.AutoStories, 
            null, 
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.outline.copy(0.2f)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "Tu historia comienza aquí",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.6f)
        )
    }
}

@Composable
private fun DiaryEditorScreen(onSave: (DiaryEntry) -> Unit, onCancel: () -> Unit) {
    var entryText by remember { mutableStateOf("") }
    val emotions = listOf("✨", "😌", "😊", "😐", "😔", "😰", "😠")
    val emotionLabels = listOf("Inspirado", "Tranquilo", "Feliz", "Neutral", "Triste", "Ansioso", "Enojado")
    var selectedEmotionIndex by remember { mutableStateOf(1) }
    val isDark = LocalIsDarkTheme.current
    
    // Emotion-based background colors
    val emotionColors = listOf(
        Color(0xFFFFE29F), // ✨
        Color(0xFFE0E7FF), // 😌
        Color(0xFFDCFCE7), // 😊
        Color(0xFFF1F5F9), // 😐
        Color(0xFFDBEAFE), // 😔
        Color(0xFFFEF3C7), // 😰
        Color(0xFFFEE2E2)  // 😠
    )
    val currentBgColor = if (isDark) {
        emotionColors[selectedEmotionIndex].copy(0.05f)
    } else {
        emotionColors[selectedEmotionIndex].copy(0.3f)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Decorative background elements
        DecorativeBackground(modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Immersive Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onCancel,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface.copy(0.5f))
                ) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, null)
                }

                Text(
                    text = LocalDate.now().format(DateTimeFormatter.ofPattern("d MMMM")),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Surface(
                    onClick = {
                        if (entryText.isNotBlank()) {
                            onSave(
                                DiaryEntry(
                                    id = UUID.randomUUID().toString(),
                                    dateIso = LocalDate.now().toString(),
                                    text = entryText,
                                    emotion = "${emotions[selectedEmotionIndex]} ${emotionLabels[selectedEmotionIndex]}"
                                )
                            )
                        }
                    },
                    color = if (entryText.isNotBlank()) LavenderDark else MaterialTheme.colorScheme.surface.copy(0.5f),
                    shape = RoundedCornerShape(16.dp),
                    enabled = entryText.isNotBlank()
                ) {
                    Text(
                        "Guardar",
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                        color = if (entryText.isNotBlank()) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Interactive Mood Selector
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text(
                    "¿Cómo te sientes hoy?",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(16.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 8.dp)
                ) {
                    itemsIndexed(emotions) { index, emotion ->
                        val isSelected = selectedEmotionIndex == index
                        val scale by animateFloatAsState(if (isSelected) 1.2f else 1f)
                        
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .graphicsLayer(scaleX = scale, scaleY = scale)
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    if (isSelected) LavenderDark 
                                    else MaterialTheme.colorScheme.surface
                                )
                                .clickable { selectedEmotionIndex = index },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(emotion, fontSize = 28.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Immersive Writing Paper
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp, bottomStart = 24.dp, bottomEnd = 24.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(28.dp)
                ) {
                    TextField(
                        value = entryText,
                        onValueChange = { entryText = it },
                        modifier = Modifier.fillMaxSize(),
                        placeholder = {
                            Text(
                                "Empieza a escribir aquí...",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.3f),
                                    fontWeight = FontWeight.Light
                                )
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 20.sp,
                            lineHeight = 32.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = FontFamily.SansSerif
                        )
                    )
                }
            }
        }

        // Floating Prompt Button (AI-Like)
        Surface(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(32.dp)
                .navigationBarsPadding(),
            onClick = {
                val prompts = listOf(
                    "¿Qué te hizo sonreír hoy?",
                    "¿Qué aprendiste de un desafío reciente?",
                    "Describe un momento de paz hoy.",
                    "¿Por qué estás agradecido hoy?"
                )
                entryText += (if (entryText.isEmpty()) "" else "\n\n") + prompts.random()
            },
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = RoundedCornerShape(20.dp),
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.AutoAwesome, null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Inspírame", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
private fun DecorativeBackground(modifier: Modifier) {
    // Background decoration can be implemented here for a more "living" feel
}
