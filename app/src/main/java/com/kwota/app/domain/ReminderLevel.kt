package com.kwota.app.domain

// FR-8 "hâlâ mobil verideysin" hatırlatma sıklığı düzeyi (kademeli).
// approxMinutes = null → kapalı; diğerleri yaklaşık cooldown süresi.
enum class ReminderLevel(val approxMinutes: Int?) {
    OFF(null),
    LOW(360),  // ~6 saat
    MID(180),  // ~3 saat
    HIGH(60),  // ~1 saat
}
