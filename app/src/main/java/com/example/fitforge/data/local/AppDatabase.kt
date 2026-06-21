package com.example.fitforge.data.local

import android.content.Context
import com.example.fitforge.data.local.dao.BodyDataDao
import com.example.fitforge.data.local.dao.WorkoutDao

class AppDatabase(context: Context) {
    val workoutDao = WorkoutDao(context)
    val bodyDataDao = BodyDataDao(context)
}
