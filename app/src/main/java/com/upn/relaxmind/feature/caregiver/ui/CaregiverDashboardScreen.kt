package com.upn.relaxmind.feature.caregiver.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upn.relaxmind.core.data.auth.AuthManager
import com.upn.relaxmind.core.data.models.User
import com.upn.relaxmind.core.ui.components.UserAvatar
import com.upn.relaxmind.core.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaregiverDashboardScreen(
    onPatientClick: (String) -> Unit,
    onOpenLink: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenNotifications: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val currentUser = remember { AuthManager.getCurrentUser(context) }
    val linkedPatients = remember { AuthManager.getLinkedUsers(context) }
    val isDark = LocalIsDarkTheme.current
    
    val bgColor = if (isDark) Color(0xFF0F172A) else CaregiverBg
    val surfaceColor = if (isDark) Color(0xFF1E293B) else Color.White
    val textColor = if (isDark) Color.White else Color(0xFF1E293B)
    val mutedTextColor = if (isDark) Color.LightGray.copy(0.7f) else Color.Gray

    Scaffold(
        containerColor = bgColor,
        topBar = {
            CaregiverHeader(
                caregiverName = currentUser?.name ?: "Cuidador",
                onSettingsClick = onOpenSettings,
                onNotificationsClick = onOpenNotifications,
                textColor = textColor,
                isDark = isDark
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onOpenLink,
                containerColor = CaregiverBlue,
                contentColor = Color.White,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .padding(bottom = 16.dp, end = 8.dp)
                    .shadow(12.dp, RoundedCornerShape(20.dp), spotColor = CaregiverBlue)
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = "Vincular Paciente")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Summary Stats (3D modern style)
            item {
                SummaryStatsSection(linkedPatients.size, surfaceColor, textColor, isDark)
            }

            // Quick Actions Section
            item {
                Text(
                    text = "Acciones Rápidas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = textColor,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    item { QuickActionItem("Nuevo Reporte", Icons.Outlined.AddChart, Color(0xFF8B5CF6), surfaceColor, textColor, {}) }
                    item { 
                        QuickActionItem("Llamar SOS", Icons.Outlined.PhoneInTalk, Color(0xFFEF4444), surfaceColor, textColor) {
                            val firstPatient = linkedPatients.firstOrNull()
                            if (firstPatient != null && firstPatient.phoneNumber.isNotEmpty()) {
                                val intent = android.content.Intent(android.content.Intent.ACTION_DIAL, android.net.Uri.parse("tel:${firstPatient.phoneNumber}"))
                                context.startActivity(intent)
                            } else {
                                val intent = android.content.Intent(android.content.Intent.ACTION_DIAL)
                                context.startActivity(intent)
                            }
                        } 
                    }
                    item { QuickActionItem("Enviar Ánimos", Icons.Outlined.EmojiEmotions, Color(0xFFF59E0B), surfaceColor, textColor, {}) }
                }
            }

            item {
                Text(
                    text = "Tus Pacientes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = textColor,
                    letterSpacing = 0.5.sp
                )
            }

            if (linkedPatients.isEmpty()) {
                item {
                    EmptyPatientsState(onOpenLink, surfaceColor, textColor, mutedTextColor)
                }
            } else {
                items(linkedPatients) { patient ->
                    PatientCard(
                        patient = patient,
                        onClick = { onPatientClick(patient.id) },
                        surfaceColor = surfaceColor,
                        textColor = textColor,
                        mutedTextColor = mutedTextColor,
                        isDark = isDark
                    )
                }
            }

            item {
                Text(
                    text = "Alertas de Bienestar",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = textColor,
                    letterSpacing = 0.5.sp
                )
            }
            
            item {
                RecentAlertsSection(surfaceColor, textColor, mutedTextColor)
            }
            
            item { Spacer(modifier = Modifier.height(40.dp)) }
        }
    }
}

@Composable
private fun SummaryStatsSection(patientCount: Int, surfaceColor: Color, textColor: Color, isDark: Boolean) {
    Surface(
        shape = RoundedCornerShape(32.dp),
        color = surfaceColor,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isDark) 0.dp else 16.dp,
                shape = RoundedCornerShape(32.dp),
                spotColor = CaregiverBlue.copy(0.2f)
            )
    ) {
        Row(
            modifier = Modifier
                .background(
                    Brush.horizontalGradient(
                        if (isDark) listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                        else listOf(Color.White, Color(0xFFF1F5F9))
                    )
                )
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Red de Apoyo",
                    style = MaterialTheme.typography.labelMedium,
                    color = CaregiverBlue,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$patientCount Pacientes",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = textColor
                )
                Text(
                    text = "Todo bajo control hoy ✓",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF10B981),
                    fontWeight = FontWeight.Medium
                )
            }
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(CaregiverBlue.copy(0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Analytics, null, tint = CaregiverBlue, modifier = Modifier.size(32.dp))
            }
        }
    }
}

@Composable
private fun QuickActionItem(title: String, icon: ImageVector, color: Color, surfaceColor: Color, textColor: Color, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        color = surfaceColor,
        modifier = Modifier.width(130.dp),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(color.copy(0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = textColor,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
private fun CaregiverHeader(
    caregiverName: String,
    onSettingsClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    textColor: Color,
    isDark: Boolean
) {
    val firstName = caregiverName.split(" ").firstOrNull() ?: caregiverName
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(start = 24.dp, top = 16.dp, end = 24.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "Hola, $firstName \uD83D\uDC4B",
                style = MaterialTheme.typography.titleMedium,
                color = if (isDark) Color.LightGray else Color.Gray,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Centro de Control",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = textColor
            )
        }
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onNotificationsClick,
                modifier = Modifier
                    .background(if (isDark) Color(0xFF334155) else Color.White, CircleShape)
                    .size(44.dp)
            ) {
                Icon(Icons.Outlined.Notifications, "Notificaciones", tint = CaregiverBlue)
            }
            Spacer(modifier = Modifier.width(12.dp))
            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier
                    .background(if (isDark) Color(0xFF334155) else Color.White, CircleShape)
                    .size(44.dp)
            ) {
                Icon(Icons.Outlined.Settings, "Ajustes", tint = CaregiverBlue)
            }
        }
    }
}

@Composable
private fun PatientCard(
    patient: User, 
    onClick: () -> Unit, 
    surfaceColor: Color, 
    textColor: Color, 
    mutedTextColor: Color,
    isDark: Boolean
) {
    val score = patient.wellnessScore
    val statusColor = when {
        score >= 70 -> RelaxGreen
        score >= 40 -> Color(0xFFEAB308) 
        else -> Color(0xFFEF4444)
    }

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(28.dp),
        color = surfaceColor,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isDark) 0.dp else 8.dp,
                shape = RoundedCornerShape(28.dp),
                spotColor = Color.Black.copy(0.1f)
            )
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                UserAvatar(user = patient, size = 64, fontSize = 26)
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .background(statusColor, CircleShape)
                        .shadow(4.dp, CircleShape)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${patient.name} ${patient.lastName}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Bienestar: $score%",
                        style = MaterialTheme.typography.bodySmall,
                        color = statusColor,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "• Hace 10m",
                        style = MaterialTheme.typography.labelSmall,
                        color = mutedTextColor
                    )
                }
                
                Spacer(modifier = Modifier.height(10.dp))
                
                // Wellness Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape)
                        .background(if (isDark) Color(0xFF334155) else CaregiverLightBlue)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(score / 100f)
                            .fillMaxHeight()
                            .background(Brush.horizontalGradient(listOf(statusColor.copy(0.7f), statusColor)))
                    )
                }
            }
            
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = mutedTextColor.copy(alpha = 0.3f),
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun EmptyPatientsState(onOpenLink: () -> Unit, surfaceColor: Color, textColor: Color, mutedTextColor: Color) {
    Surface(
        shape = RoundedCornerShape(28.dp),
        color = surfaceColor,
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(CaregiverBlue.copy(0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.PersonSearch,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = CaregiverBlue
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Comienza tu red",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Text(
                text = "Vincula a un paciente para monitorear su estado",
                style = MaterialTheme.typography.bodySmall,
                color = mutedTextColor,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onOpenLink,
                colors = ButtonDefaults.buttonColors(containerColor = CaregiverBlue),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Vincular Paciente", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun RecentAlertsSection(surfaceColor: Color, textColor: Color, mutedTextColor: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        AlertItem(
            title = "Activación SOS Crítica",
            patient = "Juan Pérez",
            time = "Hace 2 horas",
            icon = Icons.Default.Warning,
            color = Color(0xFFEF4444),
            surfaceColor = surfaceColor,
            textColor = textColor,
            mutedTextColor = mutedTextColor
        )
        AlertItem(
            title = "Tendencia de Bienestar Baja",
            patient = "Maria Garcia",
            time = "Hace 5 horas",
            icon = Icons.Default.TrendingDown,
            color = Color(0xFFF59E0B),
            surfaceColor = surfaceColor,
            textColor = textColor,
            mutedTextColor = mutedTextColor
        )
    }
}

@Composable
private fun AlertItem(
    title: String, 
    patient: String, 
    time: String, 
    icon: ImageVector, 
    color: Color,
    surfaceColor: Color,
    textColor: Color,
    mutedTextColor: Color
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = surfaceColor,
        modifier = Modifier.fillMaxWidth(),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(0.2f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(color.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = textColor)
                Text(text = "$patient \u2022 $time", fontSize = 12.sp, color = mutedTextColor)
            }
        }
    }
}
