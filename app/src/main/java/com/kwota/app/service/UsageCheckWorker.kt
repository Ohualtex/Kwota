package com.kwota.app.service

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.kwota.app.data.SettingsRepository
import com.kwota.app.data.UsageRepository
import com.kwota.app.domain.Notifier
import com.kwota.app.domain.UsageMonitor
import kotlinx.coroutines.flow.first

// WorkManager işi: periyodik (~20 dk, METERED) + bağlantı tetikli expedited.
// Oturum tüketimini okur, UsageMonitor'ı çalıştırır, yeni eşik geçildiyse Notifier'ı çağırır.
// Kalıcı / foreground servis bildirimi YOK.
class UsageCheckWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {

    private val settings = SettingsRepository(appContext)
    private val usage = UsageRepository(appContext)
    private val notifier = Notifier(appContext)

    override suspend fun doWork(): Result {
        val sessionStart = settings.sessionStartMillis.first()
        if (sessionStart <= 0L) return Result.success() // aktif oturum yok

        val stepBytes = settings.stepSizeMb.first().toLong() * BYTES_PER_MB
        val lastStep = settings.lastNotifiedStep.first()
        val usageBytes = usage.cellularUsageBetween(sessionStart, System.currentTimeMillis())

        val crossedStep = UsageMonitor.nextThresholdStep(usageBytes, stepBytes, lastStep)
        if (crossedStep != null) {
            notifier.notifyThresholdCrossed(crossedStep * stepBytes)
            settings.setLastNotifiedStep(crossedStep)
        }
        return Result.success()
    }

    companion object {
        const val UNIQUE_PERIODIC = "kwota_periodic_usage_check"
        private const val BYTES_PER_MB = 1024L * 1024L
    }
}
