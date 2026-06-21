package com.example.fitforge.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.RunCircle
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitforge.ui.theme.*
import com.example.fitforge.viewmodel.WorkoutRecordViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WorkoutScreen() {
    val viewModel: WorkoutRecordViewModel = viewModel()
    val filterState by viewModel.filterState.collectAsState()
    val stats by viewModel.stats.collectAsState()
    val entries by viewModel.entries.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { viewModel.refresh() }

    Box(modifier = Modifier.fillMaxSize().background(BackgroundDeep)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { PageHeader(title = "WORKOUT") }
            item {
                TimeFilterBar(
                    selected = filterState.timeFilter,
                    onSelect = { viewModel.setTimeFilter(it) }
                )
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatItem(value = "${stats.count}", label = "运动次数", modifier = Modifier.weight(1f))
                    StatItem(value = "${stats.totalMinutes / 60f}h", label = "运动时长", modifier = Modifier.weight(1f), isAccent = true)
                    StatItem(value = "%,d".format(stats.totalCalories), label = "消耗(kcal)", modifier = Modifier.weight(1f))
                }
            }
            item { ChartSection(chartData = stats.chartData) }
            item {
                TypeFilterBar(
                    selected = filterState.typeFilter,
                    onSelect = { viewModel.setTypeFilter(it) }
                )
            }
            if (entries.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("暂无运动记录", color = TextSecondary, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("完成训练后会自动记录，或点击右下角 + 手动添加", color = TextSecondary.copy(alpha = 0.5f), fontSize = 12.sp)
                        }
                    }
                }
            } else {
                items(entries) { entry -> WorkoutCard(entry = entry) }
            }
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }

        // FAB
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
            containerColor = NeonPink
        ) {
            Icon(Icons.Default.Add, contentDescription = "添加", tint = Color.White)
        }
    }

    if (showAddDialog) {
        AddWorkoutDialog(
            onDismiss = { showAddDialog = false },
            onSave = { type, name, duration, calories ->
                viewModel.addManualRecord(type, name, duration, calories)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun AddWorkoutDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, Int, Int) -> Unit
) {
    val types = listOf("跑步", "力量训练", "瑜伽", "有氧")
    var selectedType by remember { mutableStateOf(types[0]) }
    var durationText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BackgroundMid,
        title = { Text("添加运动记录", color = TextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("运动类型", color = TextSecondary, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    types.forEach { t ->
                        TextChip(
                            text = t,
                            isSelected = selectedType == t,
                            onClick = { selectedType = t }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = durationText,
                    onValueChange = { durationText = it },
                    label = { Text("时长（分钟）") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonBlue, focusedLabelColor = NeonBlue,
                        unfocusedLabelColor = TextSecondary, cursorColor = NeonBlue
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val mins = durationText.toIntOrNull() ?: 0
                    if (mins > 0) {
                        onSave(selectedType, selectedType, mins, mins * 6)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                shape = RoundedCornerShape(14.dp)
            ) { Text("保存") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("取消", color = TextSecondary) }
        }
    )
}

@Composable
fun TimeFilterBar(selected: String, onSelect: (String) -> Unit) {
    val filters = listOf("day" to "今日", "week" to "本周", "month" to "本月")
    LazyRow(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filters) { (key, label) ->
            TextChip(text = label, isSelected = selected == key, onClick = { onSelect(key) })
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
            TextChip(text = label, isSelected = selected == key, onClick = { onSelect(key) })
        }
    }
}

@Composable
fun StatItem(value: String, label: String, modifier: Modifier = Modifier, isAccent: Boolean = false) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = GlassBackground),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = value, color = if (isAccent) NeonBlue else NeonPink,
                fontWeight = FontWeight.Bold, fontSize = 22.sp
            )
            Text(text = label, color = TextSecondary, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
        }
    }
}

@Composable
fun ChartSection(chartData: List<Int>) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text("运动趋势", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(12.dp))
        Card(colors = CardDefaults.cardColors(containerColor = GlassBackground), shape = RoundedCornerShape(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(20.dp, 10.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                val labels = listOf("一", "二", "三", "四", "五", "六", "日")
                val barColors = listOf(NeonPink, NeonBlue, NeonPurple, NeonGreen, NeonOrange, NeonBlue, NeonPink)
                val maxVal = (chartData.maxOrNull() ?: 1).coerceAtLeast(1)

                chartData.forEachIndexed { index, value ->
                    val height = (value.toFloat() / maxVal * 70f).coerceAtLeast(if (value > 0) 6f else 2f)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier.width(20.dp).height(height.dp)
                                .clip(RoundedCornerShape(4.dp, 4.dp, 0.dp, 0.dp))
                                .background(barColors[index])
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = labels[index], color = TextSecondary, fontSize = 10.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun WorkoutCard(entry: WorkoutRecordViewModel.DisplayEntry) {
    val icons = mapOf(
        "跑步" to Icons.Default.RunCircle,
        "力量训练" to Icons.Default.FitnessCenter,
        "瑜伽" to Icons.Default.SelfImprovement,
        "有氧" to Icons.Default.Whatshot,
        "健身房" to Icons.Default.FitnessCenter
    )
    val colors = mapOf(
        "跑步" to NeonBlue, "力量训练" to NeonPink, "瑜伽" to NeonPurple, "有氧" to NeonOrange, "健身房" to NeonPink
    )

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
                        modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp))
                            .background((colors[entry.type] ?: NeonPink).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icons[entry.type] ?: Icons.Default.FitnessCenter,
                            contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(entry.name, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                        Text(
                            SimpleDateFormat("M/d HH:mm", Locale.getDefault()).format(Date(entry.date)),
                            color = TextSecondary, fontSize = 12.sp
                        )
                    }
                }
                Text("完成", color = NeonGreen, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("时长 ${entry.duration / 60}分钟", color = TextSecondary, fontSize = 12.sp)
                if (entry.sets > 0) Text("${entry.sets}组", color = TextSecondary, fontSize = 12.sp)
                Text("消耗 ${entry.calories}kcal", color = TextSecondary, fontSize = 12.sp)
            }
        }
    }
}
