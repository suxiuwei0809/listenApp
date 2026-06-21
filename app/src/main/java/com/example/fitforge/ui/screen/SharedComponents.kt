package com.example.fitforge.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.fitforge.data.local.entity.WorkoutRecordEntity
import com.example.fitforge.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

// ============================================================
// SubPageHeader - 子页面通用头部（带返回按钮）
// ============================================================
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

// ============================================================
// PageHeader - 无返回按钮的页面头部
// ============================================================
@Composable
fun PageHeader(title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(
                    listOf(NeonPink.copy(alpha = 0.15f), NeonBlue.copy(alpha = 0.15f))
                )
            )
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 20.sp)
    }
}

// ============================================================
// SectionTitle - 分区标题（可选图标）
// ============================================================
@Composable
fun SectionTitle(
    icon: ImageVector? = null,
    title: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = NeonPink,
                modifier = Modifier.size(18.dp)
            )
        }
        Text(
            text = title,
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ============================================================
// TextChip - 自定义文字标签（替代 M3 FilterChip 的简易版本）
// ============================================================
@Composable
fun TextChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) NeonPink else GlassBackground)
            .border(
                1.dp,
                if (isSelected) Color.Transparent else GlassBorder,
                RoundedCornerShape(20.dp)
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

// ============================================================
// MiniStatCard - 迷你可以统计卡片
// ============================================================
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

// ============================================================
// ChartView - 柱状图组件
// ============================================================
@Composable
fun ChartView(
    records: List<WorkoutRecordEntity>,
    barColors: List<Color>,
    unit: String
) {
    val daily = linkedMapOf<String, Int>()
    records.forEach { r ->
        val key = SimpleDateFormat("M/d", Locale.getDefault()).format(Date(r.date))
        daily[key] = (daily[key] ?: 0) + r.value
    }
    val entries = daily.entries.toList()
    val maxVal = entries.maxOfOrNull { it.value } ?: 1
    Column(modifier = Modifier.padding(12.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            entries.forEach { (date, value) ->
                val h = (value.toFloat() / maxVal * 100f).coerceAtLeast(4f)
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(h.dp)
                            .clip(
                                RoundedCornerShape(
                                    topStart = 3.dp,
                                    topEnd = 3.dp
                                )
                            )
                            .background(Brush.verticalGradient(barColors))
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        date,
                        color = TextSecondary,
                        fontSize = 9.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            "每日${unit}统计 · 共${records.size}条记录",
            color = TextSecondary,
            fontSize = 11.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

// ============================================================
// RestOverlay - 组间休息覆盖层
// ============================================================
@Composable
fun RestOverlay(seconds: Int, onSkip: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f))
            .clickable(enabled = false) { },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("组间休息", color = NeonBlue, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = seconds.toString(),
                color = NeonBlue,
                fontSize = 80.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("秒", color = TextSecondary, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(30.dp))
            OutlinedButton(
                onClick = onSkip,
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder)
            ) {
                Text("跳过休息", color = TextSecondary)
            }
        }
    }
}

// ============================================================
// HistoryRow - 历史记录行（可选删除按钮）
// ============================================================
@Composable
fun HistoryRow(label: String, value: String, onDelete: (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = TextSecondary, fontSize = 14.sp, modifier = Modifier.weight(1f))
        Text(
            text = value,
            color = NeonGreen,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(end = 8.dp)
        )
        if (onDelete != null) {
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "删除记录",
                    tint = NeonPink.copy(alpha = 0.6f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

// ============================================================
// filterRecords - 根据时间段过滤记录
// ============================================================
fun filterRecords(
    records: List<WorkoutRecordEntity>,
    period: String
): List<WorkoutRecordEntity> {
    if (period == "all") return records
    val cal = Calendar.getInstance()
    cal.add(
        if (period == "week") Calendar.DAY_OF_YEAR else Calendar.MONTH,
        -1
    )
    val cutoff = cal.timeInMillis
    return records.filter { it.date >= cutoff }
}
