package com.upn.relaxmind.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upn.relaxmind.data.AuthManager
import com.upn.relaxmind.ui.components.RelaxBackButton
import com.upn.relaxmind.ui.theme.RelaxGreen
import com.upn.relaxmind.ui.theme.RelaxMutedText
import com.upn.relaxmind.utils.QRUtils

private val QrRed = Color(0xFFEF4444)

@Composable
fun EmergencyQrScreen(
    modifier: Modifier = Modifier, 
    onBack: () -> Unit,
    onRemoteLink: () -> Unit
) {
    val context = LocalContext.current
    val user = remember { AuthManager.getCurrentUser(context) }
    
    // Generate QR content based on user info
    val qrContent = remember(user) {
        val name = user?.name ?: "Usuario"
        val lastName = user?.lastName ?: ""
        val birthday = user?.birthDate ?: "No especificada"
        val condition = user?.condition ?: "Sin diagnóstico especificado"
        
        "RelaxMind Emergency Info\n" +
        "Nombre: $name $lastName\n" +
        "Fecha Nac: $birthday\n" +
        "Condicion: $condition\n" +
        "ID: RM-${user?.id?.take(5) ?: "DEFAULT"}\n" +
        "LINK_TOKEN:RELAXMIND_LINK:${user?.id ?: ""}"
    }

    val qrBitmap = remember(qrContent) {
        QRUtils.generateQRCode(qrContent, 400)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                RelaxBackButton(onClick = onBack, modifier = Modifier.size(40.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "QR de Emergencia",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Tu información médica vital",
                        style = MaterialTheme.typography.bodyMedium,
                        color = RelaxMutedText
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // QR Preview Card
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(28.dp),
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(28.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(220.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        if (qrBitmap != null) {
                            Image(
                                bitmap = qrBitmap.asImageBitmap(),
                                contentDescription = "Código QR de Emergencia",
                                modifier = Modifier.size(200.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Outlined.QrCode,
                                contentDescription = null,
                                modifier = Modifier.size(100.dp),
                                tint = QrRed.copy(alpha = 0.5f)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    Text(
                        text = "${user?.name ?: "Usuario"} ${user?.lastName ?: ""}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Surface(
                        color = QrRed.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(
                            text = "RM-${user?.id?.take(8)?.uppercase() ?: "RELAXMIND"}",
                            style = MaterialTheme.typography.labelMedium,
                            color = QrRed,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Privacy Levels
            Text(
                text = "Niveles de Información Segura",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(12.dp))
            LevelItem("Nivel 1 (Público)", "Identidad y condición principal de alerta.", Icons.Outlined.LockOpen, RelaxGreen)
            LevelItem("Nivel 2 (Cuidador)", "Acceso a agenda médica y contactos SOS.", Icons.Outlined.FamilyRestroom, Color(0xFFEAB308))
            LevelItem("Nivel 3 (Hospitales)", "Historial clínico completo y terapeuta.", Icons.Outlined.Security, QrRed)

            Spacer(modifier = Modifier.height(32.dp))

            // Actions
            Button(
                onClick = { /* Implement sharing logic if needed */ },
                modifier = Modifier.fillMaxWidth().height(58.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = QrRed)
            ) {
                Icon(Icons.Outlined.Share, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(10.dp))
                Text("Compartir con Cuidador", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedButton(
                onClick = onRemoteLink,
                modifier = Modifier.fillMaxWidth().height(58.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface)
            ) {
                Icon(Icons.Outlined.QrCodeScanner, contentDescription = null)
                Spacer(modifier = Modifier.width(10.dp))
                Text("Vincular con Cuidador", fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun LevelItem(title: String, description: String, icon: androidx.compose.ui.graphics.vector.ImageVector, iconTint: Color) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                Text(text = description, style = MaterialTheme.typography.bodySmall, color = RelaxMutedText)
            }
        }
    }
}
