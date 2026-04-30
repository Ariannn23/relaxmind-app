package com.upn.relaxmind.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.EnergySavingsLeaf
import androidx.compose.material.icons.outlined.GraphicEq
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material.icons.outlined.Waves
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upn.relaxmind.ui.components.RelaxBackButton

@Composable
fun SoundsScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {}
) {
    val bgColors = listOf(Color(0xFF7C3AED).copy(alpha = 0.05f), MaterialTheme.colorScheme.background)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(bgColors))
            .systemBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RelaxBackButton(onClick = onBack, modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("Sonidos Relajantes", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Música para tu mente", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { SoundCard("Naturaleza", "Bosques y trinos", Icons.Outlined.EnergySavingsLeaf, Color(0xFF10B981)) }
            item { SoundCard("Lluvia", "Tormentas suaves", Icons.Outlined.WaterDrop, Color(0xFF3B82F6)) }
            item { SoundCard("Océano", "Olas de playa", Icons.Outlined.Waves, Color(0xFF0EA5E9)) }
            item { SoundCard("Frecuencias", "Binaural 432Hz", Icons.Outlined.GraphicEq, Color(0xFF8B5CF6)) }
            item { SoundCard("Meditación", "Cuencos tibetanos", Icons.Outlined.SelfImprovement, Color(0xFFF59E0B)) }
            item { Spacer(Modifier.height(40.dp)) }
        }
    }
}

@Composable
private fun SoundCard(title: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector, accentColor: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(20.dp), spotColor = accentColor.copy(0.3f))
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(listOf(MaterialTheme.colorScheme.surface, accentColor.copy(alpha = 0.08f)))
            )
            .clickable { /* TBD: Play sound */ }
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(52.dp).clip(CircleShape).background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = accentColor, modifier = Modifier.size(28.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Surface(
                shape = CircleShape,
                color = accentColor,
                modifier = Modifier.size(44.dp)
            ) {
                Icon(Icons.Filled.PlayArrow, null, tint = Color.White, modifier = Modifier.padding(8.dp))
            }
        }
    }
}
