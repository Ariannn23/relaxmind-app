package com.upn.relaxmind.ui.screens

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upn.relaxmind.data.AppPreferences
import com.upn.relaxmind.data.AuthManager
import com.upn.relaxmind.ui.theme.LocalIsDarkTheme
import com.upn.relaxmind.ui.theme.RelaxGreen
import com.upn.relaxmind.ui.theme.RelaxMutedText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onSaved: () -> Unit = onBack
) {
    val context  = LocalContext.current
    val isDark   = LocalIsDarkTheme.current
    val user     = remember { AuthManager.getCurrentUser(context) }

    // ── Form state ────────────────────────────────────────────────────────────
    var name      by remember { mutableStateOf(user?.name      ?: "") }
    var lastName  by remember { mutableStateOf(user?.lastName  ?: "") }
    var birthday  by remember { mutableStateOf(user?.birthDate ?: "15/05/1990") }
    var condition by remember { mutableStateOf(user?.condition ?: "Paciente") }

    val hasChanges = name     != (user?.name      ?: "") ||
                     lastName != (user?.lastName  ?: "") ||
                     birthday != (user?.birthDate ?: "") ||
                     condition!= (user?.condition ?: "")

    // ── Colors ────────────────────────────────────────────────────────────────
    val bgColor      = MaterialTheme.colorScheme.background
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurface    = MaterialTheme.colorScheme.onSurface
    val mutedText    = if (isDark) MaterialTheme.colorScheme.onSurfaceVariant else RelaxMutedText
    val accentGreen  = MaterialTheme.colorScheme.primary

    // ── Initials avatar ───────────────────────────────────────────────────────
    val initials = buildString {
        name.firstOrNull()?.let { append(it.uppercaseChar()) }
        lastName.firstOrNull()?.let { append(it.uppercaseChar()) }
    }.ifBlank { "U" }

    // ── Stagger animation ─────────────────────────────────────────────────────
    var showContent by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { showContent = true }

    // ── Date picker ───────────────────────────────────────────────────────────
    val cal = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, day ->
            birthday = String.format("%02d/%02d/%d", day, month + 1, year)
        },
        cal.get(Calendar.YEAR),
        cal.get(Calendar.MONTH),
        cal.get(Calendar.DAY_OF_MONTH)
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = bgColor,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Editar Perfil",
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    if (hasChanges) {
                        TextButton(onClick = {
                            AuthManager.updateProfile(context, name, lastName, birthday, condition)
                            AppPreferences.saveDisplayName(context, "$name $lastName".trim())
                            Toast.makeText(context, "Perfil actualizado ✓", Toast.LENGTH_SHORT).show()
                            onSaved()
                        }) {
                            Text(
                                "Guardar",
                                color = accentGreen,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = bgColor,
                    titleContentColor = onSurface,
                    navigationIconContentColor = onSurface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(bgColor)
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // ── Avatar preview ─────────────────────────────────────────────────
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(tween(400)) + slideInVertically(tween(400)) { it / 4 }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(16.dp, RoundedCornerShape(28.dp), spotColor = accentGreen.copy(0.18f))
                        .clip(RoundedCornerShape(28.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(accentGreen, accentGreen.copy(alpha = 0.75f))
                            )
                        )
                        .padding(28.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.22f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = initials,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "$name $lastName".trim().ifBlank { "Tu nombre" },
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = user?.email ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── Form fields ────────────────────────────────────────────────────
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { it / 6 }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(8.dp, RoundedCornerShape(24.dp), spotColor = Color(0x10000000))
                        .clip(RoundedCornerShape(24.dp))
                        .background(surfaceColor)
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SectionLabel(text = "Información Personal", mutedText = mutedText)

                    EditField(
                        value = name,
                        onValueChange = { name = it },
                        label = "Nombre",
                        icon = Icons.Outlined.Person,
                        accent = accentGreen
                    )
                    EditField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        label = "Apellido",
                        icon = Icons.Outlined.AssignmentInd,
                        accent = accentGreen
                    )

                    // Date picker / Numeric input
                    val dateTransformation = remember {
                        object : androidx.compose.ui.text.input.VisualTransformation {
                            override fun filter(text: androidx.compose.ui.text.AnnotatedString): androidx.compose.ui.text.input.TransformedText {
                                val trimmed = if (text.text.length >= 8) text.text.substring(0, 8) else text.text
                                var out = ""
                                for (i in trimmed.indices) {
                                    out += trimmed[i]
                                    if (i == 1 || i == 3) out += "/"
                                }

                                val dateOffsetTranslator = object : androidx.compose.ui.text.input.OffsetMapping {
                                    override fun originalToTransformed(offset: Int): Int {
                                        if (offset <= 1) return offset
                                        if (offset <= 3) return offset + 1
                                        if (offset <= 8) return offset + 2
                                        return 10
                                    }

                                    override fun transformedToOriginal(offset: Int): Int {
                                        if (offset <= 1) return offset
                                        if (offset <= 4) return offset - 1
                                        if (offset <= 10) return offset - 2
                                        return 8
                                    }
                                }

                                return androidx.compose.ui.text.input.TransformedText(androidx.compose.ui.text.AnnotatedString(out), dateOffsetTranslator)
                            }
                        }
                    }

                    OutlinedTextField(
                        value = birthday,
                        onValueChange = { input ->
                            if (input.length <= 8 && input.all { it.isDigit() }) {
                                birthday = input
                            }
                        },
                        visualTransformation = dateTransformation,
                        label = { Text("Fecha de Nacimiento (DD/MM/AAAA)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        leadingIcon = {
                            Icon(Icons.Outlined.CalendarToday, null, tint = accentGreen)
                        },
                        trailingIcon = {
                            IconButton(onClick = { datePickerDialog.show() }) {
                                Icon(Icons.Outlined.DateRange, null, tint = accentGreen)
                            }
                        },
                        colors = outlinedTextFieldColors(accent = accentGreen)
                    )

                    EditField(
                        value = condition,
                        onValueChange = { condition = it },
                        label = "Condición / Diagnóstico",
                        icon = Icons.Outlined.Info,
                        accent = accentGreen,
                        singleLine = false,
                        modifier = Modifier.heightIn(min = 100.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Primary save button ────────────────────────────────────────────
            AnimatedVisibility(
                visible = showContent && hasChanges,
                enter = fadeIn(tween(300)) + slideInVertically(tween(300)) { it / 3 }
            ) {
                Button(
                    onClick = {
                        AuthManager.updateProfile(context, name, lastName, birthday, condition)
                        AppPreferences.saveDisplayName(context, "$name $lastName".trim())
                        Toast.makeText(context, "Perfil actualizado ✓", Toast.LENGTH_SHORT).show()
                        onSaved()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = accentGreen),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                ) {
                    Icon(Icons.Outlined.Check, null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        "Guardar Cambios",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

// ── Private helpers ────────────────────────────────────────────────────────────

@Composable
private fun SectionLabel(text: String, mutedText: Color) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = mutedText,
        letterSpacing = 0.8.sp
    )
    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
}

@Composable
private fun EditField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    accent: Color,
    singleLine: Boolean = true,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        singleLine = singleLine,
        leadingIcon = { Icon(icon, null, tint = accent) },
        colors = outlinedTextFieldColors(accent)
    )
}

@Composable
private fun outlinedTextFieldColors(accent: Color) =
    OutlinedTextFieldDefaults.colors(
        focusedBorderColor   = accent,
        focusedLabelColor    = accent,
        focusedLeadingIconColor  = accent,
        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
        cursorColor          = accent
    )
