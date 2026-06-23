package com.kwota.app.data

import android.app.usage.NetworkStatsManager
import android.content.Context
import android.net.ConnectivityManager

// NetworkStatsManager'ı sarmalar; hücresel tüketimi byte cinsinden sorgular (FR-1, C-5, C-6).
// subscriberId = null ile cihaz toplamı alınır → READ_PHONE_STATE gerekmez.
// Not: Yalnızca "kullanım erişimi" (PACKAGE_USAGE_STATS) izni gerektirir.
class UsageRepository(context: Context) {

    private val statsManager =
        context.getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager

    // Verilen aralıkta (epoch ms) toplam hücresel tüketim (indirme + yükleme).
    @Suppress("DEPRECATION") // ConnectivityManager.TYPE_MOBILE, NetworkStatsManager API'sinde hâlâ bu biçimde isteniyor.
    fun cellularUsageBetween(startMillis: Long, endMillis: Long): Long {
        val bucket = statsManager.querySummaryForDevice(
            ConnectivityManager.TYPE_MOBILE,
            /* subscriberId = */ null,
            startMillis,
            endMillis,
        )
        return bucket.rxBytes + bucket.txBytes
    }
}
