package com.kwota.app.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kwota.app.R
import com.kwota.app.ui.theme.KwotaTheme

// Ana ekran iskeleti: oturum tüketimi (birincil) + bugün toplam (ikincil) + mobil veri durumu.
// Gerçek değerler ileride HomeViewModel'den akacak (FR-9).
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
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
            text = "— MB",
            style = MaterialTheme.typography.displaySmall,
        )
        Text(
            text = stringResource(R.string.home_daily_label) + ": — MB",
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            text = stringResource(R.string.home_mobile_off),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    KwotaTheme { HomeScreen() }
}
