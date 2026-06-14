package com.example.listenapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.listenapp.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Calendar
import java.util.Date

class HomeViewModel : ViewModel() {

    private val _todayStats = MutableStateFlow(
        TodayStats(steps = 8542, stepGoal = 10000, exerciseMinutes = 30, caloriesBurned = 320)
    )
    val todayStats: StateFlow<TodayStats> = _todayStats.asStateFlow()

    private val _personalBest = MutableStateFlow(
        PersonalBest(pushupMax = 68, plankMax = "2'15\"", abWheelMax = 42)
    )
    val personalBest: StateFlow<PersonalBest> = _personalBest.asStateFlow()

    private val _weekCheckIn = MutableStateFlow(WeekCheckIn())
    val weekCheckIn: StateFlow<WeekCheckIn> = _weekCheckIn.asStateFlow()

    private val _selectedTarget = MutableStateFlow<TrainingTarget?>(null)
    val selectedTarget: StateFlow<TrainingTarget?> = _selectedTarget.asStateFlow()

    private val _recommendedTarget = MutableStateFlow(getRecommendTargetForToday())
    val recommendedTarget: StateFlow<TrainingTarget> = _recommendedTarget.asStateFlow()

    private val _recentWorkouts = MutableStateFlow(
        listOf(
            RecentWorkout(icon = "🏋️", name = "俯卧撑", detail = "4组 × 12次", timeAgo = "今天 08:30"),
            RecentWorkout(icon = "🧘", name = "平板支撑", detail = "2组 × 45秒", timeAgo = "昨天 19:00"),
            RecentWorkout(icon = "🎯", name = "健腹轮", detail = "3组 × 8次", timeAgo = "前天 18:30"),
        )
    )
    val recentWorkouts: StateFlow<List<RecentWorkout>> = _recentWorkouts.asStateFlow()

    private val _gymPlans = MutableStateFlow(getGymPlans())
    val gymPlans: StateFlow<List<GymPlan>> = _gymPlans.asStateFlow()

    val stepProgress: Float
        get() = (_todayStats.value.steps.toFloat() / _todayStats.value.stepGoal).coerceAtMost(1f)

    fun selectTarget(target: TrainingTarget) {
        _selectedTarget.update { target }
    }

    fun checkIn() {
        val today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1
        _weekCheckIn.update { it.copy(days = it.days.toMutableList().also { d -> d[today] = true }) }
    }
}

fun getRecommendTargetForToday(): TrainingTarget {
    val today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
    return when (today) {
        Calendar.MONDAY -> TrainingTarget("上肢力量", "俯卧撑 4组", "完成4组俯卧撑，组间休息60秒", 4, 12, 60)
        Calendar.TUESDAY -> TrainingTarget("核心训练", "平板支撑 3组", "完成3组平板支撑，组间休息90秒", 3, 0, 90)
        Calendar.WEDNESDAY -> TrainingTarget("上肢力量", "健腹轮 3组", "完成3组健腹轮，每组8-10次", 3, 10, 90)
        Calendar.THURSDAY -> TrainingTarget("综合训练", "俯卧撑+平板支撑", "循环训练：俯卧撑→平板支撑→休息", 3, 0, 60)
        Calendar.FRIDAY -> TrainingTarget("上肢力量", "俯卧撑 5组", "挑战日：完成5组俯卧撑", 5, 15, 60)
        else -> TrainingTarget("休息日", "拉伸放松", "适度拉伸，让肌肉恢复", 1, 0, 0)
    }
}

fun getGymPlans(): List<GymPlan> {
    return listOf(
        GymPlan("上肢力量", "增强上肢力量，提升核心稳定性", listOf("俯卧撑", "平板支撑", "健腹轮"), 12, 45, 4),
        GymPlan("核心训练", "强化核心肌群，改善身体稳定性", listOf("平板支撑", "仰卧起坐", "俄罗斯转体"), 10, 30, 3),
        GymPlan("全身训练", "全身性功能性训练，提升整体素质", listOf("俯卧撑", "深蹲", "平板支撑", "健腹轮"), 16, 60, 5),
    )
}

fun getIconForType(type: String): String {
    return when (type) {
        "pushup" -> "🏋️"
        "plank" -> "🧘"
        "abwheel" -> "🎯"
        else -> "💪"
    }
}

fun formatSeconds(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return "${m}'${s.toString().padStart(2, '0')}\""
}

fun getStartOfDay(): Date {
    val cal = Calendar.getInstance()
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    return cal.time
}
