package com.kwota.app.domain

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.kwota.app.R

// Geçici uyarı bildirimini ve "hâlâ mobil verideysin" hatırlatmasını üretir (FR-5, FR-8).
// Yapışkan / iptal edilemez bildirim ASLA kullanılmaz — ürünün ana farkı budur.
class Notifier(private val context: Context) {

    // Bildirim kanalını oluşturur (uygulama açılışında bir kez çağrılır).
    fun createChannels() {
        val channel = NotificationChannel(
            CHANNEL_ALERTS,
            "Uyarılar",
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = "Mobil veri kullanım uyarıları"
        }
        context.getSystemService(NotificationManager::class.java)
            .createNotificationChannel(channel)
    }

    // Eşik geçildiğinde geçici (setTimeoutAfter ile kendiliğinden kaybolan) bildirim.
    fun notifyThresholdCrossed(reachedBytes: Long) {
        val mb = reachedBytes / BYTES_PER_MB
        post(ID_THRESHOLD, "Bu arada, $mb MB oldu", "Bu oturumda mobil veri kullanımın $mb MB'ı geçti.")
    }

    // Hücresel aktif + eşik aşılmış durumda hafif, dostça hatırlatma.
    fun notifyStillOnMobile() {
        post(ID_STILL_ON, "Hâlâ mobil verideysin", "Mobil veri açık ve kullanılıyor — yakında wi-fi var mı?")
    }

    private fun post(id: Int, title: String, text: String) {
        if (!hasNotificationPermission()) return
        val notification = NotificationCompat.Builder(context, CHANNEL_ALERTS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(text)
            .setAutoCancel(true)
            .setTimeoutAfter(TRANSIENT_MS) // geçici: belirli süre sonra kaybolur (FR-5)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        try {
            NotificationManagerCompat.from(context).notify(id, notification)
        } catch (_: SecurityException) {
            // Bildirim izni yoksa sessizce atla.
        }
    }

    private fun hasNotificationPermission(): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

    companion object {
        const val CHANNEL_ALERTS = "kwota_alerts"
        private const val ID_THRESHOLD = 1001
        private const val ID_STILL_ON = 1002
        private const val TRANSIENT_MS = 2 * 60 * 1000L // ~2 dakika sonra kaybolur
        private const val BYTES_PER_MB = 1024L * 1024L
    }
}
