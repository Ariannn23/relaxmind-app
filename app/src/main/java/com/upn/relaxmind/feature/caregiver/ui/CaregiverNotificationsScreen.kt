package com.upn.relaxmind.feature.caregiver.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upn.relaxmind.core.ui.theme.*
import com.upn.relaxmind.core.ui.components.RelaxBackButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaregiverNotificationsScreen(
    onBack: () -> Unit
) {
    val notifications = remember {
        listOf(
            NotificationData("Alerta SOS", "Juan Pérez ha activado el botón de pánico.", "10:30 AM", Icons.Outlined.Warning, Color(0xFFEF4444)),
            NotificationData("Bienestar Bajo", "El score de Maria Garcia ha bajado a 35%.", "09:15 AM", Icons.Outlined.TrendingDown, Color(0xFFEAB308)),
            NotificationData("Recordatorio", "Cita médica de Juan Pérez hoy a las 4:00 PM.", "Ayer", Icons.Outlined.Event, CaregiverBlue),
            NotificationData("Nuevo Paciente", "Viculación exitosa con Maria Garcia.", "Ayer", Icons.Outlined.PersonAdd, RelaxGreen)
        )
    }

    Scaffold(
        containerColor = CaregiverBg,
        topBar = {
            TopAppBar(
                title = { Text("Alertas y Notificaciones", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    Box(modifier = Modifier.padding(start = 12.dp)) {
                        RelaxBackButton(onClick = onBack, modifier = Modifier.size(40.dp))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CaregiverBg)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recientes",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = { /* Mark all as read */ }) {
                        Text("Limpiar todo", color =    CaregiverBlue, fontSize = 12.sp)
                    }
                }
            }

            items(notifications) { notification ->
                NotificationItem(notification)
            }
        }
    }
}

data class NotificationData(
    val title: String,
    val message: String,
    val time: String,
    val icon: ImageVector,
    val color: Color
)

@Composable
private fun NotificationItem(data: NotificationData) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(data.color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(data.icon, null, tint = data.color, modifier = Modifier.size(24.dp))
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = data.title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = data.time,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
                Text(
                    text = data.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
