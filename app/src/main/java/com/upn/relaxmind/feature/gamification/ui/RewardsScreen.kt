package com.upn.relaxmind.feature.gamification.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Fireplace
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upn.relaxmind.core.data.auth.AuthManager
import com.upn.relaxmind.feature.gamification.data.GamificationManager
import com.upn.relaxmind.core.ui.components.RelaxBackButton
import com.upn.relaxmind.core.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RewardsScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val user = remember { AuthManager.getCurrentUser(context) }
    val isDark = LocalIsDarkTheme.current
    val surfaceColor = if (isDark) Color(0xFF1E293B) else Color.White
    val textColor = if (isDark) Color.White else Color(0xFF1E293B)
    val mutedTextColor = if (isDark) Color.LightGray else Color.Gray

    val streak = user?.streakCount ?: 0
    val earnedBadges = user?.earnedBadges ?: emptyList()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Logros y Recompensas", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    Box(modifier = Modifier.padding(start = 12.dp)) {
                        RelaxBackButton(onClick = onBack)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = textColor
                )
            )
        },
        containerColor = if (isDark) Color(0xFF0F172A) else Color(0xFFF8FAFC)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Streak Card
            StreakProgressCard(streak, isDark)

            Spacer(modifier = Modifier.height(32.dp))

            // Badges Section
            Text(
                text = "Tus Medallas",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = textColor,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "Completa check-ins diarios para desbloquearlas",
                style = MaterialTheme.typography.bodyMedium,
                color = mutedTextColor,
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 16.dp)
            )

            BadgesGrid(earnedBadges, isDark)
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun StreakProgressCard(streak: Int, isDark: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "streak")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Surface(
        shape = RoundedCornerShape(32.dp),
        color = if (isDark) Color(0xFF1E293B) else Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(20.dp, RoundedCornerShape(32.dp), spotColor = Color(0xFFF59E0B).copy(0.3f))
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        if (isDark) listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                        else listOf(Color.White, Color(0xFFFFF7ED))
                    )
                )
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(scale),
                contentAlignment = Alignment.Center
            ) {
                // Fire Icon with glow
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFFFF7ED),
                    modifier = Modifier.size(100.dp).shadow(15.dp, CircleShape, spotColor = Color(0xFFF59E0B))
                ) {
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(id = com.upn.relaxmind.R.drawable.racha),
                        contentDescription = null,
                        modifier = Modifier.size(60.dp).padding(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "$streak Días",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Black,
                color = if (isDark) Color.White else Color(0xFF1E293B)
            )
            Text(
                text = "Racha Actual",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFFF59E0B),
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Mini progress bar for next milestone
            val nextMilestone = when {
                streak < 3 -> 3
                streak < 7 -> 7
                streak < 15 -> 15
                else -> 30
            }
            val progress = streak.toFloat() / nextMilestone

            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Próximo nivel", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                    Text("$streak/$nextMilestone días", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(10.dp).clip(CircleShape),
                    color = Color(0xFFF59E0B),
                    trackColor = if (isDark) Color(0xFF334155) else Color(0xFFFED7AA).copy(0.3f)
                )
            }
        }
    }
}

@Composable
private fun BadgesGrid(earnedBadges: List<String>, isDark: Boolean) {
    val allBadges = listOf("streak_3", "streak_7", "streak_15", "streak_30")
    
    // Using a manual grid with Column and Row because nested scrolling issues with LazyVerticalGrid in verticalScroll
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        val chunks = allBadges.chunked(2)
        chunks.forEach { rowBadges ->
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                rowBadges.forEach { badgeId ->
                    BadgeItem(
                        badgeId = badgeId,
                        isEarned = earnedBadges.contains(badgeId),
                        isDark = isDark,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (rowBadges.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun BadgeItem(badgeId: String, isEarned: Boolean, isDark: Boolean, modifier: Modifier = Modifier) {
    val info = GamificationManager.getBadgeDetails(badgeId)
    val color = when(badgeId) {
        "streak_3" -> RelaxGreen
        "streak_7" -> CaregiverBlue
        "streak_15" -> Color(0xFF8B5CF6)
        else -> Color(0xFFF59E0B)
    }

    Surface(
        shape = RoundedCornerShape(24.dp),
        color = if (isDark) Color(0xFF1E293B) else Color.White,
        modifier = modifier.shadow(
            elevation = if (isEarned) 8.dp else 0.dp,
            shape = RoundedCornerShape(24.dp),
            spotColor = color.copy(0.3f)
        ),
        border = if (!isEarned) androidx.compose.foundation.BorderStroke(1.dp, Color.Gray.copy(0.2f)) else null
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.Center) {
                Surface(
                    shape = CircleShape,
                    color = if (isEarned) color.copy(0.1f) else Color.Gray.copy(0.1f),
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(
                        imageVector = if (isEarned) Icons.Default.EmojiEvents else Icons.Outlined.Lock,
                        contentDescription = null,
                        tint = if (isEarned) color else Color.Gray.copy(0.5f),
                        modifier = Modifier.size(32.dp).padding(16.dp)
                    )
                }
                if (isEarned) {
                    Icon(
                        Icons.Outlined.CheckCircle,
                        null,
                        tint = RelaxGreen,
                        modifier = Modifier.size(20.dp).align(Alignment.BottomEnd).background(Color.White, CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = info.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = if (isEarned) (if (isDark) Color.White else Color(0xFF1E293B)) else Color.Gray,
                textAlign = TextAlign.Center
            )
            Text(
                text = info.requirement,
                style = MaterialTheme.typography.labelSmall,
                color = if (isEarned) color else Color.Gray.copy(0.6f),
                textAlign = TextAlign.Center
            )
        }
    }
}
