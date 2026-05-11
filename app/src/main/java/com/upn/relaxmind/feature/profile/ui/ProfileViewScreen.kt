package com.upn.relaxmind.feature.profile.ui

import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import com.upn.relaxmind.core.ui.components.RelaxBackButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upn.relaxmind.core.data.preferences.AppPreferences
import com.upn.relaxmind.core.data.auth.AuthManager
import com.upn.relaxmind.core.ui.theme.RelaxBackground
import com.upn.relaxmind.core.ui.theme.RelaxGreen
import com.upn.relaxmind.core.ui.theme.RelaxMutedText

@Composable
fun ProfileViewScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onEditProfile: () -> Unit,
    onOpenSettings: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val user = remember { AuthManager.getCurrentUser(context) }
    val displayName = remember { AppPreferences.getDisplayName(context) }
    val isDark = com.upn.relaxmind.core.ui.theme.LocalIsDarkTheme.current

    val fullName = listOfNotNull(
        user?.name?.ifBlank { null },
        user?.lastName?.ifBlank { null }
    ).joinToString(" ").ifBlank { displayName.ifBlank { "Usuario" } }

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    val alpha by animateFloatAsState(if (visible) 1f else 0f, tween(600), label = "alpha")

    val bgColor = MaterialTheme.colorScheme.background
    val onBg = MaterialTheme.colorScheme.onBackground
    val mutedColor = if (isDark) MaterialTheme.colorScheme.onSurfaceVariant else RelaxMutedText

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
    ) {
        // ── Header ──────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RelaxBackButton(onClick = onBack)
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Mi Perfil",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = onBg
            )
        }

        // ── Body ────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .graphicsLayer { this.alpha = alpha }
        ) {
            // Avatar Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .shadow(16.dp, RoundedCornerShape(28.dp), spotColor = RelaxGreen.copy(0.15f))
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(RelaxGreen.copy(alpha = 0.85f), Color(0xFF0D9488))
                        )
                    )
                    .padding(28.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Avatar circle
                    com.upn.relaxmind.core.ui.components.UserAvatar(user = user, size = 88, fontSize = 34)

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = fullName,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = user?.email ?: "Sin correo",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.85f),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Edit Profile button
                    OutlinedButton(
                        onClick = onEditProfile,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color.White.copy(0.7f)),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.fillMaxWidth(0.7f)
                    ) {
                        Icon(Icons.Outlined.Edit, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Editar Perfil", fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Info cards
            ProfileInfoCard(
                title = "Información Personal",
                items = listOf(
                    Triple(Icons.Outlined.Person, "Nombre completo", fullName),
                    Triple(Icons.Outlined.Email, "Correo electrónico", user?.email ?: "—"),
                    Triple(
                        Icons.Outlined.Cake, 
                        "Fecha de nacimiento", 
                        formatBirthDate(user?.birthDate ?: "")
                    ),
                    Triple(Icons.Outlined.Info, "Condición", user?.condition?.ifBlank { "No especificada" } ?: "No especificada"),
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Spacer(modifier = Modifier.height(24.dp))

            ProfileInfoCard(
                title = "Cuenta",
                items = listOf(
                    Triple(Icons.Outlined.Fingerprint, "ID de usuario", user?.id?.take(8)?.uppercase() ?: "—"),
                    Triple(Icons.Outlined.Security, "Biometría", if (user?.biometricEnabled == true) "Habilitada ✓" else "Deshabilitada"),
                )
            )

            if (user?.role == "PATIENT") {
                Spacer(modifier = Modifier.height(24.dp))
                val scope = rememberCoroutineScope()
                CaregiversCard(
                    onUnlink = { caregiverId ->
                        scope.launch { AuthManager.unlinkUser(context, caregiverId) }
                    }
                )
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
private fun CaregiversCard(onUnlink: (String) -> Unit) {
    val context = LocalContext.current
    var linkedCaregivers by remember { mutableStateOf<List<com.upn.relaxmind.core.data.models.User>>(emptyList()) }
    var caregiverToUnlink by remember { mutableStateOf<com.upn.relaxmind.core.data.models.User?>(null) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        linkedCaregivers = AuthManager.getLinkedUsers(context)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(20.dp), spotColor = Color(0x10000000))
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(20.dp)
    ) {
        Text(
            text = "Cuidadores Vinculados",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 0.8.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
        Spacer(modifier = Modifier.height(8.dp))

        if (linkedCaregivers.isEmpty()) {
            Text(
                text = "No tienes cuidadores vinculados",
                style = MaterialTheme.typography.bodyMedium,
                color = RelaxMutedText,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        } else {
            linkedCaregivers.forEach { caregiver ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    com.upn.relaxmind.core.ui.components.UserAvatar(user = caregiver, size = 40, fontSize = 16)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${caregiver.name} ${caregiver.lastName}",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = caregiver.patientRelationships[AuthManager.getCurrentUser(context)?.id] ?: caregiver.professionalRole ?: "Cuidador",
                            style = MaterialTheme.typography.labelSmall,
                            color = RelaxMutedText
                        )
                    }
                    IconButton(onClick = { 
                        caregiverToUnlink = caregiver
                        showConfirmDialog = true
                    }) {
                        Icon(Icons.Outlined.PersonRemove, "Desvincular", tint = Color(0xFFEF4444))
                    }
                }
            }
        }
    }

    if (showConfirmDialog && caregiverToUnlink != null) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("¿Desvincular Cuidador?") },
            text = { Text("¿Estás seguro de que deseas desvincular a ${caregiverToUnlink?.name}? Ya no podrá ver tu información de bienestar.") },
            confirmButton = {
                Button(
                    onClick = {
                        onUnlink(caregiverToUnlink!!.id)
                        showConfirmDialog = false
                        Toast.makeText(context, "Desvinculado con éxito", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("Desvincular")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Cancelar")
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }
}

@Composable
private fun ProfileInfoCard(
    title: String,
    items: List<Triple<ImageVector, String, String>>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(20.dp), spotColor = Color(0x10000000))
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 0.8.sp
        )
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
        items.forEach { (icon, label, value) ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(RelaxGreen.copy(alpha = 0.10f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = RelaxGreen, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileActionRow(
    icon: ImageVector,
    iconTint: Color,
    label: String,
    labelColor: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = iconTint, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = labelColor,
                modifier = Modifier.weight(1f)
            )
            Icon(
                Icons.Outlined.ChevronRight,
                null,
                tint = RelaxMutedText.copy(0.4f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// Removed RelaxBackButton

private fun formatBirthDate(raw: String): String {
    if (raw.isBlank() || raw == "—") return "—"
    if (raw.contains("/") || raw.contains("-")) return raw
    if (raw.length == 8 && raw.all { it.isDigit() }) {
        return "${raw.substring(0, 2)}-${raw.substring(2, 4)}-${raw.substring(4)}"
    }
    return raw
}
