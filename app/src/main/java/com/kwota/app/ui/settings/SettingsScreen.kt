package com.kwota.app.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kwota.app.R
import com.kwota.app.domain.ReminderLevel
import com.kwota.app.ui.theme.KwotaTheme

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = viewModel(),
) {
    val stepSize by viewModel.stepSizeMb.collectAsState()
    val level by viewModel.reminderLevel.collectAsState()
    SettingsContent(
        stepSizeMb = stepSize,
        reminderLevel = level,
        onStepSize = viewModel::setStepSize,
        onReminderLevel = viewModel::setReminderLevel,
        onBack = onBack,
        modifier = modifier,
    )
}

@Composable
private fun SettingsContent(
    stepSizeMb: Int,
    reminderLevel: ReminderLevel,
    onStepSize: (Int) -> Unit,
    onReminderLevel: (ReminderLevel) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        TextButton(onClick = onBack) { Text("← Geri") }

        Text(stringResource(R.string.settings_title), style = MaterialTheme.typography.headlineSmall)

        // Adım miktarı (FR-2/FR-3). Alt sınır SettingsRepository'de zorlanır.
        Text(stringResource(R.string.settings_step_size), style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(100, 200, 500).forEach { mb ->
                FilterChip(
                    selected = stepSizeMb == mb,
                    onClick = { onStepSize(mb) },
                    label = { Text("$mb MB") },
                )
            }
        }

        // FR-8 hatırlatma düzeyi (kademeli).
        Text(stringResource(R.string.settings_still_on_reminder), style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ReminderLevel.entries.forEach { lvl ->
                FilterChip(
                    selected = reminderLevel == lvl,
                    onClick = { onReminderLevel(lvl) },
                    label = { Text(lvl.labelTr()) },
                )
            }
        }
    }
}

private fun ReminderLevel.labelTr(): String = when (this) {
    ReminderLevel.OFF -> "Kapalı"
    ReminderLevel.LOW -> "Az"
    ReminderLevel.MID -> "Orta"
    ReminderLevel.HIGH -> "Sık"
}

@Preview(showBackground = true)
@Composable
private fun SettingsContentPreview() {
    KwotaTheme {
        SettingsContent(
            stepSizeMb = 200,
            reminderLevel = ReminderLevel.MID,
            onStepSize = {},
            onReminderLevel = {},
            onBack = {},
        )
    }
}
