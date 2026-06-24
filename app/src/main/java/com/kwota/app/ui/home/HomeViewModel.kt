package com.kwota.app.ui.home

import android.app.Application
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kwota.app.data.SettingsRepository
import com.kwota.app.data.UsageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId

// Ana ekran durumu. Şimdilik refresh() ile çekilir; ileride canlı akışa çevrilebilir.
data class HomeUiState(
    val sessionBytes: Long = 0L,
    val dailyBytes: Long = 0L,
    val mobileDataOn: Boolean = false,
)

class HomeViewModel(app: Application) : AndroidViewModel(app) {

    private val settings = SettingsRepository(app)
    private val usage = UsageRepository(app)
    private val cm = app.getSystemService(ConnectivityManager::class.java)

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    // Oturum tüketimi + günlük toplam + mobil veri durumunu okuyup durumu günceller (FR-9).
    fun refresh() {
        viewModelScope.launch(Dispatchers.IO) {
            val now = System.currentTimeMillis()
            val sessionStart = settings.sessionStartMillis.first()
            val sessionBytes = if (sessionStart > 0L) safeUsage(sessionStart, now) else 0L
            val dailyBytes = safeUsage(startOfTodayMillis(), now)
            _state.value = HomeUiState(sessionBytes, dailyBytes, isMobileActive())
        }
    }

    // İzin yoksa / sorgu başarısızsa çökme yerine 0 döner.
    private fun safeUsage(start: Long, end: Long): Long =
        try {
            usage.cellularUsageBetween(start, end)
        } catch (_: Exception) {
            0L
        }

    private fun isMobileActive(): Boolean {
        val active = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(active) ?: return false
        return caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
    }

    private fun startOfTodayMillis(): Long {
        val zone = ZoneId.systemDefault()
        return LocalDate.now(zone).atStartOfDay(zone).toInstant().toEpochMilli()
    }
}
