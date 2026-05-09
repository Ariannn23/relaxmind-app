package com.upn.relaxmind.feature.dashboard.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.upn.relaxmind.core.ui.theme.*
import java.time.LocalDate

@Composable
fun NextReminderCard() {
    val gradient = Brush.linearGradient(listOf(Color(0xFFB45309), Color(0xFFF59E0B)))
    val softBorderGradient = Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.4f), Color(0xFFB45309).copy(alpha = 0.12f)))
    val cardBg = getSoftAmber()
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(24.dp), spotColor = Color(0x2AB45309))
            .clip(RoundedCornerShape(24.dp))
            .background(cardBg)
            .border(1.dp, softBorderGradient, RoundedCornerShape(24.dp))
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = com.upn.relaxmind.R.drawable.notificaciones),
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Próximo recordatorio",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Toma de medicación - 8:00 PM",
                    style = MaterialTheme.typography.titleMedium.copy(brush = gradient),
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Composable
fun StreakCalendarCard(streak: Int, onClick: () -> Unit) {
    val today = LocalDate.now()
    val startOfWeek = today.minusDays(today.dayOfWeek.value % 7L)
    val streakStartDay = today.minusDays((streak - 1).coerceAtLeast(0).toLong())
    val dayLabels = listOf("D", "L", "M", "M", "J", "V", "S")

    val gradient = Brush.linearGradient(listOf(Color(0xFFEA580C), Color(0xFFFDBA74)))
    val softBorderGradient = Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.4f), Color(0xFFEA580C).copy(alpha = 0.12f)))
    val cardBg = getSoftOrange()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(24.dp), spotColor = Color(0x2AEA580C))
            .clip(RoundedCornerShape(24.dp))
            .background(cardBg)
            .clickable { onClick() }
            .border(1.dp, softBorderGradient, RoundedCornerShape(24.dp))
            .padding(20.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tu racha semanal",
                    style = MaterialTheme.typography.titleMedium.copy(brush = gradient),
                    fontWeight = FontWeight.Bold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = com.upn.relaxmind.R.drawable.racha),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(
                        text = "$streak días",
                        style = MaterialTheme.typography.titleMedium.copy(brush = gradient),
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for (i in 0..6) {
                    val day = startOfWeek.plusDays(i.toLong())
                    val isToday = day == today
                    val isStreak = !day.isAfter(today) && !day.isBefore(streakStartDay)
                    val isFuture = day.isAfter(today)

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = dayLabels[i],
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Gray,
                            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                        )
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isToday -> Brush.verticalGradient(listOf(Color(0xFFFFB347), Color(0xFFFB923C)))
                                        isStreak -> Brush.verticalGradient(listOf(Color(0xFFFB923C).copy(alpha=0.22f), Color(0xFFFDBA74).copy(alpha=0.12f)))
                                        else -> Brush.verticalGradient(listOf(Color.LightGray.copy(0.3f), Color.LightGray.copy(0.1f)))
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isStreak && !isFuture) {
                                Icon(Icons.Outlined.LocalFireDepartment, null, tint = if (isToday) Color.White else Color(0xFFEA580C), modifier = Modifier.size(24.dp))
                            } else {
                                Text(
                                    text = day.dayOfMonth.toString(),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (isFuture) Color.LightGray else Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardShortcutsRow(onOpenDiary: () -> Unit, onOpenLibrary: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Biblioteca card
        val libraryGradient = Brush.linearGradient(listOf(Color(0xFF3B82F6), Color(0xFF93C5FD)))
        Box(
            modifier = Modifier
                .weight(1f)
                .shadow(8.dp, RoundedCornerShape(20.dp), spotColor = Color(0x2A3B82F6))
                .clip(RoundedCornerShape(20.dp))
                .background(getSoftBlue())
                .border(1.dp, Brush.verticalGradient(listOf(Color.White.copy(0.4f), Color(0xFF3B82F6).copy(alpha = 0.12f))), RoundedCornerShape(20.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onOpenLibrary() }
                .padding(16.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Image(
                    painter = painterResource(id = com.upn.relaxmind.R.drawable.bibliteca),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Biblioteca",
                    style = MaterialTheme.typography.labelMedium.copy(brush = libraryGradient),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Artículos y consejos",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }
        // Diario personal card
        val diaryGradient = Brush.linearGradient(listOf(Color(0xFF8B5CF6), Color(0xFFC4B5FD)))
        Box(
            modifier = Modifier
                .weight(1f)
                .shadow(8.dp, RoundedCornerShape(20.dp), spotColor = Color(0x2A8B5CF6))
                .clip(RoundedCornerShape(20.dp))
                .background(getSoftPurple())
                .border(1.dp, Brush.verticalGradient(listOf(Color.White.copy(0.4f), Color(0xFF8B5CF6).copy(alpha = 0.12f))), RoundedCornerShape(20.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onOpenDiary() }
                .padding(16.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Image(
                    painter = painterResource(id = com.upn.relaxmind.R.drawable.diario),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Diario",
                    style = MaterialTheme.typography.labelMedium.copy(brush = diaryGradient),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Diario personal",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
