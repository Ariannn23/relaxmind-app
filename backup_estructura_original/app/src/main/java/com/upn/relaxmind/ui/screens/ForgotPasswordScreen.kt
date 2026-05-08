package com.upn.relaxmind.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upn.relaxmind.ui.theme.RelaxGreen
import com.upn.relaxmind.ui.theme.RelaxGreenSoft
import com.upn.relaxmind.ui.theme.RelaxMutedText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    onPasswordReset: () -> Unit,
    onBack: () -> Unit
) {
    var step by remember { mutableIntStateOf(1) }
    var email by remember { mutableStateOf("") }
    val otpValues = remember { mutableStateListOf("", "", "", "", "", "") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val context = LocalContext.current

    val gradientPrimary = Brush.linearGradient(
        colors = listOf(Color(0xFF047857), RelaxGreen, RelaxGreenSoft)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recuperar contraseña", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { if (step > 1) step-- else onBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            AnimatedContent(
                targetState = step,
                transitionSpec = {
                    (slideInHorizontally(tween(400)) { it } + fadeIn()).togetherWith(
                        slideOutHorizontally(tween(400)) { -it } + fadeOut()
                    )
                },
                label = "forgotPasswordStep"
            ) { currentStep ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    when (currentStep) {
                        1 -> EmailStep(email, onEmailChange = { email = it })
                        2 -> CodeStep(otpValues)
                        3 -> NewPasswordStep(
                            password = newPassword,
                            confirmPassword = confirmPassword,
                            onPasswordChange = { newPassword = it },
                            onConfirmChange = { confirmPassword = it }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = {
                    when (step) {
                        1 -> {
                            if (email.isNotBlank()) step = 2
                            else Toast.makeText(context, "Ingresa tu correo", Toast.LENGTH_SHORT).show()
                        }
                        2 -> {
                            if (otpValues.all { it.isNotBlank() }) step = 3
                            else Toast.makeText(context, "Ingresa el código completo", Toast.LENGTH_SHORT).show()
                        }
                        3 -> {
                            if (newPassword.length >= 8 && newPassword == confirmPassword) onPasswordReset()
                            else if (newPassword != confirmPassword) Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                            else Toast.makeText(context, "Mínimo 8 caracteres", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(gradientPrimary, RoundedCornerShape(24.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        when (step) {
                            1 -> "ENVIAR CÓDIGO"
                            2 -> "VERIFICAR CÓDIGO"
                            else -> "CAMBIAR CONTRASEÑA"
                        },
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun EmailStep(email: String, onEmailChange: (String) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Ingresa tu correo", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text("Te enviaremos un código para restablecer tu cuenta.", textAlign = TextAlign.Center, color = RelaxMutedText)
        Spacer(modifier = Modifier.height(32.dp))
        SimpleAuthField(email, onEmailChange, "Correo electrónico", Icons.Outlined.Email)
    }
}

@Composable
private fun CodeStep(otpValues: androidx.compose.runtime.snapshots.SnapshotStateList<String>) {
    val focusRequesters = remember { List(6) { FocusRequester() } }
    
    LaunchedEffect(Unit) {
        focusRequesters.first().requestFocus()
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Verifica tu identidad", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text("Ingresa el código de 6 dígitos enviado.", textAlign = TextAlign.Center, color = RelaxMutedText)
        Spacer(modifier = Modifier.height(32.dp))
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(6) { index ->
                OtpDigitFieldSmall(
                    value = otpValues[index],
                    focusRequester = focusRequesters[index],
                    onValueChange = { digit ->
                        otpValues[index] = digit
                        if (digit.isNotEmpty() && index < 5) {
                            focusRequesters[index + 1].requestFocus()
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun NewPasswordStep(
    password: String,
    confirmPassword: String,
    onPasswordChange: (String) -> Unit,
    onConfirmChange: (String) -> Unit
) {
    var passVisible by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Establecer nueva contraseña", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text("Crea una contraseña segura y fácil de recordar.", textAlign = TextAlign.Center, color = RelaxMutedText)
        Spacer(modifier = Modifier.height(32.dp))
        
        // Password Input
        SimpleAuthField(
            password, onPasswordChange, "Nueva contraseña", Icons.Outlined.Lock, 
            KeyboardType.Password, isPassword = true, visible = passVisible, 
            onToggle = { passVisible = !passVisible }
        )
        
        Spacer(modifier = Modifier.height(12.dp))

        // Strength Bar
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
                strength < 0.5f -> Color(0xFFEF4444)
                strength < 0.75f -> Color(0xFFF59E0B)
                else -> Color(0xFF10B981)
            }
            LinearProgressIndicator(
                progress = { strength },
                modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                color = strengthColor,
                trackColor = Color(0xFFE5E7EB)
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Confirm Password
        SimpleAuthField(
            confirmPassword, onConfirmChange, "Confirmar contraseña", Icons.Outlined.Lock, 
            KeyboardType.Password, isPassword = true, visible = confirmVisible, 
            onToggle = { confirmVisible = !confirmVisible }
        )
    }
}

@Composable
private fun OtpDigitFieldSmall(
    value: String,
    focusRequester: FocusRequester,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BasicTextField(
        value = value,
        onValueChange = { raw ->
            val digit = raw.filter { it.isDigit() }.take(1)
            onValueChange(digit)
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        textStyle = MaterialTheme.typography.titleLarge.copy(
            color = Color(0xFF1F2937),
            textAlign = TextAlign.Center
        ),
        modifier = modifier
            .height(56.dp)
            .focusRequester(focusRequester),
        decorationBox = { inner ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .size(56.dp)
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .border(1.dp, Color(0xFFE5ECF1), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                inner()
            }
        }
    )
}

@Composable
private fun SimpleAuthField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    visible: Boolean = false,
    onToggle: (() -> Unit)? = null
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .border(1.dp, Color(0xFFEFF3F6), RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        color = Color.White
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(icon, null, modifier = Modifier.size(20.dp), tint = RelaxMutedText)
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                textStyle = TextStyle(fontSize = 16.sp, color = Color(0xFF1E293B)),
                singleLine = true,
                visualTransformation = if (isPassword && !visible) PasswordVisualTransformation() else VisualTransformation.None,
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                cursorBrush = SolidColor(RelaxGreen),
                decorationBox = { inner ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        if (value.isEmpty()) {
                            Text(placeholder, color = RelaxMutedText.copy(alpha = 0.6f))
                        }
                        inner()
                    }
                }
            )
            if (isPassword && onToggle != null) {
                IconButton(onClick = onToggle) {
                    Icon(
                        if (visible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                        null, modifier = Modifier.size(20.dp), tint = RelaxMutedText
                    )
                }
            }
        }
    }
}
