package com.example.fitforge.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitforge.data.local.DatabaseProvider
import com.example.fitforge.data.local.entity.WorkoutRecordEntity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class WorkoutRecordViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = DatabaseProvider.getDatabase(application).workoutDao

    data class WorkoutStats(
        val count: Int = 0,
        val totalMinutes: Int = 0,
        val totalCalories: Int = 0,
        val chartData: List<Int> = List(7) { 0 }
    )

    data class DisplayEntry(
        val type: String,
        val name: String,
        val date: Long,
        val duration: Int,
        val calories: Int,
        val sets: Int = 0,
        val value: Int = 0
    )

    data class FilterState(
        val timeFilter: String = "week",
        val typeFilter: String = "all"
    )

    private val _filterState = MutableStateFlow(FilterState())
    val filterState: StateFlow<FilterState> = _filterState.asStateFlow()

    private val _stats = MutableStateFlow(WorkoutStats())
    val stats: StateFlow<WorkoutStats> = _stats.asStateFlow()

    private val _entries = MutableStateFlow<List<DisplayEntry>>(emptyList())
    val entries: StateFlow<List<DisplayEntry>> = _entries.asStateFlow()

    init {
        // 监听数据变化
        viewModelScope.launch {
            dao.getRecentWorkouts().collect { rawList ->
                val filter = _filterState.value
                _entries.value = applyFilters(rawList, filter)
                _stats.value = computeStats(rawList, filter)
            }
        }
        // 监听筛选变化
        viewModelScope.launch {
            _filterState.collect { filter ->
                val rawList = dao.loadSnapshot()
                _entries.value = applyFilters(rawList, filter)
                _stats.value = computeStats(rawList, filter)
            }
        }
    }

    /** 强制刷新数据（从 SharedPreferences 重新加载） */
    fun refresh() {
        dao.refresh()
    }

    fun setTimeFilter(time: String) { _filterState.update { it.copy(timeFilter = time) } }
    fun setTypeFilter(type: String) { _filterState.update { it.copy(typeFilter = type) } }

    fun addManualRecord(type: String, name: String, durationMinutes: Int, calories: Int) {
        viewModelScope.launch {
            dao?.insert(
                WorkoutRecordEntity(
                    type = mapDisplayType(type),
                    name = name,
                    value = 0,
                    sets = 0,
                    durationSeconds = durationMinutes * 60,
                    calories = calories,
                    date = System.currentTimeMillis(),
                    note = ""
                )
            )
        }
    }

    private fun applyFilters(
        rawList: List<WorkoutRecordEntity>,
        filter: FilterState
    ): List<DisplayEntry> {
        val timeStart = when (filter.timeFilter) {
            "day" -> getStartOfDay()
            "week" -> getStartOfWeek()
            "month" -> getStartOfMonth()
            else -> 0L
        }
        return rawList
            .filter { it.date >= timeStart }
            .filter {
                filter.typeFilter == "all" || mapToDisplayType(it.type) == filter.typeFilter
            }
            .map { it.toDisplayEntry() }
    }

    private fun computeStats(
        rawList: List<WorkoutRecordEntity>,
        filter: FilterState
    ): WorkoutStats {
        val timeStart = when (filter.timeFilter) {
            "day" -> getStartOfDay()
            "week" -> getStartOfWeek()
            "month" -> getStartOfMonth()
            else -> 0L
        }
        val inRange = rawList.filter { it.date >= timeStart }
            .filter {
                filter.typeFilter == "all" || mapToDisplayType(it.type) == filter.typeFilter
            }

        // 计算7天图表
        val chartData = (0..6).map { dayOffset ->
            val dayStart = getStartOfDay() - dayOffset * 86400000L
            val dayEnd = dayStart + 86400000L
            rawList.filter { it.date in dayStart until dayEnd }
                .filter {
                    filter.typeFilter == "all" || mapToDisplayType(it.type) == filter.typeFilter
                }
                .sumOf { it.durationSeconds / 60 }
        }.reversed()

        return WorkoutStats(
            count = inRange.size,
            totalMinutes = inRange.sumOf { it.durationSeconds } / 60,
            totalCalories = inRange.sumOf { it.calories },
            chartData = chartData
        )
    }
}

// ---- 扩展 ----

private fun WorkoutRecordEntity.toDisplayEntry() = WorkoutRecordViewModel.DisplayEntry(
    type = mapToDisplayType(type),
    name = name,
    date = date,
    duration = durationSeconds,
    calories = calories,
    sets = sets,
    value = value
)

fun mapToDisplayType(type: String): String = when (type) {
    "pushup" -> "力量训练"
    "plank" -> "力量训练"
    "abwheel" -> "力量训练"
    "gym" -> "健身房"
    "跑步", "力量训练", "瑜伽", "有氧" -> type
    else -> "力量训练"
}

fun mapDisplayType(display: String): String = when (display) {
    "跑步" -> "跑步"
    "力量训练" -> "力量训练"
    "瑜伽" -> "瑜伽"
    "有氧" -> "有氧"
    else -> "力量训练"
}

fun getStartOfDay(): Long {
    val cal = Calendar.getInstance()
    cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
    return cal.timeInMillis
}

fun getStartOfWeek(): Long {
    val cal = Calendar.getInstance()
    cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
    cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
    return cal.timeInMillis
}

fun getStartOfMonth(): Long {
    val cal = Calendar.getInstance()
    cal.set(Calendar.DAY_OF_MONTH, 1)
    cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
    return cal.timeInMillis
}
