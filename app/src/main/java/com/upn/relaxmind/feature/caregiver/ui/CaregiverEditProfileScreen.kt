package com.upn.relaxmind.feature.caregiver.ui

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upn.relaxmind.core.data.preferences.AppPreferences
import com.upn.relaxmind.core.data.auth.AuthManager
import com.upn.relaxmind.core.ui.components.RelaxBackButton
import com.upn.relaxmind.core.ui.theme.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaregiverEditProfileScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit = onBack
) {
    val context = LocalContext.current
    val user = remember { AuthManager.getCurrentUser(context) }
    val isDark = LocalIsDarkTheme.current

    val bgColor = if (isDark) Color(0xFF0F172A) else CaregiverBg
    val surfaceColor = if (isDark) Color(0xFF1E293B) else Color.White
    val textColor = if (isDark) Color.White else Color(0xFF1E293B)
    val mutedTextColor = if (isDark) Color.LightGray.copy(0.7f) else Color.Gray

    var name by remember { mutableStateOf(user?.name ?: "") }
    var lastName by remember { mutableStateOf(user?.lastName ?: "") }
    var phoneNumber by remember { mutableStateOf(user?.phoneNumber ?: "") }
    var selectedAvatar by remember { mutableStateOf(user?.avatar) }
    var showAvatarMenu by remember { mutableStateOf(false) }

    val hasChanges = name != (user?.name ?: "") ||
                     lastName != (user?.lastName ?: "") ||
                     phoneNumber != (user?.phoneNumber ?: "") ||
                     selectedAvatar != user?.avatar

    val initials = buildString {
        name.firstOrNull()?.let { append(it.uppercaseChar()) }
        lastName.firstOrNull()?.let { append(it.uppercaseChar()) }
    }.ifBlank { "C" }

    val avatarOptions = remember {
        listOf(
            "avatar_buho", "avatar_chia_agua", "avatar_chica_hada", "avatar_chica_luna",
            "avatar_chica_morado", "avatar_chico_celeste", "avatar_chico_verde",
            "avatar_conejo", "avatar_dragon", "avatar_gato", "avatar_koala",
            "avatar_oso", "avatar_panda", "avatar_zorro"
        )
    }

    Scaffold(
        containerColor = bgColor,
        topBar = {
            TopAppBar(
                title = { Text("Editar Perfil", fontWeight = FontWeight.Bold, color = textColor) },
                navigationIcon = {
                    Box(modifier = Modifier.padding(start = 12.dp)) {
                        RelaxBackButton(onClick = onBack, modifier = Modifier.size(40.dp))
                    }
                },
                actions = {
                    if (hasChanges) {
                        TextButton(onClick = {
                            AuthManager.updateProfile(context, name, lastName, phoneNumber, user?.birthDate ?: "", user?.condition ?: "", selectedAvatar)
                            Toast.makeText(context, "Perfil actualizado", Toast.LENGTH_SHORT).show()
                            onSaved()
                        }) {
                            Text("Guardar", color = CaregiverBlue, fontWeight = FontWeight.Black)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar Preview with 3D effect
            Box(
                modifier = Modifier.size(160.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(
                            Brush.verticalGradient(
                                if (isDark) listOf(Color(0xFF334155), Color(0xFF0F172A))
                                else listOf(CaregiverLightBlue, Color.White)
                            )
                        )
                        .border(
                            3.dp, 
                            if (isDark) CaregiverBlue.copy(0.4f) else CaregiverBlue.copy(0.2f), 
                            CircleShape
                        )
                        .shadow(if (isDark) 0.dp else 16.dp, CircleShape)
                        .clickable { showAvatarMenu = true },
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedAvatar != null) {
                        val resId = context.resources.getIdentifier(selectedAvatar, "drawable", context.packageName)
                        if (resId != 0) {
                            Image(
                                painter = painterResource(resId),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize().padding(16.dp),
                                contentScale = ContentScale.Fit
                            )
                        } else {
                            Text(initials, fontSize = 52.sp, fontWeight = FontWeight.Black, color = CaregiverBlue)
                        }
                    } else {
                        Text(initials, fontSize = 52.sp, fontWeight = FontWeight.Black, color = CaregiverBlue)
                    }
                }
                
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(44.dp)
                        .offset(x = (-4).dp, y = (-4).dp)
                        .shadow(8.dp, CircleShape),
                    shape = CircleShape,
                    color = CaregiverBlue
                ) {
                    Icon(
                        Icons.Outlined.PhotoCamera,
                        null,
                        tint = Color.White,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Surface(
                shape = RoundedCornerShape(32.dp),
                color = surfaceColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = if (isDark) 0.dp else 12.dp,
                        shape = RoundedCornerShape(32.dp),
                        spotColor = Color.Black.copy(0.1f)
                    )
            ) {
                Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    Text(
                        "INFORMACIÓN PERSONAL", 
                        style = MaterialTheme.typography.labelMedium, 
                        color = CaregiverBlue,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp
                    )
                    
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nombre") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        leadingIcon = { Icon(Icons.Outlined.Person, null, tint = CaregiverBlue) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CaregiverBlue,
                            unfocusedBorderColor = mutedTextColor.copy(0.3f),
                            focusedLabelColor = CaregiverBlue,
                            focusedTextColor = textColor,
                            unfocusedTextColor = textColor
                        )
                    )

                    OutlinedTextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        label = { Text("Apellido") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        leadingIcon = { Icon(Icons.Outlined.AssignmentInd, null, tint = CaregiverBlue) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CaregiverBlue,
                            unfocusedBorderColor = mutedTextColor.copy(0.3f),
                            focusedLabelColor = CaregiverBlue,
                            focusedTextColor = textColor,
                            unfocusedTextColor = textColor
                        )
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
                        leadingIcon = { Icon(Icons.Outlined.Phone, null, tint = CaregiverBlue) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CaregiverBlue,
                            unfocusedBorderColor = mutedTextColor.copy(0.3f),
                            focusedLabelColor = CaregiverBlue,
                            focusedTextColor = textColor,
                            unfocusedTextColor = textColor
                        )
                    )

                    Button(
                        onClick = {
                            AuthManager.updateProfile(context, name, lastName, phoneNumber, user?.birthDate ?: "", user?.condition ?: "", selectedAvatar)
                            Toast.makeText(context, "Perfil actualizado ✓", Toast.LENGTH_SHORT).show()
                            onSaved()
                        },
                        enabled = hasChanges,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CaregiverBlue,
                            disabledContainerColor = CaregiverBlue.copy(0.3f)
                        )
                    ) {
                        Text("Guardar Cambios", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            TextButton(onClick = onBack) {
                Text("Cancelar", color = mutedTextColor, fontWeight = FontWeight.Medium)
            }
        }

        if (showAvatarMenu) {
            ModalBottomSheet(
                onDismissRequest = { showAvatarMenu = false },
                containerColor = surfaceColor,
                scrimColor = Color.Black.copy(0.5f)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
                    Text(
                        "Elige un Avatar", 
                        style = MaterialTheme.typography.titleLarge, 
                        fontWeight = FontWeight.Black,
                        color = textColor
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.heightIn(max = 450.dp)
                    ) {
                        items(avatarOptions) { avatarName ->
                            val resId = context.resources.getIdentifier(avatarName, "drawable", context.packageName)
                            Box(
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(if (selectedAvatar == avatarName) CaregiverBlue.copy(0.12f) else Color.Transparent)
                                    .border(
                                        2.dp, 
                                        if (selectedAvatar == avatarName) CaregiverBlue else Color.Transparent, 
                                        RoundedCornerShape(24.dp)
                                    )
                                    .clickable {
                                        selectedAvatar = avatarName
                                        showAvatarMenu = false
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(resId),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize().padding(12.dp),
                                    contentScale = ContentScale.Fit
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}
