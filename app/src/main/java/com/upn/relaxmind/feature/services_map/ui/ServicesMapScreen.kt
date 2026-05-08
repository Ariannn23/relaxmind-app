package com.upn.relaxmind.feature.services_map.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.*
import com.upn.relaxmind.core.ui.theme.RelaxGreen
import com.upn.relaxmind.core.ui.components.RelaxBackButton
import kotlinx.coroutines.launch

// ─── Estilo oscuro para Google Maps (Night Mode) ───────────────────────────────
private const val DARK_MAP_STYLE = """
[
  {"elementType":"geometry","stylers":[{"color":"#242f3e"}]},
  {"elementType":"labels.text.fill","stylers":[{"color":"#746855"}]},
  {"elementType":"labels.text.stroke","stylers":[{"color":"#242f3e"}]},
  {"featureType":"administrative.locality","elementType":"labels.text.fill","stylers":[{"color":"#d59563"}]},
  {"featureType":"poi","elementType":"labels.text.fill","stylers":[{"color":"#d59563"}]},
  {"featureType":"poi.park","elementType":"geometry","stylers":[{"color":"#263c3f"}]},
  {"featureType":"poi.park","elementType":"labels.text.fill","stylers":[{"color":"#6b9a76"}]},
  {"featureType":"road","elementType":"geometry","stylers":[{"color":"#38414e"}]},
  {"featureType":"road","elementType":"geometry.stroke","stylers":[{"color":"#212a37"}]},
  {"featureType":"road","elementType":"labels.text.fill","stylers":[{"color":"#9ca5b3"}]},
  {"featureType":"road.highway","elementType":"geometry","stylers":[{"color":"#746855"}]},
  {"featureType":"road.highway","elementType":"geometry.stroke","stylers":[{"color":"#1f2835"}]},
  {"featureType":"road.highway","elementType":"labels.text.fill","stylers":[{"color":"#f3d19c"}]},
  {"featureType":"transit","elementType":"geometry","stylers":[{"color":"#2f3948"}]},
  {"featureType":"transit.station","elementType":"labels.text.fill","stylers":[{"color":"#d59563"}]},
  {"featureType":"water","elementType":"geometry","stylers":[{"color":"#17263c"}]},
  {"featureType":"water","elementType":"labels.text.fill","stylers":[{"color":"#515c6d"}]},
  {"featureType":"water","elementType":"labels.text.stroke","stylers":[{"color":"#17263c"}]}
]
"""

// ─── Modelo de Datos ───────────────────────────────────────────────────────────
data class HealthCenter(
    val id: String,
    val name: String,
    val address: String,
    val phone: String,
    val schedule: String,
    val type: String,
    val location: LatLng,
    val acceptsSIS: Boolean,
    val isEmergency: Boolean = false
)

val mockCenters = listOf(
    // ─── LIMA ───
    HealthCenter("1", "Centro de Salud Mental Comunitario Lima", "Av. Brasil 123, Lima", "+51 123 456 789", "Lun-Vie: 8am-8pm", "Público", LatLng(-12.067, -77.036), true),
    HealthCenter("2", "Clínica Psicológica Paz y Bien", "Av. Arequipa 456, Miraflores", "+51 987 654 321", "24/7", "Privado", LatLng(-12.100, -77.030), false),
    HealthCenter("3", "Hospital Hermilio Valdizán (Emergencia)", "Carretera Central Km 3.5, Ate", "+51 111 222 333", "24/7", "Público (MINSA)", LatLng(-12.030, -76.920), true, true),
    
    // ─── TRUJILLO ───
    HealthCenter("6", "Hospital Regional Docente de Trujillo", "Av. Mansiche 795, Trujillo", "+51 44 231581", "24/7", "Público (MINSA)", LatLng(-8.1065, -79.0287), true, true),
    HealthCenter("7", "Hospital Belén de Trujillo", "Jr. Bolívar 350, Trujillo", "+51 44 245281", "24/7", "Público (MINSA)", LatLng(-8.1128, -79.0274), true, true),
    HealthCenter("8", "Centro de Salud Mental Comunitario Trujillo", "Urb. Los Jardines, Trujillo", "+51 44 123456", "Lun-Vie: 8am-8pm", "Público (MINSA)", LatLng(-8.1023, -79.0195), true),
    HealthCenter("9", "Clínica San Pablo Trujillo", "Av. Húsares de Junín 690, Trujillo", "+51 44 485244", "24/7", "Privado", LatLng(-8.1252, -79.0346), false),
    HealthCenter("10", "Policlínico Víctor Lazarte Echegaray", "Prolongación Unión 1300, Trujillo", "+51 44 223344", "Lun-Sab: 8am-8pm", "EsSalud / Público", LatLng(-8.1064, -79.0135), false)
)

// ─── Utilidad para obtener la ubicación del usuario ────────────────────────────
@SuppressLint("MissingPermission")
fun getUserLocation(context: Context, onLocationFetched: (LatLng) -> Unit) {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

    var location: Location? = null
    if (isNetworkEnabled) {
        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
    }
    if (isGpsEnabled && location == null) {
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
    }
    
    location?.let {
        onLocationFetched(LatLng(it.latitude, it.longitude))
    }
}

// ─── Pantalla ──────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicesMapScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val isDarkTheme = isSystemInDarkTheme()

    // Paleta dinámica basada en el tema del sistema
    val surfaceColor = if (isDarkTheme) Color(0xFF16213E) else Color.White
    val surfaceAlphaColor = if (isDarkTheme) Color(0xEB16213E) else Color(0xF2FFFFFF)
    val textPrimary = if (isDarkTheme) Color(0xFFF0F0F0) else Color(0xFF1A1A1A)
    val textSecondary = if (isDarkTheme) Color(0xFFAAAAAA) else Color(0xFF666666)
    val dividerColor = if (isDarkTheme) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.08f)
    val AccentPurple = Color(0xFF7B2FBE)
    val AccentRed = Color(0xFFE53935)

    // Estados
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Todos") }
    var selectedCenter by remember { mutableStateOf<HealthCenter?>(null) }
    var hasPermission by remember { mutableStateOf(false) }
    var permissionDenied by remember { mutableStateOf(false) }

    // Filtro dinámico
    val filteredCenters = remember(searchQuery, selectedCategory) {
        mockCenters.filter { center ->
            val matchSearch = center.name.contains(searchQuery, ignoreCase = true) ||
                              center.address.contains(searchQuery, ignoreCase = true) ||
                              center.type.contains(searchQuery, ignoreCase = true)
            val matchCategory = when (selectedCategory) {
                "Todos" -> true
                "MINSA / Público" -> center.type.contains("Público", ignoreCase = true) || center.type.contains("MINSA", ignoreCase = true)
                "Acepta SIS" -> center.acceptsSIS
                "Privado" -> center.type.contains("Privado", ignoreCase = true)
                else -> true
            }
            matchSearch && matchCategory
        }
    }

    val lima = LatLng(-12.0464, -77.0428)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(lima, 11.5f)
    }

    // Centrar mapa en la ubicación del usuario
    val centerOnUser = {
        if (hasPermission) {
            getUserLocation(context) { userLatLng ->
                coroutineScope.launch {
                    cameraPositionState.animate(
                        CameraUpdateFactory.newLatLngZoom(userLatLng, 14f)
                    )
                }
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        hasPermission = perms[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                        perms[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        permissionDenied = !hasPermission
        
        // Si nos acaban de dar permiso, centramos inmediatamente
        if (hasPermission) {
            centerOnUser()
        }
    }

    // Efecto inicial para pedir permisos
    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    // El estilo del mapa cambia dependiendo del tema del sistema
    val mapProperties = remember(isDarkTheme, hasPermission) {
        MapProperties(
            isMyLocationEnabled = hasPermission,
            mapStyleOptions = if (isDarkTheme) MapStyleOptions(DARK_MAP_STYLE) else null
        )
    }
    
    val mapUiSettings = remember(hasPermission) {
        MapUiSettings(
            zoomControlsEnabled = false,
            myLocationButtonEnabled = false, // Desactivamos el botón por defecto porque lo cubre la UI
            compassEnabled = true
        )
    }

    // Efecto para centrar el mapa cuando se selecciona un centro
    LaunchedEffect(selectedCenter) {
        selectedCenter?.let {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(it.location, 14f)
            )
        }
    }

    Box(modifier = modifier.fillMaxSize()) {

        // ─── Google Map ───────────────────────────────────────────────────────
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = mapProperties,
            uiSettings = mapUiSettings,
            contentPadding = PaddingValues(top = 180.dp, bottom = if (selectedCenter != null) 360.dp else 100.dp) // Evita que los logos de Google queden cubiertos
        ) {
            filteredCenters.forEach { center ->
                val markerHue = if (center.isEmergency) com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_RED 
                                else if (center.type.contains("MINSA", ignoreCase = true)) com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_AZURE
                                else com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_VIOLET

                Marker(
                    state = MarkerState(position = center.location),
                    title = center.name,
                    snippet = "${center.type} · ${center.schedule}",
                    icon = com.google.android.gms.maps.model.BitmapDescriptorFactory.defaultMarker(markerHue),
                    onClick = {
                        selectedCenter = center
                        false
                    }
                )
            }
        }

        // Gradiente superior para legibilidad
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .align(Alignment.TopCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            if (isDarkTheme) Color.Black.copy(alpha = 0.7f) else Color.White.copy(alpha = 0.8f), 
                            Color.Transparent
                        )
                    )
                )
        )

        // ─── Sin permiso ──────────────────────────────────────────────────────
        if (permissionDenied) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.padding(32.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = surfaceColor)
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier.size(64.dp).background(RelaxGreen.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.LocationOff, null, modifier = Modifier.size(32.dp), tint = RelaxGreen)
                        }
                        Text("Ubicación no disponible", fontWeight = FontWeight.Bold, color = textPrimary, style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Activa los permisos de ubicación para ver tu posición y centros cercanos.",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = textSecondary
                        )
                        Button(
                            onClick = {
                                context.startActivity(
                                    Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                        Uri.fromParts("package", context.packageName, null))
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = RelaxGreen),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Abrir Ajustes", fontWeight = FontWeight.SemiBold, color = Color.White) }
                    }
                }
            }
        }

        // ─── Barra superior ───────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .systemBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                RelaxBackButton(onClick = onBack, modifier = Modifier.size(48.dp))
                
                // Buscador funcional
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar centro o servicio...", color = textSecondary) },
                    leadingIcon = { Icon(Icons.Outlined.Search, null, tint = textSecondary) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = surfaceAlphaColor,
                        unfocusedContainerColor = surfaceAlphaColor,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = textPrimary,
                        unfocusedTextColor = textPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp)
                )
            }

            Spacer(Modifier.height(12.dp))

            // Categorías de filtro
            val categories = listOf("Todos", "MINSA / Público", "Acepta SIS", "Privado")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { label ->
                    val isSelected = selectedCategory == label
                    Surface(
                        onClick = { selectedCategory = label },
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) RelaxGreen else surfaceAlphaColor,
                        shadowElevation = if (isSelected) 4.dp else 1.dp
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) Color.White else textPrimary,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        // Contador de resultados
        Surface(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .systemBarsPadding()
                .padding(top = 130.dp, end = 16.dp),
            shape = RoundedCornerShape(12.dp),
            color = surfaceAlphaColor,
            shadowElevation = 2.dp
        ) {
            Text(
                "${filteredCenters.size} centros",
                color = if (isDarkTheme) RelaxGreen else Color(0xFF0D7A6F), 
                fontSize = 12.sp, 
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            )
        }

        // ─── Botón Flotante "Mi Ubicación" ────────────────────────────────────
        if (hasPermission) {
            FloatingActionButton(
                onClick = { centerOnUser() },
                containerColor = surfaceColor,
                contentColor = RelaxGreen,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = if (selectedCenter != null) 360.dp else 32.dp)
                    .navigationBarsPadding()
            ) {
                Icon(Icons.Filled.MyLocation, contentDescription = "Ir a mi ubicación")
            }
        }

        // ─── Panel inferior ───────────────────────────────────────────────────
        AnimatedVisibility(
            visible = selectedCenter != null,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            selectedCenter?.let { center ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                    color = surfaceColor,
                    shadowElevation = 16.dp
                ) {
                    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp).navigationBarsPadding()) {
                        Box(
                            modifier = Modifier.width(40.dp).height(4.dp)
                                .background(textSecondary.copy(alpha = 0.3f), RoundedCornerShape(2.dp))
                                .align(Alignment.CenterHorizontally)
                        )
                        Spacer(Modifier.height(16.dp))
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(center.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = textPrimary)
                                Spacer(Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    val isMinsa = center.type.contains("MINSA", ignoreCase = true)
                                    val typeColor = if (center.isEmergency) AccentRed else if (isMinsa) RelaxGreen else AccentPurple
                                    
                                    Surface(shape = RoundedCornerShape(6.dp), color = typeColor.copy(alpha = 0.15f)) {
                                        Text(center.type, color = typeColor, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                    }
                                    if (center.acceptsSIS) {
                                        Surface(shape = RoundedCornerShape(6.dp), color = RelaxGreen.copy(alpha = 0.15f)) {
                                            Text("✓ Acepta SIS", color = RelaxGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                        }
                                    }
                                }
                            }
                            IconButton(onClick = { selectedCenter = null }) {
                                Icon(Icons.Outlined.Close, "Cerrar", tint = textSecondary)
                            }
                        }
                        
                        Spacer(Modifier.height(16.dp))
                        HorizontalDivider(color = dividerColor)
                        Spacer(Modifier.height(16.dp))

                        MapInfoRow(Icons.Outlined.LocationOn, center.address, RelaxGreen, textPrimary)
                        Spacer(Modifier.height(12.dp))
                        MapInfoRow(Icons.Outlined.Schedule, center.schedule, RelaxGreen, textPrimary)
                        Spacer(Modifier.height(12.dp))
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Phone, null, tint = RelaxGreen, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(12.dp))
                            Text(center.phone, color = textPrimary, style = MaterialTheme.typography.bodyMedium, fontSize = 15.sp)
                            Spacer(Modifier.weight(1f))
                            Surface(
                                onClick = { context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${center.phone}"))) },
                                color = RelaxGreen.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Llamar", color = RelaxGreen, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
                            }
                        }

                        Spacer(Modifier.height(24.dp))
                        
                        Button(
                            onClick = {
                                val uri = Uri.parse("google.navigation:q=${center.location.latitude},${center.location.longitude}")
                                val intent = Intent(Intent.ACTION_VIEW, uri).apply { setPackage("com.google.android.apps.maps") }
                                if (intent.resolveActivity(context.packageManager) != null) context.startActivity(intent)
                                else context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com/?q=${center.location.latitude},${center.location.longitude}")))
                            },
                            modifier = Modifier.fillMaxWidth().height(54.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = if(center.isEmergency) AccentRed else RelaxGreen),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(Icons.Outlined.Directions, null, modifier = Modifier.size(22.dp), tint = Color.White)
                            Spacer(Modifier.width(8.dp))
                            Text("Cómo llegar rápido", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MapInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector, 
    text: String, 
    iconTint: Color, 
    textColor: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = iconTint, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(12.dp))
        Text(text, color = textColor, style = MaterialTheme.typography.bodyMedium, fontSize = 15.sp)
    }
}
