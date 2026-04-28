package com.upn.relaxmind.ui.screens

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
import com.upn.relaxmind.data.AppPreferences
import com.upn.relaxmind.data.AuthManager
import com.upn.relaxmind.ui.theme.RelaxBackground
import com.upn.relaxmind.ui.theme.RelaxGreen
import com.upn.relaxmind.ui.theme.RelaxMutedText

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
    val isDark = com.upn.relaxmind.ui.theme.LocalIsDarkTheme.current

    val firstName = user?.name?.ifBlank { displayName }?.substringBefore(" ") ?: "Usuario"
    val fullName = listOfNotNull(
        user?.name?.ifBlank { null },
        user?.lastName?.ifBlank { null }
    ).joinToString(" ").ifBlank { displayName.ifBlank { "Usuario" } }

    val initials = buildString {
        user?.name?.firstOrNull()?.let { append(it.uppercaseChar()) }
        user?.lastName?.firstOrNull()?.let { append(it.uppercaseChar()) }
    }.ifBlank { firstName.firstOrNull()?.uppercaseChar()?.toString() ?: "U" }

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
            Surface(
                onClick = onBack,
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp,
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("←", fontSize = 20.sp, color = MaterialTheme.colorScheme.onSurface)
                }
            }
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
                    // Avatar circle with initials
                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initials,
                            fontSize = 34.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }

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

            Spacer(modifier = Modifier.height(48.dp))
        }
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

private fun formatBirthDate(raw: String): String {
    if (raw.isBlank() || raw == "—") return "—"
    if (raw.contains("/") || raw.contains("-")) return raw
    if (raw.length == 8 && raw.all { it.isDigit() }) {
        return "${raw.substring(0, 2)}-${raw.substring(2, 4)}-${raw.substring(4)}"
    }
    return raw
}
