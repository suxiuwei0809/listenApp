package com.example.listenapp.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.RunCircle
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.listenapp.data.model.WorkoutType
import com.example.listenapp.ui.theme.*

data class WorkoutEntry(
    val type: WorkoutType,
    val name: String,
    val date: String,
    val duration: Int,
    val calories: Int,
    val distance: String? = null,
    val sets: Int? = null
)

@Composable
fun WorkoutScreen() {
    var selectedTimeFilter by remember { mutableStateOf("week") }
    var selectedTypeFilter by remember { mutableStateOf("all") }

    val sampleWorkouts = listOf(
        WorkoutEntry(WorkoutType.STRENGTH, "练背日", "周二 19:30", 45, 480),
        WorkoutEntry(WorkoutType.STRENGTH, "练胸日", "周一 07:15", 40, 320),
        WorkoutEntry(WorkoutType.RUNNING, "晨跑", "上周日 06:30", 30, 280, "5.2km"),
        WorkoutEntry(WorkoutType.CARDIO, "减脂HIIT", "上周六 18:20", 25, 280),
        WorkoutEntry(WorkoutType.YOGA, "瑜伽放松", "上周五 20:00", 60, 180)
    )

    val filteredWorkouts = if (selectedTypeFilter == "all") sampleWorkouts
    else sampleWorkouts.filter { it.type.displayName == selectedTypeFilter }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDeep),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            PageHeader(title = "WORKOUT")
        }

        item {
            TimeFilterBar(
                selected = selectedTimeFilter,
                onSelect = { selectedTimeFilter = it }
            )
        }

        item {
            StatsGrid()
        }

        item {
            ChartSection()
        }

        item {
            TypeFilterBar(
                selected = selectedTypeFilter,
                onSelect = { selectedTypeFilter = it }
            )
        }

        items(filteredWorkouts) { workout ->
            WorkoutCard(workout = workout)
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
fun PageHeader(title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(
                    colors = listOf(NeonPink.copy(alpha = 0.15f), NeonBlue.copy(alpha = 0.15f))
                )
            )
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
    }
}

@Composable
fun TimeFilterBar(selected: String, onSelect: (String) -> Unit) {
    val filters = listOf("day" to "今日", "week" to "本周", "month" to "本月")
    LazyRow(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filters) { (key, label) ->
            FilterChip(
                text = label,
                isSelected = selected == key,
                onClick = { onSelect(key) }
            )
        }
    }
}

@Composable
fun TypeFilterBar(selected: String, onSelect: (String) -> Unit) {
    val filters = listOf("all" to "全部", "跑步" to "跑步", "力量训练" to "力量训练", "瑜伽" to "瑜伽", "有氧" to "有氧")
    LazyRow(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filters) { (key, label) ->
            FilterChip(
                text = label,
                isSelected = selected == key,
                onClick = { onSelect(key) }
            )
        }
    }
}

@Composable
fun FilterChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) NeonPink else GlassBackground)
            .border(
                width = 1.dp,
                color = if (isSelected) Color.Transparent else GlassBorder,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 7.dp)
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.White else TextSecondary,
            fontSize = 13.sp
        )
    }
}

@Composable
fun StatsGrid() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatItem(value = "8", label = "运动次数", modifier = Modifier.weight(1f))
        StatItem(value = "5.5h", label = "运动时长", modifier = Modifier.weight(1f), isAccent = true)
        StatItem(value = "1,890", label = "消耗(kcal)", modifier = Modifier.weight(1f))
    }
}

@Composable
fun StatItem(value: String, label: String, modifier: Modifier = Modifier, isAccent: Boolean = false) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = GlassBackground),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                color = if (isAccent) NeonBlue else NeonPink,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp
            )
            Text(
                text = label,
                color = TextSecondary,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun ChartSection() {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = "运动趋势",
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(12.dp))
        Card(
            colors = CardDefaults.cardColors(containerColor = GlassBackground),
            shape = RoundedCornerShape(14.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp, 10.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                val bars = listOf(40, 60, 30, 80, 70, 90, 50)
                val labels = listOf("一", "二", "三", "四", "五", "六", "日")
                val colors = listOf(NeonPink, NeonBlue, NeonPurple, NeonGreen, NeonOrange, NeonBlue, NeonPink)

                bars.forEachIndexed { index, height ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .width(20.dp)
                                .height(height.dp)
                                .clip(RoundedCornerShape(4.dp, 4.dp, 0.dp, 0.dp))
                                .background(colors[index])
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = labels[index],
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WorkoutCard(workout: WorkoutEntry) {
    Card(
        modifier = Modifier.padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = GlassBackground),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(15.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(NeonPink.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (workout.type) {
                                WorkoutType.RUNNING -> Icons.Default.RunCircle
                                WorkoutType.YOGA -> Icons.Default.SelfImprovement
                                else -> Icons.Default.FitnessCenter
                            },
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = workout.name,
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = workout.date,
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
                Text(
                    text = "完成",
                    color = NeonGreen,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "时长 ${workout.duration}分钟",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                workout.distance?.let {
                    Text(
                        text = "距离 $it",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
                Text(
                    text = "消耗 ${workout.calories}kcal",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }
    }
}
