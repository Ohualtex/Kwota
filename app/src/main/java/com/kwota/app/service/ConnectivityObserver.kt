package com.kwota.app.service

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import com.kwota.app.data.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

// Varsayılan (aktif) ağın hücresel olup olmadığını dinler (FR-7) ve oturum sınırını tanımlar:
// aktif ağ hücresel → oturum başlar + expedited kontrol; wi-fi'ye geçiş/kopuş → oturum biter.
class ConnectivityObserver(
    private val context: Context,
    private val scope: CoroutineScope,
) {
    private val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val settings = SettingsRepository(context)

    // Gereksiz tekrarları önlemek için son bilinen durum.
    private var onCellular: Boolean? = null

    private val callback = object : ConnectivityManager.NetworkCallback() {
        override fun onCapabilitiesChanged(network: Network, caps: NetworkCapabilities) {
            updateState(caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
        }

        override fun onLost(network: Network) {
            updateState(false)
        }
    }

    private fun updateState(cellular: Boolean) {
        if (onCellular == cellular) return
        onCellular = cellular
        scope.launch {
            if (cellular) {
                settings.startSession(System.currentTimeMillis())
                MonitorScheduler.enqueueExpedited(context)
            } else {
                settings.endSession()
            }
        }
    }

    fun start() {
        cm.registerDefaultNetworkCallback(callback)
    }

    fun stop() {
        cm.unregisterNetworkCallback(callback)
    }
}
