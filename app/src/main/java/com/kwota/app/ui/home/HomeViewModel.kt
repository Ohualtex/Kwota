package com.kwota.app.ui.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Ana ekran durumu (iskelet). İleride UsageRepository + SettingsRepository bağlanır.
data class HomeUiState(
    val sessionBytes: Long = 0L,
    val dailyBytes: Long = 0L,
    val mobileDataOn: Boolean = false,
)

class HomeViewModel : ViewModel() {
    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()
}
