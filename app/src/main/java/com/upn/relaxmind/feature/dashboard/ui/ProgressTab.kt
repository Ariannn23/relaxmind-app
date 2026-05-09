package com.upn.relaxmind.feature.dashboard.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.upn.relaxmind.core.ui.theme.*
import com.upn.relaxmind.feature.progress.ui.components.*
import com.upn.relaxmind.feature.progress.viewmodel.ProgressViewModel

@Composable
fun ProgressTab(
    modifier: Modifier = Modifier,
    viewModel: ProgressViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isDark = LocalIsDarkTheme.current
    val primaryGreen = Color(0xFF0F6E56)
    val accentGold = Color(0xFFF59E0B)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 100.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp)) {
            Text(
                text = "Tu Progreso",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Tu viaje hacia la calma",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        ProfileSummary(uiState.displayName, primaryGreen)
        
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            Spacer(modifier = Modifier.height(8.dp))
            
            ProgressChart(
                selectedRange = uiState.selectedRange,
                onRangeSelected = { viewModel.onRangeSelected(it) },
                isDark = isDark,
                accentColor = accentGold
            )
            
            Spacer(modifier = Modifier.height(32.dp))

            ProgressHistorySection()
            
            Spacer(modifier = Modifier.height(32.dp))
            
            ActivitySummarySection(isDark, primaryGreen)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            StreakCalendar(
                selectedMonth = uiState.selectedMonth,
                onMonthSelected = { viewModel.onMonthSelected(it) },
                isDark = isDark
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            BadgesGrid(isDark, accentGold)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            DiaryQuickAccessSection(isDark, primaryGreen)
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ProfileSummary(name: String, primaryGreen: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(56.dp),
            shape = CircleShape,
            color = primaryGreen.copy(0.1f),
            border = BorderStroke(2.dp, primaryGreen.copy(0.2f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = name.trim().take(1).ifBlank { "U" }.uppercase(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = primaryGreen
                )
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text("En RelaxMind desde Enero 2025", style = MaterialTheme.typography.labelSmall, color = RelaxMutedText)
        }
        
        Surface(
            color = RelaxGreen.copy(0.1f),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                "Nivel 3 · Explorador",
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                style = MaterialTheme.typography.labelSmall,
                color = primaryGreen,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
