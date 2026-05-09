package com.upn.relaxmind.feature.profile.ui

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.upn.relaxmind.core.data.auth.AuthManager
import com.upn.relaxmind.core.data.preferences.AppPreferences
import com.upn.relaxmind.core.ui.components.UserAvatar
import com.upn.relaxmind.core.ui.theme.LocalIsDarkTheme
import com.upn.relaxmind.core.ui.theme.RelaxMutedText
import com.upn.relaxmind.core.utils.BiometricHelper
import com.upn.relaxmind.feature.profile.ui.components.*
import com.upn.relaxmind.feature.profile.viewmodel.SettingsViewModel
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
    onLogoutClick: () -> Unit = {},
    viewModel: SettingsViewModel = viewModel()
) {
    val context = LocalContext.current
    val isDark = LocalIsDarkTheme.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val user = remember { AuthManager.getCurrentUser(context) }

    // Stagger animation
    var step by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) { repeat(8) { delay(80); step++ } }

    val mutedColor = if (isDark) MaterialTheme.colorScheme.onSurfaceVariant else RelaxMutedText
    val accentGreen = MaterialTheme.colorScheme.primary

    val displayName = remember { AppPreferences.getDisplayName(context) }
    val fullName = listOfNotNull(
        user?.name?.ifBlank { null },
        user?.lastName?.ifBlank { null }
    ).joinToString(" ").ifBlank { displayName }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // Page title
        AnimatedVisibility(step > 0, enter = fadeIn(tween(400)) + slideInVertically(tween(400)) { it / 3 }) {
            Column(modifier = Modifier.padding(bottom = 20.dp)) {
                Text(
                    text = "Ajustes",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Personaliza RelaxMind a tu ritmo",
                    style = MaterialTheme.typography.bodyMedium,
                    color = mutedColor
                )
            }
        }

        // SECCIÓN 1 — Tu Cuenta
        AnimatedVisibility(step > 1, enter = sectionEnter(1)) {
            SettingsSectionCard {
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
                    UserAvatar(user = user, size = 54, fontSize = 20)
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = fullName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = user?.email ?: "Sin correo",
                            style = MaterialTheme.typography.bodySmall,
                            color = mutedColor
                        )
                    }
                    Icon(Icons.Outlined.ChevronRight, null, tint = mutedColor.copy(0.4f), modifier = Modifier.size(20.dp))
                }
                SettingsDivider()
                SettingsRowItem(
                    icon = Icons.Outlined.Edit,
                    iconTint = Color(0xFF4F46E5),
                    title = "Editar Perfil",
                    subtitle = "Actualiza tu información",
                    onClick = onEditProfileClick
                )
                SettingsDivider()
                SettingsRowItem(
                    icon = Icons.Outlined.QrCode2,
                    iconTint = Color(0xFFD97706),
                    title = "QR de Emergencia",
                    subtitle = "Contactos para crisis",
                    onClick = onSecurityClick
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // SECCIÓN 2 — Apariencia
        AnimatedVisibility(step > 2, enter = sectionEnter(2)) {
            Column {
                SettingsSectionHeader(text = "Apariencia", mutedColor = mutedColor)
                Spacer(modifier = Modifier.height(8.dp))
                SettingsSectionCard {
                    SettingsToggleItem(
                        icon = Icons.Outlined.DarkMode,
                        iconTint = Color(0xFF6366F1),
                        title = "Modo Oscuro",
                        subtitle = if (uiState.darkModeEnabled) "Activado" else "Desactivado",
                        isChecked = uiState.darkModeEnabled,
                        onCheckedChange = { enabled ->
                            viewModel.toggleDarkMode(enabled)
                            onToggleDarkTheme(enabled)
                        }
                    )
                    SettingsDivider()
                    SettingsRowItem(
                        icon = Icons.Outlined.FormatSize,
                        iconTint = Color(0xFF0891B2),
                        title = "Tamaño de Fuente",
                        subtitle = "Mediano",
                        onClick = { Toast.makeText(context, "Próximamente", Toast.LENGTH_SHORT).show() }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // SECCIÓN 3 — Notificaciones
        AnimatedVisibility(step > 3, enter = sectionEnter(3)) {
            Column {
                SettingsSectionHeader(text = "Notificaciones", mutedColor = mutedColor)
                Spacer(modifier = Modifier.height(8.dp))
                SettingsSectionCard {
                    SettingsToggleItem(
                        icon = Icons.Outlined.Notifications,
                        iconTint = Color(0xFF16A34A),
                        title = "Recordatorios",
                        subtitle = "Hábitos y check-in diario",
                        isChecked = uiState.notificationsEnabled,
                        onCheckedChange = { viewModel.toggleNotifications(it) }
                    )
                    SettingsDivider()
                    SettingsRowItem(
                        icon = Icons.Outlined.Schedule,
                        iconTint = Color(0xFF0284C7),
                        title = "Horario de Silencio",
                        subtitle = "22:00 – 08:00",
                        onClick = { Toast.makeText(context, "Próximamente", Toast.LENGTH_SHORT).show() }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // SECCIÓN 4 — Privacidad & Seguridad
        AnimatedVisibility(step > 4, enter = sectionEnter(4)) {
            Column {
                SettingsSectionHeader(text = "Privacidad & Seguridad", mutedColor = mutedColor)
                Spacer(modifier = Modifier.height(8.dp))
                SettingsSectionCard {
                    SettingsToggleItem(
                        icon = Icons.Outlined.Fingerprint,
                        iconTint = accentGreen,
                        title = "Acceso Biométrico",
                        subtitle = "Huella o Face ID",
                        isChecked = uiState.biometricEnabled,
                        onCheckedChange = { checked ->
                            if (checked) {
                                val activity = context as? FragmentActivity
                                activity?.let {
                                    BiometricHelper.authenticate(
                                        activity = it,
                                        title = "Confirmar Biometría",
                                        subtitle = "Escanea tu huella para habilitar",
                                        onSuccess = {
                                            viewModel.toggleBiometric(true)
                                            Toast.makeText(context, "Biometría habilitada ✓", Toast.LENGTH_SHORT).show()
                                        },
                                        onError = { Toast.makeText(context, "Error: $it", Toast.LENGTH_SHORT).show() }
                                    )
                                }
                            } else {
                                viewModel.toggleBiometric(false)
                                Toast.makeText(context, "Biometría desactivada", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                    SettingsDivider()
                    SettingsToggleItem(
                        icon = Icons.Outlined.FavoriteBorder,
                        iconTint = Color(0xFFEC4899),
                        title = "Health Connect",
                        subtitle = "Sincroniza pasos y sueño",
                        isChecked = uiState.healthConnectEnabled,
                        onCheckedChange = { viewModel.toggleHealthConnect(it) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // SECCIÓN 5 — Información
        AnimatedVisibility(step > 5, enter = sectionEnter(5)) {
            Column {
                SettingsSectionHeader(text = "Información", mutedColor = mutedColor)
                Spacer(modifier = Modifier.height(8.dp))
                SettingsSectionCard {
                    SettingsRowItem(
                        icon = Icons.Outlined.Info,
                        iconTint = Color(0xFF64748B),
                        title = "Acerca de RelaxMind",
                        subtitle = "v2.1.0",
                        onClick = onAboutClick
                    )
                    SettingsDivider()
                    SettingsRowItem(
                        icon = Icons.Outlined.Star,
                        iconTint = Color(0xFFF59E0B),
                        title = "Calificar la App",
                        subtitle = "¿Te gusta RelaxMind?",
                        onClick = { viewModel.showFeedbackDialog(true) }
                    )
                    SettingsDivider()
                    SettingsRowItem(
                        icon = Icons.Outlined.PrivacyTip,
                        iconTint = Color(0xFF0891B2),
                        title = "Privacidad y Términos",
                        subtitle = "Política de uso",
                        onClick = onTermsClick
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // SECCIÓN 6 — Cuenta (Logout)
        AnimatedVisibility(step > 6, enter = sectionEnter(6)) {
            SettingsSectionCard {
                SettingsRowItem(
                    icon = Icons.AutoMirrored.Outlined.Logout,
                    iconTint = Color(0xFFDC2626),
                    title = "Cerrar Sesión",
                    subtitle = "Salir de tu cuenta",
                    titleColor = Color(0xFFDC2626),
                    onClick = onLogoutClick
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
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                textAlign = TextAlign.Center
            )
        }
        Spacer(modifier = Modifier.height(80.dp))
    }

    if (uiState.showFeedbackDialog) {
        FeedbackDialog(onDismiss = { viewModel.showFeedbackDialog(false) })
    }
}

@Composable
private fun sectionEnter(index: Int) =
    fadeIn(tween(400 + index * 50)) + slideInVertically(tween(400 + index * 50)) { it / 4 }
