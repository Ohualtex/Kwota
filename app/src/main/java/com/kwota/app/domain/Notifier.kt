package com.kwota.app.domain

import android.content.Context

// Geçici uyarı bildirimini ve "hâlâ mobil verideysin" hatırlatmasını üretir (FR-5, FR-8).
// Yapışkan / iptal edilemez bildirim ASLA kullanılmaz — ürünün ana farkı budur.
class Notifier(private val context: Context) {

    // Eşik geçildiğinde geçici (setTimeoutAfter ile kendiliğinden kaybolan) bildirim.
    fun notifyThresholdCrossed(reachedBytes: Long) {
        // TODO: NotificationCompat + geçici bildirim kanalı; setTimeoutAfter ile otomatik kaybolma.
    }

    // Hücresel aktif + eşik aşılmış durumda, ayarlanabilir sıklıkta hafif hatırlatma.
    fun notifyStillOnMobile() {
        // TODO: stillOnReminderLevel / rawMinutes ile sıklık denetimi (cooldown).
    }
}
