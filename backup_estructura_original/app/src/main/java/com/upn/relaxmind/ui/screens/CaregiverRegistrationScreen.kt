package com.upn.relaxmind.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upn.relaxmind.data.AuthManager
import com.upn.relaxmind.ui.components.RelaxBackButton
import com.upn.relaxmind.ui.theme.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaregiverRegistrationScreen(
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var relationship by remember { mutableStateOf("Familiar") }
    var expanded by remember { mutableStateOf(false) }

    val relationships = listOf("Familiar", "Terapeuta", "Amigo", "Otro")

    Scaffold(
        containerColor = CaregiverBg,
        topBar = {
            TopAppBar(
                title = { Text("Registro Cuidador", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    Box(modifier = Modifier.padding(start = 12.dp)) {
                        RelaxBackButton(onClick = onBack, modifier = Modifier.size(40.dp))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CaregiverBg)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Text(
                text = "Crea tu cuenta de cuidador",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = Color(0xFF1E293B)
            )
            Text(
                text = "Podrás monitorear el bienestar de tus seres queridos de forma segura.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            CaregiverInputField(
                value = name,
                onValueChange = { name = it },
                label = "Nombre Completo",
                icon = Icons.Outlined.Person
            )

            Spacer(modifier = Modifier.height(16.dp))

            CaregiverInputField(
                value = email,
                onValueChange = { email = it },
                label = "Correo Electrónico",
                icon = Icons.Outlined.Email
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            CaregiverInputField(
                value = phoneNumber,
                onValueChange = { 
                    if (it.length <= 9 && it.all { char -> char.isDigit() }) {
                        phoneNumber = it 
                    }
                },
                label = "Número de Celular (9 dígitos)",
                icon = Icons.Outlined.Phone,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(16.dp))

            CaregiverInputField(
                value = password,
                onValueChange = { password = it },
                label = "Contraseña",
                icon = Icons.Outlined.Lock,
                isPassword = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Relationship Dropdown
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = relationship,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Relación con el paciente") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(16.dp),
                    leadingIcon = { Icon(Icons.Outlined.FamilyRestroom, null, tint = CaregiverBlue) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CaregiverBlue,
                        unfocusedBorderColor = Color.LightGray
                    )
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    relationships.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption) },
                            onClick = {
                                relationship = selectionOption
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = {
                    if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                        val success = AuthManager.registerUser(
                            context = context,
                            name = name,
                            email = email,
                            password = password,
                            role = "CAREGIVER",
                            phoneNumber = phoneNumber,
                            relationship = relationship
                        )
                        if (success) {
                            Toast.makeText(context, "Cuenta creada exitosamente", Toast.LENGTH_SHORT).show()
                            onSuccess()
                        } else {
                            Toast.makeText(context, "El correo ya está registrado", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CaregiverBlue)
            ) {
                Text("Crear Cuenta", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
private fun CaregiverInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    isPassword: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        leadingIcon = { Icon(icon, null, tint = CaregiverBlue) },
        visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        keyboardOptions = keyboardOptions,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = CaregiverBlue,
            unfocusedBorderColor = Color.LightGray
        )
    )
}
