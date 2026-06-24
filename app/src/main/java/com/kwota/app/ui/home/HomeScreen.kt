package com.kwota.app.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kwota.app.R
import com.kwota.app.ui.theme.KwotaTheme

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsState()
    // Ekran her göründüğünde güncel tüketimi çek.
    LaunchedEffect(Unit) { viewModel.refresh() }
    HomeContent(state, modifier)
}

@Composable
private fun HomeContent(state: HomeUiState, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(R.string.home_session_label),
            style = MaterialTheme.typography.labelLarge,
        )
        Text(
            text = formatBytes(state.sessionBytes),
            style = MaterialTheme.typography.displaySmall,
        )
        Text(
            text = stringResource(R.string.home_daily_label) + ": " + formatBytes(state.dailyBytes),
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            text = stringResource(
                if (state.mobileDataOn) R.string.home_mobile_on else R.string.home_mobile_off,
            ),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

// Byte → sade "X MB" / "X.X GB" biçimi.
private fun formatBytes(bytes: Long): String {
    val mb = bytes / (1024.0 * 1024.0)
    return if (mb >= 1024) "%.1f GB".format(mb / 1024) else "%.0f MB".format(mb)
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
