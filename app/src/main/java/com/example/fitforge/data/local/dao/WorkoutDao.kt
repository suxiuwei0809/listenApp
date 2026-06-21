package com.example.fitforge.data.local.dao

import android.content.Context
import android.content.SharedPreferences
import com.example.fitforge.data.local.entity.WorkoutRecordEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import org.json.JSONArray

class WorkoutDao(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("workout_records", Context.MODE_PRIVATE)

    private val _records = MutableStateFlow(loadAll())

    fun insert(record: WorkoutRecordEntity) {
        val list = loadAll().toMutableList()
        list.add(0, record)
        saveList(list)
        _records.value = list
    }

    fun getRecentWorkouts(): Flow<List<WorkoutRecordEntity>> = _records

    fun getRecordsByType(type: String): Flow<List<WorkoutRecordEntity>> = _records.map { it.filter { r -> r.type == type } }

    fun loadByTypeDirect(type: String): List<WorkoutRecordEntity> = loadAll().filter { it.type == type }

    suspend fun getPersonalBestValue(type: String): Int? {
        return loadAll().filter { it.type == type }.maxOfOrNull { it.value }
    }

    suspend fun getPersonalBestDuration(type: String): Int? {
        return loadAll().filter { it.type == type }.maxOfOrNull { it.durationSeconds }
    }

    suspend fun getTodaysCalories(startOfDay: Long): Int {
        return loadAll().filter { it.date >= startOfDay }.sumOf { it.calories }
    }

    suspend fun getTodaysExerciseSeconds(startOfDay: Long): Int {
        return loadAll().filter { it.date >= startOfDay }.sumOf { it.durationSeconds }
    }

    suspend fun getTodaysWorkoutCount(startOfDay: Long): Int {
        return loadAll().count { it.date >= startOfDay }
    }

    private fun loadAll(): List<WorkoutRecordEntity> {
        val json = prefs.getString("records", "[]") ?: "[]"
        return try {
            val arr = JSONArray(json)
            (0 until arr.length()).map { WorkoutRecordEntity.fromJson(arr.getJSONObject(it)) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun delete(id: Long) {
        val list = loadAll().filter { it.id != id }
        saveList(list)
        _records.value = list
    }

    fun refresh() {
        _records.value = loadAll()
    }

    fun loadSnapshot(): List<WorkoutRecordEntity> = _records.value

    private fun saveList(list: List<WorkoutRecordEntity>) {
        val arr = JSONArray()
        list.forEach { arr.put(it.toJson()) }
        prefs.edit().putString("records", arr.toString()).commit()
    }
}
