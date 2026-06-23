package com.kwota.app.service

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest

// Aktif ağın hücresel mi olduğunu NetworkCallback ile dinler (FR-7) ve oturum sınırını tanımlar:
// hücresel aktif → oturum başlar + expedited kontrol tetiklenir; wi-fi/kapanış → oturum biter.
class ConnectivityObserver(context: Context) {

    private val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    fun start() {
        val request = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()
        cm.registerNetworkCallback(request, callback)
    }

    fun stop() {
        cm.unregisterNetworkCallback(callback)
    }

    private val callback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            // TODO: SettingsRepository.startSession(now) + expedited UsageCheckWorker tetikle.
        }

        override fun onLost(network: Network) {
            // TODO: SettingsRepository.endSession() (oturum sayacı sıfırlanır).
        }
    }
}
