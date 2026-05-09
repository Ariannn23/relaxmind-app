package com.upn.relaxmind.feature.dashboard.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.upn.relaxmind.core.ui.theme.getSoftRed
import kotlinx.coroutines.delay

@Composable
fun WellnessScoreCard(score: Int) {
    var ringTarget by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(score) {
        ringTarget = 0f
        delay(80)
        ringTarget = (score.coerceIn(0, 100)) / 100f
    }
    val ringProgress by animateFloatAsState(
        targetValue = ringTarget,
        animationSpec = tween(1100, delayMillis = 40),
        label = "wellnessRing"
    )

    val gradient = Brush.linearGradient(listOf(Color(0xFFF43F5E), Color(0xFFFDA4AF)))
    val softBorderGradient = Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.4f), Color(0xFFF43F5E).copy(alpha = 0.12f)))
    val cardBg = getSoftRed()
    val trackColor = Color(0xFFF43F5E).copy(alpha = 0.1f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(24.dp), spotColor = Color(0x2AF43F5E))
            .clip(RoundedCornerShape(24.dp))
            .background(cardBg)
            .border(1.dp, softBorderGradient, RoundedCornerShape(24.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(100.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val stroke = 10.dp.toPx()
                    val pad = stroke / 2f + 4.dp.toPx()
                    val sizeArc = Size(this.size.width - pad * 2, this.size.height - pad * 2)
                    drawArc(
                        color = trackColor,
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = Offset(pad, pad),
                        size = sizeArc,
                        style = Stroke(width = stroke, cap = StrokeCap.Round)
                    )
                    drawArc(
                        brush = gradient,
                        startAngle = -90f,
                        sweepAngle = 360f * ringProgress,
                        useCenter = false,
                        topLeft = Offset(pad, pad),
                        size = sizeArc,
                        style = Stroke(width = stroke, cap = StrokeCap.Round)
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${score.coerceIn(0, 100)}",
                        style = MaterialTheme.typography.headlineMedium.copy(brush = gradient),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.size(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = com.upn.relaxmind.R.drawable.meditar),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "Tu bienestar",
                        style = MaterialTheme.typography.titleMedium.copy(brush = gradient),
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Escala 0 - 100",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Prioriza tu descanso. Estamos contigo.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray.copy(alpha = 0.8f)
                )
            }
        }
    }
}
