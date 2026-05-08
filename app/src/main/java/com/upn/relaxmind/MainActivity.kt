package com.upn.relaxmind

import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import com.upn.relaxmind.core.data.preferences.AppPreferences
import com.upn.relaxmind.core.ui.navigation.RelaxMindNavGraph
import com.upn.relaxmind.core.ui.theme.RelaxMindTheme

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle     = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        )
        setContent {
            var isDarkTheme by remember {
                mutableStateOf(AppPreferences.isDarkMode(this))
            }

            RelaxMindTheme(darkTheme = isDarkTheme) {
                RelaxMindNavGraph(
                    onToggleDarkTheme = { enabled ->
                        isDarkTheme = enabled
                        AppPreferences.setDarkMode(this, enabled)
                    }
                )
            }
        }
    }
}