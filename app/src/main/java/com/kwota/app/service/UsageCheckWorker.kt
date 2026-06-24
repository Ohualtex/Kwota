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
// Oturum tüketimini okur, eşik geçişinde Notifier'ı çağırır ve FR-8 hatırlatmasını yönetir.
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

        val now = System.currentTimeMillis()
        val stepBytes = settings.stepSizeMb.first().toLong() * BYTES_PER_MB
        val usageBytes = usage.cellularUsageBetween(sessionStart, now)

        // Eşik geçişi → geçici bildirim (FR-4/5).
        val lastStep = settings.lastNotifiedStep.first()
        val crossedStep = UsageMonitor.nextThresholdStep(usageBytes, stepBytes, lastStep)
        if (crossedStep != null) {
            notifier.notifyThresholdCrossed(crossedStep * stepBytes)
            settings.setLastNotifiedStep(crossedStep)
        }

        // FR-8: "hâlâ mobil verideysin" — ayarlanabilir, cooldown'lı.
        maybeRemindStillOn(usageBytes, stepBytes, now)

        return Result.success()
    }

    // Etkin aralık kapalı değilse, anlamlı tüketim (>= 1 adım) varsa ve cooldown dolduysa hatırlat.
    private suspend fun maybeRemindStillOn(usageBytes: Long, stepBytes: Long, now: Long) {
        val intervalMin = settings.effectiveReminderMinutes.first() ?: return // kapalı
        if (usageBytes < stepBytes) return // henüz anlamlı tüketim yok
        val last = settings.lastStillOnReminderMillis.first()
        if (now - last >= intervalMin.toLong() * MILLIS_PER_MINUTE) {
            notifier.notifyStillOnMobile()
            settings.setLastStillOnReminderMillis(now)
        }
    }

    companion object {
        const val UNIQUE_PERIODIC = "kwota_periodic_usage_check"
        private const val BYTES_PER_MB = 1024L * 1024L
        private const val MILLIS_PER_MINUTE = 60_000L
    }
}
