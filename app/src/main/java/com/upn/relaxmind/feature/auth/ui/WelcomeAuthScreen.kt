package com.upn.relaxmind.feature.auth.ui

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upn.relaxmind.R
import com.upn.relaxmind.core.ui.theme.RelaxGreen
import com.upn.relaxmind.core.ui.theme.RelaxGreenSoft
import com.upn.relaxmind.core.ui.theme.RelaxMutedText
import kotlinx.coroutines.delay

import androidx.lifecycle.viewmodel.compose.viewModel
import com.upn.relaxmind.feature.auth.viewmodel.AuthViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.fragment.app.FragmentActivity

@Composable
fun WelcomeAuthScreen(
    onLoginNavigate: () -> Unit,
    onRegisterNavigate: () -> Unit,
    onSignInSuccess: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    var showContent by remember { mutableStateOf(false) }
    
    val gradientPrimary = Brush.linearGradient(
        colors = listOf(Color(0xFF047857), RelaxGreen, RelaxGreenSoft)
    )

    LaunchedEffect(Unit) {
        delay(300)
        showContent = true
    }

    LaunchedEffect(uiState.isGoogleLoginSuccess) {
        if (uiState.isGoogleLoginSuccess) {
            onSignInSuccess()
            viewModel.resetLoginSuccess()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.resetError()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .systemBarsPadding()
    ) {
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(tween(1200)) + slideInVertically(tween(1200)) { it / 12 },
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "RelaxMind",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = RelaxGreen
                )
                
                Spacer(modifier = Modifier.weight(0.4f))

                Image(
                    painter = painterResource(id = R.drawable.login_registro),
                    contentDescription = null,
                    modifier = Modifier
                        .size(180.dp)
                        .clip(RoundedCornerShape(32.dp))
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Tu espacio de paz",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
                
                Text(
                    text = "Crea una cuenta para guardar tu progreso y acceder a meditaciones personalizadas.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF64748B),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.weight(0.6f))

                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    AuthPillButton(text = "REGISTRARSE", brush = gradientPrimary, onClick = onRegisterNavigate)
                    AuthOutlinedPillButton(text = "INICIAR SESIÓN", onClick = onLoginNavigate)
                    
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.weight(1f).height(1.dp).background(Color(0xFFE2E8F0)))
                        Text("Ó", modifier = Modifier.padding(horizontal = 16.dp), style = MaterialTheme.typography.labelMedium, color = RelaxMutedText, fontWeight = FontWeight.Bold)
                        Box(Modifier.weight(1f).height(1.dp).background(Color(0xFFE2E8F0)))
                    }

                    OutlinedButton(
                        onClick = {
                            val activity = context as? FragmentActivity
                            if (activity != null) {
                                viewModel.onGoogleSignIn(context, activity)
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(24.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Image(painter = painterResource(id = R.drawable.google), contentDescription = "Google", modifier = Modifier.size(24.dp))
                            Text("CONTINUAR CON GOOGLE", color = Color(0xFF1E293B), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun AuthPillButton(text: String, brush: Brush, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize().background(brush, RoundedCornerShape(24.dp)), contentAlignment = Alignment.Center) {
            Text(text, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun AuthOutlinedPillButton(text: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(2.dp, RelaxGreen)
    ) {
        Text(text, color = RelaxGreen, fontWeight = FontWeight.Bold)
    }
}
