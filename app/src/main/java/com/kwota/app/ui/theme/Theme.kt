package com.kwota.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary = Green40,
    secondary = Green80,
    background = Grey95,
)

private val DarkColors = darkColorScheme(
    primary = Green80,
    secondary = Green40,
    background = Grey20,
)

@Composable
fun KwotaTheme(
    // Aydınlık-öncelikli: sistem koyu olsa da uygulama açık görünür ("aydınlık & ferah").
    darkTheme: Boolean = false,
    // Material You dinamik renk (Android 12+); kullanıcı tercihi.
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }

    // Durum çubuğu ikonları temaya göre — açık temada koyu ikonlar görünür kalsın.
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = KwotaTypography,
        content = content,
    )
}
