package com.kwota.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kwota.app.R
import com.kwota.app.ui.theme.KwotaTheme

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onSettingsClick: () -> Unit = {},
    viewModel: HomeViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsState()
    // Ekran her göründüğünde güncel tüketimi çek.
    LaunchedEffect(Unit) { viewModel.refresh() }
    HomeContent(state, onSettingsClick, modifier)
}

@Composable
private fun HomeContent(
    state: HomeUiState,
    onSettingsClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 28.dp, vertical = 16.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = stringResource(R.string.settings_title),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Spacer(Modifier.weight(1f))

        Text(
            text = stringResource(R.string.home_session_label),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        val (value, unit) = formatBytesParts(state.sessionBytes)
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = value,
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = " $unit",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 10.dp),
            )
        }

        Spacer(Modifier.height(10.dp))
        Text(
            text = stringResource(R.string.home_daily_label) + ": " + formatBytes(state.dailyBytes),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(Modifier.height(22.dp))
        StatusPill(mobileOn = state.mobileDataOn)

        Spacer(Modifier.weight(1.4f))
        Text(
            text = "Wi-fi'ye geçince sıfırlanır",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline,
        )
    }
}

// Mobil veri durumunu renkli, yumuşak bir hap (pill) olarak gösterir.
@Composable
private fun StatusPill(mobileOn: Boolean) {
    val bg = if (mobileOn) Color(0xFFFAEEDA) else Color(0xFFE1F5EE)
    val fg = if (mobileOn) Color(0xFF854F0B) else Color(0xFF0F6E56)
    val dot = if (mobileOn) Color(0xFFBA7517) else Color(0xFF1D9E75)
    Surface(color = bg, contentColor = fg, shape = RoundedCornerShape(50)) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(Modifier.size(9.dp).background(dot, CircleShape))
            Text(
                text = stringResource(if (mobileOn) R.string.home_mobile_on else R.string.home_mobile_off),
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}

private fun formatBytes(bytes: Long): String {
    val (value, unit) = formatBytesParts(bytes)
    return "$value $unit"
}

// Byte → (sayı, birim) ayrı; kahraman sayıda birimi küçük göstermek için.
private fun formatBytesParts(bytes: Long): Pair<String, String> {
    val mb = bytes / (1024.0 * 1024.0)
    return if (mb >= 1024) "%.1f".format(mb / 1024) to "GB" else "%.0f".format(mb) to "MB"
}

@Preview(showBackground = true)
@Composable
private fun HomeContentPreview() {
    KwotaTheme {
        HomeContent(
            HomeUiState(
                sessionBytes = 340L * 1024 * 1024,
                dailyBytes = 1200L * 1024 * 1024,
                mobileDataOn = true,
            ),
        )
    }
}
