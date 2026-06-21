package com.example.fitforge.ui.screen

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitforge.ui.theme.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitforge.viewmodel.WorkoutViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PushupScreen(onBack: () -> Unit) {
    val workoutVM: WorkoutViewModel = viewModel()
    var count by remember { mutableIntStateOf(0) }
    var sets by remember { mutableIntStateOf(0) }
    var total by remember { mutableIntStateOf(0) }
    var maxInSet by remember { mutableIntStateOf(0) }
    var restSeconds by remember { mutableIntStateOf(-1) }
    var historyView by remember { mutableStateOf("list") }
    var historyPeriod by remember { mutableStateOf("all") }
    var showFlash by remember { mutableStateOf(false) }
    var showShake by remember { mutableStateOf(false) }

    val history by workoutVM.getHistory("pushup").collectAsState(initial = emptyList())

    if (restSeconds > 0) {
        LaunchedEffect(restSeconds) { delay(1000); restSeconds-- }
    } else if (restSeconds == 0) {
        restSeconds = -1
    }

    // 屏幕震动效果
    val shakeOffset = remember { Animatable(0f) }
    LaunchedEffect(showShake) {
        if (showShake) {
            val seq = floatArrayOf(-4f, 4f, -3f, 3f, -2f, 2f, -1f, 1f, 0f)
            for (o in seq) { shakeOffset.snapTo(o); delay(35) }
            shakeOffset.snapTo(0f)
            showShake = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDeep)
            .offset { IntOffset(shakeOffset.value.toInt(), 0) }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item { SubPageHeader(title = "PUSH-UP", onBack = onBack) }

            // Counter button with effects
            item {
                Spacer(modifier = Modifier.height(30.dp))
                CounterButton(
                    count = count,
                    color = NeonPink,
                    size = 130.dp,
                    modifier = Modifier,
                    onIncrement = {
                        count++
                        if (count > maxInSet) maxInSet = count
                        showFlash = true
                        showShake = true
                    }
                )
            }

            // Set stats
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MiniStatCard(value = sets.toString(), label = "Sets", color = NeonBlue, modifier = Modifier.weight(1f))
                    MiniStatCard(value = total.toString(), label = "Total", color = NeonPink, modifier = Modifier.weight(1f))
                    MiniStatCard(value = maxInSet.toString(), label = "Max", color = NeonBlue, modifier = Modifier.weight(1f))
                }
            }

            // Action button
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = {
                        if (count > 0) {
                            sets++; total += count
                            // Auto-save record on set completion
                            workoutVM.savePushup(totalReps = count, sets = 1)
                            count = 0
                            restSeconds = 60
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonBlue),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Finish Set", fontWeight = FontWeight.Medium) }
            }

            // My records
            item {
                Spacer(modifier = Modifier.height(20.dp))
                SectionTitle(icon = Icons.Default.DateRange, title = "My Records")
                Spacer(modifier = Modifier.height(12.dp))
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val bestReps = history.maxOfOrNull { it.value } ?: 0
                    val totalReps = history.sumOf { it.value }
                    val totalSets = history.size
                    MiniStatCard(value = "$bestReps", label = "Best", color = NeonPink, modifier = Modifier.weight(1f))
                    MiniStatCard(value = "$totalReps", label = "Total", color = NeonBlue, modifier = Modifier.weight(1f))
                    MiniStatCard(value = "$totalSets", label = "Sets", color = NeonBlue, modifier = Modifier.weight(1f))
                }
            }

            // History filter
            item {
                Spacer(modifier = Modifier.height(20.dp))
                SectionTitle(icon = Icons.Default.DateRange, title = "History")
                Spacer(modifier = Modifier.height(12.dp))
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterChip(
                        selected = historyView == "list",
                        onClick = { historyView = "list" },
                        label = { Text("\uD83D\uDCCB") },
                        modifier = Modifier.height(32.dp)
                    )
                    FilterChip(
                        selected = historyView == "chart",
                        onClick = { historyView = "chart" },
                        label = { Text("\uD83D\uDCCA") },
                        modifier = Modifier.height(32.dp)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    FilterChip(
                        selected = historyPeriod == "all",
                        onClick = { historyPeriod = "all" },
                        label = { Text("All", fontSize = 12.sp) },
                        modifier = Modifier.height(32.dp)
                    )
                    FilterChip(
                        selected = historyPeriod == "week",
                        onClick = { historyPeriod = "week" },
                        label = { Text("Week", fontSize = 12.sp) },
                        modifier = Modifier.height(32.dp)
                    )
                    FilterChip(
                        selected = historyPeriod == "month",
                        onClick = { historyPeriod = "month" },
                        label = { Text("Month", fontSize = 12.sp) },
                        modifier = Modifier.height(32.dp)
                    )
                }
            }

            // History content
            item {
                Card(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = GlassBackground),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    val filtered = filterRecords(history, historyPeriod)
                    if (filtered.isEmpty()) {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text("No Records", color = TextSecondary, fontSize = 14.sp)
                        }
                    } else if (historyView == "chart") {
                        ChartView(records = filtered, barColors = listOf(NeonPink, NeonOrange), unit = "reps")
                    } else {
                        Column {
                            filtered.take(20).forEach { record ->
                                val dateStr = SimpleDateFormat("M/d HH:mm", Locale.getDefault())
                                    .format(Date(record.date))
                                HistoryRow(
                                    label = "${record.sets} sets - $dateStr",
                                    value = "${record.value} reps",
                                    onDelete = { workoutVM.deleteRecord(record.id) }
                                )
                            }
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(30.dp)) }
        }

        // Rest overlay
        if (restSeconds >= 0) {
            RestOverlay(seconds = restSeconds, onSkip = { restSeconds = -1 })
        }
        // Screen flash
        if (showFlash) {
            ScreenFlash(visible = showFlash, onDone = { showFlash = false })
        }
    }
}


