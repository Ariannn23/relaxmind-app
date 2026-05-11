package com.upn.relaxmind.feature.caregiver.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upn.relaxmind.core.data.auth.AuthManager
import com.upn.relaxmind.core.data.models.User
import com.upn.relaxmind.core.ui.components.RelaxBackButton
import com.upn.relaxmind.core.ui.components.UserAvatar
import com.upn.relaxmind.core.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaregiverManageLinksScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var linkedPatients by remember { mutableStateOf<List<User>>(emptyList()) }
    var patientToUnlink by remember { mutableStateOf<User?>(null) }
    var patientToEdit by remember { mutableStateOf<User?>(null) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var newRelationship by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        linkedPatients = AuthManager.getLinkedUsers(context)
    }

    fun refreshList() {
        scope.launch { linkedPatients = AuthManager.getLinkedUsers(context) }
    }

    val isDark = LocalIsDarkTheme.current
    val bgColor = if (isDark) Color(0xFF0F172A) else CaregiverBg
    val surfaceColor = if (isDark) Color(0xFF1E293B) else Color.White
    val textColor = if (isDark) Color.White else Color(0xFF1E293B)
    val mutedTextColor = if (isDark) Color.LightGray.copy(0.7f) else Color.Gray

    Scaffold(
        containerColor = bgColor,
        topBar = {
            TopAppBar(
                title = { Text("Gestionar Vinculaciones", fontWeight = FontWeight.Bold, color = textColor) },
                navigationIcon = {
                    Box(modifier = Modifier.padding(start = 12.dp)) {
                        RelaxBackButton(onClick = onBack, modifier = Modifier.size(40.dp))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Pacientes Vinculados",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = textColor
                )
                Text(
                    text = "Gestiona tu red de apoyo y los permisos de visibilidad.",
                    style = MaterialTheme.typography.bodySmall,
                    color = mutedTextColor
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (linkedPatients.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(28.dp),
                        color = surfaceColor,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Outlined.GroupOff, null, tint = mutedTextColor.copy(0.3f), modifier = Modifier.size(64.dp))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("No tienes pacientes vinculados", color = mutedTextColor, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            } else {
                items(linkedPatients) { patient ->
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = surfaceColor,
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = if (isDark) 0.dp else 4.dp,
                                shape = RoundedCornerShape(24.dp),
                                spotColor = Color.Black.copy(0.1f)
                            )
                    ) {
                        Row(
                            modifier = Modifier.padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            UserAvatar(user = patient, size = 56, fontSize = 22)
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${patient.name} ${patient.lastName}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = textColor
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Outlined.FamilyRestroom, null, tint = CaregiverBlue, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = AuthManager.getCurrentUser(context)?.patientRelationships?.get(patient.id) ?: "Paciente",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = CaregiverBlue,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Row {
                                IconButton(
                                    onClick = { 
                                        patientToEdit = patient
                                        newRelationship = AuthManager.getCurrentUser(context)?.patientRelationships?.get(patient.id) ?: ""
                                        showEditDialog = true
                                    },
                                    modifier = Modifier.background(CaregiverBlue.copy(0.1f), CircleShape).size(36.dp)
                                ) {
                                    Icon(Icons.Outlined.Edit, "Editar", tint = CaregiverBlue, modifier = Modifier.size(18.dp))
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                IconButton(
                                    onClick = { 
                                        patientToUnlink = patient
                                        showConfirmDialog = true
                                    },
                                    modifier = Modifier.background(Color(0xFFEF4444).copy(0.1f), CircleShape).size(36.dp)
                                ) {
                                    Icon(Icons.Outlined.Delete, "Eliminar", tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showEditDialog && patientToEdit != null) {
        val options = listOf("Familiar", "Terapeuta", "Amigo", "Médico", "Otro")
        var expanded by remember { mutableStateOf(false) }
        var selectedOption by remember { mutableStateOf(if (options.contains(newRelationship)) newRelationship else "Otro") }
        var customRelationship by remember { mutableStateOf(if (!options.contains(newRelationship)) newRelationship else "") }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            containerColor = surfaceColor,
            title = { Text("Ajustar Vínculo", fontWeight = FontWeight.Black, color = textColor) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Define tu relación con ${patientToEdit?.name}:", color = textColor)
                    
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = selectedOption,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Tipo de Vínculo") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CaregiverBlue,
                                unfocusedBorderColor = mutedTextColor.copy(0.3f),
                                focusedLabelColor = CaregiverBlue
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.background(surfaceColor)
                        ) {
                            options.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option, color = textColor) },
                                    onClick = {
                                        selectedOption = option
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    if (selectedOption == "Otro") {
                        OutlinedTextField(
                            value = customRelationship,
                            onValueChange = { customRelationship = it },
                            label = { Text("Especificar") },
                            placeholder = { Text("Ej: Tutor") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CaregiverBlue,
                                unfocusedBorderColor = mutedTextColor.copy(0.3f)
                            )
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val finalRel = if (selectedOption == "Otro") customRelationship else selectedOption
                        scope.launch {
                            AuthManager.updateRelationship(context, patientToEdit!!.id, finalRel)
                            refreshList()
                        }
                        showEditDialog = false
                        Toast.makeText(context, "Vínculo actualizado ✓", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CaregiverBlue),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Guardar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancelar", color = mutedTextColor)
                }
            },
            shape = RoundedCornerShape(28.dp)
        )
    }

    if (showConfirmDialog && patientToUnlink != null) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            containerColor = surfaceColor,
            title = { Text("¿Eliminar Vinculación?", fontWeight = FontWeight.Black, color = textColor) },
            text = { 
                Text(
                    "¿Estás seguro de que deseas dejar de monitorear a ${patientToUnlink?.name}? Perderás acceso a su estado de bienestar actual.",
                    color = textColor
                ) 
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            AuthManager.unlinkUser(context, patientToUnlink!!.id)
                            refreshList()
                            showConfirmDialog = false
                        }
                        Toast.makeText(context, "Vínculo eliminado", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Desvincular", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Cancelar", color = mutedTextColor)
                }
            },
            shape = RoundedCornerShape(28.dp)
        )
    }
}
