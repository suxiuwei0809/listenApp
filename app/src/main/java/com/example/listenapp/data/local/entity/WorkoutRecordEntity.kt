package com.example.listenapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.util.Date

@Entity(tableName = "workout_records")
@TypeConverters(WorkoutTypeConverters::class)
data class WorkoutRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String, // "pushup" | "plank" | "abwheel" | "gym"
    val name: String, // display name
    val value: Int, // count for pushup/abwheel, seconds for plank
    val sets: Int, // number of sets
    val durationSeconds: Int, // total duration in seconds
    val calories: Int, // calories burned
    val date: Long, // timestamp
    val note: String = ""
)

class WorkoutTypeConverters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? = date?.time
}
