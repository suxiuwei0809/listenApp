package com.example.listenapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.listenapp.data.local.dao.WorkoutDao
import com.example.listenapp.data.local.entity.WorkoutRecordEntity

@Database(entities = [WorkoutRecordEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao
}
