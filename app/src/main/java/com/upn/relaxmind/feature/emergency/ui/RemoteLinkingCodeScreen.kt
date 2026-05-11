package com.upn.relaxmind.feature.emergency.ui

import android.content.ClipboardManager
import android.content.ClipData
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upn.relaxmind.core.data.auth.AuthManager
import com.upn.relaxmind.core.ui.components.RelaxBackButton
import com.upn.relaxmind.core.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemoteLinkingCodeScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val user = remember { AuthManager.getCurrentUser(context) }
    var code by remember { mutableStateOf("") }
    var timeLeft by remember { mutableStateOf(120) }
    var isExpired by remember { mutableStateOf(false) }

    fun refreshCode() {
        scope.launch {
            if (user != null) {
                code = AuthManager.generateTempCode(context)
                timeLeft = 120
                isExpired = false
            }
        }
    }

    LaunchedEffect(Unit) {
        refreshCode()
    }

    LaunchedEffect(timeLeft) {
        if (timeLeft > 0) {
            delay(1000)
            timeLeft--
        } else {
            isExpired = true
        }
    }

    fun copyToClipboard(text: String) {
        val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("RelaxMind Code", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Código copiado al portapapeles", Toast.LENGTH_SHORT).show()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Vinculación Segura", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    Box(modifier = Modifier.padding(start = 12.dp)) {
                        RelaxBackButton(onClick = onBack, modifier = Modifier.size(40.dp))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Código de Enlace",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = Color(0xFF1E293B)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Dicta este código a tu cuidador o tócalo para copiarlo y enviarlo por chat.",
                style = MaterialTheme.typography.bodyLarge,
                color = RelaxMutedText,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 48.dp)
            )

            // Code Display Card (Interactive)
            Surface(
                onClick = { if (!isExpired) copyToClipboard(code) },
                color = Color.Transparent,
                shape = RoundedCornerShape(32.dp),
                shadowElevation = 12.dp,
                modifier = Modifier.fillMaxWidth().height(160.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            if (isExpired) {
                                Brush.linearGradient(listOf(Color(0xFF94A3B8), Color(0xFF64748B)))
                            } else {
                                Brush.linearGradient(listOf(CaregiverPurple, Color(0xFF6366F1)))
                            }
                        )
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (isExpired) "------" else code,
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            letterSpacing = 10.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        if (!isExpired) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.ContentCopy, null, tint = Color.White.copy(0.8f), modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "TOCA PARA COPIAR",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White.copy(0.8f),
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Timer UI
            Surface(
                color = if (isExpired) Color(0xFFFEE2E2) else Color(0xFFF1F5F9),
                shape = CircleShape,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        progress = timeLeft / 120f,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 3.dp,
                        color = if (isExpired) Color(0xFFEF4444) else RelaxGreen,
                        trackColor = Color.LightGray.copy(0.2f)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (isExpired) "CÓDIGO VENCIDO" else "VENCE EN ${timeLeft}s",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Black,
                        color = if (isExpired) Color(0xFFEF4444) else Color(0xFF475569)
                    )
                }
            }

            Spacer(modifier = Modifier.height(64.dp))

            Button(
                onClick = { refreshCode() },
                modifier = Modifier.fillMaxWidth().height(60.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isExpired) RelaxGreen else Color(0xFFE2E8F0),
                    contentColor = if (isExpired) Color.White else Color(0xFF475569)
                )
            ) {
                Icon(Icons.Outlined.Refresh, null)
                Spacer(modifier = Modifier.width(10.dp))
                Text("Generar nuevo código", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            
            TextButton(
                onClick = onBack,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Volver al perfil", color = RelaxMutedText, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
