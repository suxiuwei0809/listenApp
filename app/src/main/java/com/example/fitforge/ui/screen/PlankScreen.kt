package com.example.fitforge.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitforge.data.static.EncouragementLibrary
import com.example.fitforge.ui.theme.*
import com.example.fitforge.viewmodel.WorkoutViewModel
import kotlinx.coroutines.delay

@Composable
fun PlankScreen(onBack: () -> Unit) {
    val workoutVM: WorkoutViewModel = viewModel()
    var isRunning by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }
    var elapsedSeconds by remember { mutableIntStateOf(0) }
    var encouragement by remember { mutableStateOf("准备好了挑战自己了吗？") }
    var historyView by remember { mutableStateOf("list") }
    var historyPeriod by remember { mutableStateOf("all") }

    val encouragements = EncouragementLibrary.plank
    val history by workoutVM.getHistory("plank").collectAsState(initial = emptyList())
    val bestDuration = history.maxOfOrNull { it.value } ?: 0
    val totalSessions = history.size
    val totalDuration = history.sumOf { it.value }

    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(1000)
            elapsedSeconds++
            if (elapsedSeconds % 10 == 0) {
                encouragement = encouragements.random()
            }
        }
    }

    // 完成时保存并重置
    fun saveAndReset() {
        if (elapsedSeconds > 0) {
            workoutVM.savePlank(elapsedSeconds)
        }
        isRunning = false
        isPaused = false
        elapsedSeconds = 0
        encouragement = "准备好了挑战自己了吗？"
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDeep),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            SubPageHeader(title = "PLANK", onBack = onBack)
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
            Box(
                modifier = Modifier.size(220.dp),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.foundation.Canvas(modifier = Modifier.size(220.dp)) {
                    val strokeWidth = 8.dp.toPx()
                    val radius = size.minDimension / 2 - strokeWidth / 2
                    val center = Offset(size.width / 2, size.height / 2)

                    // Background ring
                    drawCircle(
                        color = Color.White.copy(alpha = 0.06f),
                        radius = radius,
                        center = center,
                        style = Stroke(width = strokeWidth)
                    )

                    // Progress ring
                    val progress = if (isRunning) {
                        (elapsedSeconds % 60) / 60f
                    } else 0f

                    if (progress > 0) {
                        drawArc(
                            brush = Brush.sweepGradient(
                                colors = listOf(NeonBlue, NeonPurple, NeonGreen, NeonBlue)
                            ),
                            startAngle = -90f,
                            sweepAngle = progress * 360f,
                            useCenter = false,
                            topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                            size = Size(size.width - strokeWidth, size.height - strokeWidth),
                            style = Stroke(width = strokeWidth)
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = formatTime(elapsedSeconds),
                        color = if (isRunning) NeonBlue else TextPrimary,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Light
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
            Card(
                modifier = Modifier.padding(horizontal = 40.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isPaused) NeonOrange.copy(alpha = 0.15f) else NeonBlue.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isPaused) NeonOrange.copy(alpha = 0.3f) else NeonBlue.copy(alpha = 0.2f))
            ) {
                Text(
                    text = if (isPaused) "⏸ Paused · ${formatTime(elapsedSeconds)}" else encouragement,
                    color = if (isPaused) NeonOrange else NeonBlue,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(14.dp, 22.dp)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Complete button: save and reset
                IconButton(
                    onClick = { saveAndReset() },
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(
                            if (elapsedSeconds > 0) NeonGreen.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.1f)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = "Finish",
                        tint = if (elapsedSeconds > 0) NeonGreen else TextSecondary,
                        modifier = Modifier.size(28.dp)
                    )
                }
                // Pause/Continue button: toggle timer state
                IconButton(
                    onClick = {
                        if (isRunning) { isRunning = false; isPaused = true }
                        else { isRunning = true; isPaused = false }
                    },
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(
                            if (isRunning) Brush.horizontalGradient(listOf(NeonOrange, NeonPink))
                            else Brush.horizontalGradient(listOf(NeonBlue, NeonGreen))
                        )
                ) {
                    Icon(
                        imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isRunning) "Pause" else if (isPaused) "Continue" else "Start",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
            SectionTitle(icon = Icons.Default.Timer, title = "My Records")
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MiniStatCard(value = formatTimeDisplay(bestDuration), label = "Best", color = NeonBlue, modifier = Modifier.weight(1f))
                MiniStatCard(value = "$totalSessions", label = "Sessions", color = NeonPink, modifier = Modifier.weight(1f))
                MiniStatCard(value = "${formatMinutes(totalDuration)}'", label = "Total", color = NeonBlue, modifier = Modifier.weight(1f))
            }
        }

        // History filter
        item {
            Spacer(modifier = Modifier.height(16.dp))
            SectionTitle(icon = Icons.Default.DateRange, title = "History")
            Spacer(modifier = Modifier.height(12.dp))
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(selected = historyView == "list", onClick = { historyView = "list" }, label = { Text("\uD83D\uDCCB") }, modifier = Modifier.height(32.dp))
                FilterChip(selected = historyView == "chart", onClick = { historyView = "chart" }, label = { Text("\uD83D\uDCCA") }, modifier = Modifier.height(32.dp))
                Spacer(modifier = Modifier.weight(1f))
                FilterChip(selected = historyPeriod == "all", onClick = { historyPeriod = "all" }, label = { Text("All", fontSize = 12.sp) }, modifier = Modifier.height(32.dp))
                FilterChip(selected = historyPeriod == "week", onClick = { historyPeriod = "week" }, label = { Text("Week", fontSize = 12.sp) }, modifier = Modifier.height(32.dp))
                FilterChip(selected = historyPeriod == "month", onClick = { historyPeriod = "month" }, label = { Text("Month", fontSize = 12.sp) }, modifier = Modifier.height(32.dp))
            }
        }
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
                    ChartView(records = filtered, barColors = listOf(NeonBlue, NeonPurple), unit = "s")
                } else {
                    Column {
                        filtered.take(20).forEach { record ->
                            val dateStr = java.text.SimpleDateFormat("M/d HH:mm", java.util.Locale.getDefault())
                                .format(java.util.Date(record.date))
                            HistoryRow(
                                label = dateStr,
                                value = formatTimeDisplay(record.value),
                                onDelete = { workoutVM.deleteRecord(record.id) }
                            )
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(30.dp)) }
    }
}

private fun formatTime(seconds: Int): String {
    val min = seconds / 60
    val sec = seconds % 60
    return "${min}'${sec.toString().padStart(2, '0')}\""
}

private fun formatTimeDisplay(seconds: Int): String {
    if (seconds <= 0) return "0'00\""
    val m = seconds / 60; val s = seconds % 60
    return "${m}'${s.toString().padStart(2, '0')}\""
}

private fun formatMinutes(seconds: Int): Int = seconds / 60

