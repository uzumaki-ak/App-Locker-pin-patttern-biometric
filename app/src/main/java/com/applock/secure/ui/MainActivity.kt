package com.applock.secure.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.applock.secure.data.local.SecurePreferences
import com.applock.secure.ui.navigation.AppNavigation
import com.applock.secure.ui.navigation.Screen
import com.applock.secure.ui.theme.AppLockerTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var securePrefs: SecurePreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        val startDestination = when {
            securePrefs.isFirstTime() -> Screen.Onboarding.route
            securePrefs.getAuthMethod().name == "NONE" -> Screen.SetupAuth.route
            else -> Screen.Home.route
        }

        setContent {
            AppLockerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(startDestination = startDestination)
                }
            }
        }
    }
}