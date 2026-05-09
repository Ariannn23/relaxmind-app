package com.upn.relaxmind.feature.progress.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.upn.relaxmind.core.ui.theme.RelaxMutedText

@Composable
fun ProgressChart(
    selectedRange: String,
    onRangeSelected: (String) -> Unit,
    isDark: Boolean,
    accentColor: Color
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Bienestar Emocional", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (isDark) Color.White.copy(0.05f) else Color(0xFFF1F5F9))
                    .padding(2.dp)
            ) {
                listOf("7 días", "1 mes", "3 meses").forEach { range ->
                    val isSelected = selectedRange == range
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (isSelected) (if(isDark) Color.White.copy(0.15f) else Color.White) else Color.Transparent)
                            .clickable { onRangeSelected(range) }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            range, 
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) (if(isDark) Color.White else Color.Black) else RelaxMutedText,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Box(modifier = Modifier.fillMaxWidth().height(160.dp)) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val points = listOf(0.4f, 0.7f, 0.3f, 0.8f, 0.5f, 0.9f, 0.6f)
                    val step = size.width / (points.size - 1)
                    val barWidth = 12.dp.toPx()
                    
                    points.forEachIndexed { i, p ->
                        val x = i * step
                        val h = p * size.height
                        drawRoundRect(
                            color = accentColor.copy(0.15f),
                            topLeft = Offset(x - barWidth/2, size.height - h),
                            size = Size(barWidth, h),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
                        )
                    }
                    
                    val path = Path()
                    points.forEachIndexed { i, p ->
                        val x = i * step
                        val y = size.height - (p * size.height)
                        if (i == 0) path.moveTo(x, y) else {
                            val prevX = (i - 1) * step
                            val prevY = size.height - (points[i - 1] * size.height)
                            path.cubicTo(
                                prevX + step/2, prevY,
                                x - step/2, y,
                                x, y
                            )
                        }
                    }
                    drawPath(path, accentColor, style = Stroke(3.dp.toPx(), cap = StrokeCap.Round))
                    points.forEachIndexed { i, p ->
                        drawCircle(accentColor, 4.dp.toPx(), Offset(i * step, size.height - (p * size.height)))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatSmallCard(
                    modifier = Modifier.weight(1f),
                    label = "Frecuente",
                    value = "😊 Feliz",
                    isDark = isDark
                )
                StatSmallCard(
                    modifier = Modifier.weight(1f),
                    label = "Mejor Día",
                    value = "Sábado",
                    icon = Icons.Default.Star,
                    isDark = isDark
                )
            }
        }
    }
}

@Composable
private fun StatSmallCard(modifier: Modifier, label: String, value: String, icon: ImageVector? = null, isDark: Boolean) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isDark) Color.White.copy(0.03f) else Color(0xFFF8FAFC))
            .padding(12.dp)
    ) {
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = RelaxMutedText)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                if (icon != null) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(icon, null, modifier = Modifier.size(12.dp), tint = Color(0xFFF59E0B))
                }
            }
        }
    }
}
