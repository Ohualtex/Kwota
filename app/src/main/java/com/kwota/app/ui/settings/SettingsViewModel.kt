package com.kwota.app.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kwota.app.data.SettingsRepository
import com.kwota.app.domain.ReminderLevel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Ayarları okuyup yazan ViewModel; adım miktarı (FR-2/3) ve FR-8 düzeyi + gelişmiş ham dakika.
class SettingsViewModel(app: Application) : AndroidViewModel(app) {

    private val settings = SettingsRepository(app)

    val stepSizeMb: StateFlow<Int> = settings.stepSizeMb
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SettingsRepository.DEFAULT_STEP_MB)

    val reminderLevel: StateFlow<ReminderLevel> = settings.reminderLevel
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ReminderLevel.MID)

    val reminderRawMinutes: StateFlow<Int> = settings.reminderRawMinutes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    fun setStepSize(mb: Int) = viewModelScope.launch { settings.setStepSizeMb(mb) }

    fun setReminderLevel(level: ReminderLevel) =
        viewModelScope.launch { settings.setReminderLevel(level) }

    fun setReminderRawMinutes(minutes: Int) =
        viewModelScope.launch { settings.setReminderRawMinutes(minutes) }
}
