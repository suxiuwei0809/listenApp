package com.example.listenapp.ui.screen

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
import com.example.listenapp.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun PlankScreen(onBack: () -> Unit) {
    var isRunning by remember { mutableStateOf(false) }
    var elapsedSeconds by remember { mutableIntStateOf(0) }
    var encouragement by remember { mutableStateOf("准备好挑战自己了吗？") }

    val encouragements = listOf(
        "坚持住！你可以的！",
        "再坚持10秒！",
        "核心收紧！不要放弃！",
        "你已经很棒了！继续！",
        "最后一下！加油！",
        "感受核心的力量！",
        "保持呼吸！稳住！"
    )

    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(1000)
            elapsedSeconds++
            if (elapsedSeconds % 10 == 0) {
                encouragement = encouragements.random()
            }
        }
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

                    // 背景圆环
                    drawCircle(
                        color = Color.White.copy(alpha = 0.06f),
                        radius = radius,
                        center = center,
                        style = Stroke(width = strokeWidth)
                    )

                    // 进度圆环
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
                    containerColor = NeonBlue.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonBlue.copy(alpha = 0.2f))
            ) {
                Text(
                    text = encouragement,
                    color = NeonBlue,
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
                IconButton(
                    onClick = { isRunning = false; elapsedSeconds = 0; encouragement = "准备好挑战自己了吗？" },
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.1f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "重置",
                        tint = TextSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                IconButton(
                    onClick = { isRunning = !isRunning },
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
                        contentDescription = if (isRunning) "暂停" else "开始",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
            SectionTitle(icon = Icons.Default.Timer, title = "我的记录")
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MiniStatCard(value = "1'32\"", label = "最长时间", color = NeonBlue, modifier = Modifier.weight(1f))
                MiniStatCard(value = "28", label = "累计次数", color = NeonPink, modifier = Modifier.weight(1f))
                MiniStatCard(value = "45'", label = "总时长", color = NeonBlue, modifier = Modifier.weight(1f))
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
            SectionTitle(icon = Icons.Default.EmojiEvents, title = "成就系统")
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AchievementCard(icon = "🌱", name = "初学者", unlocked = true, modifier = Modifier.weight(1f))
                AchievementCard(icon = "🔥", name = "坚持者", unlocked = true, modifier = Modifier.weight(1f))
                AchievementCard(icon = "👑", name = "王者", unlocked = false, modifier = Modifier.weight(1f))
            }
        }

        item { Spacer(modifier = Modifier.height(30.dp)) }
    }
}

@Composable
fun SubPageHeader(title: String, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(
                    colors = listOf(NeonPink.copy(alpha = 0.15f), NeonBlue.copy(alpha = 0.15f))
                )
            )
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.05f))
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "返回",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }
        Text(
            text = title,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier
                .weight(1f)
                .padding(start = 15.dp)
        )
    }
}

@Composable
fun MiniStatCard(value: String, label: String, color: Color, modifier: Modifier = Modifier) {
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
                color = color,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
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
fun AchievementCard(icon: String, name: String, unlocked: Boolean, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = GlassBackground),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .then(if (!unlocked) Modifier.background(Color.White.copy(alpha = 0.02f)) else Modifier),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = icon,
                fontSize = 28.sp,
                color = if (unlocked) Color.Unspecified else Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = name,
                color = if (unlocked) TextSecondary else TextSecondary.copy(alpha = 0.3f),
                fontSize = 11.sp
            )
        }
    }
}

private fun formatTime(seconds: Int): String {
    val min = seconds / 60
    val sec = seconds % 60
    return "${min}'${sec.toString().padStart(2, '0')}\""
}
