package com.upn.relaxmind.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.text.style.TextAlign
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
import androidx.fragment.app.FragmentActivity
import com.upn.relaxmind.data.AppPreferences
import com.upn.relaxmind.data.AuthManager
import com.upn.relaxmind.ui.theme.LocalIsDarkTheme
import com.upn.relaxmind.ui.theme.RelaxGreen
import com.upn.relaxmind.ui.theme.RelaxMutedText
import com.upn.relaxmind.utils.BiometricHelper
import kotlinx.coroutines.delay

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onProfileClick: () -> Unit = {},
    onEditProfileClick: () -> Unit = {},
    onSecurityClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onToggleDarkTheme: (Boolean) -> Unit = {},
    onAboutClick: () -> Unit = {},
    onTermsClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val isDark  = LocalIsDarkTheme.current
    val user    = remember { AuthManager.getCurrentUser(context) }

    // ── Local toggles ─────────────────────────────────────────────────────────
    var biometricEnabled      by remember { mutableStateOf(AppPreferences.isBiometricEnabled(context)) }
    var notificationsEnabled  by remember { mutableStateOf(true) }
    var healthConnectEnabled  by remember { mutableStateOf(false) }
    var darkModeEnabled       by remember { mutableStateOf(isDark) }
    var showFeedbackDialog    by remember { mutableStateOf(false) }

    // ── Stagger animation ─────────────────────────────────────────────────────
    var step by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) { repeat(8) { delay(80); step++ } }

    // ── Color tokens ─────────────────────────────────────────────────────────
    val bgColor      = MaterialTheme.colorScheme.background
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onBg         = MaterialTheme.colorScheme.onBackground
    val mutedColor   = if (isDark) MaterialTheme.colorScheme.onSurfaceVariant else RelaxMutedText
    val accentGreen  = MaterialTheme.colorScheme.primary

    // ── Initials ──────────────────────────────────────────────────────────────
    val displayName = remember { AppPreferences.getDisplayName(context) }
    val fullName    = listOfNotNull(
        user?.name?.ifBlank { null },
        user?.lastName?.ifBlank { null }
    ).joinToString(" ").ifBlank { displayName }
    val initials = buildString {
        user?.name?.firstOrNull()?.let { append(it.uppercaseChar()) }
        user?.lastName?.firstOrNull()?.let { append(it.uppercaseChar()) }
    }.ifBlank { displayName.firstOrNull()?.uppercaseChar()?.toString() ?: "U" }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(bgColor)
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // ── Page title ─────────────────────────────────────────────────────────
        AnimatedVisibility(step > 0, enter = fadeIn(tween(400)) + slideInVertically(tween(400)) { it / 3 }) {
            Column(modifier = Modifier.padding(bottom = 20.dp)) {
                Text(
                    text = "Ajustes",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = onBg
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Personaliza RelaxMind a tu ritmo",
                    style = MaterialTheme.typography.bodyMedium,
                    color = mutedColor
                )
            }
        }

        // ════════════════════════════════════════════════════════════════════
        // SECCIÓN 1 — Tu Cuenta
        // ════════════════════════════════════════════════════════════════════
        AnimatedVisibility(step > 1, enter = sectionEnter(1)) {
            SettingsSectionCard {
                // User banner inside settings
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onProfileClick
                        )
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(accentGreen, accentGreen.copy(alpha = 0.7f))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initials,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = fullName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = onBg
                        )
                        Text(
                            text = user?.email ?: "Sin correo",
                            style = MaterialTheme.typography.bodySmall,
                            color = mutedColor
                        )
                    }
                    Icon(
                        Icons.Outlined.ChevronRight,
                        null,
                        tint = mutedColor.copy(0.4f),
                        modifier = Modifier.size(20.dp)
                    )
                }

                SettingsDivider()

                SettingsRowItem(
                    icon     = Icons.Outlined.Edit,
                    iconTint = Color(0xFF4F46E5),
                    title    = "Editar Perfil",
                    subtitle = "Actualiza tu información",
                    onClick  = onEditProfileClick
                )

                SettingsDivider()

                SettingsRowItem(
                    icon     = Icons.Outlined.QrCode2,
                    iconTint = Color(0xFFD97706),
                    title    = "QR de Emergencia",
                    subtitle = "Contactos para crisis",
                    onClick  = onSecurityClick
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ════════════════════════════════════════════════════════════════════
        // SECCIÓN 2 — Apariencia
        // ════════════════════════════════════════════════════════════════════
        AnimatedVisibility(step > 2, enter = sectionEnter(2)) {
            Column {
                SettingsSectionHeader(text = "Apariencia", mutedColor = mutedColor)
                Spacer(modifier = Modifier.height(8.dp))
                SettingsSectionCard {
                    SettingsToggleItem(
                        icon        = Icons.Outlined.DarkMode,
                        iconTint    = Color(0xFF6366F1),
                        title       = "Modo Oscuro",
                        subtitle    = if (darkModeEnabled) "Activado" else "Desactivado",
                        isChecked   = darkModeEnabled,
                        onCheckedChange = { enabled ->
                            darkModeEnabled = enabled
                            onToggleDarkTheme(enabled)
                        }
                    )
                    SettingsDivider()
                    SettingsRowItem(
                        icon     = Icons.Outlined.FormatSize,
                        iconTint = Color(0xFF0891B2),
                        title    = "Tamaño de Fuente",
                        subtitle = "Mediano",
                        onClick  = {
                            Toast.makeText(context, "Próximamente", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ════════════════════════════════════════════════════════════════════
        // SECCIÓN 3 — Notificaciones
        // ════════════════════════════════════════════════════════════════════
        AnimatedVisibility(step > 3, enter = sectionEnter(3)) {
            Column {
                SettingsSectionHeader(text = "Notificaciones", mutedColor = mutedColor)
                Spacer(modifier = Modifier.height(8.dp))
                SettingsSectionCard {
                    SettingsToggleItem(
                        icon        = Icons.Outlined.Notifications,
                        iconTint    = Color(0xFF16A34A),
                        title       = "Recordatorios",
                        subtitle    = "Hábitos y check-in diario",
                        isChecked   = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it }
                    )
                    SettingsDivider()
                    SettingsRowItem(
                        icon     = Icons.Outlined.Schedule,
                        iconTint = Color(0xFF0284C7),
                        title    = "Horario de Silencio",
                        subtitle = "22:00 – 08:00",
                        onClick  = {
                            Toast.makeText(context, "Próximamente", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ════════════════════════════════════════════════════════════════════
        // SECCIÓN 4 — Privacidad & Seguridad
        // ════════════════════════════════════════════════════════════════════
        AnimatedVisibility(step > 4, enter = sectionEnter(4)) {
            Column {
                SettingsSectionHeader(text = "Privacidad & Seguridad", mutedColor = mutedColor)
                Spacer(modifier = Modifier.height(8.dp))
                SettingsSectionCard {
                    SettingsToggleItem(
                        icon      = Icons.Outlined.Fingerprint,
                        iconTint  = accentGreen,
                        title     = "Acceso Biométrico",
                        subtitle  = "Huella o Face ID",
                        isChecked = biometricEnabled,
                        onCheckedChange = { checked ->
                            if (checked) {
                                val activity = context as? FragmentActivity
                                if (activity != null) {
                                    BiometricHelper.authenticate(
                                        activity = activity,
                                        title    = "Confirmar Biometría",
                                        subtitle = "Escanea tu huella para habilitar",
                                        onSuccess = {
                                            biometricEnabled = true
                                            AuthManager.setBiometricEnabled(context, true)
                                            Toast.makeText(context, "Biometría habilitada ✓", Toast.LENGTH_SHORT).show()
                                        },
                                        onError = {
                                            Toast.makeText(context, "Error: $it", Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                }
                            } else {
                                biometricEnabled = false
                                AuthManager.setBiometricEnabled(context, false)
                                Toast.makeText(context, "Biometría desactivada", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                    SettingsDivider()
                    SettingsToggleItem(
                        icon      = Icons.Outlined.FavoriteBorder,
                        iconTint  = Color(0xFFEC4899),
                        title     = "Health Connect",
                        subtitle  = "Sincroniza pasos y sueño",
                        isChecked = healthConnectEnabled,
                        onCheckedChange = { healthConnectEnabled = it }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ════════════════════════════════════════════════════════════════════
        // SECCIÓN 5 — Información
        // ════════════════════════════════════════════════════════════════════
        AnimatedVisibility(step > 5, enter = sectionEnter(5)) {
            Column {
                SettingsSectionHeader(text = "Información", mutedColor = mutedColor)
                Spacer(modifier = Modifier.height(8.dp))
                SettingsSectionCard {
                    SettingsRowItem(
                        icon     = Icons.Outlined.Info,
                        iconTint = Color(0xFF64748B),
                        title    = "Acerca de RelaxMind",
                        subtitle = "v2.1.0",
                        onClick  = onAboutClick
                    )
                    SettingsDivider()
                    SettingsRowItem(
                        icon     = Icons.Outlined.Star,
                        iconTint = Color(0xFFF59E0B),
                        title    = "Calificar la App",
                        subtitle = "¿Te gusta RelaxMind?",
                        onClick  = { showFeedbackDialog = true }
                    )
                    SettingsDivider()
                    SettingsRowItem(
                        icon     = Icons.Outlined.PrivacyTip,
                        iconTint = Color(0xFF0891B2),
                        title    = "Privacidad y Términos",
                        subtitle = "Política de uso",
                        onClick  = onTermsClick
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ════════════════════════════════════════════════════════════════════
        // SECCIÓN 6 — Cuenta (Logout)
        // ════════════════════════════════════════════════════════════════════
        AnimatedVisibility(step > 6, enter = sectionEnter(6)) {
            SettingsSectionCard {
                SettingsRowItem(
                    icon      = Icons.AutoMirrored.Outlined.Logout,
                    iconTint  = Color(0xFFDC2626),
                    title     = "Cerrar Sesión",
                    subtitle  = "Salir de tu cuenta",
                    titleColor = Color(0xFFDC2626),
                    onClick   = onLogoutClick
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        // Version footer
        AnimatedVisibility(step > 7, enter = fadeIn(tween(600))) {
            Text(
                text = "RelaxMind v2.1.0  •  Hecho con 💚 para tu bienestar",
                style = MaterialTheme.typography.labelSmall,
                color = mutedColor.copy(alpha = 0.45f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
        Spacer(modifier = Modifier.height(80.dp))
    }

    if (showFeedbackDialog) {
        FeedbackDialog(onDismiss = { showFeedbackDialog = false })
    }
}

@Composable
private fun FeedbackDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    var rating by remember { mutableIntStateOf(0) }
    var comment by remember { mutableStateOf("") }
    val isDark = com.upn.relaxmind.ui.theme.LocalIsDarkTheme.current
    val accent = Color(0xFFF59E0B)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        icon = { Icon(Icons.Outlined.RateReview, null, tint = accent) },
        title = { Text("¿Te gusta RelaxMind?", fontWeight = FontWeight.Bold) },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "Tu opinión nos ayuda a mejorar y brindar un mejor apoyo.",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(20.dp))
                
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(5) { i ->
                        IconButton(onClick = { rating = i + 1 }) {
                            Icon(
                                imageVector = if (i < rating) Icons.Filled.Star else Icons.Outlined.StarOutline,
                                contentDescription = null,
                                tint = accent,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    placeholder = { Text("Déjanos un comentario (opcional)") },
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = accent,
                        focusedLabelColor = accent,
                        cursorColor = accent
                    )
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    Toast.makeText(context, "¡Gracias por tu apoyo! ❤️", Toast.LENGTH_SHORT).show()
                    onDismiss()
                }
            ) {
                Text("Enviar", fontWeight = FontWeight.Bold, color = accent)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    )
}

// ── Reusable composables ───────────────────────────────────────────────────────

@Composable
private fun sectionEnter(index: Int) =
    fadeIn(tween(400 + index * 50)) + slideInVertically(tween(400 + index * 50)) { it / 4 }

@Composable
private fun SettingsSectionHeader(text: String, mutedColor: Color) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = mutedColor.copy(alpha = 0.6f),
        letterSpacing = 1.2.sp,
        modifier = Modifier.padding(start = 4.dp)
    )
}

@Composable
private fun SettingsSectionCard(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(20.dp), spotColor = Color(0x0D000000)),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        Column(content = content)
    }
}

@Composable
private fun SettingsDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 20.dp),
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
        thickness = 0.8.dp
    )
}

@Composable
private fun SettingsRowItem(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String? = null,
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconTint.copy(alpha = 0.13f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = iconTint, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = titleColor
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Icon(
            Icons.Outlined.ChevronRight,
            null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.35f),
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun SettingsToggleItem(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String? = null,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconTint.copy(alpha = 0.13f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = iconTint, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor  = Color.White,
                checkedTrackColor  = iconTint,
                uncheckedTrackColor= MaterialTheme.colorScheme.outline.copy(0.3f)
            )
        )
    }
}
