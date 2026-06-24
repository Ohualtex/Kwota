package com.kwota.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.kwota.app.domain.ReminderLevel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Modül düzeyinde tek DataStore örneği.
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "kwota_settings")

// Ayarları ve oturum durumunu DataStore üzerinde okur/yazar (DESIGN §7).
class SettingsRepository(private val context: Context) {

    private object Keys {
        val STEP_SIZE_MB = intPreferencesKey("step_size_mb")
        val MONITORING_ENABLED = booleanPreferencesKey("monitoring_enabled")
        val SESSION_START_MILLIS = longPreferencesKey("session_start_millis")
        val LAST_NOTIFIED_STEP = intPreferencesKey("last_notified_step_this_session")
        val STILL_ON_LEVEL = intPreferencesKey("still_on_reminder_level")
        val STILL_ON_RAW_MIN = intPreferencesKey("still_on_reminder_raw_minutes")
        val STILL_ON_LAST_MILLIS = longPreferencesKey("still_on_last_millis")
    }

    // Adım boyutu; okurken alt sınır (100 MB) zorlanır (FR-3).
    val stepSizeMb: Flow<Int> = context.dataStore.data.map { prefs ->
        (prefs[Keys.STEP_SIZE_MB] ?: DEFAULT_STEP_MB).coerceAtLeast(MIN_STEP_MB)
    }

    suspend fun setStepSizeMb(value: Int) {
        context.dataStore.edit { it[Keys.STEP_SIZE_MB] = value.coerceAtLeast(MIN_STEP_MB) }
    }

    // Oturum başlangıcı (epoch ms); 0 = aktif oturum yok.
    val sessionStartMillis: Flow<Long> =
        context.dataStore.data.map { it[Keys.SESSION_START_MILLIS] ?: 0L }

    // Hücresel oturum başlar — yalnızca aktif oturum yoksa damgalanır; son uyarılan adım sıfırlanır.
    suspend fun startSession(nowMillis: Long) {
        context.dataStore.edit { prefs ->
            if ((prefs[Keys.SESSION_START_MILLIS] ?: 0L) == 0L) {
                prefs[Keys.SESSION_START_MILLIS] = nowMillis
                prefs[Keys.LAST_NOTIFIED_STEP] = 0
            }
        }
    }

    // Oturum biter (wi-fi'ye geçince / veri kapanınca) — sayaç ve FR-8 cooldown'ı temizlenir.
    suspend fun endSession() {
        context.dataStore.edit { prefs ->
            prefs[Keys.SESSION_START_MILLIS] = 0L
            prefs[Keys.LAST_NOTIFIED_STEP] = 0
            prefs[Keys.STILL_ON_LAST_MILLIS] = 0L
        }
    }

    val lastNotifiedStep: Flow<Int> =
        context.dataStore.data.map { it[Keys.LAST_NOTIFIED_STEP] ?: 0 }

    suspend fun setLastNotifiedStep(step: Int) {
        context.dataStore.edit { it[Keys.LAST_NOTIFIED_STEP] = step }
    }

    // FR-8 hatırlatma düzeyi (Kapalı/Az/Orta/Sık); varsayılan Orta.
    val reminderLevel: Flow<ReminderLevel> = context.dataStore.data.map { prefs ->
        levelOf(prefs[Keys.STILL_ON_LEVEL])
    }

    suspend fun setReminderLevel(level: ReminderLevel) {
        context.dataStore.edit { it[Keys.STILL_ON_LEVEL] = level.ordinal }
    }

    // FR-8 gelişmiş: ham dakika override (0 = ayarlı değil, düzey kullanılır).
    val reminderRawMinutes: Flow<Int> =
        context.dataStore.data.map { it[Keys.STILL_ON_RAW_MIN] ?: 0 }

    suspend fun setReminderRawMinutes(minutes: Int) {
        context.dataStore.edit { prefs ->
            if (minutes > 0) prefs[Keys.STILL_ON_RAW_MIN] = minutes
            else prefs.remove(Keys.STILL_ON_RAW_MIN)
        }
    }

    // Etkin FR-8 aralığı (dakika): ham değer varsa onu, yoksa düzeyi kullanır; null = kapalı.
    val effectiveReminderMinutes: Flow<Int?> = context.dataStore.data.map { prefs ->
        val raw = prefs[Keys.STILL_ON_RAW_MIN] ?: 0
        if (raw > 0) raw else levelOf(prefs[Keys.STILL_ON_LEVEL]).approxMinutes
    }

    // FR-8 cooldown durumu: son "hâlâ mobil verideysin" bildirimi zamanı (epoch ms).
    val lastStillOnReminderMillis: Flow<Long> =
        context.dataStore.data.map { it[Keys.STILL_ON_LAST_MILLIS] ?: 0L }

    suspend fun setLastStillOnReminderMillis(millis: Long) {
        context.dataStore.edit { it[Keys.STILL_ON_LAST_MILLIS] = millis }
    }

    private fun levelOf(ordinal: Int?): ReminderLevel =
        ReminderLevel.entries.getOrElse(ordinal ?: ReminderLevel.MID.ordinal) { ReminderLevel.MID }

    companion object {
        const val MIN_STEP_MB = 100
        const val DEFAULT_STEP_MB = 200
    }
}
