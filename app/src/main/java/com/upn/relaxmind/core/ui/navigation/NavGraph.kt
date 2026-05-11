package com.upn.relaxmind.core.ui.navigation

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import java.util.Locale
import kotlinx.coroutines.launch
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.upn.relaxmind.core.data.preferences.AppPreferences
import com.upn.relaxmind.core.data.auth.AuthManager
import com.upn.relaxmind.feature.ai_chat.ui.*
import com.upn.relaxmind.feature.auth.ui.*
import com.upn.relaxmind.feature.caregiver.ui.*
import com.upn.relaxmind.feature.checkin.ui.*
import com.upn.relaxmind.feature.dashboard.ui.*
import com.upn.relaxmind.feature.diary.ui.*
import com.upn.relaxmind.feature.emergency.ui.*
import com.upn.relaxmind.feature.gamification.ui.*
import com.upn.relaxmind.feature.info.ui.*
import com.upn.relaxmind.feature.library.ui.*
import com.upn.relaxmind.feature.meditation.ui.*
import com.upn.relaxmind.feature.profile.ui.*
import com.upn.relaxmind.feature.reminders.ui.*
import com.upn.relaxmind.feature.services_map.ui.*
import com.upn.relaxmind.feature.sounds.ui.*
import com.upn.relaxmind.feature.ai_chat.ui.AiChatScreen
import com.upn.relaxmind.feature.checkin.ui.CheckInScreen
import com.upn.relaxmind.feature.emergency.ui.CrisisScreen
import com.upn.relaxmind.feature.dashboard.ui.DashboardScreen
import com.upn.relaxmind.feature.gamification.ui.DiagnosticTestScreen
import com.upn.relaxmind.feature.diary.ui.DiaryScreen
import com.upn.relaxmind.feature.emergency.ui.EmergencyQrScreen
import com.upn.relaxmind.feature.library.ui.LibraryScreen
import com.upn.relaxmind.feature.auth.ui.ForgotPasswordScreen
import com.upn.relaxmind.feature.auth.ui.LoginViewScreen
import com.upn.relaxmind.feature.auth.ui.OnboardingScreen
import com.upn.relaxmind.feature.profile.ui.ProfileScreen
import com.upn.relaxmind.feature.profile.ui.ProfileViewScreen
import com.upn.relaxmind.feature.profile.ui.EditProfileScreen
import com.upn.relaxmind.feature.auth.ui.WelcomeAuthScreen
import com.upn.relaxmind.feature.auth.ui.RegistrationSuccessScreen
import com.upn.relaxmind.feature.auth.ui.RoleSelectionScreen
import com.upn.relaxmind.feature.services_map.ui.ServicesMapScreen
import com.upn.relaxmind.feature.auth.ui.SignUpScreen
import com.upn.relaxmind.feature.auth.ui.UserRole
import com.upn.relaxmind.feature.auth.ui.VerifyEmailScreen
import com.upn.relaxmind.feature.info.ui.AboutScreen
import com.upn.relaxmind.feature.info.ui.TermsScreen

object RelaxMindRoutes {
    const val AUTH_WELCOME = "auth_welcome"
    const val LOGIN_FORM = "login_form"
    const val FORGOT_PASSWORD = "forgot_password"
    const val ONBOARDING = "onboarding"
    const val ROLE_SELECTION = "role_selection"
    const val SIGN_UP = "sign_up"
    const val VERIFY_EMAIL = "verify_email"
    const val DIAGNOSTIC_TEST = "diagnostic_test"
    const val REGISTRATION_SUCCESS = "registration_success"
    const val DASHBOARD = "dashboard"
    const val CRISIS = "crisis"
    const val DIARY = "diary"
    const val LIBRARY = "library"
    const val PROFILE = "profile"
    const val PROFILE_VIEW = "profile_view"
    const val EDIT_PROFILE = "edit_profile"
    const val ABOUT = "about"
    const val TERMS = "terms"
    const val AI_CHAT = "ai_chat"
    const val EMERGENCY_QR = "emergency_qr"
    const val SERVICES_MAP = "services_map"
    const val CHECK_IN = "check_in"
    const val SOUNDS_RELAXING = "sounds_relaxing"
    const val REWARDS = "rewards"
    
    // Caregiver Routes
    const val CAREGIVER_DASHBOARD = "caregiver_dashboard"
    const val CAREGIVER_REGISTRATION = "caregiver_registration"
    const val CAREGIVER_LINKING = "caregiver_linking"
    const val CAREGIVER_PATIENT_DETAIL = "caregiver_patient_detail"
    const val CAREGIVER_NOTIFICATIONS = "caregiver_notifications"
    const val CAREGIVER_SETTINGS = "caregiver_settings"
    const val CAREGIVER_QR_SCANNER = "caregiver_qr_scanner"
    const val REMOTE_LINKING_CODE = "remote_linking_code"
    const val CAREGIVER_EDIT_PROFILE = "caregiver_edit_profile"
    const val CAREGIVER_MANAGE_LINKS = "caregiver_manage_links"
}

@Composable
fun RelaxMindNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    onToggleDarkTheme: (Boolean) -> Unit = {}
) {
    val context = LocalContext.current
    val scope = androidx.compose.runtime.rememberCoroutineScope()
    val hasSeenOnboarding = AppPreferences.hasSeenOnboarding(context)
    val currentUser = remember { AuthManager.getCurrentUser(context) }
    val isLoggedIn = currentUser != null
    
    val startDest = when {
        isLoggedIn -> if (currentUser?.role == "CAREGIVER") RelaxMindRoutes.CAREGIVER_DASHBOARD else RelaxMindRoutes.DASHBOARD
        !hasSeenOnboarding -> RelaxMindRoutes.ONBOARDING
        else -> RelaxMindRoutes.AUTH_WELCOME
    }

    Box(modifier = modifier.fillMaxSize()) {
        NavHost(
            modifier = Modifier.fillMaxSize(),
            navController = navController,
            startDestination = startDest
        ) {
            composable(RelaxMindRoutes.ONBOARDING) {
                val goWelcome = {
                    navController.navigate(RelaxMindRoutes.AUTH_WELCOME) {
                        popUpTo(RelaxMindRoutes.ONBOARDING) { inclusive = true }
                        launchSingleTop = true
                    }
                }
                OnboardingScreen(
                    onFinish = goWelcome,
                    onSkipToLogin = goWelcome
                )
            }
            composable(RelaxMindRoutes.AUTH_WELCOME) {
                WelcomeAuthScreen(
                    onLoginNavigate = { navController.navigate(RelaxMindRoutes.LOGIN_FORM) },
                    onRegisterNavigate = { navController.navigate(RelaxMindRoutes.ROLE_SELECTION) },
                    onGoogleSignIn = {
                        AppPreferences.saveDisplayName(context, "Usuario Google")
                        AppPreferences.setGuestMode(context, false)
                        navController.navigate(RelaxMindRoutes.DASHBOARD) {
                            popUpTo(RelaxMindRoutes.AUTH_WELCOME) { inclusive = true }
                        }
                    }
                )
            }
            composable(RelaxMindRoutes.LOGIN_FORM) {
                val scope = androidx.compose.runtime.rememberCoroutineScope()
                LoginViewScreen(
                    onLoginClick = { email, password ->
                        scope.launch {
                            val user = AuthManager.loginUser(context, email, password)
                            if (user != null) {
                                AppPreferences.setGuestMode(context, false)
                                val dest = if (user.role == "CAREGIVER") RelaxMindRoutes.CAREGIVER_DASHBOARD else RelaxMindRoutes.DASHBOARD
                                navController.navigate(dest) {
                                    popUpTo(RelaxMindRoutes.AUTH_WELCOME) { inclusive = true }
                                }
                            } else {
                                Toast.makeText(context, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    onBiometricClick = {
                        val activity = context as? androidx.fragment.app.FragmentActivity
                        if (activity != null) {
                            if (!AuthManager.isBiometricAvailable(context)) {
                                Toast.makeText(context, "El inicio biométrico no está habilitado para esta cuenta. Inicia sesión con contraseña primero.", Toast.LENGTH_LONG).show()
                                return@LoginViewScreen
                            }

                            com.upn.relaxmind.core.utils.BiometricHelper.authenticate(
                                activity = activity,
                                title = "Inicio Biométrico",
                                subtitle = "Usa tu huella para entrar a RelaxMind",
                                onSuccess = {
                                    scope.launch {
                                        val user = AuthManager.loginWithBiometrics(context)
                                        if (user != null) {
                                            AppPreferences.setGuestMode(context, false)
                                            val dest = if (user.role == "CAREGIVER") RelaxMindRoutes.CAREGIVER_DASHBOARD else RelaxMindRoutes.DASHBOARD
                                            navController.navigate(dest) {
                                                popUpTo(RelaxMindRoutes.AUTH_WELCOME) { inclusive = true }
                                            }
                                        }
                                    }
                                },
                                onError = { error ->
                                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                                }
                            )
                        } else {
                            Toast.makeText(context, "Actividad no compatible con biometría", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onForgotPasswordClick = { navController.navigate(RelaxMindRoutes.FORGOT_PASSWORD) },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(RelaxMindRoutes.FORGOT_PASSWORD) {
                ForgotPasswordScreen(
                    onPasswordReset = {
                        Toast.makeText(context, "Contraseña actualizada exitosamente", Toast.LENGTH_SHORT).show()
                        navController.navigate(RelaxMindRoutes.LOGIN_FORM) {
                            popUpTo(RelaxMindRoutes.AUTH_WELCOME) { inclusive = false }
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(RelaxMindRoutes.SIGN_UP) {
                val scope = androidx.compose.runtime.rememberCoroutineScope()
                SignUpScreen(
                    onCreateAccount = { name, phone, email, password ->
                        scope.launch {
                            val success = AuthManager.registerUser(context, name, email, password, "PATIENT", phone)
                            if (success) {
                                AuthManager.loginUser(context, email, password)
                                AppPreferences.setGuestMode(context, false)
                                AppPreferences.setJustRegistered(context, true)
                                navController.navigate(RelaxMindRoutes.VERIFY_EMAIL)
                            } else {
                                Toast.makeText(context, "El correo ya está registrado", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    onBackToLogin = { navController.popBackStack() }
                )
            }
            composable(RelaxMindRoutes.VERIFY_EMAIL) {
                VerifyEmailScreen(
                    onVerify = {
                        val user = AuthManager.getCurrentUser(context)
                        val dest = if (user?.role == "CAREGIVER") RelaxMindRoutes.CAREGIVER_DASHBOARD else RelaxMindRoutes.DIAGNOSTIC_TEST
                        navController.navigate(dest) {
                            popUpTo(RelaxMindRoutes.AUTH_WELCOME) { inclusive = false }
                        }
                    },
                    onResendCode = {
                        Toast.makeText(context, "Codigo reenviado", Toast.LENGTH_SHORT).show()
                    }
                )
            }
            composable(RelaxMindRoutes.ROLE_SELECTION) {
                RoleSelectionScreen(
                    onContinue = { role ->
                        AppPreferences.saveSelectedRole(context, role.value)
                        if (role == UserRole.PATIENT) {
                            navController.navigate(RelaxMindRoutes.SIGN_UP)
                        } else {
                            navController.navigate(RelaxMindRoutes.CAREGIVER_REGISTRATION)
                        }
                    }
                )
            }
            composable(RelaxMindRoutes.DIAGNOSTIC_TEST) {
                val goDashboardFromTest = {
                    navController.navigate(RelaxMindRoutes.DASHBOARD) {
                        popUpTo(RelaxMindRoutes.ROLE_SELECTION) { inclusive = true }
                        launchSingleTop = true
                    }
                }
                DiagnosticTestScreen(
                    onTestCompleted = { score ->
                        AppPreferences.saveWellnessScore(context, score)
                        goDashboardFromTest()
                    },
                    onExitToRoleSelection = { navController.popBackStack() },
                    onSkipToDashboard = {
                        AppPreferences.saveWellnessScore(context, 55)
                        goDashboardFromTest()
                    }
                )
            }
            composable(RelaxMindRoutes.REGISTRATION_SUCCESS) {
                RegistrationSuccessScreen()
            }
            composable(
                route = "${RelaxMindRoutes.DASHBOARD}?tab={tab}",
                arguments = listOf(
                    androidx.navigation.navArgument("tab") { 
                        type = androidx.navigation.NavType.IntType
                        defaultValue = 0 
                    }
                )
            ) { backStackEntry ->
                val initialTab = backStackEntry.arguments?.getInt("tab") ?: 0
                DashboardScreen(
                    initialTabIndex = initialTab,
                    onOpenCheckIn = { navController.navigate(RelaxMindRoutes.CHECK_IN) },
                    onOpenCrisis = { navController.navigate(RelaxMindRoutes.CRISIS) },
                    onOpenChatbot = { navController.navigate(RelaxMindRoutes.AI_CHAT) },
                    onOpenDiary = { navController.navigate(RelaxMindRoutes.DIARY) },
                    onOpenLibrary = { navController.navigate(RelaxMindRoutes.LIBRARY) },
                    onOpenProfile = { navController.navigate(RelaxMindRoutes.PROFILE_VIEW) },
                    onOpenEditProfile = { navController.navigate(RelaxMindRoutes.EDIT_PROFILE) },
                    onOpenEmergencyQr = { navController.navigate(RelaxMindRoutes.EMERGENCY_QR) },
                    onOpenServicesMap = { navController.navigate(RelaxMindRoutes.SERVICES_MAP) },
                    onOpenSounds = { navController.navigate(RelaxMindRoutes.SOUNDS_RELAXING) },
                    onOpenAbout = { navController.navigate(RelaxMindRoutes.ABOUT) },
                    onOpenTerms = { navController.navigate(RelaxMindRoutes.TERMS) },
                    onOpenRewards = { navController.navigate(RelaxMindRoutes.REWARDS) },
                    onToggleDarkTheme = onToggleDarkTheme,
                    onLogout = {
                        scope.launch {
                            AuthManager.logout(context)
                            navController.navigate(RelaxMindRoutes.AUTH_WELCOME) {
                                popUpTo(RelaxMindRoutes.DASHBOARD) { inclusive = true }
                            }
                        }
                    }
                )
            }
            composable(RelaxMindRoutes.CHECK_IN) {
                CheckInScreen(
                    onBack = { navController.popBackStack() },
                    onNavigateToHistory = { 
                        navController.navigate("${RelaxMindRoutes.DASHBOARD}?tab=2") {
                            popUpTo(RelaxMindRoutes.DASHBOARD) { inclusive = true }
                        }
                    }
                )
            }
            composable(RelaxMindRoutes.CRISIS) {
                CrisisScreen(
                    contactsNotified = 1,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(RelaxMindRoutes.DIARY) {
                DiaryScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable(RelaxMindRoutes.LIBRARY) {
                LibraryScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable(RelaxMindRoutes.PROFILE_VIEW) {
                ProfileViewScreen(
                    onBack = { navController.popBackStack() },
                    onEditProfile = { navController.navigate(RelaxMindRoutes.EDIT_PROFILE) },
                    onOpenSettings = { navController.navigate(RelaxMindRoutes.PROFILE) },
                    onLogout = {
                        scope.launch {
                            com.upn.relaxmind.core.data.auth.AuthManager.logout(context)
                            navController.navigate(RelaxMindRoutes.AUTH_WELCOME) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }
                )
            }
            composable(RelaxMindRoutes.EDIT_PROFILE) {
                EditProfileScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable(RelaxMindRoutes.ABOUT) {
                AboutScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable(RelaxMindRoutes.TERMS) {
                TermsScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable(RelaxMindRoutes.AI_CHAT) {
                AiChatScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable(RelaxMindRoutes.PROFILE) {
                ProfileScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable(RelaxMindRoutes.EMERGENCY_QR) {
                EmergencyQrScreen(
                    onBack = { navController.popBackStack() },
                    onRemoteLink = { navController.navigate(RelaxMindRoutes.REMOTE_LINKING_CODE) }
                )
            }
            composable(RelaxMindRoutes.SERVICES_MAP) {
                ServicesMapScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable(RelaxMindRoutes.SOUNDS_RELAXING) {
                SoundsScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable(RelaxMindRoutes.REWARDS) {
                RewardsScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            
            // ── Caregiver Experience ──────────────────────────────────────────
            composable(RelaxMindRoutes.CAREGIVER_REGISTRATION) {
                CaregiverRegistrationScreen(
                    onBack = { navController.popBackStack() },
                    onSuccess = {
                        navController.navigate(RelaxMindRoutes.CAREGIVER_DASHBOARD) {
                            popUpTo(RelaxMindRoutes.AUTH_WELCOME) { inclusive = true }
                        }
                    }
                )
            }
            composable(RelaxMindRoutes.CAREGIVER_DASHBOARD) {
                CaregiverDashboardScreen(
                    onPatientClick = { id -> navController.navigate("${RelaxMindRoutes.CAREGIVER_PATIENT_DETAIL}/$id") },
                    onOpenLink = { navController.navigate(RelaxMindRoutes.CAREGIVER_LINKING) },
                    onOpenSettings = { navController.navigate(RelaxMindRoutes.CAREGIVER_SETTINGS) },
                    onOpenNotifications = { navController.navigate(RelaxMindRoutes.CAREGIVER_NOTIFICATIONS) },
                    onLogout = {
                        scope.launch {
                            AuthManager.logout(context)
                            navController.navigate(RelaxMindRoutes.AUTH_WELCOME) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }
                )
            }
            composable(RelaxMindRoutes.CAREGIVER_LINKING) {
                CaregiverLinkingScreen(
                    onBack = { navController.popBackStack() },
                    onSuccess = { navController.popBackStack() },
                    onOpenScanner = { navController.navigate(RelaxMindRoutes.CAREGIVER_QR_SCANNER) }
                )
            }
            composable(
                route = "${RelaxMindRoutes.CAREGIVER_PATIENT_DETAIL}/{patientId}",
                arguments = listOf(
                    androidx.navigation.navArgument("patientId") { type = androidx.navigation.NavType.StringType }
                )
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("patientId") ?: ""
                CaregiverPatientDetailScreen(
                    patientId = id,
                    onBack = { navController.popBackStack() },
                    onViewHistory = { /* History detail CAR-05 */ }
                )
            }
            composable(RelaxMindRoutes.CAREGIVER_NOTIFICATIONS) {
                CaregiverNotificationsScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable(RelaxMindRoutes.CAREGIVER_SETTINGS) {
                CaregiverSettingsScreen(
                    onBack = { navController.popBackStack() },
                    onEditProfile = { navController.navigate(RelaxMindRoutes.CAREGIVER_EDIT_PROFILE) },
                    onManageLinks = { navController.navigate(RelaxMindRoutes.CAREGIVER_MANAGE_LINKS) },
                    onLogout = {
                        scope.launch {
                            AuthManager.logout(context)
                            navController.navigate(RelaxMindRoutes.AUTH_WELCOME) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }
                )
            }
            composable(RelaxMindRoutes.CAREGIVER_QR_SCANNER) {
                CaregiverQrScannerScreen(
                    onBack = { navController.popBackStack() },
                    onLinked = {
                        navController.navigate(RelaxMindRoutes.CAREGIVER_DASHBOARD) {
                            popUpTo(RelaxMindRoutes.CAREGIVER_DASHBOARD) { inclusive = true }
                        }
                    }
                )
            }
            composable(RelaxMindRoutes.REMOTE_LINKING_CODE) {
                RemoteLinkingCodeScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable(RelaxMindRoutes.CAREGIVER_EDIT_PROFILE) {
                CaregiverEditProfileScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable(RelaxMindRoutes.CAREGIVER_MANAGE_LINKS) {
                CaregiverManageLinksScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
