package com.upn.relaxmind.feature.profile.ui

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.fragment.app.FragmentActivity
import com.upn.relaxmind.core.data.auth.AuthManager
import com.upn.relaxmind.core.ui.theme.RelaxBackground
import com.upn.relaxmind.core.ui.theme.RelaxGreen
import com.upn.relaxmind.core.ui.theme.RelaxMutedText
import com.upn.relaxmind.core.utils.BiometricHelper
import java.util.Calendar

@Composable
fun ProfileScreen(modifier: Modifier = Modifier, onBack: () -> Unit) {
    val context = LocalContext.current
    val currentUser = remember { AuthManager.getCurrentUser(context) }
    
    // Local state for editing
    var name by remember { mutableStateOf(currentUser?.name ?: "") }
    var lastName by remember { mutableStateOf(currentUser?.lastName ?: "") }
    var birthday by remember { mutableStateOf(currentUser?.birthDate ?: "15/05/1990") }
    var phoneNumber by remember { mutableStateOf(currentUser?.phoneNumber ?: "") }
    var condition by remember { mutableStateOf(currentUser?.condition ?: "Paciente") }
    
    // App settings
    var isDarkMode by remember { mutableStateOf(false) }
    var highContrast by remember { mutableStateOf(false) }
    var healthConnectEnabled by remember { mutableStateOf(true) }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var biometricEnabled by remember { mutableStateOf(currentUser?.biometricEnabled ?: false) }
    
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Cuenta", "Editar", "Ajustes", "Seguridad")

    val hasChanges = name != (currentUser?.name ?: "") || 
                     lastName != (currentUser?.lastName ?: "") ||
                     phoneNumber != (currentUser?.phoneNumber ?: "") ||
                     birthday != (currentUser?.birthDate ?: "") || 
                     condition != (currentUser?.condition ?: "")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(RelaxBackground)
            .systemBarsPadding()
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                onClick = onBack,
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                modifier = Modifier.size(44.dp),
                shadowElevation = 2.dp
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
                color = Color(0xFF1E293B)
            )
            Spacer(modifier = Modifier.weight(1f))
            
            if (hasChanges) {
                TextButton(onClick = {
                    AuthManager.updateProfile(context, name, lastName, phoneNumber, birthday    , condition, currentUser?.avatar)
                    Toast.makeText(context, "Perfil actualizado", Toast.LENGTH_SHORT).show()
                    onBack() // Or just refresh
                }) {
                    Text("Guardar", color = RelaxGreen, fontWeight = FontWeight.Bold)
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            // Profile Overview Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .shadow(12.dp, RoundedCornerShape(24.dp), spotColor = Color(0x1A000000))
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White)
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    com.upn.relaxmind.core.ui.components.UserAvatar(user = currentUser, size = 68, fontSize = 28)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("$name $lastName".trim().ifEmpty { "Usuario" }, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("$birthday • Paciente", style = MaterialTheme.typography.bodyMedium, color = RelaxMutedText)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Custom Segmented Tabs
            Surface(
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(26.dp),
                color = Color(0xFFE2E8F0).copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp)
                        .horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    tabs.forEachIndexed { index, title ->
                        val isSelected = selectedTabIndex == index
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .fillMaxHeight()
                                .widthIn(min = 80.dp)
                                .clip(RoundedCornerShape(22.dp))
                                .background(if (isSelected) Color.White else Color.Transparent)
                                .clickable { selectedTabIndex = index },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color(0xFF1E293B) else RelaxMutedText
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            AnimatedContent(
                targetState = selectedTabIndex,
                transitionSpec = { fadeIn().togetherWith(fadeOut()) },
                label = "tabAnimation"
            ) { targetIndex ->
                Box(
                    modifier = Modifier.fillMaxWidth().shadow(12.dp, RoundedCornerShape(24.dp), spotColor = Color(0x1A000000)).clip(RoundedCornerShape(24.dp)).background(Color.White)
                ) {
                    Column {
                        when (targetIndex) {
                            0 -> { // TAB CUENTA (RESUMEN)
                                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                    InfoCard(label = "Correo Electrónico", value = currentUser?.email ?: "Sin correo", icon = Icons.Outlined.Email)
                                    InfoCard(label = "Rol de Usuario", value = condition, icon = Icons.Outlined.AccountCircle)
                                    InfoCard(label = "ID de Usuario", value = currentUser?.id?.take(8) ?: "---", icon = Icons.Outlined.Fingerprint)
                                    
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = { selectedTabIndex = 1 },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(containerColor = RelaxGreen),
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        Text("Editar Información", color = Color.White)
                                    }
                                }
                            }
                            1 -> { // TAB EDICIÓN (CAMPOS DE TEXTO)
                                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                    OutlinedTextField(
                                        value = name,
                                        onValueChange = { name = it },
                                        label = { Text("Nombre") },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(16.dp),
                                        singleLine = true,
                                        leadingIcon = { Icon(Icons.Outlined.Person, null, tint = RelaxGreen) }
                                    )
                                    OutlinedTextField(
                                        value = lastName,
                                        onValueChange = { lastName = it },
                                        label = { Text("Apellido") },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(16.dp),
                                        singleLine = true,
                                        leadingIcon = { Icon(Icons.Outlined.AssignmentInd, null, tint = RelaxGreen) }
                                    )
                                    
                                    val calendar = Calendar.getInstance()
                                    val datePickerDialog = DatePickerDialog(
                                        context,
                                        { _, year, month, dayOfMonth ->
                                            birthday = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year)
                                        },
                                        calendar.get(Calendar.YEAR),
                                        calendar.get(Calendar.MONTH),
                                        calendar.get(Calendar.DAY_OF_MONTH)
                                    )

                                    OutlinedTextField(
                                        value = birthday,
                                        onValueChange = { birthday = it },
                                        label = { Text("Fecha de Nacimiento") },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(16.dp),
                                        readOnly = true,
                                        leadingIcon = { Icon(Icons.Outlined.CalendarToday, null, tint = RelaxGreen) },
                                        trailingIcon = {
                                            IconButton(onClick = { datePickerDialog.show() }) {
                                                Icon(Icons.Outlined.Edit, null, tint = RelaxGreen)
                                            }
                                        },
                                        interactionSource = remember { MutableInteractionSource() }.also { interactionSource ->
                                            LaunchedEffect(interactionSource) {
                                                // interactionSource.collectIsPressedAsState() could be used here
                                            }
                                        }
                                    )
                                    
                                    OutlinedTextField(
                                        value = phoneNumber,
                                        onValueChange = { 
                                            if (it.length <= 9 && it.all { char -> char.isDigit() }) {
                                                phoneNumber = it 
                                            }
                                        },
                                        label = { Text("Número de Celular (9 dígitos)") },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(16.dp),
                                        leadingIcon = { Icon(Icons.Outlined.Phone, null, tint = RelaxGreen) },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = RelaxGreen,
                                            focusedLabelColor = RelaxGreen
                                        )
                                    )

                                    OutlinedTextField(
                                        value = condition,
                                        onValueChange = { condition = it },
                                        label = { Text("Condición / Diagnóstico") },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(16.dp),
                                        leadingIcon = { Icon(Icons.Outlined.Info, null, tint = RelaxGreen) }
                                    )
                                }
                            }
                            2 -> { // TAB ACCESIBILIDAD
                                ToggleSettingItem(icon = Icons.Outlined.DarkMode, tint = Color(0xFF6366F1), title = "Modo Oscuro", isChecked = isDarkMode, onCheckedChange = { isDarkMode = it })
                                HorizontalDivider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(horizontal = 20.dp))
                                ToggleSettingItem(icon = Icons.Outlined.Visibility, tint = Color(0xFFF59E0B), title = "Alto Contraste", isChecked = highContrast, onCheckedChange = { highContrast = it })
                                HorizontalDivider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(horizontal = 20.dp))
                                SimpleSettingItem(icon = Icons.Outlined.FormatSize, tint = Color(0xFF10B981), title = "Tamaño de Fuente", subtitle = "Mediano")
                            }
                            3 -> { // TAB SEGURIDAD
                                ToggleSettingItem(
                                    icon = Icons.Outlined.Fingerprint, 
                                    tint = RelaxGreen, 
                                    title = "Acceso Biométrico", 
                                    subtitle = "Huella o Reconocimiento Facial", 
                                    isChecked = biometricEnabled, 
                                    onCheckedChange = { checked ->
                                        if (checked) {
                                            // Trigger auth before enabling
                                            val activity = context as? FragmentActivity
                                            if (activity != null) {
                                                BiometricHelper.authenticate(
                                                    activity = activity,
                                                    title = "Confirmar Biometría",
                                                    subtitle = "Escanea tu huella para habilitar el acceso rápido",
                                                    onSuccess = {
                                                        biometricEnabled = true
                                                        AuthManager.setBiometricEnabled(context, true)
                                                        Toast.makeText(context, "Biometría habilitada con éxito", Toast.LENGTH_SHORT).show()
                                                    },
                                                    onError = {
                                                        Toast.makeText(context, "Error: $it", Toast.LENGTH_SHORT).show()
                                                    }
                                                )
                                            }
                                        } else {
                                            biometricEnabled = false
                                            AuthManager.setBiometricEnabled(context, false)
                                        }
                                    }
                                )
                                HorizontalDivider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(horizontal = 20.dp))
                                ToggleSettingItem(icon = Icons.Outlined.NotificationsActive, tint = Color(0xFFEF4444), title = "Notificaciones", subtitle = "Recordatorios de hábitos", isChecked = notificationsEnabled, onCheckedChange = { notificationsEnabled = it })
                                HorizontalDivider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(horizontal = 20.dp))
                                ToggleSettingItem(icon = Icons.Outlined.FavoriteBorder, tint = Color(0xFFEC4899), title = "Health Connect", subtitle = "Sincroniza pasos y sueño", isChecked = healthConnectEnabled, onCheckedChange = { healthConnectEnabled = it })
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            TextButton(
                onClick = { /* TODO */ },
                modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
                colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFEF4444))
            ) {
                Icon(Icons.Outlined.DeleteForever, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Eliminar cuenta permanentemente", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun InfoCard(label: String, value: String, icon: ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(40.dp).clip(CircleShape).background(RelaxGreen.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = RelaxGreen, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = RelaxMutedText)
            Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun ToggleSettingItem(icon: ImageVector, tint: Color, title: String, subtitle: String? = null, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 18.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(42.dp).clip(CircleShape).background(tint.copy(alpha = 0.12f)), contentAlignment = Alignment.Center) {
            Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
            if (subtitle != null) Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = RelaxMutedText)
        }
        Switch(checked = isChecked, onCheckedChange = onCheckedChange, colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = tint))
    }
}

@Composable
private fun SimpleSettingItem(icon: ImageVector, tint: Color, title: String, subtitle: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 18.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(42.dp).clip(CircleShape).background(tint.copy(alpha = 0.12f)), contentAlignment = Alignment.Center) {
            Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = RelaxMutedText)
        }
        Icon(Icons.Outlined.ChevronRight, null, tint = RelaxMutedText.copy(alpha = 0.3f), modifier = Modifier.size(20.dp))
    }
}
