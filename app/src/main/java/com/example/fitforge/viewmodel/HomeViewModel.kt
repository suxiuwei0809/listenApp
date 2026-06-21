package com.example.fitforge.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitforge.data.local.AppPreferences
import com.example.fitforge.data.local.DatabaseProvider
import com.example.fitforge.data.local.entity.WorkoutRecordEntity
import com.example.fitforge.data.model.*
import com.example.fitforge.data.static.GymPlanLibrary
import com.example.fitforge.service.StepSensorHelper
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val db = DatabaseProvider.getDatabase(application)
    private val workoutDao = db.workoutDao
    private val bodyDataDao = db.bodyDataDao
    private val prefs = AppPreferences(application)

    // ---- Today steps + workout stats ----
    private val _todayStats = MutableStateFlow(
        TodayStats(steps = 0, stepGoal = 10000, exerciseMinutes = 0, caloriesBurned = 0)
    )
    val todayStats: StateFlow<TodayStats> = _todayStats.asStateFlow()

    // ---- Personal best ----
    private val _personalBest = MutableStateFlow(
        PersonalBest(pushupMax = 0, plankMax = "0'00\"", abWheelMax = 0)
    )
    val personalBest: StateFlow<PersonalBest> = _personalBest.asStateFlow()

    // ---- Weekly workout stats ----
    private val _weeklyStats = MutableStateFlow(WeeklyStats(0, 0, 0))
    val weeklyStats: StateFlow<WeeklyStats> = _weeklyStats.asStateFlow()

    // ---- Training target ----
    private val _selectedTarget = MutableStateFlow<TrainingTarget?>(null)
    val selectedTarget: StateFlow<TrainingTarget?> = _selectedTarget.asStateFlow()

    private val _recommendedTarget = MutableStateFlow(GymPlanLibrary.getRecommendTargetForToday())
    val recommendedTarget: StateFlow<TrainingTarget> = _recommendedTarget.asStateFlow()

    // ---- Recent workouts (from Room) ----
    private val _recentWorkouts = MutableStateFlow<List<RecentWorkout>>(emptyList())
    val recentWorkouts: StateFlow<List<RecentWorkout>> = _recentWorkouts.asStateFlow()

    // ---- Gym plan list (static) ----
    val gymPlans: StateFlow<List<GymPlan>> = MutableStateFlow(GymPlanLibrary.getGymPlans()).asStateFlow()

    // ---- Step progress ----
    val stepProgress: Float
        get() = (_todayStats.value.steps.toFloat() / _todayStats.value.stepGoal).coerceAtMost(1f)

    // ---- Step weekly trend (last 7 days) ----
    val stepTrend: StateFlow<List<Int>> = prefs.stepHistory
        .map { history ->
            // Fill missing days with 0 for last 7 days
            val cal = Calendar.getInstance()
            (0..6).map { offset ->
                cal.timeInMillis = System.currentTimeMillis()
                cal.add(Calendar.DAY_OF_YEAR, -offset)
                cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
                val dayKey = cal.timeInMillis
                history.find { it.date == dayKey }?.steps ?: 0
            }.reversed() // Mon -> Sun
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), List(7) { 0 })

    init {
        // Load step goal from DataStore
        viewModelScope.launch {
            prefs.stepGoal.collect { goal ->
                _todayStats.update { it.copy(stepGoal = goal) }
            }
        }
        // Read real-time steps from sensor and persist to DataStore
        viewModelScope.launch {
            StepSensorHelper.observeSteps(application).collect { steps ->
                _todayStats.update { it.copy(steps = steps) }
                if (steps > 0) prefs.saveTodaySteps(steps)
            }
        }
        // Load recent workout records from Room
        loadRecordData()
        // Load personal bests from Room
        loadPersonalBests()
        // Refresh today stats
        refreshTodayStats()
    }

    fun selectTarget(target: TrainingTarget) {
        _selectedTarget.update { target }
    }

    fun selectRecommended() {
        _selectedTarget.update { null }
    }

    /** Save a workout record to Room */
    fun saveWorkout(
        type: String, name: String, value: Int = 0,
        sets: Int = 0, durationSeconds: Int = 0, calories: Int = 0, note: String = ""
    ) {
        viewModelScope.launch {
            workoutDao?.insert(
                WorkoutRecordEntity(
                    type = type, name = name, value = value,
                    sets = sets, durationSeconds = durationSeconds,
                    calories = calories, date = System.currentTimeMillis(), note = note
                )
            )
            loadRecordData()
            loadPersonalBests()
            refreshTodayStats()
        }
    }

    private fun loadRecordData() {
        viewModelScope.launch {
            workoutDao?.getRecentWorkouts()?.collect { records ->
                _recentWorkouts.value = records.take(4).map { it.toRecentWorkout() }
                // 本周运动概览
                val weekStart = getWeekStartMillis()
                val weekRecords = records.filter { it.date >= weekStart }
                val activeDays = weekRecords.map { getDateKey(it.date) }.distinct().size
                val totalMin = weekRecords.sumOf { it.durationSeconds } / 60
                val totalCal = weekRecords.sumOf { it.calories }
                _weeklyStats.value = WeeklyStats(activeDays, totalMin, totalCal)
            }
        }
    }

    private fun loadPersonalBests() {
        viewModelScope.launch {
            val pushupMax = workoutDao?.getPersonalBestValue("pushup") ?: 0
            val plankMax = workoutDao?.getPersonalBestDuration("plank") ?: 0
            val abwheelMax = workoutDao?.getPersonalBestValue("abwheel") ?: 0
            _personalBest.value = PersonalBest(
                pushupMax = pushupMax,
                plankMax = formatSeconds(plankMax),
                abWheelMax = abwheelMax
            )
        }
    }

    private fun refreshTodayStats() {
        viewModelScope.launch {
            val startOfDay = getStartOfDayMillis()
            val calories = workoutDao?.getTodaysCalories(startOfDay) ?: 0
            val seconds = workoutDao?.getTodaysExerciseSeconds(startOfDay) ?: 0
            _todayStats.update {
                it.copy(
                    exerciseMinutes = seconds / 60,
                    caloriesBurned = calories
                )
            }
        }
    }
}

// ---- Utility functions ----

private fun WorkoutRecordEntity.toRecentWorkout(): RecentWorkout {
    return RecentWorkout(
        icon = getIconForType(type),
        name = name,
        meta = "${sets} sets - ${formatSeconds(durationSeconds)}",
        calories = calories,
        color = when (type) {
            "pushup" -> "#ff2d75"
            "plank" -> "#ff6b35"
            "abwheel" -> "#00ff88"
            else -> "#00d4ff"
        }
    )
}

data class WeeklyStats(
    val activeDays: Int,
    val totalMinutes: Int,
    val totalCalories: Int
)

private fun getWeekStartMillis(): Long {
    val cal = Calendar.getInstance()
    // 本周一 00:00
    cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    return cal.timeInMillis
}

private fun getDateKey(timestamp: Long): Long {
    val cal = Calendar.getInstance()
    cal.timeInMillis = timestamp
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    return cal.timeInMillis
}

fun getIconForType(type: String): String = when (type) {
    "pushup" -> "\uD83D\uDCAA"
    "plank" -> "\uD83E\uDDD8"
    "abwheel" -> "\uD83C\uDFC6"
    else -> "\uD83C\uDFCB"
}

fun formatSeconds(seconds: Int): String {
    if (seconds <= 0) return "0'00\""
    val m = seconds / 60
    val s = seconds % 60
    return "${m}'${s.toString().padStart(2, '0')}\""
}

private fun getStartOfDayMillis(): Long {
    val cal = Calendar.getInstance()
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    return cal.timeInMillis
}
