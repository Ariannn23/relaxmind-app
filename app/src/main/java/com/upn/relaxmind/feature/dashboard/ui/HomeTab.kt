package com.upn.relaxmind.feature.dashboard.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.upn.relaxmind.core.ui.theme.*
import com.upn.relaxmind.feature.dashboard.ui.components.*
import com.upn.relaxmind.feature.dashboard.viewmodel.HomeViewModel

@Composable
fun HomeTab(
    onOpenCheckIn: () -> Unit,
    onOpenCrisis: () -> Unit,
    onOpenChatbot: () -> Unit,
    onOpenDiary: () -> Unit,
    onOpenLibrary: () -> Unit,
    onOpenServicesMap: () -> Unit,
    onOpenSounds: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenRewards: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val quickAccessItems = listOf(
        QuickAccessItem(
            title = "Check-in Diario",
            subtitle = "¿Cómo estás hoy?",
            iconRes = com.upn.relaxmind.R.drawable.checkin,
            gradient = Brush.linearGradient(listOf(Color(0xFF10B981), Color(0xFF6EE7B7))),
            bgColor = getSoftGreen(),
            onClick = onOpenCheckIn
        ),
        QuickAccessItem(
            title = "Lumi ✨",
            subtitle = "Tu amigo IA",
            iconRes = com.upn.relaxmind.R.drawable.lumi,
            gradient = Brush.linearGradient(listOf(Color(0xFF0EA5E9), Color(0xFF7DD3FC))),
            bgColor = getSoftBlue(),
            onClick = onOpenChatbot
        ),
        QuickAccessItem(
            title = "Centros Cerca",
            subtitle = "Mapa de ayuda",
            iconRes = com.upn.relaxmind.R.drawable.centroscerca,
            gradient = Brush.linearGradient(listOf(Color(0xFFF97316), Color(0xFFFDBA74))),
            bgColor = getSoftOrange(),
            onClick = onOpenServicesMap
        ),
        QuickAccessItem(
            title = "Sonidos",
            subtitle = "Música para tu mente",
            iconRes = com.upn.relaxmind.R.drawable.sonido,
            gradient = Brush.linearGradient(listOf(Color(0xFFEAB308), Color(0xFFFEF08A))),
            bgColor = getSoftYellow(),
            onClick = onOpenSounds
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .padding(bottom = 8.dp)
    ) {
        AnimatedVisibility(
            visible = uiState.showHeader,
            enter = fadeIn(tween(420)) + slideInVertically(tween(420)) { it / 6 }
        ) {
            HomeHeader(
                displayName = uiState.displayName,
                streak = uiState.streak,
                onOpenProfile = onOpenProfile,
                onOpenRewards = onOpenRewards
            )
        }
        
        Spacer(modifier = Modifier.height(18.dp))
        
        AnimatedVisibility(
            visible = uiState.showScore,
            enter = fadeIn(tween(440)) + slideInVertically(tween(440)) { it / 8 }
        ) {
            WellnessScoreCard(score = uiState.wellnessScore)
        }
        
        Spacer(modifier = Modifier.height(18.dp))
        
        AnimatedVisibility(
            visible = uiState.showGrid,
            enter = fadeIn(tween(460)) + slideInVertically(tween(460)) { fullHeight: Int -> fullHeight / 8 }
        ) {
            QuickAccessGrid(items = quickAccessItems)
        }
        
        Spacer(modifier = Modifier.height(18.dp))
        
        AnimatedVisibility(
            visible = uiState.showWidgets,
            enter = fadeIn(tween(480)) + slideInVertically(tween(480)) { fullHeight: Int -> fullHeight / 8 }
        ) {
            Column {
                NextReminderCard()
                Spacer(modifier = Modifier.height(14.dp))
                StreakCalendarCard(streak = uiState.streak, onClick = onOpenRewards)
                Spacer(modifier = Modifier.height(14.dp))
                DashboardShortcutsRow(onOpenDiary = onOpenDiary, onOpenLibrary = onOpenLibrary)
            }
        }
        
        Spacer(modifier = Modifier.height(100.dp))
    }
}
