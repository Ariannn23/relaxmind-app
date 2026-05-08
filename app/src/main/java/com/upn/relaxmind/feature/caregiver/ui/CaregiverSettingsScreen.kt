package com.upn.relaxmind.feature.caregiver.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import com.upn.relaxmind.core.ui.components.RelaxBackButton
import com.upn.relaxmind.core.ui.components.UserAvatar
import com.upn.relaxmind.core.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaregiverSettingsScreen(
    onBack: () -> Unit,
    onEditProfile: () -> Unit,
    onManageLinks: () -> Unit,
    onToggleDarkTheme: (Boolean) -> Unit = {},
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val user = remember { AuthManager.getCurrentUser(context) }
    val isDark = LocalIsDarkTheme.current

    val surfaceColor = if (isDark) Color(0xFF1E293B) else Color.White
    val bgColor = if (isDark) Color(0xFF0F172A) else CaregiverBg
    val textColor = if (isDark) Color.White else Color(0xFF1E293B)
    val mutedTextColor = if (isDark) Color.LightGray.copy(0.7f) else Color.Gray

    Scaffold(
        containerColor = bgColor,
        topBar = {
            TopAppBar(
                title = { Text("Configuración", fontWeight = FontWeight.Bold, color = textColor) },
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
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            // Profile Card with 3D Effect
            Surface(
                shape = RoundedCornerShape(32.dp),
                color = surfaceColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = if (isDark) 0.dp else 12.dp,
                        shape = RoundedCornerShape(32.dp),
                        spotColor = CaregiverBlue.copy(alpha = 0.2f)
                    )
                    .clickable { onEditProfile() }
            ) {
                Row(
                    modifier = Modifier.padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(contentAlignment = Alignment.BottomEnd) {
                        UserAvatar(user = user, size = 72, fontSize = 28)
                        Surface(
                            modifier = Modifier
                                .size(28.dp)
                                .offset(x = 4.dp, y = 4.dp)
                                .shadow(6.dp, CircleShape),
                            shape = CircleShape,
                            color = CaregiverBlue
                        ) {
                            Icon(
                                Icons.Outlined.Edit,
                                null,
                                tint = Color.White,
                                modifier = Modifier.padding(6.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    Column {
                        Text(
                            text = user?.name ?: "Cuidador",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                        Text(
                            text = user?.professionalRole ?: "Especialista en Salud",
                            style = MaterialTheme.typography.bodySmall,
                            color = CaregiverBlue,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = user?.email ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = mutedTextColor
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(Icons.Outlined.ChevronRight, null, tint = mutedTextColor.copy(0.5f))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Appearance Section
            SettingsSectionTitle("APARIENCIA", mutedTextColor)
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = surfaceColor,
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SettingsIconBox(Icons.Outlined.DarkMode, if (isDark) Color(0xFF818CF8) else Color(0xFFF59E0B))
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Modo Oscuro", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = textColor)
                    Spacer(modifier = Modifier.weight(1f))
                    Switch(
                        checked = isDark,
                        onCheckedChange = onToggleDarkTheme,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = CaregiverBlue,
                            uncheckedThumbColor = Color.Gray,
                            uncheckedTrackColor = Color.LightGray.copy(0.3f)
                        )
                    )
                }
            }

            // Patients Section
            SettingsSectionTitle("GESTIÓN DE PACIENTES", mutedTextColor)
            SettingsGroup(surfaceColor) {
                SettingsItem(
                    "Mis Pacientes Vinculados", 
                    "Gestiona quiénes pueden compartir datos contigo",
                    Icons.Outlined.Group, 
                    CaregiverBlue, 
                    textColor, 
                    mutedTextColor,
                    onClick = onManageLinks
                )
                SettingsItem(
                    "Permisos de Visualización", 
                    "Configura qué métricas puedes ver de cada paciente",
                    Icons.Outlined.LockPerson, 
                    Color(0xFF8B5CF6), 
                    textColor, 
                    mutedTextColor
                )
                SettingsItem(
                    "Reportes Mensuales", 
                    "Configura el envío automático de resúmenes",
                    Icons.Outlined.Analytics, 
                    Color(0xFF10B981), 
                    textColor, 
                    mutedTextColor
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Safety Section
            SettingsSectionTitle("SEGURIDAD Y ALERTAS", mutedTextColor)
            SettingsGroup(surfaceColor) {
                SettingsItem(
                    "Notificaciones SOS", 
                    "Alertas instantáneas en caso de crisis",
                    Icons.Outlined.NotificationsActive, 
                    Color(0xFFEF4444), 
                    textColor, 
                    mutedTextColor
                )
                SettingsItem(
                    "Umbrales de Alerta", 
                    "Define cuándo recibir avisos de bienestar bajo",
                    Icons.Outlined.TrendingDown, 
                    Color(0xFFF59E0B), 
                    textColor, 
                    mutedTextColor
                )
                SettingsItem(
                    "Ubicación en Tiempo Real", 
                    "Permisos para rastreo en emergencias",
                    Icons.Outlined.LocationOn, 
                    Color(0xFF3B82F6), 
                    textColor, 
                    mutedTextColor
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Account Section
            SettingsSectionTitle("CUENTA", mutedTextColor)
            SettingsGroup(surfaceColor) {
                SettingsItem(
                    "Seguridad de la Cuenta", 
                    "Cambiar contraseña y biometría",
                    Icons.Outlined.Shield, 
                    Color(0xFF64748B), 
                    textColor, 
                    mutedTextColor
                )
                SettingsItem(
                    "Centro de Ayuda", 
                    "Tutoriales y soporte técnico",
                    Icons.Outlined.HelpCenter, 
                    CaregiverBlue, 
                    textColor, 
                    mutedTextColor
                )
                SettingsItem(
                    "Cerrar Sesión", 
                    "Salir de tu cuenta actual",
                    Icons.Outlined.Logout, 
                    Color(0xFFEF4444), 
                    textColor, 
                    mutedTextColor,
                    onClick = onLogout
                )
            }

            Spacer(modifier = Modifier.height(48.dp))
            
            // App Version
            Text(
                "RelaxMind v2.4.0 • Pro Caregiver",
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                style = MaterialTheme.typography.labelSmall,
                color = mutedTextColor.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SettingsSectionTitle(title: String, color: Color) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium,
        color = color,
        fontWeight = FontWeight.Black,
        letterSpacing = 1.5.sp,
        modifier = Modifier.padding(start = 8.dp, bottom = 12.dp)
    )
}

@Composable
private fun SettingsGroup(backgroundColor: Color, content: @Composable ColumnScope.() -> Unit) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = backgroundColor,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            content()
        }
    }
}

@Composable
private fun SettingsItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    textColor: Color,
    mutedTextColor: Color,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SettingsIconBox(icon, color)
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = mutedTextColor,
                maxLines = 1
            )
        }
        Icon(Icons.Outlined.ChevronRight, null, tint = mutedTextColor.copy(alpha = 0.3f), modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun SettingsIconBox(icon: ImageVector, color: Color) {
    Box(
        modifier = Modifier
            .size(42.dp)
            .background(color.copy(alpha = 0.12f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, null, tint = color, modifier = Modifier.size(22.dp))
    }
}
