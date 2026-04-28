package com.upn.relaxmind.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.upn.relaxmind.ui.theme.RelaxGreen
import com.upn.relaxmind.ui.theme.RelaxMutedText

data class HealthCenter(
    val id: String,
    val name: String,
    val address: String,
    val phone: String,
    val schedule: String,
    val type: String,
    val location: LatLng,
    val acceptsSIS: Boolean
)

val mockCenters = listOf(
    HealthCenter("1", "Centro de Salud Mental Comunitario Lima", "Av. Brasil 123, Lima", "+51 123 456 789", "Lun-Vie: 8am - 8pm", "Público", LatLng(-12.067, -77.036), true),
    HealthCenter("2", "Clínica Psicológica Paz y Bien", "Av. Arequipa 456, Miraflores", "+51 987 654 321", "24/7", "Privado", LatLng(-12.100, -77.030), false),
    HealthCenter("3", "Hospital Hermilio Valdizán", "Carretera Central Km 3.5, Ate", "+51 111 222 333", "24/7", "Público (Minsa)", LatLng(-12.030, -76.920), true)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicesMapScreen(modifier: Modifier = Modifier, onBack: () -> Unit = {}) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedCenter by remember { mutableStateOf<HealthCenter?>(null) }

    val lima = LatLng(-12.0464, -77.0428)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(lima, 11f)
    }

    Box(modifier = modifier.fillMaxSize()) {
        // 1. Google Map
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(zoomControlsEnabled = false, compassEnabled = true)
        ) {
            mockCenters.forEach { center ->
                Marker(
                    state = MarkerState(position = center.location),
                    title = center.name,
                    snippet = center.type,
                    onClick = {
                        selectedCenter = center
                        false
                    }
                )
            }
        }

        // 2. Search & Filters Bar (Top)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .systemBarsPadding()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    onClick = onBack,
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 4.dp,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(Icons.Outlined.ArrowBack, contentDescription = "Volver", modifier = Modifier.padding(12.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar centro o servicio...") },
                    leadingIcon = { Icon(Icons.Outlined.Search, null) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(24.dp))
                        .clip(RoundedCornerShape(24.dp))
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            // Filters
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = true, onClick = {}, label = { Text("Todos") })
                FilterChip(selected = false, onClick = {}, label = { Text("SIS/Minsa") })
                FilterChip(selected = false, onClick = {}, label = { Text("< 5 km") })
                FilterChip(selected = false, onClick = {}, label = { Text("Emergencia 24/7") })
            }
        }

        // 3. Bottom Sheet Info Panel
        AnimatedVisibility(
            visible = selectedCenter != null,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            selectedCenter?.let { center ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(16.dp, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    color = Color.White
                ) {
                    Column(modifier = Modifier.padding(24.dp).navigationBarsPadding()) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                text = center.name,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = { selectedCenter = null }) {
                                Icon(Icons.Outlined.Close, contentDescription = "Cerrar")
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(color = Color(0xFFEFF6FF), shape = RoundedCornerShape(8.dp)) {
                            Text(text = center.type, color = Color(0xFF3B82F6), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall)
                        }
                        if (center.acceptsSIS) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Acepta SIS", color = RelaxGreen, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.LocationOn, null, tint = RelaxMutedText)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(center.address, style = MaterialTheme.typography.bodyMedium)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Phone, null, tint = RelaxMutedText)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(center.phone, style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.weight(1f))
                            TextButton(onClick = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${center.phone}"))
                                context.startActivity(intent)
                            }) {
                                Text("Llamar", color = RelaxGreen)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Schedule, null, tint = RelaxMutedText)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(center.schedule, style = MaterialTheme.typography.bodyMedium)
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                val uri = Uri.parse("google.navigation:q=${center.location.latitude},${center.location.longitude}")
                                val intent = Intent(Intent.ACTION_VIEW, uri)
                                intent.setPackage("com.google.android.apps.maps")
                                if (intent.resolveActivity(context.packageManager) != null) {
                                    context.startActivity(intent)
                                } else {
                                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com/?q=${center.location.latitude},${center.location.longitude}")))
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = RelaxGreen),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(Icons.Outlined.Directions, null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Cómo llegar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
