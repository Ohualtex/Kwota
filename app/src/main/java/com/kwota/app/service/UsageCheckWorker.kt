package com.kwota.app.service

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

// WorkManager işi: periyodik (~20 dk, METERED) omurga + bağlantı tetikli expedited tek-seferlik.
// Çalışınca oturum tüketimini okur, UsageMonitor'ı çalıştırır, gerekiyorsa Notifier'ı çağırır.
// Kalıcı / foreground servis bildirimi YOK.
class UsageCheckWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        // TODO: UsageRepository (oturum tüketimi) + SettingsRepository + UsageMonitor + Notifier bağla.
        return Result.success()
    }

    companion object {
        const val UNIQUE_PERIODIC = "kwota_periodic_usage_check"
    }
}
