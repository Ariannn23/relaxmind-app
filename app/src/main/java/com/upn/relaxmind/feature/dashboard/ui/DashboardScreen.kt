package com.upn.relaxmind.feature.dashboard.ui

import com.upn.relaxmind.core.ui.theme.*

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SentimentSatisfiedAlt
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.HealthAndSafety
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import com.upn.relaxmind.core.ui.components.RelaxBackButton
import com.upn.relaxmind.core.data.preferences.AppPreferences
import com.upn.relaxmind.core.data.auth.AuthManager
import com.upn.relaxmind.feature.gamification.data.GamificationManager
import com.upn.relaxmind.core.ui.theme.RelaxBackground
import com.upn.relaxmind.core.ui.theme.RelaxGreen
import com.upn.relaxmind.core.ui.theme.RelaxGreenSoft
import com.upn.relaxmind.core.ui.theme.RelaxMutedText
import com.upn.relaxmind.core.ui.theme.RelaxDarkSurface
import com.upn.relaxmind.core.ui.theme.RelaxDarkSurfaceVar
import com.upn.relaxmind.core.ui.theme.RelaxDarkBackground
import com.upn.relaxmind.core.ui.theme.LocalIsDarkTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.upn.relaxmind.feature.meditation.ui.MeditationScreen
import com.upn.relaxmind.feature.reminders.ui.RemindersScreen
import com.upn.relaxmind.feature.profile.ui.SettingsScreen
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import androidx.compose.foundation.layout.systemBarsPadding

private val ScoreGreen = Color(0xFF10B981)
private val ScoreYellow = Color(0xFFF59E0B)
private val ScoreRed = Color(0xFFDC2626)
private val StreakOrange = Color(0xFFFF8A5C)
private val EmeraldDeep = Color(0xFF059669)
private val EmeraldMist = Color(0xFFD1FAE5)
private val LavenderSoft = Color(0xFFEDE9FE)
private val LavenderMid = Color(0xFFC4B5FD)
private val PastelBlue = Color(0xFF93C5FD)
private val PastelBlueMist = Color(0xFFEFF6FF)
private val SosRed = Color(0xFFEF4444)
private val SosCoral = Color(0xFFFF6B6B)
private val ChartLine = RelaxGreen
private val NavBarShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)

private enum class DashboardTab(val label: String, val activeColor: Color) {
    Home("Inicio",    Color(0xFF10B981)),
    Breathing("Meditar", Color(0xFF7C3AED)),
    Progress("Progreso", Color(0xFFF59E0B)),
    Reminders("Agenda", Color(0xFF2563EB)),
    Settings("Ajustes", Color(0xFF475569))
}

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    onOpenCheckIn: () -> Unit = {},
    onOpenCrisis: () -> Unit = {},
    onOpenChatbot: () -> Unit = {},
    onOpenDiary: () -> Unit = {},
    onOpenLibrary: () -> Unit = {},
    onOpenProfile: () -> Unit = {},
    onOpenEditProfile: () -> Unit = {},
    onOpenEmergencyQr: () -> Unit = {},
    onOpenServicesMap: () -> Unit = {},
    onOpenSounds: () -> Unit = {},
    onOpenAbout: () -> Unit = {},
    onOpenTerms: () -> Unit = {},
    onOpenRewards: () -> Unit = {},
    onToggleDarkTheme: (Boolean) -> Unit = {},
    onLogout: () -> Unit = {},
    initialTabIndex: Int = 0
) {
    val context = LocalContext.current
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(initialTabIndex) }
    val selectedTab = DashboardTab.entries[selectedTabIndex]
    val isGuest = AppPreferences.isGuestMode(context)
    val justRegistered = AppPreferences.isJustRegistered(context)
    var showBiometricPrompt by rememberSaveable { mutableStateOf(false) }
    // Local state for immediate UI feedback, initialized from global session state
    var isUnlocked by remember { 
        mutableStateOf(isGuest || justRegistered || AuthManager.isSessionUnlocked || !AppPreferences.isBiometricEnabled(context)) 
    }
    var authError by remember { mutableStateOf<String?>(null) }

    // Sync local state with global state
    LaunchedEffect(AuthManager.isSessionUnlocked) {
        if (AuthManager.isSessionUnlocked) {
            isUnlocked = true
        }
    }
    
    LaunchedEffect(isUnlocked) {
        if (isUnlocked) {
            AuthManager.isSessionUnlocked = true
        }
    }

    LaunchedEffect(Unit) {
        // Safety exit: if already unlocked in this session, do nothing
        if (AuthManager.isSessionUnlocked) {
            isUnlocked = true
            return@LaunchedEffect
        }

        val biometricEnabled = AppPreferences.isBiometricEnabled(context)
        val promptShown = AppPreferences.isBiometricPromptShown(context)
        if (!biometricEnabled && !promptShown && !isGuest && !justRegistered) {
            showBiometricPrompt = true
            AppPreferences.setBiometricPromptShown(context, true)
        } else if (biometricEnabled && !isUnlocked && !isGuest && !justRegistered) {
            // Trigger auth automatically on start
            val activity = context as? androidx.fragment.app.FragmentActivity
            if (activity != null) {
                com.upn.relaxmind.core.utils.BiometricHelper.authenticate(
                    activity = activity,
                    onSuccess = { 
                        isUnlocked = true 
                        AuthManager.isSessionUnlocked = true
                    },
                    onError = { authError = it }
                )
            } else {
                authError = "Activity must be FragmentActivity"
            }
        }
        if (justRegistered) {
            AppPreferences.setJustRegistered(context, false)
        }
    }

    if (!isUnlocked) {
        // Pantalla de Bloqueo
        Box(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).systemBarsPadding(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Outlined.Lock, contentDescription = null, modifier = Modifier.size(64.dp), tint = RelaxMutedText)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "App Bloqueada",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Usa tu huella o Face ID para entrar",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(32.dp))
                if (authError != null) {
                    Text(authError!!, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(16.dp))
                }
                androidx.compose.material3.Button(
                    onClick = {
                        val activity = context as? androidx.fragment.app.FragmentActivity
                        if (activity != null) {
                            com.upn.relaxmind.core.utils.BiometricHelper.authenticate(
                                activity = activity,
                                onSuccess = { 
                                    isUnlocked = true 
                                    AuthManager.isSessionUnlocked = true
                                },
                                onError = { authError = it }
                            )
                        }
                    },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = RelaxGreen)
                ) {
                    Text("Desbloquear")
                }
            }
        }
        return // Do not render scaffold
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        // Use a Box to layer the content, the SOS button and the truly floating Navbar
        Box(modifier = Modifier.fillMaxSize()) {
            AnimatedContent(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent),
                targetState = selectedTab,
                transitionSpec = {
                    (fadeIn(tween(700)) + scaleIn(initialScale = 0.98f, animationSpec = tween(700)))
                        .togetherWith(fadeOut(tween(500)))
                },
                label = "dashboardTab"
            ) { tab ->
                val contentModifier = Modifier.fillMaxSize()
                
                when (tab) {
                    DashboardTab.Home -> DashboardHomeContent(
                        onOpenCheckIn = onOpenCheckIn,
                        onOpenCrisis = onOpenCrisis,
                        onOpenChatbot = onOpenChatbot,
                        onOpenDiary = onOpenDiary,
                        onOpenLibrary = onOpenLibrary,
                        onOpenServicesMap = onOpenServicesMap,
                        onOpenSounds = onOpenSounds,
                        onOpenProfile = onOpenProfile,
                        onOpenRewards = onOpenRewards
                    )
                    DashboardTab.Breathing -> MeditationScreen(
                        modifier = contentModifier
                    )
                    DashboardTab.Progress -> ProgressTabContent(
                        modifier = contentModifier
                    )
                    DashboardTab.Reminders -> RemindersScreen(
                        modifier = contentModifier
                    )
                    DashboardTab.Settings -> SettingsTabContent(
                        onOpenProfile = onOpenProfile,
                        onOpenEditProfile = onOpenEditProfile,
                        onOpenEmergencyQr = onOpenEmergencyQr,
                        onOpenAbout = onOpenAbout,
                        onOpenTerms = onOpenTerms,
                        onToggleDarkTheme = onToggleDarkTheme,
                        onLogout = onLogout
                    )
                }
            }
            
            // SOS Button - Replaced inside the overlay Box to control its position
            if (selectedTab != DashboardTab.Breathing && 
                selectedTab != DashboardTab.Reminders && 
                selectedTab != DashboardTab.Progress) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 120.dp, end = 16.dp)
                ) {
                    SosFloatingButton(
                        onSosTriggered = onOpenCrisis
                    )
                }
            }

            // Truly floating Navbar at the bottom
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            ) {
                PremiumBottomNavigationBar(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTabIndex = it.ordinal }
                )
            }
        }
    }
    if (showBiometricPrompt) {
        AlertDialog(
            onDismissRequest = { showBiometricPrompt = false },
            title = {
                Text(
                    text = "Activar biometría",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Protege tu cuenta y entra más rápido con huella o Face ID en este dispositivo.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = RelaxMutedText
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        AuthManager.setBiometricEnabled(context, true)
                        Toast.makeText(context, "Biometría activada", Toast.LENGTH_SHORT).show()
                        showBiometricPrompt = false
                    }
                ) {
                    Text("Activar", color = RelaxGreen, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showBiometricPrompt = false }) {
                    Text("Ahora no", color = RelaxMutedText)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}

@Composable
private fun SettingsTabContent(
    onOpenProfile: () -> Unit,
    onOpenEditProfile: () -> Unit,
    onOpenEmergencyQr: () -> Unit,
    onOpenAbout: () -> Unit,
    onOpenTerms: () -> Unit,
    onToggleDarkTheme: (Boolean) -> Unit,
    onLogout: () -> Unit
) {
    SettingsScreen(
        modifier = Modifier.fillMaxSize(),
        onProfileClick = onOpenProfile,
        onEditProfileClick = onOpenEditProfile,
        onSecurityClick = onOpenEmergencyQr,
        onAboutClick = onOpenAbout,
        onTermsClick = onOpenTerms,
        onToggleDarkTheme = onToggleDarkTheme,
        onLogoutClick = onLogout
    )
}

@Composable
private fun DashboardHomeContent(
    onOpenCheckIn: () -> Unit,
    onOpenCrisis: () -> Unit,
    onOpenChatbot: () -> Unit,
    onOpenDiary: () -> Unit,
    onOpenLibrary: () -> Unit,
    onOpenServicesMap: () -> Unit,
    onOpenSounds: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenRewards: () -> Unit
) {
    val context = LocalContext.current
    val user = AuthManager.getCurrentUser(context)
    val displayName = user?.name ?: AppPreferences.getDisplayName(context)
    val streak = user?.streakCount ?: AppPreferences.getStreak(context)
    val wellnessScore = user?.wellnessScore ?: AppPreferences.getWellnessScore(context)

    LaunchedEffect(Unit) {
        GamificationManager.updateActivity(context)
    }

    var showHeader by remember { mutableStateOf(false) }
    var showScore by remember { mutableStateOf(false) }
    var showGrid by remember { mutableStateOf(false) }
    var showWidgets by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(50)
        showHeader = true
        delay(90)
        showScore = true
        delay(90)
        showGrid = true
        delay(90)
        showWidgets = true
    }

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
            visible = showHeader,
            enter = fadeIn(tween(420)) + slideInVertically(tween(420)) { it / 6 }
        ) {
            DashboardHeader(displayName = displayName, streak = streak, onOpenProfile = onOpenProfile, onOpenRewards = onOpenRewards)
        }
        Spacer(modifier = Modifier.height(18.dp))
        AnimatedVisibility(
            visible = showScore,
            enter = fadeIn(tween(440)) + slideInVertically(tween(440)) { it / 8 }
        ) {
            WellnessScorePremiumCard(score = wellnessScore)
        }
        Spacer(modifier = Modifier.height(18.dp))
        AnimatedVisibility(
            visible = showGrid,
            enter = fadeIn(tween(460)) + slideInVertically(tween(460)) { fullHeight: Int -> fullHeight / 8 }
        ) {
            QuickAccessGridPremium(
                onCheckIn = onOpenCheckIn,
                onChatbot = onOpenChatbot,
                onMap = onOpenServicesMap,
                onSounds = onOpenSounds
            )
        }
        Spacer(modifier = Modifier.height(18.dp))
        AnimatedVisibility(
            visible = showWidgets,
            enter = fadeIn(tween(480)) + slideInVertically(tween(480)) { fullHeight: Int -> fullHeight / 8 }
        ) {
            Column {
                NextReminderPremiumCard()
                Spacer(modifier = Modifier.height(14.dp))
                StreakCalendarCard(streak = streak, onClick = onOpenRewards)
                Spacer(modifier = Modifier.height(14.dp))
                DashboardShortcutsRow(onOpenDiary = onOpenDiary, onOpenLibrary = onOpenLibrary)
            }
        }
        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
private fun DashboardShortcutsRow(onOpenDiary: () -> Unit, onOpenLibrary: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Biblioteca card
        val libraryGradient = Brush.linearGradient(listOf(Color(0xFF3B82F6), Color(0xFF93C5FD)))
        Box(
            modifier = Modifier
                .weight(1f)
                .shadow(8.dp, RoundedCornerShape(20.dp), spotColor = Color(0x2A3B82F6))
                .clip(RoundedCornerShape(20.dp))
                .background(getSoftBlue())
                .border(1.dp, Brush.verticalGradient(listOf(Color.White.copy(0.4f), Color(0xFF3B82F6).copy(alpha = 0.12f))), RoundedCornerShape(20.dp))
                .clickable(
                    interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                    indication = null
                ) { onOpenLibrary() }
                .padding(16.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(id = com.upn.relaxmind.R.drawable.bibliteca),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Biblioteca",
                    style = MaterialTheme.typography.labelMedium.copy(brush = libraryGradient),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Artículos y consejos",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }
        // Diario personal card
        val diaryGradient = Brush.linearGradient(listOf(Color(0xFF8B5CF6), Color(0xFFC4B5FD)))
        Box(
            modifier = Modifier
                .weight(1f)
                .shadow(8.dp, RoundedCornerShape(20.dp), spotColor = Color(0x2A8B5CF6))
                .clip(RoundedCornerShape(20.dp))
                .background(getSoftPurple())
                .border(1.dp, Brush.verticalGradient(listOf(Color.White.copy(0.4f), Color(0xFF8B5CF6).copy(alpha = 0.12f))), RoundedCornerShape(20.dp))
                .clickable(
                    interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                    indication = null
                ) { onOpenDiary() }
                .padding(16.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(id = com.upn.relaxmind.R.drawable.diario),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Diario",
                    style = MaterialTheme.typography.labelMedium.copy(brush = diaryGradient),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Diario personal",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun PlaceholderTab(

    title: String,
    subtitle: String,
    gradient: Brush,
    showPrimaryButton: Boolean = false,
    onPrimaryAction: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(18.dp, RoundedCornerShape(28.dp), spotColor = Color(0x22000000), ambientColor = Color(0x12000000))
                .clip(RoundedCornerShape(28.dp))
                .background(gradient)
                .padding(28.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyLarge,
                    color = RelaxMutedText,
                    textAlign = TextAlign.Center
                )
                if (showPrimaryButton) {
                    Spacer(modifier = Modifier.height(20.dp))
                    ScalePressSurface(
                        onClick = onPrimaryAction,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Abrir",
                            modifier = Modifier.padding(vertical = 14.dp),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = RelaxGreen
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PremiumBottomNavigationBar(
    selectedTab: DashboardTab,
    onTabSelected: (DashboardTab) -> Unit
) {
    val interactionHome = remember { MutableInteractionSource() }
    val interactionBreath = remember { MutableInteractionSource() }
    val interactionProgress = remember { MutableInteractionSource() }
    val interactionReminders = remember { MutableInteractionSource() }
    val interactionSettings = remember { MutableInteractionSource() }
    val isDark = com.upn.relaxmind.core.ui.theme.LocalIsDarkTheme.current

    val glassLayer: Modifier = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    if (isDark) {
                        listOf(RelaxDarkSurface.copy(alpha = 0.72f), RelaxDarkSurface.copy(alpha = 0.88f))
                    } else {
                        listOf(Color.White.copy(alpha = 0.72f), Color.White.copy(alpha = 0.88f))
                    }
                )
            )
            .blur(14.dp)
    } else {
        Modifier
            .fillMaxWidth()
            .background(if (isDark) RelaxDarkSurface.copy(alpha = 0.94f) else Color.White.copy(alpha = 0.94f))
    }

    val islandShape = RoundedCornerShape(32.dp)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 12.dp, vertical = 12.dp)
            .shadow(
                elevation = 14.dp,
                shape = islandShape,
                spotColor = if (isDark) Color.Black else Color(0x33000000),
                ambientColor = if (isDark) Color.Black else Color(0x11000000)
            )
            .clip(islandShape),
        shape = islandShape,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = glassLayer)
            NavigationBar(
                containerColor = Color.Transparent,
                tonalElevation = 0.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(78.dp)
                    .padding(horizontal = 16.dp)
            ) {
                NavigationBarItemWithScale(
                    selected = selectedTab == DashboardTab.Home,
                    onClick = { onTabSelected(DashboardTab.Home) },
                    interactionSource = interactionHome,
                    itemModifier = Modifier.weight(1f).padding(horizontal = 2.dp),
                    iconContent = { Icon(Icons.Outlined.Home, contentDescription = null) },
                    tabLabel = DashboardTab.Home.label,
                    activeColor = DashboardTab.Home.activeColor
                )
                NavigationBarItemWithScale(
                    selected = selectedTab == DashboardTab.Breathing,
                    onClick = { onTabSelected(DashboardTab.Breathing) },
                    interactionSource = interactionBreath,
                    itemModifier = Modifier.weight(1f).padding(horizontal = 2.dp),
                    iconContent = { Icon(Icons.Outlined.Air, contentDescription = null) },
                    tabLabel = DashboardTab.Breathing.label,
                    activeColor = DashboardTab.Breathing.activeColor
                )
                NavigationBarItemWithScale(
                    selected = selectedTab == DashboardTab.Progress,
                    onClick = { onTabSelected(DashboardTab.Progress) },
                    interactionSource = interactionProgress,
                    itemModifier = Modifier.weight(1f).padding(horizontal = 2.dp),
                    iconContent = { Icon(Icons.Outlined.Insights, contentDescription = null) },
                    tabLabel = DashboardTab.Progress.label,
                    activeColor = DashboardTab.Progress.activeColor
                )
                NavigationBarItemWithScale(
                    selected = selectedTab == DashboardTab.Reminders,
                    onClick = { onTabSelected(DashboardTab.Reminders) },
                    interactionSource = interactionReminders,
                    itemModifier = Modifier.weight(1f).padding(horizontal = 2.dp),
                    iconContent = { Icon(Icons.Outlined.CalendarMonth, contentDescription = null) },
                    tabLabel = DashboardTab.Reminders.label,
                    activeColor = DashboardTab.Reminders.activeColor
                )
                NavigationBarItemWithScale(
                    selected = selectedTab == DashboardTab.Settings,
                    onClick = { onTabSelected(DashboardTab.Settings) },
                    interactionSource = interactionSettings,
                    itemModifier = Modifier.weight(1f).padding(horizontal = 2.dp),
                    iconContent = { Icon(Icons.Outlined.Settings, contentDescription = null) },
                    tabLabel = DashboardTab.Settings.label,
                    activeColor = DashboardTab.Settings.activeColor
                )
            }
        }
    }
}

@Composable
private fun NearbyCentersPremiumCard(onOpenMap: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(16.dp, RoundedCornerShape(24.dp), spotColor = RelaxGreen.copy(0.25f))
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    listOf(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.surface)
                )
            )
            .clickable { onOpenMap() }
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(RelaxGreen.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Map, null, tint = RelaxGreen, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Centros de salud cercanos",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Hay 3 centros disponibles ahora",
                    style = MaterialTheme.typography.bodySmall,
                    color = RelaxMutedText
                )
            }
            Icon(Icons.AutoMirrored.Outlined.ArrowForwardIos, null, tint = RelaxMutedText.copy(0.4f), modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
private fun RowScope.NavigationBarItemWithScale(
    selected: Boolean,
    onClick: () -> Unit,
    interactionSource: MutableInteractionSource,
    itemModifier: Modifier = Modifier,
    iconContent: @Composable () -> Unit,
    tabLabel: String,
    activeColor: Color
) {
    val combined by animateFloatAsState(
        targetValue = if (selected) 1.08f else 1f,
        animationSpec = spring(dampingRatio = 0.64f, stiffness = 360f),
        label = "navSelection"
    )

    Column(
        modifier = itemModifier
            .weight(1f)
            .height(78.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .graphicsLayer {
                scaleX = combined
                scaleY = combined
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(if (selected) activeColor.copy(alpha = 0.14f) else Color.Transparent)
                .padding(horizontal = 14.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            val iconTint = if (selected) activeColor else MaterialTheme.colorScheme.onSurfaceVariant.copy(0.6f)
            CompositionLocalProvider(LocalContentColor provides iconTint) {
                iconContent()
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = tabLabel,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Medium,
            color = if (selected) activeColor else MaterialTheme.colorScheme.onSurfaceVariant.copy(0.6f),
            maxLines = 1
        )
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class, androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
private fun DashboardHeader(displayName: String, streak: Int, onOpenProfile: () -> Unit, onOpenRewards: () -> Unit) {
    val context = LocalContext.current
    val user = remember { AuthManager.getCurrentUser(context) }
    val isDark = com.upn.relaxmind.core.ui.theme.LocalIsDarkTheme.current

    val fullName = listOfNotNull(
        user?.name?.ifBlank { null },
        user?.lastName?.ifBlank { null }
    ).joinToString(" ").ifBlank { displayName.ifBlank { "Usuario" } }

    val dateText = remember {
        try {
            LocalDate.now().format(
                DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
                    .withLocale(Locale.forLanguageTag("es-ES"))
            )
        } catch (_: Throwable) {
            LocalDate.now().toString()
        }
    }

    // Bottom sheet state
    var showSheet by remember { mutableStateOf(false) }
    val sheetState = androidx.compose.material3.rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Hola, ${displayName.substringBefore(" ")}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.width(6.dp))
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(id = com.upn.relaxmind.R.drawable.emoji_saludo),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = dateText,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isDark) MaterialTheme.colorScheme.onSurfaceVariant else RelaxMutedText
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 4.dp)
        ) {
            // Streak badge
            Surface(
                onClick = onOpenRewards,
                shape = RoundedCornerShape(20.dp),
                color = StreakOrange.copy(alpha = 0.12f),
                modifier = Modifier.height(34.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(id = com.upn.relaxmind.R.drawable.racha),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$streak",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = StreakOrange
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            // Premium avatar button
            Surface(
                onClick = { showSheet = true },
                shape = CircleShape,
                modifier = Modifier
                    .size(44.dp)
                    .shadow(
                        elevation = 6.dp,
                        shape = CircleShape,
                        spotColor = MaterialTheme.colorScheme.primary.copy(0.25f)
                    ),
                color = Color.Transparent
            ) {
                com.upn.relaxmind.core.ui.components.UserAvatar(user = user, size = 44, fontSize = 16)
            }
        }
    }

    // Quick action sheet
    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .size(40.dp, 4.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f))
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 20.dp, bottom = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Large avatar
                com.upn.relaxmind.core.ui.components.UserAvatar(user = user, size = 84, fontSize = 32)
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = fullName,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = user?.email ?: "Sin correo",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Stats Grid - Using Rows to avoid experimental layout issues
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            modifier = Modifier.weight(1f),
                            painter = androidx.compose.ui.res.painterResource(id = com.upn.relaxmind.R.drawable.racha),
                            label = "Racha Actual",
                            value = "$streak días",
                            color = StreakOrange,
                            bgColor = getSoftOrange()
                        )
                        StatCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Filled.SentimentSatisfiedAlt,
                            label = "Estado Paz",
                            value = "Equilibrado",
                            color = RelaxGreen,
                            bgColor = getSoftGreen()
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Filled.Star,
                            label = "Nivel Relax",
                            value = "Bronce II",
                            color = Color(0xFFF59E0B),
                            bgColor = getSoftYellow()
                        )
                        StatCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Filled.SelfImprovement,
                            label = "Sesiones",
                            value = "12 completas",
                            color = Color(0xFF7C3AED),
                            bgColor = getSoftPurple()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action buttons
                Button(
                    onClick = {
                        showSheet = false
                        onOpenProfile()
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Outlined.Person, null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ver Perfil Completo", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = {
                        showSheet = false
                        // Navigate to edit — using the profile route as fallback
                        onOpenProfile()
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    )
                ) {
                    Icon(Icons.Outlined.Edit, null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Editar Perfil",
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    painter: androidx.compose.ui.graphics.painter.Painter? = null,
    label: String,
    value: String,
    color: Color,
    bgColor: Color
) {
    Surface(
        modifier = modifier.shadow(6.dp, RoundedCornerShape(20.dp), spotColor = color.copy(alpha = 0.15f)),
        shape = RoundedCornerShape(20.dp),
        color = bgColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, Brush.verticalGradient(listOf(Color.White.copy(0.4f), color.copy(0.1f))))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            if (painter != null) {
                androidx.compose.foundation.Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            } else if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = RelaxMutedText,
                fontWeight = FontWeight.Medium
            )
        }
    }
}


@Composable
private fun WellnessScorePremiumCard(score: Int) {
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
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(id = com.upn.relaxmind.R.drawable.meditar),
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

@Composable
private fun QuickAccessGridPremium(
    onCheckIn: () -> Unit,
    onChatbot: () -> Unit,
    onMap: () -> Unit,
    onSounds: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            QuickTilePremium(
                title = "Check-in Diario",
                subtitle = "¿Cómo estás hoy?",
                icon = { 
                    Image(
                        painter = androidx.compose.ui.res.painterResource(id = com.upn.relaxmind.R.drawable.checkin),
                        contentDescription = null,
                        modifier = Modifier.size(36.dp)
                    ) 
                },
                gradient = Brush.linearGradient(listOf(Color(0xFF10B981), Color(0xFF6EE7B7))),
                bgColor = getSoftGreen(),
                onClick = onCheckIn,
                modifier = Modifier.weight(1f)
            )
            QuickTilePremium(
                title = "Lumi ✨",
                subtitle = "Tu amigo IA",
                icon = { 
                    Image(
                        painter = androidx.compose.ui.res.painterResource(id = com.upn.relaxmind.R.drawable.lumi),
                        contentDescription = null,
                        modifier = Modifier.size(36.dp)
                    ) 
                },
                gradient = Brush.linearGradient(listOf(Color(0xFF0EA5E9), Color(0xFF7DD3FC))),
                bgColor = getSoftBlue(),
                onClick = onChatbot,
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            QuickTilePremium(
                title = "Centros Cerca",
                subtitle = "Mapa de ayuda",
                icon = { 
                    Image(
                        painter = androidx.compose.ui.res.painterResource(id = com.upn.relaxmind.R.drawable.centroscerca),
                        contentDescription = null,
                        modifier = Modifier.size(36.dp)
                    ) 
                },
                gradient = Brush.linearGradient(listOf(Color(0xFFF97316), Color(0xFFFDBA74))),
                bgColor = getSoftOrange(),
                onClick = onMap,
                modifier = Modifier.weight(1f)
            )
            QuickTilePremium(
                title = "Sonidos",
                subtitle = "Música para tu mente",
                icon = { 
                    Image(
                        painter = androidx.compose.ui.res.painterResource(id = com.upn.relaxmind.R.drawable.sonido),
                        contentDescription = null,
                        modifier = Modifier.size(36.dp)
                    ) 
                },
                gradient = Brush.linearGradient(listOf(Color(0xFFEAB308), Color(0xFFFEF08A))),
                bgColor = getSoftYellow(),
                onClick = onSounds,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun QuickTilePremium(
    title: String,
    subtitle: String,
    icon: @Composable () -> Unit,
    gradient: Brush,
    bgColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (pressed) 0.96f else 1f, animationSpec = tween(150), label = "")
    
    ScalePressSurface(
        onClick = onClick,
        interactionSource = interaction,
        modifier = modifier
            .height(140.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .shadow(8.dp, RoundedCornerShape(24.dp), spotColor = Color(0x12000000))
                .clip(RoundedCornerShape(24.dp))
                .background(bgColor)
                .border(1.dp, Brush.verticalGradient(listOf(Color.White.copy(0.4f), Color.Black.copy(0.05f))), RoundedCornerShape(24.dp))
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            icon()
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(brush = gradient),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun ScalePressSurface(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier.clickable(
            interactionSource = interactionSource,
            indication = ripple(bounded = true),
            onClick = onClick
        )
    ) {
        content()
    }
}

@Composable
private fun NextReminderPremiumCard() {
    val gradient = Brush.linearGradient(listOf(Color(0xFFB45309), Color(0xFFF59E0B)))
    val softBorderGradient = Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.4f), Color(0xFFB45309).copy(alpha = 0.12f)))
    val cardBg = getSoftAmber()
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(24.dp), spotColor = Color(0x2AB45309))
            .clip(RoundedCornerShape(24.dp))
            .background(cardBg)
            .border(1.dp, softBorderGradient, RoundedCornerShape(24.dp))
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.foundation.Image(
                painter = androidx.compose.ui.res.painterResource(id = com.upn.relaxmind.R.drawable.notificaciones),
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Próximo recordatorio",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Toma de medicación - 8:00 PM",
                    style = MaterialTheme.typography.titleMedium.copy(brush = gradient),
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Composable
private fun WellnessTrendPremiumCard(values: List<Int>) {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val brush = Brush.linearGradient(
        listOf(surfaceColor, MaterialTheme.colorScheme.primary.copy(0.08f), surfaceColor)
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(3.dp, RoundedCornerShape(24.dp), spotColor = Color(0x16000000))
            .clip(RoundedCornerShape(24.dp))
            .background(brush)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Ultimos 7 dias",
                style = MaterialTheme.typography.labelMedium,
                color = RelaxMutedText
            )
            Spacer(modifier = Modifier.height(10.dp))
            WellnessSparkline(values = values)
        }
    }
}

@Composable
private fun StreakCalendarCard(streak: Int, onClick: () -> Unit) {
    val today = LocalDate.now()
    val startOfWeek = today.minusDays(today.dayOfWeek.value % 7L)
    val streakStartDay = today.minusDays((streak - 1).coerceAtLeast(0).toLong())
    val dayLabels = listOf("D", "L", "M", "M", "J", "V", "S")

    val gradient = Brush.linearGradient(listOf(Color(0xFFEA580C), Color(0xFFFDBA74)))
    val softBorderGradient = Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.4f), Color(0xFFEA580C).copy(alpha = 0.12f)))
    val cardBg = getSoftOrange()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(24.dp), spotColor = Color(0x2AEA580C))
            .clip(RoundedCornerShape(24.dp))
            .background(cardBg)
            .clickable { onClick() }
            .border(1.dp, softBorderGradient, RoundedCornerShape(24.dp))
            .padding(20.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tu racha semanal",
                    style = MaterialTheme.typography.titleMedium.copy(brush = gradient),
                    fontWeight = FontWeight.Bold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(id = com.upn.relaxmind.R.drawable.racha),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(
                        text = "$streak días",
                        style = MaterialTheme.typography.titleMedium.copy(brush = gradient),
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for (i in 0..6) {
                    val day = startOfWeek.plusDays(i.toLong())
                    val isToday = day == today
                    val isStreak = !day.isAfter(today) && !day.isBefore(streakStartDay)
                    val isFuture = day.isAfter(today)

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = dayLabels[i],
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Gray,
                            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                        )
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isToday -> Brush.verticalGradient(listOf(Color(0xFFFFB347), Color(0xFFFB923C)))
                                        isStreak -> Brush.verticalGradient(listOf(Color(0xFFFB923C).copy(alpha=0.22f), Color(0xFFFDBA74).copy(alpha=0.12f)))
                                        else -> Brush.verticalGradient(listOf(Color.LightGray.copy(0.3f), Color.LightGray.copy(0.1f)))
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isStreak && !isFuture) {
                                Icon(Icons.Filled.LocalFireDepartment, null, tint = if (isToday) Color.White else Color(0xFFEA580C), modifier = Modifier.size(24.dp))
                            } else {
                                Text(
                                    text = day.dayOfMonth.toString(),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (isFuture) Color.LightGray else Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BadgeItem(icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, isUnlocked: Boolean) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(if (isUnlocked) color.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        if (isUnlocked) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
        } else {
            Icon(imageVector = Icons.Outlined.Lock, contentDescription = null, tint = RelaxMutedText.copy(alpha = 0.4f), modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
private fun WellnessSparkline(values: List<Int>) {

    val maxV = values.maxOrNull() ?: 0
    val minV = values.minOrNull() ?: 0
    val range = (maxV - minV).coerceAtLeast(1)
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
    ) {
        val w = size.width
        val h = size.height
        val pad = 8.dp.toPx()
        val step = (w - pad * 2) / (values.size - 1).coerceAtLeast(1)
        val path = Path()
        values.forEachIndexed { i, v ->
            val t = (v - minV).toFloat() / range
            val x = pad + i * step
            val y = h - pad - t * (h - pad * 2)
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        drawPath(
            path = path,
            color = ChartLine,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )
        values.forEachIndexed { i, v ->
            val t = (v - minV).toFloat() / range
            val x = pad + i * step
            val y = h - pad - t * (h - pad * 2)
            drawCircle(color = ChartLine, radius = 4.dp.toPx(), center = Offset(x, y))
        }
    }
}

@Composable
private fun SosFloatingButton(onSosTriggered: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var holdProgress by remember { mutableFloatStateOf(0f) }
    var isHolding by remember { mutableStateOf(false) }

    val infinite = rememberInfiniteTransition(label = "sosPulse")
    val pulse by infinite.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    LaunchedEffect(isHolding) {
        if (!isHolding) {
            holdProgress = 0f
            return@LaunchedEffect
        }
        holdProgress = 0f
        repeat(20) {
            delay(100)
            if (!isHolding) return@LaunchedEffect
            holdProgress += 0.05f
        }
    }

    val sosBrush = Brush.linearGradient(listOf(SosRed, SosCoral))
    // Arc stroke width in dp — drawn outside the 64dp button
    val arcStroke = 4.dp

    Box(
        modifier = Modifier
            .graphicsLayer {
                scaleX = pulse
                scaleY = pulse
            }
            // Extra space around button for the arc indicator
            .size(80.dp),
        contentAlignment = Alignment.Center
    ) {
        // Circular arc progress drawn around button — no track, gradient only
        if (isHolding && holdProgress > 0f) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokePx = arcStroke.toPx()
                val arcSize = androidx.compose.ui.geometry.Size(
                    size.width - strokePx, size.height - strokePx
                )
                val topLeft = androidx.compose.ui.geometry.Offset(
                    strokePx / 2f, strokePx / 2f
                )
                // Gradient: bone-white start → deep red at end of sweep
                val arcBrush = Brush.sweepGradient(
                    colors = listOf(
                        Color(0xFFFFF5EC),   // hueso cálido
                        Color(0xFFFFAA88),   // coral suave
                        Color(0xFFEF4444)    // rojo profundo
                    ),
                    center = androidx.compose.ui.geometry.Offset(
                        size.width / 2f, size.height / 2f
                    )
                )
                drawArc(
                    brush = arcBrush,
                    startAngle = -90f,
                    sweepAngle = 360f * holdProgress,
                    useCenter = false,
                    style = Stroke(width = strokePx + 1.5.dp.toPx(), cap = StrokeCap.Round),
                    topLeft = topLeft,
                    size = arcSize
                )
            }
        }

        // Main button
        Box(
            modifier = Modifier
                .size(64.dp)
                .shadow(14.dp, CircleShape, spotColor = SosRed.copy(0.5f))
                .clip(CircleShape)
                .background(sosBrush)
                .pointerInput(scope) {
                    detectTapGestures(
                        onPress = {
                            isHolding = true
                            vibrateSoft(context)
                            var sosJob: Job? = null
                            sosJob = scope.launch {
                                delay(2000)
                                vibrateConfirm(context)
                                onSosTriggered()
                                isHolding = false
                                holdProgress = 0f
                            }
                            try {
                                awaitRelease()
                            } finally {
                                sosJob?.cancel()
                                isHolding = false
                                holdProgress = 0f
                            }
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = com.upn.relaxmind.R.drawable.boton_sos),
                contentDescription = "S.O.S.",
                modifier = Modifier.size(36.dp)
            )
        }
    }
}


private fun buildSyntheticHistory(score: Int): List<Int> {
    val base = score.coerceIn(0, 100)
    return List(7) { i ->
        (base - 18 + i * 5 + (i % 3) * 2).coerceIn(0, 100)
    }
}

@Suppress("DEPRECATION")
private fun vibrateSoft(context: Context) {
    val v = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator ?: return
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        v.vibrate(VibrationEffect.createOneShot(25, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        v.vibrate(25)
    }
}

@Suppress("DEPRECATION")
private fun vibrateConfirm(context: Context) {
    val v = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vm.defaultVibrator
    } else {
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        v.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 50, 80, 50, 80, 120), -1))
    } else {
        v.vibrate(longArrayOf(0, 50, 80, 50, 80, 120), -1)
    }
}

@Composable
private fun ProgressTabContent(modifier: Modifier = Modifier) {
    val isDark = LocalIsDarkTheme.current
    val primaryGreen = Color(0xFF0F6E56)
    val accentGold = Color(0xFFF59E0B)
    val context = LocalContext.current
    val displayName = remember { AppPreferences.getDisplayName(context) }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 100.dp)
    ) {
        // REORDERED: Title first
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

        ProfileCompactHeader(displayName, isDark, primaryGreen)
        
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            Spacer(modifier = Modifier.height(8.dp))
            
            // 1. ENHANCED STATISTICS SECTION
            EnhancedStatsSection(isDark, accentGold)
            
            Spacer(modifier = Modifier.height(32.dp))

            // 2. CHECK-IN HISTORY SECTION
            Text(
                text = "Historial de Bienestar",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                CheckInHistoryItem(
                    date = "Hoy, 10:30 AM",
                    mood = "Excelente",
                    score = 92,
                    color = RelaxGreen,
                    bgColor = getSoftGreen(),
                    icon = Icons.Default.SentimentVerySatisfied
                )
                CheckInHistoryItem(
                    date = "Ayer, 09:15 AM",
                    mood = "Bien",
                    score = 78,
                    color = Color(0xFF3B82F6),
                    bgColor = getSoftBlue(),
                    icon = Icons.Default.SentimentSatisfied
                )
                CheckInHistoryItem(
                    date = "27 Oct, 08:45 PM",
                    mood = "Neutral",
                    score = 65,
                    color = Color(0xFFF59E0B),
                    bgColor = getSoftYellow(),
                    icon = Icons.Default.SentimentNeutral
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // 2. ACTIVITY SUMMARY SECTION
            ActivitySummarySection(isDark, primaryGreen)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 3. EMOTIONAL CHECK-IN HISTORY (Calendar)
            EmotionalCalendarSection(isDark)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 4. EXPANDED ACHIEVEMENTS SECTION
            ExpandedAchievementsSection(isDark, accentGold)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 5. DIARY QUICK ACCESS
            DiaryQuickAccessSection(isDark, primaryGreen)
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ProfileCompactHeader(name: String, isDark: Boolean, primaryGreen: Color) {
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

@Composable
private fun CheckInHistoryItem(
    date: String,
    mood: String,
    score: Int,
    color: Color,
    bgColor: Color,
    icon: ImageVector
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(20.dp), spotColor = color.copy(0.15f)),
        shape = RoundedCornerShape(20.dp),
        color = bgColor,
        border = BorderStroke(1.dp, Brush.verticalGradient(listOf(Color.White.copy(0.4f), color.copy(0.1f))))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color.copy(0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = mood,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = date,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$score",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = color
                )
                Text("PTS", style = MaterialTheme.typography.labelSmall, color = color.copy(0.6f))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowForwardIos,
                contentDescription = null,
                tint = color.copy(0.3f),
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
private fun EnhancedStatsSection(isDark: Boolean, accentColor: Color) {
    var selectedRange by remember { mutableStateOf("7 días") }
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Bienestar Emocional", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Date Selector moved below Title
            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (isDark) Color.White.copy(0.05f) else Color(0xFFF1F5F9))
                    .padding(2.dp)
            ) {
                listOf("7 días", "1 mes", "3 meses").forEach { range ->
                    val isSelected = selectedRange == range
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (isSelected) (if(isDark) Color.White.copy(0.15f) else Color.White) else Color.Transparent)
                            .clickable { selectedRange = range }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            range, 
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) (if(isDark) Color.White else Color.Black) else RelaxMutedText,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Hybrid Chart: Bar + Curves
            Box(modifier = Modifier.fillMaxWidth().height(160.dp)) {
                androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                    val points = listOf(0.4f, 0.7f, 0.3f, 0.8f, 0.5f, 0.9f, 0.6f)
                    val step = size.width / (points.size - 1)
                    val barWidth = 12.dp.toPx()
                    
                    // Draw Bars
                    points.forEachIndexed { i, p ->
                        val x = i * step
                        val h = p * size.height
                        drawRoundRect(
                            color = accentColor.copy(0.15f),
                            topLeft = Offset(x - barWidth/2, size.height - h),
                            size = androidx.compose.ui.geometry.Size(barWidth, h),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
                        )
                    }
                    
                    // Draw Connecting Curve
                    val path = Path()
                    points.forEachIndexed { i, p ->
                        val x = i * step
                        val y = size.height - (p * size.height)
                        if (i == 0) path.moveTo(x, y) else {
                            val prevX = (i - 1) * step
                            val prevY = size.height - (points[i - 1] * size.height)
                            path.cubicTo(
                                prevX + step/2, prevY,
                                x - step/2, y,
                                x, y
                            )
                        }
                    }
                    drawPath(path, accentColor, style = Stroke(3.dp.toPx(), cap = StrokeCap.Round))
                    
                    // Draw Points
                    points.forEachIndexed { i, p ->
                        drawCircle(accentColor, 4.dp.toPx(), Offset(i * step, size.height - (p * size.height)))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatSmallCard(
                    modifier = Modifier.weight(1f),
                    label = "Frecuente",
                    value = "😊 Feliz",
                    isDark = isDark
                )
                StatSmallCard(
                    modifier = Modifier.weight(1f),
                    label = "Mejor Día",
                    value = "Sábado",
                    icon = Icons.Default.Star,
                    isDark = isDark
                )
            }
        }
    }
}

@Composable
private fun StatSmallCard(modifier: Modifier, label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector? = null, isDark: Boolean) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isDark) Color.White.copy(0.03f) else Color(0xFFF8FAFC))
            .padding(12.dp)
    ) {
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = RelaxMutedText)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                if (icon != null) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(icon, null, modifier = Modifier.size(12.dp), tint = Color(0xFFF59E0B))
                }
            }
        }
    }
}

@Composable
private fun ActivitySummarySection(isDark: Boolean, primary: Color) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Resumen de Actividad", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(16.dp))
            
            ActivityRow(Icons.Outlined.Air, "Respiración 4-7-8", "Ejercicio más usado", primary)
            androidx.compose.material3.HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outline.copy(0.1f))
            ActivityRow(Icons.Outlined.AccessTime, "320 min totales", "Tiempo acumulado", primary)
            androidx.compose.material3.HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outline.copy(0.1f))
            ActivityRow(Icons.Outlined.History, "23 min/día", "Promedio diario", primary)
        }
    }
}

@Composable
private fun ActivityRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier.size(40.dp).clip(CircleShape).background(color.copy(0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, modifier = Modifier.size(20.dp), tint = color)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
            Text(subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.7f))
        }
    }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
private fun EmotionalCalendarSection(isDark: Boolean) {
    var selectedMonth by remember { mutableStateOf("Abril") }
    var showMonthPicker by remember { mutableStateOf(false) }
    val months = listOf("Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio")
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Tu Mes Emocional", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
                
                Box {
                    TextButton(onClick = { showMonthPicker = true }) {
                        Text(selectedMonth, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        Icon(Icons.Default.ArrowDropDown, null, modifier = Modifier.size(16.dp))
                    }
                    DropdownMenu(expanded = showMonthPicker, onDismissRequest = { showMonthPicker = false }) {
                        months.forEach { m ->
                            DropdownMenuItem(
                                text = { Text(m) },
                                onClick = { selectedMonth = m; showMonthPicker = false }
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Calendar Grid with Days Labels
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("L", "M", "X", "J", "V", "S", "D").forEach { day ->
                    Text(
                        day, 
                        modifier = Modifier.weight(1f), 
                        textAlign = TextAlign.Center, 
                        style = MaterialTheme.typography.labelSmall,
                        color = RelaxMutedText
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            var selectedDayDetail by remember { mutableStateOf<Int?>(null) }
            
            if (selectedDayDetail != null) {
                CheckInDetailDialog(day = selectedDayDetail!!, onDismiss = { selectedDayDetail = null })
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                for (week in 0..4) { // 5 weeks approx
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (dayOfWeek in 0..6) {
                            val i = week * 7 + dayOfWeek
                            if (i < 30) {
                                val color = when {
                                    i % 7 == 0 -> Color(0xFF93C5FD) // Muy Mal
                                    i % 5 == 0 -> Color(0xFFC4B5FD) // Mal
                                    i % 4 == 0 -> Color(0xFF6EE7B7) // Neutral
                                    i % 3 == 0 -> Color(0xFFFDBA74) // Bien
                                    i % 2 == 0 -> Color(0xFFFDE047) // Muy Bien
                                    else -> (if(isDark) Color.White.copy(0.05f) else Color(0xFFF1F5F9))
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(color)
                                        .clickable { selectedDayDetail = i + 1 },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "${i + 1}", 
                                        style = MaterialTheme.typography.labelSmall, 
                                        fontSize = 10.sp, 
                                        fontWeight = FontWeight.Bold,
                                        color = if(isDark) Color.White.copy(0.6f) else Color.Black.copy(0.4f)
                                    )
                                }
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Legend - Replaced FlowRow with Column for stability
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    LegendItem(Color(0xFF93C5FD), "Muy Mal")
                    LegendItem(Color(0xFFC4B5FD), "Mal")
                    LegendItem(Color(0xFF6EE7B7), "Neutral")
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    LegendItem(Color(0xFFFDBA74), "Bien")
                    LegendItem(Color(0xFFFDE047), "Muy Bien")
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun CheckInDetailDialog(
    day: Int,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RelaxGreen)
            ) { 
                Text("Cerrar", fontWeight = FontWeight.Bold) 
            }
        },
        title = {
            Text("Detalle del día $day", fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleLarge)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                DetailRow(Icons.Default.SentimentVerySatisfied, "Bienestar General", "Excelente (92%)", RelaxGreen)
                DetailRow(Icons.Default.NightsStay, "Calidad de Sueño", "Muy Bueno (8h)", Color(0xFF6366F1))
                DetailRow(Icons.Default.Healing, "Molestias", "Ninguna reportada", Color.Gray)
                DetailRow(Icons.Default.AutoAwesome, "Medicación", "Dosis completada", Color(0xFF10B981))
            }
        },
        shape = RoundedCornerShape(32.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}

@Composable
private fun DetailRow(icon: ImageVector, label: String, value: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(44.dp).clip(CircleShape).background(color.copy(0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = RelaxMutedText)
            Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
        Spacer(modifier = Modifier.width(6.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = RelaxMutedText)
    }
}

@Composable
private fun ExpandedAchievementsSection(isDark: Boolean, accent: Color) {
    Column {
        Text("Logros Alcanzados", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(16.dp))
        
        androidx.compose.foundation.lazy.LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            item { AchievementItem("Maestro Zen", Icons.Default.SelfImprovement, Color(0xFF8B5CF6), true) }
            item { AchievementItem("7 Días", Icons.Default.LocalFireDepartment, Color(0xFFFF8A5C), true) }
            item { AchievementItem("Explorador", Icons.Default.Map, Color(0xFF10B981), false) }
            item { AchievementItem("Noctámbulo", Icons.Default.NightsStay, Color(0xFF2563EB), false) }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Progress to next
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(0.1f))
        ) {
            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Lock, null, modifier = Modifier.size(16.dp), tint = RelaxMutedText)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Siguiente: Disciplinado (7 días)", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = 0.42f,
                        modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape),
                        color = accent,
                        trackColor = accent.copy(0.1f)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text("3/7", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = accent)
            }
        }
    }
}

@Composable
private fun AchievementItem(name: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, isUnlocked: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(80.dp)) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(if (isUnlocked) color.copy(0.15f) else MaterialTheme.colorScheme.surfaceVariant)
                .border(2.dp, if (isUnlocked) color.copy(0.3f) else Color.Transparent, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isUnlocked) icon else Icons.Default.Lock,
                contentDescription = null,
                tint = if (isUnlocked) color else RelaxMutedText.copy(0.4f),
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(name, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold, color = if (isUnlocked) MaterialTheme.colorScheme.onSurface else RelaxMutedText)
    }
}

@Composable
private fun DiaryQuickAccessSection(isDark: Boolean, primary: Color) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Mi Diario", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(16.dp))
            
            DiaryPreviewTile("27 Abr", "Hoy me sentí mucho más tranquilo...", "😌")
            Spacer(modifier = Modifier.height(12.dp))
            DiaryPreviewTile("26 Abr", "Tuve un pequeño desafío en el trabajo...", "😐")
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Button(
                onClick = { /* Navigate to new entry */ },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RelaxGreen)
            ) {
                Icon(Icons.Default.Add, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Nueva entrada", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
private fun DiaryPreviewTile(date: String, text: String, emoji: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(0.3f))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(date, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = RelaxMutedText)
        Spacer(modifier = Modifier.width(12.dp))
        Text(text, style = MaterialTheme.typography.bodySmall, maxLines = 1, modifier = Modifier.weight(1f))
        Text(emoji, fontSize = 16.sp)
    }
}
