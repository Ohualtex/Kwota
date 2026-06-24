package com.kwota.app

import android.app.Application
import com.kwota.app.domain.Notifier
import com.kwota.app.service.ConnectivityObserver
import com.kwota.app.service.MonitorScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

// Uygulama giriş noktası: bildirim kanalı, periyodik izleme ve bağlantı gözlemcisi burada bağlanır.
class KwotaApp : Application() {

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var connectivityObserver: ConnectivityObserver

    override fun onCreate() {
        super.onCreate()
        Notifier(this).createChannels()
        MonitorScheduler.schedulePeriodic(this)
        connectivityObserver = ConnectivityObserver(this, appScope).also { it.start() }
    }
}
