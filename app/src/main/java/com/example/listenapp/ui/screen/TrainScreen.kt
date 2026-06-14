package com.example.listenapp.ui.screen

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
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.listenapp.data.model.*
import com.example.listenapp.ui.theme.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.listenapp.viewmodel.HomeViewModel

@Composable
fun TrainScreen(viewModel: HomeViewModel = viewModel()) {
    var selectedTarget by remember { mutableStateOf<TrainingTarget?>(null) }
    val gymPlans by viewModel.gymPlans.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDeep),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            PageHeader(title = "TRAIN")
        }

        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                SectionTitle(icon = Icons.Default.FitnessCenter, title = "快速训练")
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "选择训练目标，立即开始",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        FilterChip(
                            text = "★ 推荐",
                            isSelected = selectedTarget == null,
                            onClick = { selectedTarget = null }
                        )
                    }
                    items(TrainingTarget.values()) { target ->
                        FilterChip(
                            text = target.displayName,
                            isSelected = selectedTarget == target,
                            onClick = { selectedTarget = target }
                        )
                    }
                }

                val currentPlan = gymPlans.find {
                    it.target == (selectedTarget ?: TrainingTarget.BACK)
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
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("开始训练", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            SectionTitle(
                icon = Icons.Default.Assignment,
                title = "长期计划",
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        items(gymPlans) { plan ->
            PlanCard(plan = plan)
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
fun SectionTitle(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = NeonPink,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = title,
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun PlanCard(plan: GymPlan) {
    Card(
        modifier = Modifier.padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = GlassBackground),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(modifier = Modifier.padding(22.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.horizontalGradient(GradientPinkPurple)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FitnessCenter,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = plan.name,
                            color = TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${plan.steps.size}个动作",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(NeonPink.copy(alpha = 0.2f))
                        .border(1.dp, NeonPink.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = plan.target.displayName,
                        color = NeonPink,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            plan.steps.forEach { step ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = when (step.phase) {
                            TrainingPhase.WARMUP -> "热身"
                            TrainingPhase.MAIN -> "主训练"
                            TrainingPhase.STRETCH -> "拉伸"
                        },
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                    Text(
                        text = step.name,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("开始训练", fontWeight = FontWeight.Bold)
            }
        }
    }
}
