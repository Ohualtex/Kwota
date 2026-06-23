package com.kwota.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kwota.app.ui.home.HomeScreen
import com.kwota.app.ui.theme.KwotaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KwotaAppRoot()
        }
    }
}

// Uygulamanın kök bileşeni. İskelet: şimdilik doğrudan Home ekranı.
// İleride onboarding + izin akışı + ekranlar arası gezinme (navigation) buraya gelir.
@Composable
private fun KwotaAppRoot() {
    KwotaTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            HomeScreen(modifier = Modifier.padding(innerPadding))
        }
    }
}
