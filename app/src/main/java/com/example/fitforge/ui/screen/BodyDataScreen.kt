package com.example.fitforge.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitforge.ui.theme.*
import com.example.fitforge.viewmodel.BodyDataViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun BodyDataScreen(onBack: () -> Unit) {
    val viewModel: BodyDataViewModel = viewModel()
    val state by viewModel.state.collectAsState()
    var showEditDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDeep),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SubPageHeader(title = "BODY", onBack = onBack)
        }

        // Current weight
        item {
            Card(
                modifier = Modifier.padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = NeonBlue.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonBlue.copy(alpha = 0.2f))
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = if (state.latestWeight > 0) String.format("%.1f", state.latestWeight) else "--",
                            color = TextPrimary, fontSize = 48.sp, fontWeight = FontWeight.Bold
                        )
                        Text(text = "kg", color = TextSecondary, fontSize = 16.sp,
                            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp))
                    }
                    Text(text = "Current Weight", color = TextSecondary, fontSize = 14.sp)
                    if (state.monthlyChange != 0f) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (state.monthlyChange < 0) "↓ ${String.format("%.1f", -state.monthlyChange)}kg (this month)"
                            else "↑ ${String.format("%.1f", state.monthlyChange)}kg (this month)",
                            color = if (state.monthlyChange < 0) NeonGreen else NeonPink,
                            fontSize = 13.sp, fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Stat cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BodyStatCard(
                    value = state.latestBodyFat?.let { "${String.format("%.1f", it)}%" } ?: "--",
                    label = "Body Fat", modifier = Modifier.weight(1f)
                )
                BodyStatCard(
                    value = state.height?.let { "${it.toInt()}cm" } ?: "--",
                    label = "Height", isAccent = true, modifier = Modifier.weight(1f)
                )
            }
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BodyStatCard(
                    value = state.bmi?.let { String.format("%.1f", it) } ?: "--",
                    label = "BMI", modifier = Modifier.weight(1f)
                )
                BodyStatCard(
                    value = state.weightGoal?.let { "${it.toInt()}kg" } ?: "--",
                    label = "Goal Weight", isAccent = true, modifier = Modifier.weight(1f)
                )
            }
        }

        // Weight trend
        if (state.records.isNotEmpty()) {
            item {
                SectionTitle(title = "Weight Trend", modifier = Modifier.padding(horizontal = 16.dp))
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = GlassBackground),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        val recentRecords = state.records.take(7).reversed()
                        if (recentRecords.isNotEmpty()) {
                            val maxW = recentRecords.maxOf { it.weightKg } + 1f
                            val minW = recentRecords.minOf { it.weightKg } - 1f
                            recentRecords.forEach { record ->
                                val height = ((record.weightKg - minW) / (maxW - minW) * 80f).coerceAtLeast(4f)
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier
                                            .width(16.dp).height(height.dp)
                                            .clip(RoundedCornerShape(4.dp, 4.dp, 0.dp, 0.dp))
                                            .background(Brush.verticalGradient(listOf(NeonPink, NeonBlue)))
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = SimpleDateFormat("M/d", Locale.getDefault()).format(Date(record.date)),
                                        color = TextSecondary, fontSize = 9.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Recent records
        item {
            Spacer(modifier = Modifier.height(8.dp))
            SectionTitle(title = "Recent Records", modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(modifier = Modifier.height(12.dp))
        }
        item {
            Card(
                modifier = Modifier.padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = GlassBackground),
                shape = RoundedCornerShape(14.dp)
            ) {
                if (state.records.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("No data. Tap + to add", color = TextSecondary, fontSize = 14.sp)
                    }
                } else {
                    Column {
                        state.records.take(10).forEachIndexed { i, record ->
                            val isDown = if (i < state.records.size - 1) {
                                record.weightKg < state.records[i + 1].weightKg
                            } else false
                            BodyRecordRow(
                                date = SimpleDateFormat("M/d", Locale.getDefault()).format(Date(record.date)),
                                weight = "${String.format("%.1f", record.weightKg)}kg",
                                isDown = isDown
                            )
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }

    // Add button
    FloatingActionButton(
        onClick = { showEditDialog = true },
        modifier = Modifier.padding(16.dp),
        containerColor = NeonPink
    ) {
        Icon(Icons.Default.Add, contentDescription = "Add", tint = TextPrimary)
    }

    // Add/Edit dialog
    if (showEditDialog) {
        BodyDataEditDialog(
            onDismiss = { showEditDialog = false },
            onSave = { weight, bodyFat ->
                viewModel.saveWeight(weight, bodyFat)
                showEditDialog = false
            }
        )
    }
}

@Composable
private fun BodyDataEditDialog(
    onDismiss: () -> Unit,
    onSave: (Float, Float?) -> Unit
) {
    var weightText by remember { mutableStateOf("") }
    var bodyFatText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BackgroundMid,
        title = { Text("Record Body Data", color = TextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                OutlinedTextField(
                    value = weightText,
                    onValueChange = { weightText = it },
                    label = { Text("Weight (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonBlue,
                        focusedLabelColor = NeonBlue,
                        unfocusedLabelColor = TextSecondary,
                        cursorColor = NeonBlue
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = bodyFatText,
                    onValueChange = { bodyFatText = it },
                    label = { Text("Body Fat (%) Optional") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonBlue,
                        focusedLabelColor = NeonBlue,
                        unfocusedLabelColor = TextSecondary,
                        cursorColor = NeonBlue
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val w = weightText.toFloatOrNull()
                    if (w != null && w > 0) {
                        onSave(w, bodyFatText.toFloatOrNull())
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                shape = RoundedCornerShape(14.dp)
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = TextSecondary) }
        }
    )
}

@Composable
fun BodyStatCard(value: String, label: String, isAccent: Boolean = false, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = GlassBackground),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = value,
                color = if (isAccent) NeonBlue else NeonPink,
                fontWeight = FontWeight.Bold, fontSize = 20.sp
            )
            Text(text = label, color = TextSecondary, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
        }
    }
}

@Composable
fun BodyRecordRow(date: String, weight: String, isDown: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = date, color = TextSecondary, fontSize = 14.sp)
        Text(
            text = weight,
            color = if (isDown) NeonGreen else NeonPink,
            fontSize = 14.sp, fontWeight = FontWeight.Medium
        )
    }
}
