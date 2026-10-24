package com.example.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GamePreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("block_fury_prefs", Context.MODE_PRIVATE)

    private val _highScoreClassic = MutableStateFlow(prefs.getInt("high_score_classic", 0))
    val highScoreClassic: StateFlow<Int> = _highScoreClassic.asStateFlow()

    private val _highScoreTimeAttack = MutableStateFlow(prefs.getInt("high_score_time_attack", 0))
    val highScoreTimeAttack: StateFlow<Int> = _highScoreTimeAttack.asStateFlow()

    private val _coins = MutableStateFlow(prefs.getInt("coins_balance", 100)) // 100 starter coins
    val coins: StateFlow<Int> = _coins.asStateFlow()

    private val _selectedThemeId = MutableStateFlow(prefs.getString("selected_theme", "NEON_ARCADE") ?: "NEON_ARCADE")
    val selectedThemeId: StateFlow<String> = _selectedThemeId.asStateFlow()

    private val _unlockedThemeIds = MutableStateFlow(
        prefs.getStringSet("unlocked_themes", setOf("NEON_ARCADE")) ?: setOf("NEON_ARCADE")
    )
    val unlockedThemeIds: StateFlow<Set<String>> = _unlockedThemeIds.asStateFlow()

    private val _unlockedAchievements = MutableStateFlow(
        prefs.getStringSet("unlocked_achievements", emptySet()) ?: emptySet()
    )
    val unlockedAchievements: StateFlow<Set<String>> = _unlockedAchievements.asStateFlow()

    private val _lastDailyCompletedDate = MutableStateFlow(prefs.getString("last_daily_date", "") ?: "")
    val lastDailyCompletedDate: StateFlow<String> = _lastDailyCompletedDate.asStateFlow()

    // Settings
    private val _sfxEnabled = MutableStateFlow(prefs.getBoolean("sfx_enabled", true))
    val sfxEnabled: StateFlow<Boolean> = _sfxEnabled.asStateFlow()

    private val _musicEnabled = MutableStateFlow(prefs.getBoolean("music_enabled", true))
    val musicEnabled: StateFlow<Boolean> = _musicEnabled.asStateFlow()

    private val _sfxVolume = MutableStateFlow(prefs.getFloat("sfx_volume", 0.8f))
    val sfxVolume: StateFlow<Float> = _sfxVolume.asStateFlow()

    private val _musicVolume = MutableStateFlow(prefs.getFloat("music_volume", 0.6f))
    val musicVolume: StateFlow<Float> = _musicVolume.asStateFlow()

    private val _hapticsEnabled = MutableStateFlow(prefs.getBoolean("haptics_enabled", true))
    val hapticsEnabled: StateFlow<Boolean> = _hapticsEnabled.asStateFlow()

    private val _reducedMotion = MutableStateFlow(prefs.getBoolean("reduced_motion", false))
    val reducedMotion: StateFlow<Boolean> = _reducedMotion.asStateFlow()

    fun updateClassicScore(score: Int): Boolean {
        if (score > _highScoreClassic.value) {
            _highScoreClassic.value = score
            prefs.edit().putInt("high_score_classic", score).apply()
            return true
        }
        return false
    }

    fun updateTimeAttackScore(score: Int): Boolean {
        if (score > _highScoreTimeAttack.value) {
            _highScoreTimeAttack.value = score
            prefs.edit().putInt("high_score_time_attack", score).apply()
            return true
        }
        return false
    }

    fun addCoins(amount: Int) {
        val newBalance = (_coins.value + amount).coerceAtLeast(0)
        _coins.value = newBalance
        prefs.edit().putInt("coins_balance", newBalance).apply()
    }

    fun spendCoins(amount: Int): Boolean {
        if (_coins.value >= amount) {
            val newBalance = _coins.value - amount
            _coins.value = newBalance
            prefs.edit().putInt("coins_balance", newBalance).apply()
            return true
        }
        return false
    }

    fun unlockTheme(themeId: String) {
        val updated = _unlockedThemeIds.value.toMutableSet().apply { add(themeId) }
        _unlockedThemeIds.value = updated
        prefs.edit().putStringSet("unlocked_themes", updated).apply()
    }

    fun selectTheme(themeId: String) {
        _selectedThemeId.value = themeId
        prefs.edit().putString("selected_theme", themeId).apply()
    }

    fun unlockAchievement(achievementId: String): Boolean {
        if (!_unlockedAchievements.value.contains(achievementId)) {
            val updated = _unlockedAchievements.value.toMutableSet().apply { add(achievementId) }
            _unlockedAchievements.value = updated
            prefs.edit().putStringSet("unlocked_achievements", updated).apply()
            return true
        }
        return false
    }

    fun isDailyCompletedToday(): Boolean {
        val today = getTodayDateString()
        return _lastDailyCompletedDate.value == today
    }

    fun markDailyCompleted() {
        val today = getTodayDateString()
        _lastDailyCompletedDate.value = today
        prefs.edit().putString("last_daily_date", today).apply()
    }

    fun getTodayDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    fun setSfxEnabled(enabled: Boolean) {
        _sfxEnabled.value = enabled
        prefs.edit().putBoolean("sfx_enabled", enabled).apply()
    }

    fun setMusicEnabled(enabled: Boolean) {
        _musicEnabled.value = enabled
        prefs.edit().putBoolean("music_enabled", enabled).apply()
    }

    fun setSfxVolume(vol: Float) {
        _sfxVolume.value = vol
        prefs.edit().putFloat("sfx_volume", vol).apply()
    }

    fun setMusicVolume(vol: Float) {
        _musicVolume.value = vol
        prefs.edit().putFloat("music_volume", vol).apply()
    }

    fun setHapticsEnabled(enabled: Boolean) {
        _hapticsEnabled.value = enabled
        prefs.edit().putBoolean("haptics_enabled", enabled).apply()
    }

    fun setReducedMotion(enabled: Boolean) {
        _reducedMotion.value = enabled
        prefs.edit().putBoolean("reduced_motion", enabled).apply()
    }

    fun resetAllProgress() {
        prefs.edit().clear().apply()
        _highScoreClassic.value = 0
        _highScoreTimeAttack.value = 0
        _coins.value = 100
        _selectedThemeId.value = "NEON_ARCADE"
        _unlockedThemeIds.value = setOf("NEON_ARCADE")
        _unlockedAchievements.value = emptySet()
        _lastDailyCompletedDate.value = ""
        _sfxEnabled.value = true
        _musicEnabled.value = true
        _sfxVolume.value = 0.8f
        _musicVolume.value = 0.6f
        _hapticsEnabled.value = true
        _reducedMotion.value = false
    }
}
