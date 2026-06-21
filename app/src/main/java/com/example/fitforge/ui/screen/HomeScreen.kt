package com.example.fitforge.ui.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitforge.ui.theme.*
import com.example.fitforge.viewmodel.HomeViewModel
import com.example.fitforge.data.model.*

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onNavigateToPlank: () -> Unit = {},
    onNavigateToPushup: () -> Unit = {},
    onNavigateToAbwheel: () -> Unit = {},
    onNavigateToExercise: () -> Unit = {},
    onNavigateToWorkout: () -> Unit = {},
    onNavigateToGymExec: (String) -> Unit = {}
) {
    val viewModel: HomeViewModel = viewModel()
    val todayStats by viewModel.todayStats.collectAsState()
    val personalBest by viewModel.personalBest.collectAsState()
    val weeklyStats by viewModel.weeklyStats.collectAsState()
    val recommendedTarget by viewModel.recommendedTarget.collectAsState()
    val selectedTarget by viewModel.selectedTarget.collectAsState()
    val recentWorkouts by viewModel.recentWorkouts.collectAsState()
    val gymPlans by viewModel.gymPlans.collectAsState()
    val stepTrend by viewModel.stepTrend.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDeep)
            .padding(bottom = 70.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            GymStartCard(
                recommendedTarget = recommendedTarget,
                selectedTarget = selectedTarget,
                gymPlans = gymPlans,
                onSelectTarget = { target ->
                    if (target == "recommend") {
                        viewModel.selectRecommended()
                    } else {
                        viewModel.selectTarget(TrainingTarget.values().find {
                            it.name.lowercase() == target.lowercase()
                        } ?: TrainingTarget.BACK)
                    }
                },
                onStartWorkout = {
                    val target = (selectedTarget ?: recommendedTarget)
                    onNavigateToGymExec(target.name.lowercase())
                }
            )
        }

        item {
            TodayStatsBar(
                steps = todayStats.steps,
                stepGoal = todayStats.stepGoal,
                exerciseMinutes = todayStats.exerciseMinutes,
                caloriesBurned = todayStats.caloriesBurned,
                stepProgress = viewModel.stepProgress
            )
        }

        item {
            WeeklySummaryCard(weeklyStats = weeklyStats)
        }

        // 步数周趋势
        item {
            StepTrendSection(
                steps = stepTrend,
                stepGoal = todayStats.stepGoal
            )
        }

        item {
            QuickPersonalBestSection(
                pushupMax = personalBest.pushupMax,
                plankMax = personalBest.plankMax,
                abWheelMax = personalBest.abWheelMax,
                onNavigateToPlank = onNavigateToPlank,
                onNavigateToPushup = onNavigateToPushup,
                onNavigateToAbwheel = onNavigateToAbwheel,
                onNavigateToExercise = onNavigateToExercise
            )
        }

        item {
            RecentWorkoutSection(
                recentWorkouts = recentWorkouts,
                onViewAll = onNavigateToWorkout
            )
        }
    }
}

@Composable
fun GymStartCard(
    recommendedTarget: TrainingTarget,
    selectedTarget: TrainingTarget?,
    gymPlans: List<GymPlan>,
    onSelectTarget: (String) -> Unit,
    onStartWorkout: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable {
                if (!isExpanded) isExpanded = true
            },
        colors = CardDefaults.cardColors(
            containerColor = GlassBackground
        ),
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FitnessCenter,
                        contentDescription = null,
                        tint = NeonPink,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = if (selectedTarget == null) {
                            "⭐ 推荐：${recommendedTarget.displayName} →"
                        } else {
                            "${selectedTarget.displayName} →"
                        },
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                IconButton(
                    onClick = { isExpanded = !isExpanded }
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "展开",
                        tint = TextSecondary
                    )
                }
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "选择今日训练目标",
                    color = TextSecondary,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        TargetButton(
                            text = "⭐ 推荐",
                            isSelected = selectedTarget == null,
                            isRecommend = true,
                            onClick = { onSelectTarget("recommend") }
                        )
                    }
                    items(TrainingTarget.values()) { target ->
                        TargetButton(
                            text = target.displayName,
                            isSelected = selectedTarget == target,
                            isRecommend = false,
                            onClick = { onSelectTarget(target.name) }
                        )
                    }
                }

                val currentPlan = gymPlans.find {
                    it.target == (selectedTarget ?: recommendedTarget)
                }

                currentPlan?.let { plan ->
                    Spacer(modifier = Modifier.height(12.dp))

                    plan.steps.take(4).forEach { step ->
                        StepPreviewItem(step = step)
                    }

                    Text(
                        text = "... 共${plan.steps.size}个动作",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onStartWorkout,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonPink
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = "开始训练",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun TargetButton(
    text: String,
    isSelected: Boolean,
    isRecommend: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = when {
            isRecommend && isSelected -> NeonPurple
            isSelected -> NeonPink
            else -> GlassBackground
        }
    )

    val borderColor = when {
        isRecommend && isSelected -> Color.Transparent
        isSelected -> Color.Transparent
        else -> GlassBorder
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 7.dp)
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.White else TextSecondary,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun StepPreviewItem(step: GymStep) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(
                    when (step.phase) {
                        TrainingPhase.WARMUP -> NeonOrange.copy(alpha = 0.2f)
                        TrainingPhase.MAIN -> NeonPink.copy(alpha = 0.2f)
                        TrainingPhase.STRETCH -> NeonGreen.copy(alpha = 0.2f)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = when (step.phase) {
                    TrainingPhase.WARMUP -> "▴"
                    TrainingPhase.MAIN -> "●"
                    TrainingPhase.STRETCH -> "▾"
                },
                fontSize = 10.sp,
                color = when (step.phase) {
                    TrainingPhase.WARMUP -> NeonOrange
                    TrainingPhase.MAIN -> NeonPink
                    TrainingPhase.STRETCH -> NeonGreen
                }
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = step.name,
                color = TextPrimary,
                fontSize = 14.sp
            )
            Text(
                text = step.detail,
                color = TextSecondary,
                fontSize = 12.sp
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(
                    when (step.phase) {
                        TrainingPhase.WARMUP -> NeonOrange.copy(alpha = 0.15f)
                        TrainingPhase.MAIN -> NeonPink.copy(alpha = 0.15f)
                        TrainingPhase.STRETCH -> NeonGreen.copy(alpha = 0.15f)
                    }
                )
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = step.phase.displayName,
                color = when (step.phase) {
                    TrainingPhase.WARMUP -> NeonOrange
                    TrainingPhase.MAIN -> NeonPink
                    TrainingPhase.STRETCH -> NeonGreen
                },
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun TodayStatsBar(
    steps: Int,
    stepGoal: Int,
    exerciseMinutes: Int,
    caloriesBurned: Int,
    stepProgress: Float
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = GlassBackground
        ),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = steps.toString(),
                    color = NeonPink,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Text(
                    text = "步数",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(GlassBackground)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(stepProgress)
                            .background(
                                brush = Brush.horizontalGradient(GradientPinkBlue)
                            )
                    )
                }
            }

            DividerVertical()

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = exerciseMinutes.toString(),
                        color = NeonBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Text(
                        text = "min",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
                Text(
                    text = "运动",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }

            DividerVertical()

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${caloriesBurned}kcal",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Text(
                    text = "消耗",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun DividerVertical() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(32.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        GlassBorder,
                        Color.Transparent
                    )
                )
            )
    )
}

@Composable
fun WeeklySummaryCard(
    weeklyStats: com.example.fitforge.viewmodel.WeeklyStats
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = GlassBackground),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            WeeklyStatItem(
                icon = "\uD83D\uDCC5", // 📅
                value = "${weeklyStats.activeDays}",
                label = "天",
                color = NeonBlue
            )
            DividerVertical()
            WeeklyStatItem(
                icon = "\u23F1\uFE0F", // ⏱️
                value = "${weeklyStats.totalMinutes}",
                label = "min",
                color = NeonPink
            )
            DividerVertical()
            WeeklyStatItem(
                icon = "\uD83D\uDD25", // 🔥
                value = "${weeklyStats.totalCalories}",
                label = "kcal",
                color = NeonOrange
            )
        }
        // 标题栏
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(NeonBlue.copy(alpha = 0.08f), NeonPink.copy(alpha = 0.08f))
                    )
                )
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "本周运动",
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "周一起算",
                color = TextSecondary,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun WeeklyStatItem(
    icon: String,
    value: String,
    label: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = icon, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = value,
                color = color,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Text(
                text = label,
                color = TextSecondary,
                fontSize = 11.sp,
                modifier = Modifier.padding(bottom = 2.dp, start = 2.dp)
            )
        }
    }
}

@Composable
fun QuickPersonalBestSection(
    pushupMax: Int,
    plankMax: String,
    abWheelMax: Int,
    onNavigateToPlank: () -> Unit,
    onNavigateToPushup: () -> Unit,
    onNavigateToAbwheel: () -> Unit,
    onNavigateToExercise: () -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Text(
            text = "快捷训练",
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 3 个有个人最佳的运动卡片——合并了"最高记录"和"快捷入口"
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickPBCard(
                icon = Icons.Default.Timer,
                label = "平板支撑",
                best = plankMax,
                unit = "分/秒",
                color = NeonOrange,
                onClick = onNavigateToPlank,
                modifier = Modifier.weight(1f)
            )
            QuickPBCard(
                icon = Icons.Default.FitnessCenter,
                label = "俯卧撑",
                best = "${pushupMax}次/组",
                unit = "",
                color = NeonPink,
                onClick = onNavigateToPushup,
                modifier = Modifier.weight(1f)
            )
            QuickPBCard(
                icon = Icons.Default.Loop,
                label = "腹肌轮",
                best = "${abWheelMax}次/组",
                unit = "",
                color = NeonGreen,
                onClick = onNavigateToAbwheel,
                modifier = Modifier.weight(1f)
            )
        }

        // 动作库入口独立一行
        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onNavigateToExercise),
            colors = CardDefaults.cardColors(containerColor = GlassBackground),
            shape = RoundedCornerShape(14.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayCircle,
                    contentDescription = "动作库",
                    tint = NeonPurple,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "动作库",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "→",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
fun QuickPBCard(
    icon: ImageVector,
    label: String,
    best: String,
    unit: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = GlassBackground),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = label,
                color = TextPrimary,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (best == "0" || best == "0次/组") "—" else best,
                color = color.copy(alpha = 0.85f),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            if (unit.isNotEmpty()) {
                Text(
                    text = unit,
                    color = TextSecondary,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
fun RecentWorkoutSection(
    recentWorkouts: List<RecentWorkout>,
    onViewAll: () -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "最近训练",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            TextButton(onClick = onViewAll) {
                Text(
                    text = "查看全部 →",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        recentWorkouts.forEach { workout ->
            RecentWorkoutItem(workout = workout)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun RecentWorkoutItem(workout: RecentWorkout) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = GlassBackground
        ),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        try {
                            Color(android.graphics.Color.parseColor(workout.color)).copy(alpha = 0.2f)
                        } catch (e: Exception) {
                            NeonPink.copy(alpha = 0.2f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = workout.icon,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = workout.name,
                    color = TextPrimary,
                    fontSize = 15.sp
                )
                Text(
                    text = workout.meta,
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }

            Text(
                text = "${workout.calories}",
                color = try {
                    Color(android.graphics.Color.parseColor(workout.color))
                } catch (e: Exception) {
                    NeonPink
                },
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )
            Text(
                text = "kcal",
                color = TextSecondary,
                fontSize = 10.sp,
                modifier = Modifier.padding(start = 2.dp)
            )
        }
    }
}

@Composable
fun StepTrendSection(steps: List<Int>, stepGoal: Int) {
    val days = listOf("一", "二", "三", "四", "五", "六", "日")
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = GlassBackground),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("步数趋势", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Text("目标 $stepGoal", color = TextSecondary, fontSize = 11.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                val maxVal = (steps.maxOrNull() ?: stepGoal).coerceAtLeast(stepGoal)
                steps.forEachIndexed { index, value ->
                    val height = (value.toFloat() / maxVal * 48f).coerceAtLeast(if (value > 0) 4f else 2f)
                    val isAboveGoal = value >= stepGoal
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .width(16.dp)
                                .height(height.dp)
                                .clip(RoundedCornerShape(3.dp, 3.dp, 0.dp, 0.dp))
                                .background(
                                    if (isAboveGoal) Brush.verticalGradient(listOf(NeonGreen, NeonBlue))
                                    else Brush.verticalGradient(listOf(NeonPink, NeonBlue.copy(alpha = 0.5f)))
                                )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(days[index], color = TextSecondary, fontSize = 9.sp)
                    }
                }
            }
        }
    }
}
