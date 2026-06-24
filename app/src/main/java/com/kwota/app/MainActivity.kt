package com.kwota.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.kwota.app.service.UsageAccess
import com.kwota.app.ui.home.HomeScreen
import com.kwota.app.ui.onboarding.OnboardingScreen
import com.kwota.app.ui.settings.SettingsScreen
import com.kwota.app.ui.theme.KwotaTheme

class MainActivity : ComponentActivity() {

    // İzin durumu Compose tarafından gözlenir; onResume'de tazelenir ki kullanıcı
    // ayarlardan dönünce ekran otomatik güncellensin (FR-11).
    private var hasUsageAccess by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KwotaTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val contentModifier = Modifier.padding(innerPadding)
                    if (!hasUsageAccess) {
                        OnboardingScreen(
                            onGrantClick = { startActivity(UsageAccess.settingsIntent()) },
                            modifier = contentModifier,
                        )
                    } else {
                        // Basit gezinme: Home ↔ Settings (ileride Navigation Compose'a taşınabilir).
                        var showSettings by rememberSaveable { mutableStateOf(false) }
                        if (showSettings) {
                            SettingsScreen(onBack = { showSettings = false }, modifier = contentModifier)
                        } else {
                            HomeScreen(onSettingsClick = { showSettings = true }, modifier = contentModifier)
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        hasUsageAccess = UsageAccess.isGranted(this)
    }
}
