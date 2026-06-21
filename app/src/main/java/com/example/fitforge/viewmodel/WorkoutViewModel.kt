package com.example.fitforge.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.fitforge.data.local.DatabaseProvider
import com.example.fitforge.data.local.entity.WorkoutRecordEntity
import kotlinx.coroutines.flow.Flow

/**
 * Shared workout record ViewModel for Plank, Push-up, and Ab Wheel
 */
class WorkoutViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = DatabaseProvider.getDatabase(application).workoutDao

    fun savePlank(durationSeconds: Int, sets: Int = 1) {
        save("plank", "平板支撑", durationSeconds, sets, durationSeconds, estimateCalories("plank", durationSeconds))
    }

    fun savePushup(totalReps: Int, sets: Int, durationSeconds: Int = 0) {
        save("pushup", "俯卧撑", totalReps, sets, durationSeconds, estimateCalories("pushup", totalReps))
    }

    fun saveAbwheel(totalReps: Int, sets: Int, durationSeconds: Int = 0) {
        save("abwheel", "腹肌轮", totalReps, sets, durationSeconds, estimateCalories("abwheel", totalReps))
    }

    fun saveGymExec(planName: String, durationSeconds: Int) {
        save("gym", planName, durationSeconds / 60, 1, durationSeconds, estimateCalories("gym", durationSeconds))
    }

    fun getHistory(type: String): Flow<List<WorkoutRecordEntity>> {
        return dao.getRecordsByType(type)
    }

    fun deleteRecord(id: Long) {
        dao.delete(id)
    }

    private fun save(type: String, name: String, value: Int, sets: Int, durationSeconds: Int, calories: Int) {
        if (sets <= 0 && value <= 0) return
        dao.insert(
            WorkoutRecordEntity(
                type = type, name = name, value = value,
                sets = sets, durationSeconds = durationSeconds,
                calories = calories, date = System.currentTimeMillis()
            )
        )
    }

    private fun estimateCalories(type: String, value: Int): Int = when (type) {
        "plank" -> (value / 60) * 7  // 每分钟约7kcal
        "pushup" -> value * 1        // 每个约1kcal
        "abwheel" -> value * 1       // 每个约1kcal
        "gym" -> (value / 60) * 8    // 健身房每分钟约8kcal
        else -> 0
    }
}
