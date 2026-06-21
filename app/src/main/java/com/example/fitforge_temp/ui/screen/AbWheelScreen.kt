package com.example.listenapp.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.listenapp.ui.theme.*

@Composable
fun AbWheelScreen(onBack: () -> Unit) {
    var count by remember { mutableIntStateOf(0) }
    var sets by remember { mutableIntStateOf(0) }
    var total by remember { mutableIntStateOf(0) }
    var maxInSet by remember { mutableIntStateOf(0) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDeep),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            SubPageHeader(title = "AB WHEEL", onBack = onBack)
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = "当前组计数",
                color = TextSecondary,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = count.toString(),
                    color = TextPrimary,
                    fontSize = 72.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "次",
                    color = TextSecondary,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 16.dp, start = 4.dp)
                )
            }
        }

        item {
            IconButton(
                onClick = {
                    count++
                    if (count > maxInSet) maxInSet = count
                },
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Brush.horizontalGradient(listOf(NeonGreen, NeonBlue)))
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "增加",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MiniStatCard(value = sets.toString(), label = "已完成组", color = NeonBlue, modifier = Modifier.weight(1f))
                MiniStatCard(value = total.toString(), label = "总次数", color = NeonGreen, modifier = Modifier.weight(1f))
                MiniStatCard(value = maxInSet.toString(), label = "单组最多", color = NeonBlue, modifier = Modifier.weight(1f))
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        if (count > 0) {
                            sets++
                            total += count
                            count = 0
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("完成本组", fontWeight = FontWeight.Medium, color = Color.Black)
                }
                OutlinedButton(
                    onClick = { count = 0; sets = 0; total = 0; maxInSet = 0 },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder)
                ) {
                    Text("重置", color = TextSecondary)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
            SectionTitle(icon = Icons.Default.DateRange, title = "历史记录")
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            Card(
                modifier = Modifier.padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = GlassBackground),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column {
                    HistoryRow(label = "昨天", value = "4组 / 32次")
                    HistoryRow(label = "3天前", value = "3组 / 24次")
                }
            }
        }

        item { Spacer(modifier = Modifier.height(30.dp)) }
    }
}
