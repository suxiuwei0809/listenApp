package com.example.listenapp.data.local

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase? {
        if (INSTANCE != null) return INSTANCE
        synchronized(this) {
            if (INSTANCE == null) {
                try {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "fitness_db"
                    ).build()
                } catch (e: Exception) {
                    e.printStackTrace()
                    return null
                }
            }
            return INSTANCE
        }
    }
}
