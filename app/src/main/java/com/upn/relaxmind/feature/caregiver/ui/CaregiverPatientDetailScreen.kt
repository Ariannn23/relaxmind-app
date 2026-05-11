package com.upn.relaxmind.feature.caregiver.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.upn.relaxmind.core.data.auth.AuthManager
import com.upn.relaxmind.core.ui.components.RelaxBackButton
import com.upn.relaxmind.core.ui.components.UserAvatar
import com.upn.relaxmind.core.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaregiverPatientDetailScreen(
    patientId: String,
    onBack: () -> Unit,
    onViewHistory: () -> Unit
) {
    val context = LocalContext.current
    var patient by remember { mutableStateOf<com.upn.relaxmind.core.data.models.User?>(null) }
    
    LaunchedEffect(patientId) {
        patient = AuthManager.getRegisteredUsers(context).find { it.id == patientId }
    }
    
    val p = patient ?: return
    val score = p.wellnessScore
    val statusColor = when {
        score >= 70 -> RelaxGreen
        score >= 40 -> Color(0xFFEAB308)
        else -> Color(0xFFEF4444)
    }

    Scaffold(
        containerColor = CaregiverBg,
        topBar = {
            TopAppBar(
                title = { Text("Perfil del Paciente", fontWeight = FontWeight.Bold) },
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
            // Header Info
            Row(verticalAlignment = Alignment.CenterVertically) {
                UserAvatar(user = p, size = 80, fontSize = 32)
                Spacer(modifier = Modifier.width(20.dp))
                Column {
                    Text(
                        text = "${p.name} ${p.lastName}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF1E293B)
                    )
                    Text(
                        text = p.professionalRole ?: "Paciente",
                        style = MaterialTheme.typography.bodyMedium,
                        color = CaregiverBlue,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Wellness Score Large
            WellnessSummaryCard(score = score, color = statusColor)

            Spacer(modifier = Modifier.height(24.dp))

            // Check-in Details
            Text(
                text = "Detalles del Check-in (Hoy)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )
            Spacer(modifier = Modifier.height(16.dp))
            CheckInGrid()

            Spacer(modifier = Modifier.height(32.dp))

            // Trend Graph
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tendencia Semanal",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
                TextButton(onClick = onViewHistory) {
                    Text("Ver historial", color = CaregiverBlue)
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth().height(200.dp)
            ) {
                WellnessTrendChart(score)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Call Button
            Button(
                onClick = {
                    if (p.phoneNumber.isNotEmpty()) {
                        val intent = android.content.Intent(android.content.Intent.ACTION_DIAL, android.net.Uri.parse("tel:${p.phoneNumber}"))
                        context.startActivity(intent)
                    } else {
                        val intent = android.content.Intent(android.content.Intent.ACTION_DIAL)
                        context.startActivity(intent)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CaregiverBlue)
            ) {
                Icon(Icons.Default.Phone, contentDescription = null)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Llamar al Paciente", fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun WellnessSummaryCard(score: Int, color: Color) {
    Surface(
        shape = RoundedCornerShape(28.dp),
        color = Color.White,
        shadowElevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = score / 100f,
                    modifier = Modifier.size(100.dp),
                    color = color,
                    strokeWidth = 10.dp,
                    trackColor = color.copy(alpha = 0.1f)
                )
                Text(
                    text = "$score%",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF1E293B)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Puntaje de Bienestar",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (score < 40) "Se recomienda contactar al paciente" else "Estado estable",
                style = MaterialTheme.typography.bodySmall,
                color = RelaxMutedText
            )
        }
    }
}

@Composable
private fun CheckInGrid() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            IndicatorItem(Modifier.weight(1f), "Ánimo", "Tranquilo", Icons.Outlined.Mood, CaregiverPurple)
            IndicatorItem(Modifier.weight(1f), "Sueño", "7h 30m", Icons.Outlined.Bedtime, CaregiverBlue)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            IndicatorItem(Modifier.weight(1f), "Dolor", "Ninguno", Icons.Outlined.MonitorHeart, Color(0xFFEF4444))
            IndicatorItem(Modifier.weight(1f), "Medicina", "Tomada", Icons.Outlined.Medication, RelaxGreen)
        }
    }
}

@Composable
private fun IndicatorItem(modifier: Modifier, label: String, value: String, icon: ImageVector, color: Color) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = color.copy(alpha = 0.05f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(32.dp).background(color.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun WellnessTrendChart(currentScore: Int) {
    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                description.isEnabled = false
                setTouchEnabled(false)
                setDrawGridBackground(false)
                xAxis.isEnabled = false
                axisRight.isEnabled = false
                legend.isEnabled = false
                
                val entries = listOf(
                    Entry(0f, 65f),
                    Entry(1f, 70f),
                    Entry(2f, 62f),
                    Entry(3f, 55f),
                    Entry(4f, 68f),
                    Entry(5f, 75f),
                    Entry(6f, currentScore.toFloat())
                )
                
                val dataSet = LineDataSet(entries, "Wellness").apply {
                    color = CaregiverBlue.hashCode()
                    setCircleColor(CaregiverBlue.hashCode())
                    lineWidth = 3f
                    circleRadius = 5f
                    setDrawValues(false)
                    mode = LineDataSet.Mode.CUBIC_BEZIER
                    setDrawFilled(true)
                    fillColor = CaregiverBlue.hashCode()
                    fillAlpha = 50
                }
                
                data = LineData(dataSet)
                invalidate()
            }
        },
        modifier = Modifier.fillMaxSize().padding(16.dp)
    )
}
