package com.upn.relaxmind.feature.auth.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.PeopleAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.animation.core.tween
import androidx.compose.ui.draw.clip
import com.upn.relaxmind.core.ui.modifiers.relaxMindScreenInsets
import com.upn.relaxmind.core.ui.theme.RelaxGreen
import com.upn.relaxmind.core.ui.theme.RelaxMutedText

enum class UserRole(val value: String) {
    PATIENT("PATIENT"),
    CAREGIVER("CAREGIVER")
}

@Composable
fun RoleSelectionScreen(
    modifier: Modifier = Modifier,
    onContinue: (UserRole) -> Unit
) {
    var selectedRole by remember { mutableStateOf<UserRole?>(null) }
    val caregiverAccent = Color(0xFF8B5CF6)
    val patientAccent = RelaxGreen
    val screenBg = when (selectedRole) {
        UserRole.CAREGIVER -> Color(0xFFF3EEFF)
        UserRole.PATIENT -> Color(0xFFEFF9F6)
        null -> MaterialTheme.colorScheme.background
    }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .relaxMindScreenInsets(),
        color = screenBg
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "PASO 1 DE 2",
                style = MaterialTheme.typography.labelMedium,
                color = RelaxMutedText
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Como vas a usar\nRelaxMind?",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Elige tu rol para personalizar tu experiencia.",
                style = MaterialTheme.typography.bodyMedium,
                color = RelaxMutedText
            )
            Spacer(modifier = Modifier.height(20.dp))

            RoleCard(
                title = "Uso personal",
                description = "Gestiona tu bienestar y seguimiento emocional.",
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.FavoriteBorder,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                },
                selected = selectedRole == UserRole.PATIENT,
                onClick = { selectedRole = UserRole.PATIENT },
                accentColor = patientAccent
            )
            Spacer(modifier = Modifier.height(16.dp))
            RoleCard(
                title = "Cuidar de alguien",
                description = "Monitorea el bienestar de tus seres queridos.",
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.PeopleAlt,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                },
                selected = selectedRole == UserRole.CAREGIVER,
                onClick = { selectedRole = UserRole.CAREGIVER },
                accentColor = caregiverAccent
            )
            Spacer(modifier = Modifier.height(26.dp))
            Button(
                onClick = { selectedRole?.let(onContinue) },
                enabled = selectedRole != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Siguiente")
            }
        }
    }
}

@Composable
private fun RoleCard(
    title: String,
    description: String,
    icon: @Composable () -> Unit,
    selected: Boolean,
    onClick: () -> Unit,
    accentColor: Color
) {
    val borderColor by animateColorAsState(
        targetValue = if (selected) accentColor.copy(alpha = 0.78f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.45f),
        animationSpec = tween(180),
        label = "roleCardBorder"
    )
    val cardBackground = if (selected) accentColor.copy(alpha = 0.25f) else Color.White

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(126.dp)
            .clip(RoundedCornerShape(22.dp))
            .clickable(
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(22.dp),
            border = BorderStroke(1.2.dp, borderColor),
            color = cardBackground,
            tonalElevation = 0.dp,
            shadowElevation = 0.dp
        ) {
            Row(modifier = Modifier.padding(18.dp)) {
                icon()
                Spacer(modifier = Modifier.size(12.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (selected) Color(0xFF1F2937) else MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (selected) Color(0xFF475569) else RelaxMutedText
                    )
                }
            }
        }
    }
}
