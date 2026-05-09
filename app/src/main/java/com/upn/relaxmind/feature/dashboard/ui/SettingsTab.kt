package com.upn.relaxmind.feature.dashboard.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.upn.relaxmind.feature.profile.ui.SettingsScreen

@Composable
fun SettingsTab(
    modifier: Modifier = Modifier,
    onProfileClick: () -> Unit = {},
    onEditProfileClick: () -> Unit = {},
    onSecurityClick: () -> Unit = {},
    onToggleDarkTheme: (Boolean) -> Unit = {},
    onAboutClick: () -> Unit = {},
    onTermsClick: () -> Unit = {},
    onLogout: () -> Unit
) {
    SettingsScreen(
        modifier = modifier,
        onProfileClick = onProfileClick,
        onEditProfileClick = onEditProfileClick,
        onSecurityClick = onSecurityClick,
        onToggleDarkTheme = onToggleDarkTheme,
        onAboutClick = onAboutClick,
        onTermsClick = onTermsClick,
        onLogoutClick = onLogout
    )
}
