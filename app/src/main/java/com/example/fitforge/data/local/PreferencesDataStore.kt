package com.example.fitforge.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "fitness_settings")

object PreferenceKeys {
    val STEP_GOAL = intPreferencesKey("step_goal")
    val WEIGHT_GOAL = floatPreferencesKey("weight_goal")
    val HEIGHT = floatPreferencesKey("height_cm")
    val REMINDER_ENABLED = booleanPreferencesKey("reminder_enabled")
    val THEME_DARK = booleanPreferencesKey("theme_dark")
    val STEP_HISTORY = stringPreferencesKey("step_history") // JSON: [{"d":20240614,"s":8542},...]
}

class AppPreferences(private val context: Context) {

    // 每日步数目标（默�?10000�?
    val stepGoal: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[PreferenceKeys.STEP_GOAL] ?: 10000
    }

    // 目标体重 (kg)
    val weightGoal: Flow<Float?> = context.dataStore.data.map { prefs ->
        prefs[PreferenceKeys.WEIGHT_GOAL]
    }

    // 身高 (cm)
    val height: Flow<Float?> = context.dataStore.data.map { prefs ->
        prefs[PreferenceKeys.HEIGHT]
    }

    // 提醒开�?
    val reminderEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[PreferenceKeys.REMINDER_ENABLED] ?: false
    }

    // 深色主题
    val themeDark: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[PreferenceKeys.THEME_DARK] ?: true
    }

    // Setters
    suspend fun setStepGoal(steps: Int) {
        context.dataStore.edit { it[PreferenceKeys.STEP_GOAL] = steps }
    }

    suspend fun setWeightGoal(kg: Float) {
        context.dataStore.edit { it[PreferenceKeys.WEIGHT_GOAL] = kg }
    }

    suspend fun setHeight(cm: Float) {
        context.dataStore.edit { it[PreferenceKeys.HEIGHT] = cm }
    }

    suspend fun setReminderEnabled(enabled: Boolean) {
        context.dataStore.edit { it[PreferenceKeys.REMINDER_ENABLED] = enabled }
    }

    suspend fun setThemeDark(dark: Boolean) {
        context.dataStore.edit { it[PreferenceKeys.THEME_DARK] = dark }
    }

    // ---- 步数历史（最�?天） ----

    data class StepDay(val date: Long, val steps: Int)

    val stepHistory: Flow<List<StepDay>> = context.dataStore.data.map { prefs ->
        val json = prefs[PreferenceKeys.STEP_HISTORY] ?: "[]"
        parseStepHistory(json)
    }

    suspend fun saveTodaySteps(steps: Int) {
        val todayKey = getTodayDateKey()
        context.dataStore.edit { prefs ->
            val json = prefs[PreferenceKeys.STEP_HISTORY] ?: "[]"
            val list = parseStepHistory(json).toMutableList()

            // 更新或添加今日记�?
            val existing = list.indexOfFirst { it.date == todayKey }
            if (existing >= 0) {
                list[existing] = list[existing].copy(steps = steps)
            } else {
                list.add(0, StepDay(todayKey, steps))
            }

            // 保留最�?�?
            val trimmed = list.take(7)
            prefs[PreferenceKeys.STEP_HISTORY] = formatStepHistory(trimmed)
        }
    }

    private fun getTodayDateKey(): Long {
        val cal = java.util.Calendar.getInstance()
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
        cal.set(java.util.Calendar.MINUTE, 0)
        cal.set(java.util.Calendar.SECOND, 0)
        cal.set(java.util.Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    private fun parseStepHistory(json: String): List<StepDay> {
        return try {
            // 格式: "20240614:8542;20240613:7800"
            if (json.isBlank() || json == "[]") return emptyList()
            json.trim(';').split(";").mapNotNull { entry ->
                val parts = entry.split(":")
                if (parts.size == 2) StepDay(parts[0].toLong(), parts[1].toInt()) else null
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun formatStepHistory(list: List<StepDay>): String {
        return list.joinToString(";") { "${it.date}:${it.steps}" }
    }
}
