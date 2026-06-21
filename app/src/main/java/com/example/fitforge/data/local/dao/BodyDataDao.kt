package com.example.fitforge.data.local.dao

import android.content.Context
import android.content.SharedPreferences
import com.example.fitforge.data.local.entity.BodyDataEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.json.JSONArray

class BodyDataDao(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("body_data", Context.MODE_PRIVATE)

    private val _records = MutableStateFlow(loadAll())

    fun insert(record: BodyDataEntity) {
        val list = loadAll().toMutableList()
        list.add(0, record)
        saveList(list)
        _records.value = list
    }

    fun getRecentRecords(): Flow<List<BodyDataEntity>> = _records

    suspend fun getLatest(): BodyDataEntity? = loadAll().firstOrNull()

    fun getTodayRecords(startOfDay: Long): Flow<List<BodyDataEntity>> {
        return MutableStateFlow(loadAll().filter { it.date >= startOfDay })
    }

    suspend fun delete(id: Long) {
        val list = loadAll().filter { it.id != id }
        saveList(list)
        _records.value = list
    }

    private fun loadAll(): List<BodyDataEntity> {
        val json = prefs.getString("records", "[]") ?: "[]"
        return try {
            val arr = JSONArray(json)
            (0 until arr.length()).map { BodyDataEntity.fromJson(arr.getJSONObject(it)) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun saveList(list: List<BodyDataEntity>) {
        val arr = JSONArray()
        list.forEach { arr.put(it.toJson()) }
        prefs.edit().putString("records", arr.toString()).commit()
    }
}
