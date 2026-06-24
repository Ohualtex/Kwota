package com.kwota.app.service

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.os.Process
import android.provider.Settings

// "Kullanım erişimi" (PACKAGE_USAGE_STATS) izni özeldir; pop-up yoktur, kullanıcı
// sistem ayarlarından açar (C-4). Durumu AppOpsManager ile kontrol edilir.
object UsageAccess {

    @Suppress("DEPRECATION") // unsafeCheckOpNoThrow yeni SDK'da deprecated işaretli; kullanım erişimi kontrolü için standart yol.
    fun isGranted(context: Context): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.unsafeCheckOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            context.packageName,
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    // Kullanıcıyı sistemin "kullanım erişimi" ayar ekranına götüren intent (FR-10).
    fun settingsIntent(): Intent =
        Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
}
