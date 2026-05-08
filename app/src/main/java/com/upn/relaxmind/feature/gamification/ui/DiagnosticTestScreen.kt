package com.upn.relaxmind.feature.gamification.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.compose.BackHandler
import com.upn.relaxmind.R
import com.upn.relaxmind.core.ui.theme.RelaxBackground
import com.upn.relaxmind.core.ui.modifiers.relaxMindScreenInsets
import com.upn.relaxmind.core.ui.theme.RelaxMutedText
import com.upn.relaxmind.core.ui.components.RelaxBackButton
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private enum class QuestionType {
    EMOJI, SCALE, OPTIONS, SLIDER, CHIPS_GRID, SWIPE, PICKER
}

private data class DiagnosticQuestion(
    val id: Int,
    val text: String,
    val type: QuestionType,
    val options: List<String> = emptyList(),
    val subtitle: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiagnosticTestScreen(
    modifier: Modifier = Modifier,
    onTestCompleted: (score: Int) -> Unit = {},
    onExitToRoleSelection: () -> Unit = {},
    onSkipToDashboard: () -> Unit = {}
) {
    val questions = remember {
        listOf(
            DiagnosticQuestion(1, "¿Cómo describirías tu estado de ánimo?", QuestionType.EMOJI, subtitle = "Selecciona el que mejor te represente."),
            DiagnosticQuestion(2, "¿Te sientes incapaz de dejar de preocuparte?", QuestionType.SWIPE, subtitle = "Desliza a la derecha para Sí, izquierda para No."),
            DiagnosticQuestion(3, "¿Has sentido poco interés por tus actividades?", QuestionType.OPTIONS, listOf("Para nada", "Un poco", "Bastante", "Extremadamente"), "Evaluación de anhedonia."),
            DiagnosticQuestion(4, "¿Cómo calificarías tu calidad de vida hoy?", QuestionType.SLIDER, subtitle = "Evalúa sueño, energía y concentración."),
            DiagnosticQuestion(5, "¿Cuántas horas dormiste anoche?", QuestionType.PICKER, subtitle = "Gira la rueda para seleccionar."),
            DiagnosticQuestion(6, "¿Qué áreas te gustaría priorizar?", QuestionType.CHIPS_GRID, listOf("Ansiedad", "Sueño", "Relaciones", "Trabajo", "Autoestima", "Estrés"), "Selecciona una para hoy."),
            DiagnosticQuestion(7, "¿Sientes que los problemas te sobrepasan?", QuestionType.SCALE, listOf("Nunca", "A veces", "Frecuentemente", "Siempre"), "Percepción de resiliencia.")
        )
    }

    var currentQuestionIndex by rememberSaveable { mutableIntStateOf(0) }
    var scores by rememberSaveable { mutableStateOf(List(questions.size) { -1f }) }
    
    var sleepValue by rememberSaveable { mutableFloatStateOf(5f) }
    var energyValue by rememberSaveable { mutableFloatStateOf(5f) }
    var concentrationValue by rememberSaveable { mutableFloatStateOf(5f) }

    var currentSelection by remember(currentQuestionIndex) { 
        val q = questions[currentQuestionIndex]
        mutableFloatStateOf(
            if (q.type == QuestionType.SLIDER) (sleepValue + energyValue + concentrationValue) / 3f
            else if (q.type == QuestionType.PICKER && scores[currentQuestionIndex] < 0) 7f
            else scores[currentQuestionIndex]
        )
    }

    if (questions[currentQuestionIndex].type == QuestionType.SLIDER) {
        currentSelection = (sleepValue + energyValue + concentrationValue) / 3f
    }

    val totalQuestions = questions.size
    val progress by animateFloatAsState(
        targetValue = (currentQuestionIndex + 1).toFloat() / totalQuestions,
        animationSpec = tween(500)
    )

    BackHandler {
        if (currentQuestionIndex == 0) onExitToRoleSelection() else currentQuestionIndex--
    }

    Surface(modifier = modifier.fillMaxSize().relaxMindScreenInsets(), color = RelaxBackground) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxWidth().height(56.dp).padding(horizontal = 8.dp)) {
                Box(modifier = Modifier.align(Alignment.CenterStart).padding(start = 8.dp)) {
                    RelaxBackButton(
                        onClick = { if (currentQuestionIndex == 0) onExitToRoleSelection() else currentQuestionIndex-- },
                        modifier = Modifier.size(40.dp)
                    )
                }
                Text("Test Inicial", style = MaterialTheme.typography.titleMedium, modifier = Modifier.align(Alignment.Center))
                TextButton(onClick = onSkipToDashboard, modifier = Modifier.align(Alignment.CenterEnd)) {
                    Text("Omitir", color = RelaxMutedText)
                }
            }

            Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 24.dp)) {
                Spacer(modifier = Modifier.height(16.dp))
                LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape), color = MaterialTheme.colorScheme.primary, trackColor = Color(0xFFE2E8F0))
                Spacer(modifier = Modifier.height(32.dp))

                AnimatedContent(
                    targetState = currentQuestionIndex, 
                    transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(300)) },
                    label = "questionAnim"
                ) { index ->
                    val question = questions[index]
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text("Pregunta ${index + 1} de $totalQuestions", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        Text(question.text, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(vertical = 8.dp))
                        if (question.subtitle.isNotEmpty()) Text(question.subtitle, style = MaterialTheme.typography.bodyMedium, color = RelaxMutedText)
                        
                        Spacer(modifier = Modifier.height(32.dp))

                        when (question.type) {
                            QuestionType.EMOJI -> EmojiGridUltraClean(currentSelection.toInt()) { currentSelection = it.toFloat() }
                            QuestionType.SCALE, QuestionType.OPTIONS -> VerticalChoiceList(currentSelection.toInt(), question.options) { currentSelection = it.toFloat() }
                            QuestionType.SLIDER -> {
                                Column(verticalArrangement = Arrangement.spacedBy(28.dp)) {
                                    DynamicSlider(label = "Calidad de Sueño", value = sleepValue) { sleepValue = it }
                                    DynamicSlider(label = "Nivel de Energía", value = energyValue) { energyValue = it }
                                    DynamicSlider(label = "Concentración", value = concentrationValue) { concentrationValue = it }
                                }
                            }
                            QuestionType.CHIPS_GRID -> PriorityAreaGrid(currentSelection.toInt()) { currentSelection = it.toFloat() }
                            QuestionType.SWIPE -> SwipeCardInteraction(onAnswer = { currentSelection = if (it) 1f else 0f })
                            QuestionType.PICKER -> HourWheelPicker(currentSelection.toInt()) { currentSelection = it.toFloat() }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
            }

            Row(modifier = Modifier.fillMaxWidth().padding(24.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedButton(onClick = { if (currentQuestionIndex > 0) currentQuestionIndex-- }, modifier = Modifier.weight(1f).height(56.dp), shape = RoundedCornerShape(28.dp), enabled = currentQuestionIndex > 0) {
                    Text("Anterior")
                }
                Button(
                    onClick = {
                        val newScores = scores.toMutableList()
                        newScores[currentQuestionIndex] = currentSelection
                        scores = newScores
                        
                        if (currentQuestionIndex == totalQuestions - 1) {
                            var total = 0f
                            scores.forEach { if (it >= 0f) total += it }
                            onTestCompleted(((total / 32f) * 100).roundToInt().coerceIn(0, 100))
                        } else {
                            currentQuestionIndex++
                        }
                    },
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    enabled = currentSelection >= 0 || questions[currentQuestionIndex].type == QuestionType.SLIDER
                ) {
                    Text(if (currentQuestionIndex == totalQuestions - 1) "Finalizar" else "Siguiente")
                }
            }
        }
    }
}

@Composable
private fun EmojiGridUltraClean(selectedIndex: Int, onSelect: (Int) -> Unit) {
    val emojiColors = listOf(Color(0xFF3B82F6), Color(0xFF8B5CF6), Color(0xFF10B981), Color(0xFFF59E0B), Color(0xFFFFD700))
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            EmojiCardUltraClean(0, selectedIndex == 0, emojiColors[0], R.drawable.emoji1, "Muy mal", Modifier.weight(1f)) { onSelect(0) }
            EmojiCardUltraClean(1, selectedIndex == 1, emojiColors[1], R.drawable.emoji2, "Mal", Modifier.weight(1f)) { onSelect(1) }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            EmojiCardUltraClean(2, selectedIndex == 2, emojiColors[2], R.drawable.emoji3, "Neutral", Modifier.weight(1f)) { onSelect(2) }
            EmojiCardUltraClean(3, selectedIndex == 3, emojiColors[3], R.drawable.emoji4, "Bien", Modifier.weight(1f)) { onSelect(3) }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            EmojiCardUltraClean(4, selectedIndex == 4, emojiColors[4], R.drawable.emoji5, "Muy bien", Modifier.fillMaxWidth(0.5f)) { onSelect(4) }
        }
    }
}

@Composable
private fun EmojiCardUltraClean(index: Int, isSelected: Boolean, color: Color, resId: Int, label: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(onClick = onClick, modifier = modifier.height(130.dp), shape = RoundedCornerShape(28.dp), color = Color.White, border = BorderStroke(if (isSelected) 3.dp else 1.dp, if (isSelected) color else Color(0xFFE2E8F0)), shadowElevation = if (isSelected) 4.dp else 0.dp) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier.padding(8.dp)) {
            Image(painter = painterResource(id = resId), contentDescription = label, modifier = Modifier.size(72.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(label, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.ExtraBold, color = if (isSelected) color else RelaxMutedText)
        }
    }
}

@Composable
private fun VerticalChoiceList(selectedIndex: Int, options: List<String>, onSelect: (Int) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        options.forEachIndexed { index, text ->
            Surface(modifier = Modifier.fillMaxWidth().height(64.dp).clickable { onSelect(index) }, shape = RoundedCornerShape(16.dp), color = if (selectedIndex == index) MaterialTheme.colorScheme.primary else Color.White, border = BorderStroke(1.dp, if (selectedIndex == index) Color.Transparent else Color(0xFFE2E8F0))) {
                Row(modifier = Modifier.padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(text, style = MaterialTheme.typography.bodyLarge, fontWeight = if (selectedIndex == index) FontWeight.Bold else FontWeight.Medium, color = if (selectedIndex == index) Color.White else Color(0xFF334155))
                    Spacer(modifier = Modifier.weight(1f))
                    if (selectedIndex == index) Icon(Icons.Default.Check, null, tint = Color.White)
                }
            }
        }
    }
}

@Composable
private fun DynamicSlider(label: String, value: Float, onValueChange: (Float) -> Unit) {
    val color = when { value < 3.5f -> Color(0xFFEF4444); value < 7f -> Color(0xFFF59E0B); else -> Color(0xFF10B981) }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(label, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Text(value.roundToInt().toString(), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black, color = color)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Slider(value = value, onValueChange = onValueChange, valueRange = 1f..10f, steps = 8, colors = SliderDefaults.colors(thumbColor = color, activeTrackColor = color, inactiveTrackColor = color.copy(alpha = 0.2f)))
    }
}

@Composable
private fun SwipeCardInteraction(onAnswer: (Boolean) -> Unit) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var answer by remember { mutableStateOf<Boolean?>(null) }
    val animatedX by animateFloatAsState(targetValue = offsetX)
    val rotation = (animatedX / 10f).coerceIn(-15f, 15f)
    Box(modifier = Modifier.fillMaxWidth().height(300.dp), contentAlignment = Alignment.Center) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Icon(Icons.Default.Close, null, tint = Color.Red.copy(alpha = 0.2f), modifier = Modifier.size(48.dp))
            Icon(Icons.Default.Check, null, tint = Color.Green.copy(alpha = 0.2f), modifier = Modifier.size(48.dp))
        }
        Surface(
            modifier = Modifier.size(width = 240.dp, height = 280.dp).offset { IntOffset(animatedX.roundToInt(), 0) }.rotate(rotation).shadow(8.dp, RoundedCornerShape(24.dp)).pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = { if (offsetX > 200) { offsetX = 1000f; answer = true; onAnswer(true) } else if (offsetX < -200) { offsetX = -1000f; answer = false; onAnswer(false) } else { offsetX = 0f } },
                    onDrag = { change, dragAmount -> change.consume(); offsetX += dragAmount.x }
                )
            },
            shape = RoundedCornerShape(24.dp), color = Color.White, border = BorderStroke(2.dp, if (offsetX > 50) Color.Green.copy(0.5f) else if (offsetX < -50) Color.Red.copy(0.5f) else Color(0xFFE2E8F0))
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Icon(imageVector = if (answer == true) Icons.Default.Check else if (answer == false) Icons.Default.Close else Icons.Default.Check, contentDescription = null, tint = if (answer == true) Color.Green else if (answer == false) Color.Red else Color.Gray.copy(0.2f), modifier = Modifier.size(80.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = if (answer == true) "SÍ" else if (answer == false) "NO" else "Desliza", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black, color = if (answer == true) Color.Green else if (answer == false) Color.Red else RelaxMutedText)
            }
        }
    }
}

@Composable
private fun HourWheelPicker(current: Int, onSelect: (Int) -> Unit) {
    val hours = (0..12).toList()
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = if (current >= 0) current else 7)
    LaunchedEffect(listState.isScrollInProgress) { if (!listState.isScrollInProgress) onSelect(listState.firstVisibleItemIndex) }
    Box(modifier = Modifier.fillMaxWidth().height(180.dp), contentAlignment = Alignment.Center) {
        Box(modifier = Modifier.fillMaxWidth().height(50.dp).background(MaterialTheme.colorScheme.primaryContainer.copy(0.3f), RoundedCornerShape(12.dp)))
        LazyColumn(state = listState, modifier = Modifier.height(150.dp), contentPadding = PaddingValues(vertical = 50.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            items(hours.size) { index ->
                val h = hours[index]
                val isSelected = listState.firstVisibleItemIndex == index
                Text(text = "${h}h", style = MaterialTheme.typography.headlineMedium, fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal, color = if (isSelected) MaterialTheme.colorScheme.primary else RelaxMutedText.copy(0.4f), modifier = Modifier.padding(vertical = 8.dp).graphicsLayer { scaleX = if (isSelected) 1.2f else 0.8f; scaleY = if (isSelected) 1.2f else 0.8f })
            }
        }
    }
}

@Composable
private fun PriorityAreaGrid(selectedIndex: Int, onSelect: (Int) -> Unit) {
    val areas = listOf(
        PriorityItem("Ansiedad", Icons.Default.SelfImprovement, Color(0xFF0D9488)),
        PriorityItem("Sueño", Icons.Default.Bedtime, Color(0xFF7C3AED)),
        PriorityItem("Relaciones", Icons.Default.Favorite, Color(0xFFE11D48)),
        PriorityItem("Trabajo", Icons.Default.Work, Color(0xFFD97706)),
        PriorityItem("Autoestima", Icons.Default.Spa, Color(0xFF4F46E5)),
        PriorityItem("Estrés", Icons.Default.Thunderstorm, Color(0xFF059669))
    )

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        areas.chunked(2).forEach { rowAreas ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                rowAreas.forEach { area ->
                    val index = areas.indexOf(area)
                    val isSelected = selectedIndex == index
                    
                    val scale by animateFloatAsState(if (isSelected) 1.05f else 1f)
                    
                    Surface(
                        onClick = { onSelect(index) },
                        modifier = Modifier.weight(1f).height(100.dp).graphicsLayer { scaleX = scale; scaleY = scale },
                        shape = RoundedCornerShape(24.dp),
                        color = if (isSelected) area.color else Color.White,
                        border = BorderStroke(if (isSelected) 0.dp else 1.5.dp, if (isSelected) Color.Transparent else area.color.copy(alpha = 0.2f)),
                        shadowElevation = if (isSelected) 8.dp else 0.dp
                    ) {
                        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            Icon(
                                imageVector = area.icon,
                                contentDescription = null,
                                tint = if (isSelected) Color.White else area.color,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = area.name,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else Color(0xFF334155),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

private data class PriorityItem(val name: String, val icon: ImageVector, val color: Color)
