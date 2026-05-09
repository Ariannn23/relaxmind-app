package com.upn.relaxmind.feature.progress.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
import androidx.compose.material.icons.filled.SentimentNeutral
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.SentimentVerySatisfied
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.upn.relaxmind.core.ui.theme.*

@Composable
fun ProgressHistorySection() {
    Column {
        Text(
            text = "Historial de Bienestar",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            CheckInHistoryItem(
                date = "Hoy, 10:30 AM",
                mood = "Excelente",
                score = 92,
                color = RelaxGreen,
                bgColor = getSoftGreen(),
                icon = Icons.Default.SentimentVerySatisfied
            )
            CheckInHistoryItem(
                date = "Ayer, 09:15 AM",
                mood = "Bien",
                score = 78,
                color = Color(0xFF3B82F6),
                bgColor = getSoftBlue(),
                icon = Icons.Default.SentimentSatisfied
            )
            CheckInHistoryItem(
                date = "27 Oct, 08:45 PM",
                mood = "Neutral",
                score = 65,
                color = Color(0xFFF59E0B),
                bgColor = getSoftYellow(),
                icon = Icons.Default.SentimentNeutral
            )
        }
    }
}

@Composable
private fun CheckInHistoryItem(
    date: String,
    mood: String,
    score: Int,
    color: Color,
    bgColor: Color,
    icon: ImageVector
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(20.dp), spotColor = color.copy(0.15f)),
        shape = RoundedCornerShape(20.dp),
        color = bgColor,
        border = BorderStroke(1.dp, Brush.verticalGradient(listOf(Color.White.copy(0.4f), color.copy(0.1f))))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color.copy(0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = mood,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = date,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$score",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = color
                )
                Text("PTS", style = MaterialTheme.typography.labelSmall, color = color.copy(0.6f))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowForwardIos,
                contentDescription = null,
                tint = color.copy(0.3f),
                modifier = Modifier.size(14.dp)
            )
        }
    }
}
