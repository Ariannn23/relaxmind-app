package com.upn.relaxmind.feature.profile.ui

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upn.relaxmind.R
import com.upn.relaxmind.core.data.preferences.AppPreferences
import com.upn.relaxmind.core.data.auth.AuthManager
import com.upn.relaxmind.core.ui.theme.LocalIsDarkTheme
import com.upn.relaxmind.core.ui.theme.RelaxGreen
import com.upn.relaxmind.core.ui.theme.RelaxMutedText
import com.upn.relaxmind.core.ui.components.RelaxBackButton
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
    var name           by remember { mutableStateOf(user?.name           ?: "") }
    var lastName       by remember { mutableStateOf(user?.lastName       ?: "") }
    var phoneNumber    by remember { mutableStateOf(user?.phoneNumber    ?: "") }
    
    // Almacenamos solo los dígitos de la fecha para evitar saltos del cursor con el VisualTransformation
    var birthdayDigits by remember { 
        mutableStateOf(user?.birthDate?.filter { it.isDigit() } ?: "15051990") 
    }
    
    var condition      by remember { mutableStateOf(user?.condition      ?: "Paciente") }
    var selectedAvatar by remember { mutableStateOf(user?.avatar) }
    var showAvatarMenu by remember { mutableStateOf(false) }

    val currentFormattedDate = formatAsDate(birthdayDigits)
    val hasChanges = name           != (user?.name      ?: "") ||
                     lastName       != (user?.lastName  ?: "") ||
                     phoneNumber    != (user?.phoneNumber ?: "") ||
                     currentFormattedDate != (user?.birthDate ?: "") ||
                     condition      != (user?.condition ?: "") ||
                     selectedAvatar != user?.avatar

    // ── Colors ────────────────────────────────────────────────────────────────
    val bgColor      = MaterialTheme.colorScheme.background
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurface    = MaterialTheme.colorScheme.onSurface
    val mutedText    = if (isDark) MaterialTheme.colorScheme.onSurfaceVariant else RelaxMutedText
    val accentGreen  = MaterialTheme.colorScheme.primary

    // ── Initials fallback ─────────────────────────────────────────────────────
    val initials = buildString {
        name.firstOrNull()?.let { append(it.uppercaseChar()) }
        lastName.firstOrNull()?.let { append(it.uppercaseChar()) }
    }.ifBlank { "U" }

    // ── Avatars list ──────────────────────────────────────────────────────────
    val avatarOptions = remember {
        listOf(
            "avatar_buho", "avatar_chia_agua", "avatar_chica_hada", "avatar_chica_luna",
            "avatar_chica_morado", "avatar_chico_celeste", "avatar_chico_verde",
            "avatar_conejo", "avatar_dragon", "avatar_gato", "avatar_koala",
            "avatar_oso", "avatar_panda", "avatar_zorro"
        )
    }

    // ── Date picker ───────────────────────────────────────────────────────────
    val cal = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, day ->
            val d = String.format("%02d", day)
            val m = String.format("%02d", month + 1)
            val y = year.toString()
            birthdayDigits = "$d$m$y"
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
                    Box(modifier = Modifier.padding(start = 12.dp)) {
                        RelaxBackButton(onClick = onBack, modifier = Modifier.size(40.dp))
                    }
                },
                actions = {
                    if (hasChanges) {
                        TextButton(onClick = {
                            AuthManager.updateProfile(context, name, lastName, phoneNumber, currentFormattedDate, condition, selectedAvatar)
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
            Spacer(modifier = Modifier.height(16.dp))

            // ── Avatar preview ─────────────────────────────────────────────────
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier.size(140.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(accentGreen.copy(0.1f))
                            .clickable { showAvatarMenu = true },
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedAvatar != null) {
                            val resId = context.resources.getIdentifier(selectedAvatar, "drawable", context.packageName)
                            if (resId != 0) {
                                Image(
                                    painter = painterResource(resId),
                                    contentDescription = "Avatar",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                FallbackInitials(initials)
                            }
                        } else {
                            FallbackInitials(initials)
                        }
                    }

                    // Edit Badge (Pencil)
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(40.dp)
                            .offset(x = (-4).dp, y = (-4).dp)
                            .shadow(8.dp, CircleShape),
                        shape = CircleShape,
                        color = accentGreen,
                        tonalElevation = 6.dp
                    ) {
                        Icon(
                            Icons.Outlined.Edit,
                            null,
                            tint = Color.White,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── Form fields ────────────────────────────────────────────────────
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
                EditField(
                    value = phoneNumber,
                    onValueChange = { 
                        if (it.length <= 9 && it.all { char -> char.isDigit() }) {
                            phoneNumber = it 
                        }
                    },
                    label = "Número de Celular (9 dígitos)",
                    icon = Icons.Outlined.Phone,
                    accent = accentGreen,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                OutlinedTextField(
                    value = birthdayDigits,
                    onValueChange = { input ->
                        if (input.length <= 8 && input.all { it.isDigit() }) {
                            birthdayDigits = input
                        }
                    },
                    label = { Text("Fecha de Nacimiento (DD/MM/AAAA)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    visualTransformation = DateVisualTransformation(),
                    leadingIcon = { Icon(Icons.Outlined.CalendarToday, null, tint = accentGreen) },
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

            Spacer(modifier = Modifier.height(32.dp))

            if (hasChanges) {
                Button(
                    onClick = {
                        AuthManager.updateProfile(context, name, lastName, phoneNumber, currentFormattedDate, condition, selectedAvatar)
                        AppPreferences.saveDisplayName(context, "$name $lastName".trim())
                        Toast.makeText(context, "Perfil actualizado ✓", Toast.LENGTH_SHORT).show()
                        onSaved()
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = accentGreen)
                ) {
                    Text("Guardar Cambios", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }

        if (showAvatarMenu) {
            ModalBottomSheet(
                onDismissRequest = { showAvatarMenu = false },
                containerColor = surfaceColor,
                dragHandle = { BottomSheetDefaults.DragHandle() }
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(24.dp).padding(bottom = 20.dp)) {
                    Text(
                        "Elige un Avatar",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(bottom = 20.dp)
                    )
                    
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.heightIn(max = 400.dp)
                    ) {
                        items(avatarOptions) { avatarName ->
                            val resId = context.resources.getIdentifier(avatarName, "drawable", context.packageName)
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (selectedAvatar == avatarName) accentGreen.copy(0.15f) else Color.Transparent)
                                    .border(
                                        width = 2.dp,
                                        color = if (selectedAvatar == avatarName) accentGreen else Color.Transparent,
                                        shape = RoundedCornerShape(20.dp)
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
                                    modifier = Modifier.size(64.dp).clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                        
                        item {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .border(1.dp, mutedText.copy(0.3f), RoundedCornerShape(20.dp))
                                    .clickable {
                                        selectedAvatar = null
                                        showAvatarMenu = false
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Outlined.Person, null, tint = mutedText)
                                    Text("Sin avatar", style = MaterialTheme.typography.labelSmall, color = mutedText)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FallbackInitials(initials: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = initials,
            fontSize = 42.sp,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

// ── Private helpers ────────────────────────────────────────────────────────────

private fun formatAsDate(digits: String): String {
    val out = StringBuilder()
    for (i in digits.indices) {
        out.append(digits[i])
        if (i == 1 || i == 3) out.append("/")
    }
    return out.toString()
}

class DateVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.text.length >= 8) text.text.substring(0, 8) else text.text
        var out = ""
        for (i in trimmed.indices) {
            out += trimmed[i]
            if (i == 1 || i == 3) out += "/"
        }

        val dateOffsetTranslator = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 1) return offset
                if (offset <= 3) return offset + 1
                if (offset <= 8) return offset + 2
                return out.length
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 2) return offset
                if (offset <= 5) return offset - 1
                if (offset <= 10) return offset - 2
                return text.text.length
            }
        }

        return TransformedText(AnnotatedString(out), dateOffsetTranslator)
    }
}

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
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
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
        keyboardOptions = keyboardOptions,
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
