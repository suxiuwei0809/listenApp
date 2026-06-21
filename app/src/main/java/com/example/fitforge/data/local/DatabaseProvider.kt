package com.example.fitforge.data.local

import android.content.Context

object DatabaseProvider {
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        if (INSTANCE != null) return INSTANCE!!
        synchronized(this) {
            if (INSTANCE == null) {
                INSTANCE = AppDatabase(context.applicationContext)
            }
            return INSTANCE!!
        }
    }
}
