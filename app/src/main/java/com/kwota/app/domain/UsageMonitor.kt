package com.kwota.app.domain

// Eşik (threshold) hesaplama mantığının kalbi (DESIGN §5).
// Oturum bazlı: tüketim oturum başından beri ölçülür; her "adım" geçişinde tek bildirim.
object UsageMonitor {

    // Saf hesaplama — yan etkisiz, kolayca test edilir.
    // Oturum tüketimi + adım boyutu + son uyarılan adım verilince, yeni bir eşik geçildiyse
    // ulaşılan adım indeksini döndürür; yoksa null.
    //
    // Bildirim yağmuru önleme (NFR-1): iki okuma arasında birden çok adım geçildiyse, her
    // adım için ayrı değil; en güncel adım (currentStep) üzerinden tek bir sonuç döner.
    fun nextThresholdStep(
        usageBytesThisSession: Long,
        stepSizeBytes: Long,
        lastNotifiedStep: Int,
    ): Int? {
        require(stepSizeBytes > 0) { "stepSizeBytes pozitif olmalı" }
        if (usageBytesThisSession < 0) return null
        val currentStep = (usageBytesThisSession / stepSizeBytes).toInt()
        return if (currentStep > lastNotifiedStep) currentStep else null
    }
}
