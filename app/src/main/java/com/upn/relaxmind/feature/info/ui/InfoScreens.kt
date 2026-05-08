package com.upn.relaxmind.feature.info.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upn.relaxmind.core.ui.theme.RelaxGreen
import com.upn.relaxmind.core.ui.theme.RelaxMutedText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBack: () -> Unit) {
    val bgColor = MaterialTheme.colorScheme.background
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Acerca de RelaxMind", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = bgColor)
            )
        },
        containerColor = bgColor
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(Brush.linearGradient(listOf(RelaxGreen, Color(0xFF2D6A4F)))),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Outlined.Psychology, null, tint = Color.White, modifier = Modifier.size(48.dp))
                    Text("RelaxMind", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Black)
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text("Nuestra Misión", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "RelaxMind nació con el propósito de democratizar el acceso a herramientas de salud mental. " +
                "Creemos que cada persona merece un espacio seguro para entender sus emociones y encontrar la calma en un mundo acelerado.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text("¿Por qué lo hacemos?", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "La salud mental es un pilar fundamental del bienestar general. " +
                "Utilizamos tecnología y diseño centrado en el humano para crear experiencias que reduzcan la ansiedad, " +
                "mejoren el sueño y fortalezcan la resiliencia emocional.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            Text(
                "Versión 2.0.4 - Hecho con ❤️ para tu bienestar.",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelMedium,
                color = RelaxMutedText
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsScreen(onBack: () -> Unit) {
    val bgColor = MaterialTheme.colorScheme.background
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Términos y Condiciones", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = bgColor)
            )
        },
        containerColor = bgColor
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(24.dp)
        ) {
            Text("Última actualización: Abril 2026", style = MaterialTheme.typography.labelMedium, color = RelaxMutedText)
            Spacer(modifier = Modifier.height(24.dp))
            
            TermSection(
                title = "1. Uso de la Aplicación",
                content = "RelaxMind es una herramienta de apoyo y no sustituye el diagnóstico o tratamiento médico profesional. Al usar la app, aceptas que eres responsable de tu propio bienestar."
            )
            
            TermSection(
                title = "2. Privacidad de Datos",
                content = "Tus datos emocionales y de salud son privados. No vendemos tu información a terceros. Utilizamos encriptación de grado bancario para proteger tu diario y preferencias."
            )
            
            TermSection(
                title = "3. Suscripción y Servicios",
                content = "Algunas funciones pueden requerir una suscripción premium. Los pagos se procesan a través de las tiendas oficiales de aplicaciones."
            )
            
            TermSection(
                title = "4. Conducta del Usuario",
                content = "No se permite el uso de la aplicación para fines ilegales o el acoso a otros usuarios a través de las funciones comunitarias."
            )
        }
    }
}

@Composable
private fun TermSection(title: String, content: String) {
    Column(modifier = Modifier.padding(bottom = 20.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(content, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
