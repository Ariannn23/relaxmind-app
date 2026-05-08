package com.upn.relaxmind.feature.ai_chat.ui

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upn.relaxmind.core.ui.theme.*
import com.upn.relaxmind.core.ui.components.RelaxBackButton
import com.upn.relaxmind.feature.ai_chat.data.LumiService
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class ChatMessage(
    val id: String,
    val text: String,
    val isUser: Boolean,
    val isTyping: Boolean = false,
    val action: String? = null
)

internal val LumiViolet = Color(0xFF6366F1)
internal val LumiCyan = Color(0xFF06B6D4)
internal val LumiGradient = Brush.linearGradient(listOf(LumiViolet, LumiCyan))
private val UserBubbleColor = Color(0xFF1E293B)

@Composable
fun AiChatScreen(modifier: Modifier = Modifier, onBack: () -> Unit) {
    val context = LocalContext.current
    val isDark = LocalIsDarkTheme.current
    val scope = rememberCoroutineScope()
    var inputText by remember { mutableStateOf("") }
    val messages = remember {
        mutableStateListOf(
            ChatMessage("1", "Hola, soy Lumi ✨. Tu compañero de bienestar. Estoy aquí para escucharte, sin juzgar. ¿Cómo va tu día?", isUser = false)
        )
    }
    var showCrisisAlert by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    val suggestions = listOf(
        "Tengo mucha ansiedad",
        "No puedo dormir",
        "¿Qué ejercicio me recomiendas?",
        "Solo quiero desahogarme"
    )

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(if (isDark) RelaxDarkBackground else Color(0xFFF8FAFC))
            .systemBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Premium Header (Glassmorphic)
            LumiHeader(onBack, isDark)

            // Crisis Banner
            AnimatedVisibility(
                visible = showCrisisAlert,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                CrisisBanner()
            }

            // Chat Content
            Box(modifier = Modifier.weight(1f)) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 80.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(messages) { msg ->
                        if (msg.isTyping) {
                            TypingIndicator(isDark)
                        } else {
                            LumiMessageBubble(msg, isDark)
                        }
                    }
                }
            }

            // Suggestions & Input
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isDark) RelaxDarkSurface.copy(0.95f) else Color.White.copy(0.95f))
                    .padding(bottom = 8.dp)
            ) {
                // Suggestions Chips
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(suggestions) { text ->
                        SuggestionChip(text, isDark) {
                            inputText = text
                        }
                    }
                }

                // Modern Input Field
                LumiInputField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    onSend = {
                        if (inputText.isNotBlank()) {
                            val userText = inputText
                            messages.add(ChatMessage(System.currentTimeMillis().toString(), userText, isUser = true))
                            inputText = ""
                            
                            val lowerText = userText.lowercase()
                            if (lowerText.contains("no puedo más") || lowerText.contains("rendirme") || lowerText.contains("morir")) {
                                showCrisisAlert = true
                            }

                            scope.launch {
                                val typingId = "typing_${System.currentTimeMillis()}"
                                messages.add(ChatMessage(typingId, "", isUser = false, isTyping = true))
                                
                                // Llamada a la IA real usando LumiService
                                val response = LumiService.getResponse(userText)
                                
                                messages.removeIf { it.id == typingId }
                                messages.add(ChatMessage(System.currentTimeMillis().toString(), response, isUser = false))
                            }
                        }
                    },
                    isDark = isDark
                )
            }
        }
    }
}

@Composable
private fun LumiHeader(onBack: () -> Unit, isDark: Boolean) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (isDark) RelaxDarkSurface.copy(0.8f) else Color.White.copy(0.8f),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RelaxBackButton(onClick = onBack, modifier = Modifier.size(40.dp))
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(id = com.upn.relaxmind.R.drawable.lumi),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column {
                Text(
                    text = "Lumi",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = if(isDark) Color.White else Color(0xFF1E293B)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF10B981)))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Escuchándote",
                        style = MaterialTheme.typography.labelSmall,
                        color = if(isDark) Color.White.copy(0.6f) else Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
private fun LumiMessageBubble(message: ChatMessage, isDark: Boolean) {
    val isUser = message.isUser
    val alignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
    
    val bubbleShape = if (isUser) {
        RoundedCornerShape(24.dp, 24.dp, 4.dp, 24.dp)
    } else {
        RoundedCornerShape(24.dp, 24.dp, 24.dp, 4.dp)
    }

    val containerColor = if (isUser) {
        if (isDark) Color(0xFF334155) else Color(0xFF1E293B)
    } else {
        if (isDark) Color(0xFF1E1E2E) else Color.White
    }

    val textColor = if (isUser) Color.White else (if(isDark) Color.White else Color(0xFF334155))

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Row(
            modifier = Modifier.widthIn(max = 300.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            if (!isUser) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color.Transparent),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(id = com.upn.relaxmind.R.drawable.lumi),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            Surface(
                color = containerColor,
                shape = bubbleShape,
                shadowElevation = if(isUser) 2.dp else 1.dp,
                border = if(!isUser) BorderStroke(1.dp, (if(isDark) Color.White.copy(0.1f) else Color.Black.copy(0.05f))) else null
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = message.text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = textColor,
                        lineHeight = 22.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun SuggestionChip(text: String, isDark: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        color = if (isDark) Color.White.copy(0.05f) else Color.White,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, if (isDark) Color.White.copy(0.1f) else Color(0xFFE2E8F0))
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            style = MaterialTheme.typography.labelMedium,
            color = if (isDark) Color.White.copy(0.8f) else Color(0xFF64748B),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun LumiInputField(value: String, onValueChange: (String) -> Unit, onSend: () -> Unit, isDark: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text("Escribe algo, te escucho...") },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(28.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = LumiViolet,
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = if (isDark) Color.White.copy(0.05f) else Color(0xFFF1F5F9),
                unfocusedContainerColor = if (isDark) Color.White.copy(0.05f) else Color(0xFFF1F5F9)
            ),
            maxLines = 4
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(if (value.isBlank()) SolidColor(Color.Gray.copy(alpha = 0.3f)) else LumiGradient)
                .clickable(enabled = value.isNotBlank()) { onSend() },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.AutoMirrored.Outlined.Send, null, tint = Color.White)
        }
    }
}

@Composable
private fun TypingIndicator(isDark: Boolean) {
    val transition = rememberInfiniteTransition(label = "typing")
    val anims = (0..2).map { index ->
        transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(600, delayMillis = index * 200, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "dot$index"
        )
    }

    Row(
        modifier = Modifier
            .padding(start = 36.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(if (isDark) Color.White.copy(0.05f) else Color.White)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        anims.forEach { anim ->
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .graphicsLayer { alpha = anim.value }
                    .clip(CircleShape)
                    .background(LumiViolet)
            )
        }
    }
}

@Composable
private fun CrisisBanner() {
    val context = LocalContext.current
    Surface(
        color = Color(0xFFFFF1F2),
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, Color(0xFFFDA4AF).copy(0.3f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.HeartBroken, null, tint = Color(0xFFE11D48))
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "Lumi está preocupado por ti",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color(0xFF9F1239),
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                "Detectamos que podrías estar pasando un momento difícil. No estás solo.",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFBE123C),
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
            )
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:080000015"))
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE11D48)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Outlined.Phone, null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Llamar Ayuda Profesional")
            }
        }
    }
}


