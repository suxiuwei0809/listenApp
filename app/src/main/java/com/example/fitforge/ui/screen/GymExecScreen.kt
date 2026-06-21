package com.example.fitforge.ui.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitforge.data.model.GymPlan
import com.example.fitforge.data.model.GymStep
import com.example.fitforge.data.model.TrainingPhase
import com.example.fitforge.data.static.GymPlanLibrary
import com.example.fitforge.ui.theme.*
import com.example.fitforge.viewmodel.WorkoutViewModel

private sealed class FlatItem {
    data class PhaseHeader(val label: String, val phase: TrainingPhase) : FlatItem()
    data class Step(val index: Int, val step: GymStep) : FlatItem()
}

@Composable
fun GymExecScreen(target: String, onBack: () -> Unit) {
    val workoutVM: WorkoutViewModel = viewModel()
    val plan = GymPlanLibrary.getGymPlans().find { it.target.name.equals(target, ignoreCase = true) || it.id == target }
        ?: GymPlanLibrary.getGymPlans().first()
    val steps = plan.steps
    val completedSteps = remember { mutableStateListOf(*BooleanArray(steps.size) { false }.toTypedArray()) }
    val allDone = completedSteps.all { it }

    val startTime = remember { System.currentTimeMillis() }

    val flatItems = remember(steps) {
        buildList {
            var lastPhase: TrainingPhase? = null
            steps.forEachIndexed { index, step ->
                if (step.phase != lastPhase) {
                    add(FlatItem.PhaseHeader(step.phase.displayName, step.phase))
                    lastPhase = step.phase
                }
                add(FlatItem.Step(index, step))
            }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(BackgroundDeep)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .background(Brush.linearGradient(listOf(NeonPink.copy(alpha = 0.2f), NeonPurple.copy(alpha = 0.2f))))
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(36.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.05f))
                ) {
                    Icon(Icons.Default.ArrowBack, "返回", tint = Color.White, modifier = Modifier.size(18.dp))
                }
                Text(
                    text = plan.name,
                    color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp,
                    modifier = Modifier.padding(start = 15.dp)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PhaseLabel("热身", NeonOrange)
                Text("→", color = TextSecondary, fontSize = 14.sp)
                PhaseLabel("主训练", NeonPink)
                Text("→", color = TextSecondary, fontSize = 14.sp)
                PhaseLabel("拉伸", NeonGreen)
            }
        }

        items(flatItems.size) { i ->
            when (val item = flatItems[i]) {
                is FlatItem.PhaseHeader -> {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.label,
                        color = when (item.phase) {
                            TrainingPhase.WARMUP -> NeonOrange
                            TrainingPhase.MAIN -> NeonPink
                            TrainingPhase.STRETCH -> NeonGreen
                        },
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
                is FlatItem.Step -> {
                    val isDone = completedSteps[item.index]
                    StepCard(
                        step = item.step,
                        index = item.index + 1,
                        isDone = isDone,
                        onToggle = { completedSteps[item.index] = !isDone }
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {
                    val duration = ((System.currentTimeMillis() - startTime) / 1000).toInt()
                    workoutVM.saveGymExec(planName = plan.name, durationSeconds = duration)
                    onBack()
                },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                enabled = allDone,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (allDone) NeonPink else GlassBorder,
                    disabledContainerColor = GlassBorder
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    if (allDone) "完成训练" else "请完成所有步骤",
                    fontWeight = FontWeight.Bold,
                    color = if (allDone) Color.White else TextSecondary
                )
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun PhaseLabel(text: String, color: Color) {
    Box(
        modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(color.copy(alpha = 0.15f)).padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text, color = color, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun StepCard(step: GymStep, index: Int, isDone: Boolean, onToggle: () -> Unit) {
    val bgColor by animateColorAsState(
        if (isDone) NeonGreen.copy(alpha = 0.08f) else GlassBackground,
        label = "stepBg"
    )

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(32.dp).clip(CircleShape)
                    .background(if (isDone) NeonGreen else NeonPink.copy(alpha = 0.3f))
                    .border(1.dp, if (isDone) NeonGreen else NeonPink, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (isDone) {
                    Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(14.dp))
                } else {
                    Text("$index", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = step.name,
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    textDecoration = if (isDone) TextDecoration.LineThrough else TextDecoration.None
                )
                Text(
                    text = step.detail + (step.tip?.let { " · $it" } ?: ""),
                    color = TextSecondary,
                    fontSize = 12.sp,
                    textDecoration = if (isDone) TextDecoration.LineThrough else TextDecoration.None
                )
            }

            Box(
                modifier = Modifier.size(28.dp).clip(CircleShape)
                    .then(
                        if (isDone) Modifier.background(NeonGreen)
                        else Modifier.border(2.dp, GlassBorder, CircleShape)
                    )
                    .clickable(onClick = onToggle),
                contentAlignment = Alignment.Center
            ) {
                if (isDone) Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(14.dp))
            }
        }
    }
}
