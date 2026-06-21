package com.example.listenapp.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
fun BodyDataScreen(onBack: () -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDeep),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SubPageHeader(title = "BODY", onBack = onBack)
        }

        item {
            Card(
                modifier = Modifier.padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = NeonBlue.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonBlue.copy(alpha = 0.2f))
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "72.5",
                            color = TextPrimary,
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "kg",
                            color = TextSecondary,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                        )
                    }
                    Text(
                        text = "当前体重",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "↓ 2.3kg（本月）",
                        color = NeonGreen,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BodyStatCard(value = "18.5%", label = "体脂率", modifier = Modifier.weight(1f))
                BodyStatCard(value = "175cm", label = "身高", isAccent = true, modifier = Modifier.weight(1f))
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BodyStatCard(value = "23.5", label = "BMI", modifier = Modifier.weight(1f))
                BodyStatCard(value = "70kg", label = "目标体重", isAccent = true, modifier = Modifier.weight(1f))
            }
        }

        item {
            SectionTitle(
                icon = Icons.Default.ArrowDownward,
                title = "体重变化趋势",
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier.padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = GlassBackground),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    val weights = listOf(74.0f, 73.5f, 73.8f, 73.2f, 72.8f, 72.5f, 72.5f)
                    val dates = listOf("1/1", "1/8", "1/15", "1/22", "1/29", "2/5", "2/12")
                    val maxW = 75f
                    val minW = 71f

                    weights.forEachIndexed { index, weight ->
                        val height = ((weight - minW) / (maxW - minW) * 80f).coerceAtLeast(4f)
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(16.dp)
                                    .height(height.dp)
                                    .clip(RoundedCornerShape(4.dp, 4.dp, 0.dp, 0.dp))
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(NeonPink, NeonBlue)
                                        )
                                    )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = dates[index],
                                color = TextSecondary,
                                fontSize = 9.sp
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            SectionTitle(
                icon = Icons.Default.ArrowDownward,
                title = "近期记录",
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            Card(
                modifier = Modifier.padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = GlassBackground),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column {
                    BodyRecordRow(date = "2/12", weight = "72.5kg", isDown = true)
                    BodyRecordRow(date = "2/5", weight = "72.8kg", isDown = true)
                    BodyRecordRow(date = "1/29", weight = "73.2kg", isDown = true)
                    BodyRecordRow(date = "1/22", weight = "73.8kg", isDown = false)
                    BodyRecordRow(date = "1/15", weight = "73.5kg", isDown = true)
                }
            }
        }

        item { Spacer(modifier = Modifier.height(30.dp)) }
    }
}

@Composable
fun BodyStatCard(value: String, label: String, isAccent: Boolean = false, modifier: Modifier = Modifier) {
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
                fontSize = 20.sp
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
fun BodyRecordRow(date: String, weight: String, isDown: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = date, color = TextSecondary, fontSize = 14.sp)
        Text(
            text = weight,
            color = if (isDown) NeonGreen else NeonPink,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
