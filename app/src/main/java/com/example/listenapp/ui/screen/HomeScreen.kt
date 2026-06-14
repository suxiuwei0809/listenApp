package com.example.listenapp.ui.screen

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
import com.example.listenapp.ui.theme.*
import com.example.listenapp.viewmodel.HomeViewModel
import com.example.listenapp.data.model.*

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onNavigateToPlank: () -> Unit = {},
    onNavigateToPushup: () -> Unit = {},
    onNavigateToAbwheel: () -> Unit = {},
    onNavigateToExercise: () -> Unit = {},
    onNavigateToWorkout: () -> Unit = {}
) {
    val viewModel: HomeViewModel = viewModel()
    val todayStats by viewModel.todayStats.collectAsState()
    val personalBest by viewModel.personalBest.collectAsState()
    val weekCheckIn by viewModel.weekCheckIn.collectAsState()
    val recommendedTarget by viewModel.recommendedTarget.collectAsState()
    val selectedTarget by viewModel.selectedTarget.collectAsState()
    val recentWorkouts by viewModel.recentWorkouts.collectAsState()
    val gymPlans by viewModel.gymPlans.collectAsState()

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
                onStartWorkout = onNavigateToWorkout
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
            WeekCheckInBar(weekCheckIn = weekCheckIn)
        }

        item {
            PersonalBestSection(
                pushupMax = personalBest.pushupMax,
                plankMax = personalBest.plankMax,
                abWheelMax = personalBest.abWheelMax
            )
        }

        item {
            QuickAccessSection(
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
                            "★ 推荐：${recommendedTarget.displayName} →"
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
                            text = "★ 推荐",
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
                    TrainingPhase.WARMUP -> "▲"
                    TrainingPhase.MAIN -> "◆"
                    TrainingPhase.STRETCH -> "▼"
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
fun WeekCheckInBar(weekCheckIn: WeekCheckIn) {
    val days = listOf("一", "二", "三", "四", "五", "六", "日")
    val completedList = listOf(
        weekCheckIn.monday,
        weekCheckIn.tuesday,
        weekCheckIn.wednesday,
        weekCheckIn.thursday,
        weekCheckIn.friday,
        weekCheckIn.saturday,
        weekCheckIn.sunday
    )

    val todayIndex = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_WEEK) - 2

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        days.forEachIndexed { index, day ->
            val isCompleted = completedList[index]
            val isToday = index == todayIndex
            val isFuture = index > todayIndex

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isCompleted -> NeonPink
                                isToday -> NeonPink.copy(alpha = 0.3f)
                                else -> GlassBackground
                            }
                        )
                        .border(
                            width = if (isToday) 1.dp else 0.dp,
                            color = if (isToday) NeonPink else Color.Transparent,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = day,
                        color = if (isCompleted) Color.White else TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Text(
            text = "本周 ${weekCheckIn.getCompletedDays()}/7 天",
            color = TextSecondary,
            fontSize = 12.sp
        )
    }
}

@Composable
fun PersonalBestSection(
    pushupMax: Int,
    plankMax: String,
    abWheelMax: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        PersonalBestCard(
            value = pushupMax.toString(),
            unit = "次/组",
            label = "俯卧撑最高",
            color = NeonPink,
            modifier = Modifier.weight(1f)
        )
        PersonalBestCard(
            value = plankMax,
            unit = "分:秒",
            label = "平板最长",
            color = NeonOrange,
            modifier = Modifier.weight(1f)
        )
        PersonalBestCard(
            value = abWheelMax.toString(),
            unit = "次/组",
            label = "腹肌轮最高",
            color = NeonGreen,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun PersonalBestCard(
    value: String,
    unit: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = GlassBackground
        ),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                color = color,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )
            Text(
                text = unit,
                color = TextSecondary,
                fontSize = 10.sp
            )
            Text(
                text = label,
                color = TextSecondary,
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun QuickAccessSection(
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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickAccessButton(
                icon = Icons.Default.Timer,
                label = "平板支撑",
                color = NeonPink,
                onClick = onNavigateToPlank,
                modifier = Modifier.weight(1f)
            )
            QuickAccessButton(
                icon = Icons.Default.FitnessCenter,
                label = "俯卧撑",
                color = NeonOrange,
                onClick = onNavigateToPushup,
                modifier = Modifier.weight(1f)
            )
            QuickAccessButton(
                icon = Icons.Default.Loop,
                label = "腹肌轮",
                color = NeonGreen,
                onClick = onNavigateToAbwheel,
                modifier = Modifier.weight(1f)
            )
            QuickAccessButton(
                icon = Icons.Default.PlayCircle,
                label = "动作库",
                color = NeonPurple,
                onClick = onNavigateToExercise,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun QuickAccessButton(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = GlassBackground
        ),
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
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = label,
                color = TextPrimary,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
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
