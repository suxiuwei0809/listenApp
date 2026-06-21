package com.example.fitforge.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitforge.data.local.AppPreferences
import com.example.fitforge.ui.theme.*

data class MenuItem(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val label: String,
    val desc: String,
    val onClick: (() -> Unit)?
)

@Composable
fun ProfileScreen(
    onNavigateToBodyData: () -> Unit = {}
) {
    val prefs = AppPreferences(
        androidx.compose.ui.platform.LocalContext.current.applicationContext as android.app.Application
    )
    val coroutineScope = rememberCoroutineScope()

    var showWaterDialog by remember { mutableStateOf(false) }
    var showStepDialog by remember { mutableStateOf(false) }
    var showReminderDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(BackgroundDeep),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { ProfileHeader() }
        item { ProfileStats() }

        item {
            MenuSection(items = listOf(
                MenuItem(Icons.Default.MonitorWeight, "身体数据", "体重、体脂率等", onNavigateToBodyData),
                MenuItem(Icons.Default.WaterDrop, "喝水提醒", "每30分钟提醒", { showWaterDialog = true }),
                MenuItem(Icons.Default.DirectionsWalk, "计步设置", "每日步数目标", { showStepDialog = true }),
                MenuItem(Icons.Default.Notifications, "打卡提醒", "运动提醒设置", { showReminderDialog = true })
            ))
        }

        item {
            MenuSection(items = listOf(
                MenuItem(Icons.Default.Palette, "主题设置", "深色/浅色主题", { showThemeDialog = true }),
                MenuItem(Icons.Default.Info, "关于", "版本 1.0.0", { showAboutDialog = true })
            ))
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }

    // ---- Dialogs ----

    if (showWaterDialog) {
        AlertDialog(
            onDismissRequest = { showWaterDialog = false },
            containerColor = BackgroundMid,
            title = { Text("💧 喝水提醒", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = { Text("每30分钟提醒一次 · 目标: 2000ml/天\n\n功能开发中，敬请期待。", color = TextSecondary, fontSize = 14.sp) },
            confirmButton = {
                Button(onClick = { showWaterDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                    shape = RoundedCornerShape(14.dp)) { Text("知道了") }
            }
        )
    }

    if (showStepDialog) {
        var stepText by remember { mutableStateOf("10000") }
        val stepGoal by prefs.stepGoal.collectAsState(initial = 10000)
        LaunchedEffect(showStepDialog) { stepText = stepGoal.toString() }
        AlertDialog(
            onDismissRequest = { showStepDialog = false },
            containerColor = BackgroundMid,
            title = { Text("计步设置", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("设置每日步数目标", color = TextSecondary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = stepText,
                        onValueChange = { stepText = it.filter { c -> c.isDigit() } },
                        label = { Text("步数目标") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonBlue, focusedLabelColor = NeonBlue,
                            unfocusedLabelColor = TextSecondary, cursorColor = NeonBlue
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    coroutineScope.launch { prefs.setStepGoal(stepText.toIntOrNull() ?: 10000) }
                    showStepDialog = false
                },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                    shape = RoundedCornerShape(14.dp)) { Text("保存") }
            },
            dismissButton = {
                TextButton(onClick = { showStepDialog = false }) { Text("取消", color = TextSecondary) }
            }
        )
    }

    if (showReminderDialog) {
        var enabled by remember { mutableStateOf(true) }
        AlertDialog(
            onDismissRequest = { showReminderDialog = false },
            containerColor = BackgroundMid,
            title = { Text("打卡提醒", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("开启后将每日推送运动提醒", color = TextSecondary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("提醒开关", color = TextPrimary, fontSize = 14.sp, modifier = Modifier.weight(1f))
                        Switch(checked = enabled, onCheckedChange = { enabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = NeonBlue, checkedTrackColor = NeonBlue.copy(alpha = 0.4f)))
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    coroutineScope.launch { prefs.setReminderEnabled(enabled) }
                    showReminderDialog = false
                },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                    shape = RoundedCornerShape(14.dp)) { Text("保存") }
            },
            dismissButton = {
                TextButton(onClick = { showReminderDialog = false }) { Text("取消", color = TextSecondary) }
            }
        )
    }

    if (showThemeDialog) {
        val themeDark by prefs.themeDark.collectAsState(initial = true)
        var dark by remember { mutableStateOf(themeDark) }
        LaunchedEffect(showThemeDialog) { dark = themeDark }
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            containerColor = BackgroundMid,
            title = { Text("主题设置", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("深色模式", color = TextPrimary, fontSize = 14.sp, modifier = Modifier.weight(1f))
                    Switch(checked = dark, onCheckedChange = { dark = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = NeonPink, checkedTrackColor = NeonPink.copy(alpha = 0.4f)))
                }
            },
            confirmButton = {
                Button(onClick = {
                    coroutineScope.launch { prefs.setThemeDark(dark) }
                    showThemeDialog = false
                },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                    shape = RoundedCornerShape(14.dp)) { Text("保存") }
            },
            dismissButton = {
                TextButton(onClick = { showThemeDialog = false }) { Text("取消", color = TextSecondary) }
            }
        )
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            containerColor = BackgroundMid,
            title = { Text("关于", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = { Text("FitX 健身助手\n版本 1.0.0\n\n专为健身爱好者打造\n数据本地存储，无需登录\n\n© 2026", color = TextSecondary, fontSize = 14.sp) },
            confirmButton = {
                Button(onClick = { showAboutDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                    shape = RoundedCornerShape(14.dp)) { Text("关闭") }
            }
        )
    }
}

@Composable
fun ProfileHeader() {
    Box(
        modifier = Modifier.fillMaxWidth()
            .background(Brush.linearGradient(listOf(NeonPink.copy(alpha = 0.15f), NeonBlue.copy(alpha = 0.15f))))
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(15.dp)) {
            Box(
                modifier = Modifier.size(72.dp).clip(CircleShape).background(Brush.horizontalGradient(GradientPinkPurple)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, null, tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(36.dp))
            }
            Column {
                Text("健身达人", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(NeonBlue.copy(alpha = 0.2f))
                        .border(1.dp, NeonBlue.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 4.dp)
                ) { Text("Lv.12 运动健将", color = NeonBlue, fontSize = 12.sp) }
            }
        }
    }
}

@Composable
fun ProfileStats() {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).offset(y = (-30).dp),
        colors = CardDefaults.cardColors(containerColor = GlassBackground),
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(20.dp), horizontalArrangement = Arrangement.SpaceAround) {
            ProfileStatItem(value = "256", label = "运动天数")
            ProfileStatItem(value = "128h", label = "运动时长")
            ProfileStatItem(value = "52kg", label = "累计减重")
        }
    }
}

@Composable
fun ProfileStatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = NeonBlue, fontWeight = FontWeight.Bold, fontSize = 22.sp)
        Text(label, color = TextSecondary, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
    }
}

@Composable
fun MenuSection(items: List<MenuItem>) {
    Card(
        modifier = Modifier.padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = GlassBackground),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            items.forEachIndexed { index, item ->
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { item.onClick?.invoke() }.padding(14.dp, 15.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp))
                            .background(NeonPink.copy(alpha = 0.15f))
                            .border(1.dp, NeonPink.copy(alpha = 0.2f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(item.icon, null, tint = NeonPink, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(item.label, color = TextPrimary, fontSize = 15.sp)
                        Text(item.desc, color = TextSecondary, fontSize = 12.sp)
                    }
                    Icon(Icons.Default.ChevronRight, null, tint = TextSecondary)
                }
                if (index < items.size - 1) {
                    HorizontalDivider(color = Color.White.copy(alpha = 0.05f), modifier = Modifier.padding(start = 64.dp))
                }
            }
        }
    }
}
