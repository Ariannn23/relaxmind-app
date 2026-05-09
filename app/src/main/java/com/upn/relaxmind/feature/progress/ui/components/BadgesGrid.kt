package com.upn.relaxmind.feature.progress.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.upn.relaxmind.core.ui.theme.RelaxMutedText

@Composable
fun BadgesGrid(isDark: Boolean, accent: Color) {
    Column {
        Text("Logros Alcanzados", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            item { AchievementItem("Maestro Zen", Icons.Default.SelfImprovement, Color(0xFF8B5CF6), true) }
            item { AchievementItem("7 Días", Icons.Outlined.LocalFireDepartment, Color(0xFFFF8A5C), true) }
            item { AchievementItem("Explorador", Icons.Default.Map, Color(0xFF10B981), false) }
            item { AchievementItem("Noctámbulo", Icons.Default.NightsStay, Color(0xFF2563EB), false) }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(0.1f))
        ) {
            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Lock, null, modifier = Modifier.size(16.dp), tint = RelaxMutedText)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Siguiente: Disciplinado (7 días)", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { 0.42f },
                        modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape),
                        color = accent,
                        trackColor = accent.copy(0.1f)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text("3/7", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = accent)
            }
        }
    }
}

@Composable
private fun AchievementItem(name: String, icon: ImageVector, color: Color, isUnlocked: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(80.dp)) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(if (isUnlocked) color.copy(0.15f) else MaterialTheme.colorScheme.surfaceVariant)
                .border(2.dp, if (isUnlocked) color.copy(0.3f) else Color.Transparent, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isUnlocked) icon else Icons.Default.Lock,
                contentDescription = null,
                tint = if (isUnlocked) color else RelaxMutedText.copy(0.4f),
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(name, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold, color = if (isUnlocked) MaterialTheme.colorScheme.onSurface else RelaxMutedText)
    }
}
