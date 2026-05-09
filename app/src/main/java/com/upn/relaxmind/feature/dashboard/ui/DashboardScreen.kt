package com.upn.relaxmind.feature.dashboard.ui

import com.upn.relaxmind.core.ui.theme.*
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upn.relaxmind.core.data.auth.AuthManager
import com.upn.relaxmind.core.data.preferences.AppPreferences
import com.upn.relaxmind.feature.meditation.ui.MeditationScreen
import com.upn.relaxmind.feature.reminders.ui.RemindersScreen
import com.upn.relaxmind.feature.profile.ui.SettingsScreen
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
    
    var isUnlocked by remember { 
        mutableStateOf(isGuest || justRegistered || AuthManager.isSessionUnlocked || !AppPreferences.isBiometricEnabled(context)) 
    }
    var authError by remember { mutableStateOf<String?>(null) }

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
        }
        if (justRegistered) {
            AppPreferences.setJustRegistered(context, false)
        }
    }

    if (!isUnlocked) {
        Box(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).systemBarsPadding(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = com.upn.relaxmind.R.drawable.icnono4),
                    contentDescription = null,
                    modifier = Modifier.size(120.dp).padding(bottom = 24.dp)
                )
                Text(
                    "Sesión Protegida",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "Usa tu huella o PIN para continuar",
                    style = MaterialTheme.typography.bodyMedium,
                    color = RelaxMutedText
                )
                Spacer(modifier = Modifier.height(40.dp))
                Button(
                    onClick = {
                        val activity = context as? androidx.fragment.app.FragmentActivity
                        if (activity != null) {
                            com.upn.relaxmind.core.utils.BiometricHelper.authenticate(
                                activity = activity,
                                onSuccess = { isUnlocked = true },
                                onError = { authError = it }
                            )
                        }
                    },
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.height(56.dp).padding(horizontal = 40.dp)
                ) {
                    Icon(Icons.Default.Fingerprint, null)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Desbloquear con Biometría", fontWeight = FontWeight.Bold)
                }
                
                authError?.let {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
        return
    }

    if (showBiometricPrompt) {
        AlertDialog(
            onDismissRequest = { showBiometricPrompt = false },
            title = { Text("Seguridad Biométrica") },
            text = { Text("¿Deseas activar el desbloqueo por huella digital para proteger tu diario y progreso?") },
            confirmButton = {
                TextButton(onClick = {
                    AppPreferences.setBiometricEnabled(context, true)
                    showBiometricPrompt = false
                    Toast.makeText(context, "Biometría activada", Toast.LENGTH_SHORT).show()
                }) { Text("Activar") }
            },
            dismissButton = {
                TextButton(onClick = { showBiometricPrompt = false }) { Text("Ahora no") }
            },
            shape = RoundedCornerShape(28.dp)
        )
    }

    Scaffold(
        bottomBar = {
            PremiumBottomNavigationBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTabIndex = it.ordinal }
            )
        },
        floatingActionButton = {
            if (selectedTab != DashboardTab.Breathing && 
                selectedTab != DashboardTab.Reminders && 
                selectedTab != DashboardTab.Progress) {
                SosFloatingButton(onSosTriggered = onOpenCrisis)
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = {
                    if (targetState.ordinal > initialState.ordinal) {
                        (slideInHorizontally { it } + fadeIn()).togetherWith(slideOutHorizontally { -it } + fadeOut())
                    } else {
                        (slideInHorizontally { -it } + fadeIn()).togetherWith(slideOutHorizontally { it } + fadeOut())
                    }.using(androidx.compose.animation.SizeTransform(clip = false))
                },
                label = "tabAnim",
                modifier = Modifier.weight(1f)
            ) { tab ->
                when (tab) {
                    DashboardTab.Home -> HomeTab(
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
                    DashboardTab.Breathing -> BreathingTab(modifier = Modifier.fillMaxSize())
                    DashboardTab.Progress -> ProgressTab(modifier = Modifier.fillMaxSize())
                    DashboardTab.Reminders -> RemindersTab(modifier = Modifier.fillMaxSize())
                    DashboardTab.Settings -> SettingsTab(
                        modifier = Modifier.fillMaxSize(),
                        onProfileClick = onOpenProfile,
                        onEditProfileClick = onOpenEditProfile,
                        onSecurityClick = onOpenEmergencyQr,
                        onToggleDarkTheme = onToggleDarkTheme,
                        onAboutClick = onOpenAbout,
                        onTermsClick = onOpenTerms,
                        onLogout = onLogout
                    )
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
    val isDark = LocalIsDarkTheme.current
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(24.dp, NavBarShape, spotColor = Color.Black.copy(0.12f)),
        shape = NavBarShape,
        color = if (isDark) RelaxDarkSurface else Color.White,
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .navigationBarsPadding()
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            DashboardTab.entries.forEach { tab ->
                val selected = selectedTab == tab
                val interactionSource = remember { MutableInteractionSource() }
                
                NavigationBarItemWithScale(
                    selected = selected,
                    onClick = { onTabSelected(tab) },
                    interactionSource = interactionSource,
                    iconContent = {
                        Icon(
                            imageVector = when (tab) {
                                DashboardTab.Home -> if (selected) Icons.Filled.Home else Icons.Outlined.Home
                                DashboardTab.Breathing -> if (selected) Icons.Filled.SelfImprovement else Icons.Outlined.SelfImprovement
                                DashboardTab.Progress -> if (selected) Icons.Filled.Insights else Icons.Outlined.Insights
                                DashboardTab.Reminders -> if (selected) Icons.Filled.CalendarMonth else Icons.Outlined.CalendarMonth
                                DashboardTab.Settings -> if (selected) Icons.Filled.Settings else Icons.Outlined.Settings
                            },
                            contentDescription = tab.label,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    tabLabel = tab.label,
                    activeColor = tab.activeColor
                )
            }
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
    val arcStroke = 4.dp

    Box(
        modifier = Modifier
            .graphicsLayer {
                scaleX = pulse
                scaleY = pulse
            }
            .size(80.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isHolding && holdProgress > 0f) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokePx = arcStroke.toPx()
                val arcSize = Size(
                    size.width - strokePx, size.height - strokePx
                )
                val topLeft = Offset(
                    strokePx / 2f, strokePx / 2f
                )
                val arcBrush = Brush.sweepGradient(
                    colors = listOf(
                        Color(0xFFFFF5EC),
                        Color(0xFFFFAA88),
                        Color(0xFFEF4444)
                    ),
                    center = Offset(
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
