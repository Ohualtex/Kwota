package com.kwota.app.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kwota.app.R

// Onboarding iskeleti: ürünü kısaca anlatır ve "kullanım erişimi" iznine yönlendirir (FR-10).
@Composable
fun OnboardingScreen(
    onGrantClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(R.string.onboarding_title),
            style = MaterialTheme.typography.headlineSmall,
        )
        Text(
            text = stringResource(R.string.onboarding_body),
            style = MaterialTheme.typography.bodyMedium,
        )
        Button(onClick = onGrantClick) {
            Text(stringResource(R.string.onboarding_grant))
        }
    }
}
