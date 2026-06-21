package com.example.fitforge.data.model

data class WorkoutRecord(
    val id: Long = System.currentTimeMillis(),
    val type: WorkoutType,
    val name: String,
    val duration: Int,
    val calories: Int,
    val distance: String? = null,
    val sets: Int? = null,
    val timestamp: Long = System.currentTimeMillis()
)

enum class WorkoutType(val displayName: String) {
    RUNNING("跑步"),
    STRENGTH("力量训练"),
    YOGA("瑜伽"),
    CARDIO("有氧"),
    GYM("健身房")
}

data class TodayStats(
    val steps: Int,
    val stepGoal: Int = 10000,
    val exerciseMinutes: Int,
    val caloriesBurned: Int
)

data class PersonalBest(
    val pushupMax: Int,
    val plankMax: String,
    val abWheelMax: Int
)

data class WeekCheckIn(
    val monday: Boolean = false,
    val tuesday: Boolean = false,
    val wednesday: Boolean = false,
    val thursday: Boolean = false,
    val friday: Boolean = false,
    val saturday: Boolean = false,
    val sunday: Boolean = false
) {
    fun getCompletedDays(): Int {
        return listOf(monday, tuesday, wednesday, thursday, friday, saturday, sunday)
            .count { it }
    }
}

data class RecentWorkout(
    val icon: String,
    val name: String,
    val meta: String,
    val calories: Int,
    val color: String
)
