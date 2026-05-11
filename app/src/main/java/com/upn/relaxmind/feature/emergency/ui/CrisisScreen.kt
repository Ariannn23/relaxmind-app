package com.upn.relaxmind.feature.emergency.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upn.relaxmind.core.ui.theme.RelaxBackground
import com.upn.relaxmind.core.ui.theme.RelaxGreen
import com.upn.relaxmind.core.ui.theme.RelaxMutedText
import com.upn.relaxmind.core.ui.components.RelaxBackButton
import com.upn.relaxmind.core.data.auth.AuthManager
import kotlinx.coroutines.delay

private val CrisisRed = Color(0xFFEF4444)
private val CrisisCoral = Color(0xFFFF6B6B)
private val CrisisRedSoft = Color(0xFFFFF1F1)
private val CrisisAmber = Color(0xFFF59E0B)

@Composable
fun CrisisScreen(
    modifier: Modifier = Modifier,
    contactsNotified: Int = 1,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    var showAlert by remember { mutableStateOf(false) }
    var showActions by remember { mutableStateOf(false) }
    var linkedUsers by remember { mutableStateOf<List<com.upn.relaxmind.core.data.models.User>>(emptyList()) }

    LaunchedEffect(Unit) {
        delay(120)
        showAlert = true
        delay(400)
        showActions = true
        linkedUsers = AuthManager.getLinkedUsers(context)
    }


    Surface(
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Top bar with back
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RelaxBackButton(onClick = onBack)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Alert status card
            AnimatedVisibility(
                visible = showAlert,
                enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { it / 4 }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Pulsing SOS icon with ripple waves
                    val waveTransition = rememberInfiniteTransition(label = "sosWave")
                    val wave1 by waveTransition.animateFloat(
                        initialValue = 0f, targetValue = 1f,
                        animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing)),
                        label = "w1"
                    )
                    val wave2 by waveTransition.animateFloat(
                        initialValue = 0f, targetValue = 1f,
                        animationSpec = infiniteRepeatable(tween(2000, 600, easing = LinearEasing)),
                        label = "w2"
                    )
                    val wave3 by waveTransition.animateFloat(
                        initialValue = 0f, targetValue = 1f,
                        animationSpec = infiniteRepeatable(tween(2000, 1200, easing = LinearEasing)),
                        label = "w3"
                    )

                    Box(
                        modifier = Modifier.size(180.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Ripple wave rings drawn on Canvas
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val center = androidx.compose.ui.geometry.Offset(
                                size.width / 2f, size.height / 2f
                            )
                            val baseRadius = 48.dp.toPx()
                            val maxRadius = size.minDimension / 2f

                            listOf(wave1, wave2, wave3).forEach { progress ->
                                val radius = baseRadius + (maxRadius - baseRadius) * progress
                                val alpha = (1f - progress) * 0.35f
                                drawCircle(
                                    color = CrisisRed.copy(alpha = alpha),
                                    radius = radius,
                                    center = center,
                                    style = Stroke(width = 3.dp.toPx())
                                )
                            }
                        }

                        // SOS button on top
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .shadow(20.dp, CircleShape, spotColor = CrisisRed.copy(0.5f))
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(listOf(CrisisCoral, CrisisRed))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "SOS",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Alerta enviada",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tu alerta fue enviada a $contactsNotified contacto${if (contactsNotified != 1) "s" else ""}.\nEstamos contigo.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = RelaxMutedText,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Notified contacts chip
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(if (com.upn.relaxmind.core.ui.theme.LocalIsDarkTheme.current) CrisisRed.copy(0.15f) else CrisisRedSoft)
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.NotificationsActive,
                            contentDescription = null,
                            tint = CrisisRed,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "$contactsNotified contacto${if (contactsNotified != 1) "s" else ""} notificado${if (contactsNotified != 1) "s" else ""}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = CrisisRed
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Action buttons
            AnimatedVisibility(
                visible = showActions,
                enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { it / 3 }
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Acciones inmediatas",
                        style = MaterialTheme.typography.labelMedium,
                        color = RelaxMutedText,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )

                    // Call MINSA crisis line
                    CrisisActionButton(
                        icon = Icons.Outlined.LocalHospital,
                        title = "Llamar línea de crisis MINSA",
                        subtitle = "0800-00015 · Gratuita 24/7",
                        iconBackground = CrisisRed,
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:080000015"))
                            context.startActivity(intent)
                        }
                    )

                    // Breathing technique
                    CrisisActionButton(
                        icon = Icons.Outlined.Air,
                        title = "Técnica de respiración 4-7-8",
                        subtitle = "Inhala 4 seg · Retén 7 · Exhala 8",
                        iconBackground = RelaxGreen,
                        onClick = {
                            // Future: launch breathing screen with audio
                        }
                    )

                    // Call first contact
                    val firstCaregiver = linkedUsers.firstOrNull { it.role == "CAREGIVER" }
                    val caregiverPhone = firstCaregiver?.phoneNumber ?: ""

                    CrisisActionButton(
                        icon = Icons.Outlined.Call,
                        title = if (firstCaregiver != null) "Llamar a ${firstCaregiver.name}" else "Llamar a mi contacto SOS",
                        subtitle = if (firstCaregiver != null) "Cuidador principal" else "Contacto de emergencia principal",
                        iconBackground = CrisisAmber,
                        onClick = {
                            if (caregiverPhone.isNotEmpty()) {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$caregiverPhone"))
                                context.startActivity(intent)
                            } else {
                                val intent = Intent(Intent.ACTION_DIAL)
                                context.startActivity(intent)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CrisisActionButton(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconBackground: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
        shadowElevation = 6.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(iconBackground.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconBackground,
                    modifier = Modifier.size(22.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = RelaxMutedText
                )
            }
        }
    }
}
