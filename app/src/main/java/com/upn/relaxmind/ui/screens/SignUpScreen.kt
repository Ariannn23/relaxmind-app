package com.upn.relaxmind.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.*
import androidx.compose.ui.text.input.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.upn.relaxmind.ui.theme.RelaxGreen
import com.upn.relaxmind.ui.theme.RelaxGreenSoft
import com.upn.relaxmind.ui.theme.RelaxMutedText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    onCreateAccount: (name: String, email: String, password: String) -> Unit,
    onBackToLogin: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val isEmailValid = email.isBlank() || android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val doPasswordsMatch = confirmPassword.isBlank() || password == confirmPassword
    val isFormValid = fullName.isNotBlank() && email.isNotBlank() && password.isNotBlank() &&
        confirmPassword.isNotBlank() && isEmailValid && doPasswordsMatch
    LaunchedEffect(isFormValid) {
        if (isFormValid) {
            focusManager.clearFocus(force = true)
            keyboardController?.hide()
        }
    }

    val gradient = Brush.linearGradient(listOf(Color(0xFF047857), RelaxGreen, RelaxGreenSoft))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear cuenta", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackToLogin) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .imePadding() // Ensures content is pushed up by keyboard
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            
            Image(
                painter = painterResource(id = com.upn.relaxmind.R.drawable.registro_screen),
                contentDescription = null,
                modifier = Modifier
                    .size(150.dp)
                    .clip(RoundedCornerShape(24.dp))
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Únete a RelaxMind",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Completa tus datos para comenzar tu viaje.",
                style = MaterialTheme.typography.bodyLarge,
                color = RelaxMutedText,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))

            SignUpInput(
                value = fullName,
                onValueChange = { fullName = it },
                placeholder = "Nombre completo",
                imeAction = androidx.compose.ui.text.input.ImeAction.Next
            )
            Spacer(modifier = Modifier.height(12.dp))
            SignUpInput(
                value = email,
                onValueChange = { email = it },
                placeholder = "Correo electronico",
                keyboardType = KeyboardType.Email,
                imeAction = androidx.compose.ui.text.input.ImeAction.Next,
                isError = !isEmailValid
            )
            Spacer(modifier = Modifier.height(12.dp))
            SignUpInput(
                value = password,
                onValueChange = { password = it },
                placeholder = "Contrasena",
                keyboardType = KeyboardType.Password,
                isPassword = true,
                imeAction = androidx.compose.ui.text.input.ImeAction.Next,
                passwordVisible = passwordVisible,
                onTogglePassword = { passwordVisible = !passwordVisible }
            )
            
            if (password.isNotEmpty()) {
                val strength = remember(password) {
                    var s = 0f
                    if (password.length >= 8) s += 0.25f
                    if (password.any { it.isDigit() }) s += 0.25f
                    if (password.any { it.isUpperCase() }) s += 0.25f
                    if (password.any { it.isLowerCase() }) s += 0.25f
                    s
                }
                val strengthColor = when {
                    strength < 0.5f -> Color(0xFFEF4444) // Red
                    strength < 0.75f -> Color(0xFFF59E0B) // Amber
                    else -> Color(0xFF10B981) // Green
                }
                val strengthText = when {
                    strength < 0.5f -> "Debil"
                    strength < 0.75f -> "Media"
                    else -> "Fuerte"
                }

                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp)) {
                    androidx.compose.material3.LinearProgressIndicator(
                        progress = { strength },
                        modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                        color = strengthColor,
                        trackColor = Color(0xFFE5E7EB)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Seguridad: $strengthText",
                        style = MaterialTheme.typography.labelSmall,
                        color = strengthColor
                    )
                }
            }

            Text(
                text = "* La contrasena debe tener al menos 8 caracteres, letras y numeros.",
                style = MaterialTheme.typography.labelSmall,
                color = RelaxMutedText.copy(alpha = 0.7f),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp).align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(12.dp))
            SignUpInput(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                placeholder = "Confirmar contrasena",
                keyboardType = KeyboardType.Password,
                isPassword = true,
                isError = !doPasswordsMatch,
                imeAction = androidx.compose.ui.text.input.ImeAction.Done,
                passwordVisible = confirmPasswordVisible,
                onTogglePassword = { confirmPasswordVisible = !confirmPasswordVisible }
            )

            Spacer(modifier = Modifier.height(32.dp))
            GradientActionButton(
                text = "CREAR CUENTA",
                enabled = isFormValid,
                brush = gradient,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                onClick = { onCreateAccount(fullName.trim(), email.trim(), password) }
            ) {
                Text("CREAR CUENTA", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "¿Ya tienes cuenta? Inicia sesion",
                style = MaterialTheme.typography.labelLarge,
                color = RelaxGreen,
                modifier = Modifier.clickable(onClick = onBackToLogin)
            )
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun SignUpInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: androidx.compose.ui.text.input.ImeAction = androidx.compose.ui.text.input.ImeAction.Default,
    isPassword: Boolean = false,
    isError: Boolean = false,
    passwordVisible: Boolean = false,
    onTogglePassword: (() -> Unit)? = null
) {
    val focusManager = LocalFocusManager.current
    val visualTransformation = if (isPassword && !passwordVisible) {
        androidx.compose.ui.text.input.PasswordVisualTransformation()
    } else {
        androidx.compose.ui.text.input.VisualTransformation.None
    }
    androidx.compose.foundation.text.BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
        keyboardActions = androidx.compose.foundation.text.KeyboardActions(
            onNext = { focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Next) },
            onDone = { focusManager.clearFocus() }
        ),
        visualTransformation = visualTransformation,
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF1F2937)),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color.White, RoundedCornerShape(24.dp))
            .border(
                width = 1.dp,
                color = if (isError) Color(0xFFFCA5A5) else Color(0xFFE5ECF1),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(horizontal = 18.dp),
        decorationBox = { inner ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    if (value.isBlank()) {
                        Text(placeholder, color = RelaxMutedText.copy(alpha = 0.7f))
                    }
                    inner()
                }
                if (isPassword && onTogglePassword != null) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                        contentDescription = if (passwordVisible) "Ocultar contrasena" else "Mostrar contrasena",
                        tint = RelaxMutedText.copy(alpha = 0.8f),
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .clickable(onClick = onTogglePassword)
                    )
                }
            }
        }
    )
}

@Composable
private fun GradientActionButton(
    text: String,
    brush: Brush,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    val interaction = remember { MutableInteractionSource() }
    Surface(
        modifier = modifier
            .background(Color.Transparent, RoundedCornerShape(24.dp))
            .clickable(
                enabled = enabled,
                interactionSource = interaction,
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(24.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = if (enabled) brush else Brush.linearGradient(
                        listOf(Color(0xFF9CA3AF), Color(0xFF9CA3AF))
                    ),
                    shape = RoundedCornerShape(24.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}
