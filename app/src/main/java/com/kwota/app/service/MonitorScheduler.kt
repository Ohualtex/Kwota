package com.kwota.app.service

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

// İzleme tetikleyicilerini WorkManager'a kurar (DESIGN §4):
// (A) periyodik omurga (METERED kısıtı ≈ hücresel), (B) bağlantı olayında expedited tek-seferlik.
object MonitorScheduler {

    fun schedulePeriodic(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.METERED) // wi-fi'de çalışmaz
            .build()
        val request = PeriodicWorkRequestBuilder<UsageCheckWorker>(PERIOD_MINUTES, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            UsageCheckWorker.UNIQUE_PERIODIC,
            ExistingPeriodicWorkPolicy.KEEP,
            request,
        )
    }

    fun enqueueExpedited(context: Context) {
        val request = OneTimeWorkRequestBuilder<UsageCheckWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()
        WorkManager.getInstance(context).enqueue(request)
    }

    private const val PERIOD_MINUTES = 20L
}
