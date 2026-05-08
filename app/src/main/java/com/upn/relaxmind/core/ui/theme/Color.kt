package com.upn.relaxmind.core.ui.theme

import androidx.compose.ui.graphics.Color

// ── Common ────────────────────────────────────────────────────────────────────
val StreakOrange = Color(0xFFFF8A5C)
val SosRed       = Color(0xFFEF4444)
val SosCoral     = Color(0xFFFF6B6B)

// ── Light palette ─────────────────────────────────────────────────────────────
val RelaxBackground    = Color(0xFFF8FAFC)
val RelaxGreen         = Color(0xFF1C8A72)
val RelaxPrimary       = RelaxGreen
val RelaxGreenSoft     = Color(0xFF7EBFAF)
val RelaxSurface       = Color(0xFFFFFFFF)
val RelaxOnPrimary     = Color(0xFFFFFFFF)
val RelaxOnBackground  = Color(0xFF1F2933)
val RelaxOnSurface     = Color(0xFF1F2933)
val RelaxOutline       = Color(0xFFB8C7CC)
val RelaxMutedText     = Color(0xFF6E7E86)
val RelaxCardShadow    = Color(0x140F2E2E)

// ── Caregiver palette ─────────────────────────────────────────────────────────
val CaregiverBlue      = Color(0xFF3B82F6)
val CaregiverLightBlue = Color(0xFFEFF6FF)
val CaregiverPurple    = Color(0xFFA855F7)
val CaregiverBg        = Color(0xFFF8FAFC)

// ── Dark palette ──────────────────────────────────────────────────────────────
val RelaxDarkBackground   = Color(0xFF0D1117)
val RelaxDarkSurface      = Color(0xFF161B22)
val RelaxDarkSurfaceVar   = Color(0xFF1C2330)
val RelaxDarkGreen        = Color(0xFF27C69F)
val RelaxDarkGreenSoft    = Color(0xFF4DB8A0)
val RelaxDarkOnBackground = Color(0xFFE6EDF3)
val RelaxDarkOnSurface    = Color(0xFFE6EDF3)
val RelaxDarkOutline      = Color(0xFF30363D)
val RelaxDarkMutedText    = Color(0xFF8B949E)
val RelaxDarkOnPrimary    = Color(0xFF0D1117)
val RelaxDarkCardShadow   = Color(0xFF010409)

// ── Soft Tranquil Palette ──────────────────────────────────────────────────────
val SoftRed_L = Color(0xFFFEF2F2)
val SoftGreen_L = Color(0xFFF0FDF4)
val SoftBlue_L = Color(0xFFF0F9FF)
val SoftOrange_L = Color(0xFFFFF7ED)
val SoftYellow_L = Color(0xFFFEFCE8)
val SoftPurple_L = Color(0xFFF5F3FF)
val SoftAmber_L = Color(0xFFFFFBEB)
val SoftMint_L = Color(0xFFF1F8EE)

val SoftRed_D = Color(0xFF2D1616)
val SoftGreen_D = Color(0xFF14291E)
val SoftBlue_D = Color(0xFF142129)
val SoftOrange_D = Color(0xFF2D1F14)
val SoftYellow_D = Color(0xFF2D2B14)
val SoftPurple_D = Color(0xFF1F142D)
val SoftAmber_D = Color(0xFF2D2614)
val SoftMint_D = Color(0xFF121411)

@androidx.compose.runtime.Composable
fun getSoftRed() = if (LocalIsDarkTheme.current) SoftRed_D else SoftRed_L
@androidx.compose.runtime.Composable
fun getSoftGreen() = if (LocalIsDarkTheme.current) SoftGreen_D else SoftGreen_L
@androidx.compose.runtime.Composable
fun getSoftBlue() = if (LocalIsDarkTheme.current) SoftBlue_D else SoftBlue_L
@androidx.compose.runtime.Composable
fun getSoftOrange() = if (LocalIsDarkTheme.current) SoftOrange_D else SoftOrange_L
@androidx.compose.runtime.Composable
fun getSoftYellow() = if (LocalIsDarkTheme.current) SoftYellow_D else SoftYellow_L
@androidx.compose.runtime.Composable
fun getSoftPurple() = if (LocalIsDarkTheme.current) SoftPurple_D else SoftPurple_L
@androidx.compose.runtime.Composable
fun getSoftAmber() = if (LocalIsDarkTheme.current) SoftAmber_D else SoftAmber_L
@androidx.compose.runtime.Composable
fun getSoftMint() = if (LocalIsDarkTheme.current) SoftMint_D else SoftMint_L
