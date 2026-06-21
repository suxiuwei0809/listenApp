package com.example.fitforge.data.local.entity

import org.json.JSONObject

data class BodyDataEntity(
    val id: Long = System.currentTimeMillis(),
    val weightKg: Float,
    val bodyFatPercent: Float? = null,
    val heightCm: Float? = null,
    val bmi: Float? = null,
    val date: Long
) {
    fun toJson(): JSONObject = JSONObject().apply {
        put("id", id)
        put("weightKg", weightKg.toDouble())
        bodyFatPercent?.let { put("bodyFatPercent", it.toDouble()) }
        heightCm?.let { put("heightCm", it.toDouble()) }
        bmi?.let { put("bmi", it.toDouble()) }
        put("date", date)
    }

    companion object {
        fun fromJson(obj: JSONObject) = BodyDataEntity(
            id = obj.optLong("id", System.currentTimeMillis()),
            weightKg = obj.optDouble("weightKg").toFloat(),
            bodyFatPercent = if (obj.has("bodyFatPercent")) obj.optDouble("bodyFatPercent").toFloat() else null,
            heightCm = if (obj.has("heightCm")) obj.optDouble("heightCm").toFloat() else null,
            bmi = if (obj.has("bmi")) obj.optDouble("bmi").toFloat() else null,
            date = obj.optLong("date")
        )
    }
}
