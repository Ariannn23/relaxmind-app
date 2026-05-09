package com.upn.relaxmind.feature.progress.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.SentimentVerySatisfied
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upn.relaxmind.core.ui.theme.RelaxGreen
import com.upn.relaxmind.core.ui.theme.RelaxMutedText

@Composable
fun StreakCalendar(
    selectedMonth: String,
    onMonthSelected: (String) -> Unit,
    isDark: Boolean
) {
    var showMonthPicker by remember { mutableStateOf(false) }
    val months = listOf("Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio")
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Tu Mes Emocional", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
                
                Box {
                    TextButton(onClick = { showMonthPicker = true }) {
                        Text(selectedMonth, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        Icon(Icons.Default.ArrowDropDown, null, modifier = Modifier.size(16.dp))
                    }
                    DropdownMenu(expanded = showMonthPicker, onDismissRequest = { showMonthPicker = false }) {
                        months.forEach { m ->
                            DropdownMenuItem(
                                text = { Text(m) },
                                onClick = { onMonthSelected(m); showMonthPicker = false }
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("L", "M", "X", "J", "V", "S", "D").forEach { day ->
                    Text(
                        day, 
                        modifier = Modifier.weight(1f), 
                        textAlign = TextAlign.Center, 
                        style = MaterialTheme.typography.labelSmall,
                        color = RelaxMutedText
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            var selectedDayDetail by remember { mutableStateOf<Int?>(null) }
            
            if (selectedDayDetail != null) {
                CheckInDetailDialog(day = selectedDayDetail!!, onDismiss = { selectedDayDetail = null })
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                for (week in 0..4) { 
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (dayOfWeek in 0..6) {
                            val i = week * 7 + dayOfWeek
                            if (i < 30) {
                                val color = when {
                                    i % 7 == 0 -> Color(0xFF93C5FD) 
                                    i % 5 == 0 -> Color(0xFFC4B5FD) 
                                    i % 4 == 0 -> Color(0xFF6EE7B7) 
                                    i % 3 == 0 -> Color(0xFFFDBA74) 
                                    i % 2 == 0 -> Color(0xFFFDE047) 
                                    else -> (if(isDark) Color.White.copy(0.05f) else Color(0xFFF1F5F9))
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(color)
                                        .clickable { selectedDayDetail = i + 1 },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "${i + 1}", 
                                        style = MaterialTheme.typography.labelSmall, 
                                        fontSize = 10.sp, 
                                        fontWeight = FontWeight.Bold,
                                        color = if(isDark) Color.White.copy(0.6f) else Color.Black.copy(0.4f)
                                    )
                                }
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    LegendItem(Color(0xFF93C5FD), "Muy Mal")
                    LegendItem(Color(0xFFC4B5FD), "Mal")
                    LegendItem(Color(0xFF6EE7B7), "Neutral")
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    LegendItem(Color(0xFFFDBA74), "Bien")
                    LegendItem(Color(0xFFFDE047), "Muy Bien")
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun CheckInDetailDialog(
    day: Int,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RelaxGreen)
            ) { 
                Text("Cerrar", fontWeight = FontWeight.Bold) 
            }
        },
        title = {
            Text("Detalle del día $day", fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleLarge)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                DetailRow(Icons.Default.SentimentVerySatisfied, "Bienestar General", "Excelente (92%)", RelaxGreen)
                DetailRow(Icons.Default.NightsStay, "Calidad de Sueño", "Muy Bueno (8h)", Color(0xFF6366F1))
                DetailRow(Icons.Default.Healing, "Molestias", "Ninguna reportada", Color.Gray)
                DetailRow(Icons.Default.AutoAwesome, "Medicación", "Dosis completada", Color(0xFF10B981))
            }
        },
        shape = RoundedCornerShape(32.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}

@Composable
private fun DetailRow(icon: ImageVector, label: String, value: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(44.dp).clip(CircleShape).background(color.copy(0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = RelaxMutedText)
            Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
        Spacer(modifier = Modifier.width(6.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = RelaxMutedText)
    }
}
