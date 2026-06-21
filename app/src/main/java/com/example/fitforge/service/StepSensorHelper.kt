package com.example.fitforge.service

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.content.SharedPreferences
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.*

/**
 * 轻量计步 —�?读取系统 TYPE_STEP_COUNTER 传感器�?
 * �?SharedPreferences 存储每日基线，SensorManager 实时监听�?
 */
object StepSensorHelper {

    private const val PREFS_NAME = "step_counter_prefs"
    private const val KEY_BASELINE = "today_baseline"
    private const val KEY_DATE = "today_date"

    fun observeSteps(context: Context): Flow<Int> = callbackFlow {
        val manager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        val sensor = manager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (sensor == null) {
            trySend(0)
            close()
            return@callbackFlow
        }

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val today = getTodayKey()

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event == null) return
                val totalSteps = event.values[0].toLong()
                val savedDate = prefs.getLong(KEY_DATE, 0)

                val baseline = if (savedDate != today) {
                    prefs.edit().putLong(KEY_DATE, today).putLong(KEY_BASELINE, totalSteps).apply()
                    totalSteps
                } else {
                    prefs.getLong(KEY_BASELINE, totalSteps)
                }

                val todaySteps = (totalSteps - baseline).coerceAtLeast(0).toInt()
                trySend(todaySteps)
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        manager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        awaitClose { manager.unregisterListener(listener) }
    }

    private fun getTodayKey(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
}
