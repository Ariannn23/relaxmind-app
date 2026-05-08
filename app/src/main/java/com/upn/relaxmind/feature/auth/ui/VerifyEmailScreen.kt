package com.upn.relaxmind.feature.auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.upn.relaxmind.core.ui.theme.RelaxGreen
import com.upn.relaxmind.core.ui.theme.RelaxMutedText
import kotlinx.coroutines.delay

@Composable
fun VerifyEmailScreen(
    modifier: Modifier = Modifier,
    onVerify: (String) -> Unit,
    onResendCode: () -> Unit,
    email: String = "tu correo"
) {
    val otpValues = remember { mutableStateListOf("", "", "", "", "", "") }
    val focusRequesters = remember { List(6) { FocusRequester() } }
    var secondsLeft by remember { mutableStateOf(60) }

    val otpCode = otpValues.joinToString("")
    val canVerify = otpCode.length == 6 && otpValues.all { it.length == 1 }
    val canResend = secondsLeft == 0

    LaunchedEffect(Unit) {
        focusRequesters.first().requestFocus()
    }
    LaunchedEffect(secondsLeft) {
        if (secondsLeft > 0) {
            delay(1000)
            secondsLeft -= 1
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.linearGradient(listOf(Color.White, Color(0xFFF0F4F8))))
            .systemBarsPadding()
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.weight(0.8f))
                Text(
                    text = "Verifica tu correo",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Hemos enviado un codigo de 6 digitos a $email. Por favor, ingresalo debajo para activar tu cuenta.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = RelaxMutedText,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(22.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(6) { index ->
                        OtpDigitField(
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

                Spacer(modifier = Modifier.height(22.dp))
                Button(
                    onClick = { onVerify(otpCode) },
                    enabled = canVerify,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RelaxGreen)
                ) {
                    Text("VERIFICAR", fontWeight = FontWeight.SemiBold, color = Color.White)
                }

                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    text = if (canResend) "Reenviar codigo" else "Reenviar codigo ${secondsLeft}s",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (canResend) RelaxGreen else RelaxMutedText,
                    modifier = Modifier.clickable(enabled = canResend) {
                        onResendCode()
                        secondsLeft = 60
                    }
                )
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun OtpDigitField(
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
