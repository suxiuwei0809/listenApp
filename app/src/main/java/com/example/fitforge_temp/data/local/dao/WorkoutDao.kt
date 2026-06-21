package com.example.listenapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.listenapp.data.local.entity.WorkoutRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {

    @Insert
    suspend fun insert(workout: WorkoutRecordEntity)

    @Query("SELECT * FROM workout_records ORDER BY date DESC LIMIT 20")
    fun getRecentWorkouts(): Flow<List<WorkoutRecordEntity>>

    @Query("SELECT * FROM workout_records WHERE date >= :startOfDay ORDER BY date DESC")
    fun getTodaysWorkouts(startOfDay: Long): Flow<List<WorkoutRecordEntity>>

    @Query("SELECT MAX(value) FROM workout_records WHERE type = :type")
    suspend fun getPersonalBestValue(type: String): Int?

    @Query("SELECT MAX(durationSeconds) FROM workout_records WHERE type = :type")
    suspend fun getPersonalBestDuration(type: String): Int?

    @Query("SELECT SUM(calories) FROM workout_records WHERE date >= :startOfDay")
    suspend fun getTodaysCalories(startOfDay: Long): Int?

    @Query("SELECT SUM(durationSeconds) FROM workout_records WHERE date >= :startOfDay")
    suspend fun getTodaysExerciseSeconds(startOfDay: Long): Int?

    @Query("SELECT COUNT(*) FROM workout_records WHERE date >= :startOfDay")
    suspend fun getTodaysWorkoutCount(startOfDay: Long): Int
}
