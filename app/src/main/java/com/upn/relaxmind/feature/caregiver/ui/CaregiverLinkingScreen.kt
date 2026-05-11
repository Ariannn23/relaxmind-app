package com.upn.relaxmind.feature.caregiver.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upn.relaxmind.core.data.auth.AuthManager
import com.upn.relaxmind.core.ui.components.RelaxBackButton
import com.upn.relaxmind.core.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaregiverLinkingScreen(
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    onOpenScanner: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var code by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var detectedPatient by remember { mutableStateOf<com.upn.relaxmind.core.data.models.User?>(null) }

    Scaffold(
        containerColor = CaregiverBg,
        topBar = {
            TopAppBar(
                title = { Text("Vincular Paciente", fontWeight = FontWeight.Bold) },
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(CaregiverBlue.copy(alpha = 0.1f), RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.VerifiedUser, null, tint = CaregiverBlue, modifier = Modifier.size(40.dp))
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Ingresar Código de Vinculación",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "Solicita al paciente su código de 6 dígitos desde su pantalla de seguridad.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // 6-digit input
            OutlinedTextField(
                value = code,
                onValueChange = { if (it.length <= 6) code = it.uppercase() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp),
                shape = RoundedCornerShape(16.dp),
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    letterSpacing = 8.sp
                ),
                placeholder = { 
                    Text(
                        "XXXXXX", 
                        modifier = Modifier.fillMaxWidth(), 
                        textAlign = TextAlign.Center,
                        letterSpacing = 8.sp,
                        color = Color.LightGray
                    ) 
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CaregiverBlue,
                    unfocusedBorderColor = Color.LightGray
                )
            )

            Spacer(modifier = Modifier.height(32.dp))


            Button(
                onClick = {
                    if (code.length == 6) {
                        scope.launch {
                            isLoading = true
                            // Try validating as temporary code first
                            val patientFromCode = AuthManager.validateTempCode(code)
                            val idToSearch = patientFromCode?.id ?: code
                            
                            val patient = AuthManager.getRegisteredUsers(context).find { 
                                it.id == idToSearch || it.id.take(6).uppercase() == idToSearch.uppercase() 
                            }
                            
                            isLoading = false
                            if (patient != null && patient.role == "PATIENT") {
                                detectedPatient = patient
                                showConfirmDialog = true
                            } else {
                                Toast.makeText(context, "Código inválido o vencido", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(context, "El código debe tener 6 dígitos", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CaregiverBlue),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text("Confirmar Vinculación", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                HorizontalDivider(modifier = Modifier.weight(1f))
                Text("O TAMBIÉN", modifier = Modifier.padding(horizontal = 16.dp), color = Color.Gray, fontSize = 12.sp)
                HorizontalDivider(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedButton(
                onClick = onOpenScanner,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = CaregiverBlue)
            ) {
                Icon(Icons.Outlined.QrCodeScanner, null)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Escanear Código QR", fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showConfirmDialog && detectedPatient != null) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Confirmar Vinculación", fontWeight = FontWeight.Bold) },
            text = { 
                Text("Se ha detectado a ${detectedPatient?.name} ${detectedPatient?.lastName}. ¿Deseas vincularte como su cuidador?") 
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            AuthManager.linkPatient(context, detectedPatient!!)
                            showConfirmDialog = false
                            Toast.makeText(context, "¡Viculación exitosa!", Toast.LENGTH_SHORT).show()
                            onSuccess()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CaregiverBlue)
                ) {
                    Text("Sí, Vincular")
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
