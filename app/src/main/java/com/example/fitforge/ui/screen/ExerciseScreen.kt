package com.example.fitforge.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitforge.data.static.Exercise
import com.example.fitforge.data.static.ExerciseLibrary
import com.example.fitforge.ui.theme.*

@Composable
fun ExerciseScreen(onBack: () -> Unit) {
    var selectedCat by remember { mutableStateOf("chest") }
    var showDetail by remember { mutableStateOf<Exercise?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDeep),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SubPageHeader(title = "EXERCISES", onBack = onBack)
        }

        item {
            LazyRow(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(ExerciseLibrary.categories.toList()) { key ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (selectedCat == key) NeonPink.copy(alpha = 0.2f) else GlassBackground)
                            .border(
                                1.dp,
                                if (selectedCat == key) NeonPink.copy(alpha = 0.3f) else GlassBorder,
                                RoundedCornerShape(14.dp)
                            )
                            .clickable { selectedCat = key }
                            .padding(12.dp)
                    ) {
                        Text(text = ExerciseLibrary.catIcons[key] ?: "", fontSize = 20.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = ExerciseLibrary.catNames[key] ?: "",
                            color = if (selectedCat == key) TextPrimary else TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        val exercises = ExerciseLibrary.getByCategory(selectedCat)
        items(exercises) { exercise ->
            ExerciseCard(
                exercise = exercise,
                onClick = { showDetail = exercise }
            )
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }

    if (showDetail != null) {
        ExerciseDetailSheet(
            exercise = showDetail!!,
            onDismiss = { showDetail = null }
        )
    }
}

@Composable
fun ExerciseCard(exercise: Exercise, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = GlassBackground),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(15.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = exercise.name,
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(NeonPink.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = exercise.cat,
                        color = NeonPink,
                        fontSize = 12.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(text = exercise.sets, color = TextSecondary, fontSize = 12.sp)
                Text(text = "${exercise.tips.size} tips", color = TextSecondary, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun ExerciseDetailSheet(exercise: Exercise, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BackgroundMid,
        title = {
            Column {
                Text(
                    text = exercise.name,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = "${exercise.cat} 路 ${exercise.sets}",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }
        },
        text = {
            Column {
                Text(
                    text = "Tips",
                    color = NeonBlue,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                exercise.tips.forEach { tip ->
                    Row(modifier = Modifier.padding(vertical = 2.dp)) {
                        Text(text = "·", color = TextSecondary, fontSize = 14.sp)
                        Text(text = tip, color = TextPrimary, fontSize = 14.sp)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Caution",
                    color = NeonOrange,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = exercise.caution, color = TextPrimary, fontSize = 14.sp)
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("OK")
            }
        }
    )
}
