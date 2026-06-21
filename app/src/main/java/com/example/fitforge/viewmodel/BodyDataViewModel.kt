package com.example.fitforge.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitforge.data.local.AppPreferences
import com.example.fitforge.data.local.DatabaseProvider
import com.example.fitforge.data.local.entity.BodyDataEntity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

data class BodyDataState(
    val latestWeight: Float = 0f,
    val latestBodyFat: Float? = null,
    val height: Float? = null,
    val bmi: Float? = null,
    val weightGoal: Float? = null,
    val monthlyChange: Float = 0f,
    val records: List<BodyDataEntity> = emptyList()
)

class BodyDataViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = DatabaseProvider.getDatabase(application).bodyDataDao
    private val prefs = AppPreferences(application)

    private val _state = MutableStateFlow(BodyDataState())
    val state: StateFlow<BodyDataState> = _state.asStateFlow()

    init {
        // Load height and goal weight from DataStore
        viewModelScope.launch {
            combine(
                prefs.height,
                prefs.weightGoal
            ) { h, wg -> Pair(h, wg) }.collect { (height, weightGoal) ->
                _state.update { it.copy(height = height, weightGoal = weightGoal) }
            }
        }
        // Load body data from Room
        viewModelScope.launch {
            dao?.getRecentRecords()?.collect { records ->
                val latest = records.firstOrNull()
                val monthStart = getMonthStartMillis()
                val monthRecords = records.filter { it.date >= monthStart }
                val firstOfMonth = monthRecords.lastOrNull()
                val monthlyChange = if (latest != null && firstOfMonth != null && firstOfMonth != latest) {
                    latest.weightKg - firstOfMonth.weightKg
                } else 0f

                _state.update {
                    it.copy(
                        latestWeight = latest?.weightKg ?: 0f,
                        latestBodyFat = latest?.bodyFatPercent,
                        bmi = if (latest != null && it.height != null && it.height!! > 0) {
                            latest.weightKg / ((it.height!! / 100) * (it.height!! / 100))
                        } else null,
                        monthlyChange = monthlyChange,
                        records = records
                    )
                }
            }
        }
    }

    fun saveWeight(weightKg: Float, bodyFatPercent: Float? = null) {
        viewModelScope.launch {
            val state = _state.value
            val bmi = if (state.height != null && state.height!! > 0) {
                weightKg / ((state.height!! / 100) * (state.height!! / 100))
            } else null
            dao?.insert(
                BodyDataEntity(
                    weightKg = weightKg,
                    bodyFatPercent = bodyFatPercent,
                    heightCm = state.height,
                    bmi = bmi,
                    date = System.currentTimeMillis()
                )
            )
        }
    }

    fun setHeight(cm: Float) {
        viewModelScope.launch { prefs.setHeight(cm) }
    }

    fun setWeightGoal(kg: Float) {
        viewModelScope.launch { prefs.setWeightGoal(kg) }
    }

    private fun getMonthStartMillis(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
}
