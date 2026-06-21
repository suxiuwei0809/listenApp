package com.example.fitforge.data.local.entity

import org.json.JSONArray
import org.json.JSONObject

data class WorkoutRecordEntity(
    val id: Long = System.currentTimeMillis(),
    val type: String,
    val name: String,
    val value: Int,
    val sets: Int,
    val durationSeconds: Int,
    val calories: Int,
    val date: Long,
    val note: String = ""
) {
    fun toJson(): JSONObject = JSONObject().apply {
        put("id", id)
        put("type", type)
        put("name", name)
        put("value", value)
        put("sets", sets)
        put("durationSeconds", durationSeconds)
        put("calories", calories)
        put("date", date)
        put("note", note)
    }

    companion object {
        fun fromJson(obj: JSONObject) = WorkoutRecordEntity(
            id = obj.optLong("id", System.currentTimeMillis()),
            type = obj.optString("type"),
            name = obj.optString("name"),
            value = obj.optInt("value"),
            sets = obj.optInt("sets"),
            durationSeconds = obj.optInt("durationSeconds"),
            calories = obj.optInt("calories"),
            date = obj.optLong("date"),
            note = obj.optString("note")
        )
    }
}
