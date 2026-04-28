package com.upn.relaxmind.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Article
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upn.relaxmind.ui.theme.RelaxBackground
import com.upn.relaxmind.ui.theme.RelaxMutedText

private val PastelBlue = Color(0xFF93C5FD)
private val DarkBlueText = Color(0xFF1E3A8A)

data class LibraryItem(
    val id: Int,
    val title: String,
    val description: String,
    val duration: String,
    val isVideo: Boolean
)

@Composable
fun LibraryScreen(modifier: Modifier = Modifier, onBack: () -> Unit) {
    val items = listOf(
        LibraryItem(1, "Entendiendo la ansiedad", "Aprende qué sucede en tu cuerpo durante un episodio.", "5 min lectura", false),
        LibraryItem(2, "Guía de meditación para principiantes", "Conceptos básicos para iniciar tu práctica diaria.", "10 min video", true),
        LibraryItem(3, "Importancia de la rutina de sueño", "Cómo el descanso afecta directamente tu bienestar.", "4 min lectura", false),
        LibraryItem(4, "Técnicas de grounding (arraigo)", "Ejercicios rápidos para volver al momento presente.", "7 min lectura", false)
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
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
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Biblioteca",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Recursos y aprendizaje",
                        style = MaterialTheme.typography.bodyMedium,
                        color = RelaxMutedText
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            LazyColumn(
                contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(items) { item ->
                    LibraryCard(item)
                }
            }
        }
    }
}

@Composable
private fun LibraryCard(item: LibraryItem) {
    val icon: ImageVector = if (item.isVideo) Icons.Outlined.PlayCircle else Icons.Outlined.Article

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(16.dp), spotColor = PastelBlue.copy(0.3f))
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable { /* TODO: Open article/video */ }
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (com.upn.relaxmind.ui.theme.LocalIsDarkTheme.current) Color(0xFF3B82F6).copy(alpha = 0.25f) else PastelBlue.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon, 
                    contentDescription = null, 
                    tint = if (com.upn.relaxmind.ui.theme.LocalIsDarkTheme.current) Color(0xFF93C5FD) else DarkBlueText, 
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = RelaxMutedText
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = item.duration,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (com.upn.relaxmind.ui.theme.LocalIsDarkTheme.current) Color(0xFF93C5FD) else DarkBlueText,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = RelaxMutedText.copy(alpha = 0.5f)
            )
        }
    }
}
