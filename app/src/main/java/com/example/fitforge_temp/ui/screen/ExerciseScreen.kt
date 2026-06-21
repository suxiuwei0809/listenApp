package com.example.listenapp.ui.screen

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
import com.example.listenapp.ui.theme.*

data class Exercise(
    val name: String,
    val cat: String,
    val sets: String,
    val tips: List<String>,
    val caution: String
)

val exerciseData = mapOf(
    "chest" to listOf(
        Exercise("平板杠铃卧推", "胸部", "4组x10次", listOf("肩胛骨收紧贴凳", "下放至胸口位置", "推起时不要锁死肘关节", "双脚踩实地面稳定身体"), "重量循序渐进，切勿盲目加片"),
        Exercise("上斜哑铃卧推", "胸部", "4组x10次", listOf("凳面角度30-45度", "下放时肘部成90度", "推起时哑铃微微靠拢", "感受上胸发力"), "控制哑铃轨迹，避免肩部代偿"),
        Exercise("蝴蝶机夹胸", "胸部", "3组x12次", listOf("挺胸收腹", "顶峰收缩停顿1秒", "缓慢回放", "不要用手臂发力"), "重量不宜过大，注重收缩感")
    ),
    "back" to listOf(
        Exercise("引体向上", "背部", "4组x8次", listOf("核心收紧", "肩胛骨下沉发力", "下巴过杠", "缓慢下放"), "不要借力摆荡，做不到可用弹力带辅助"),
        Exercise("杠铃划船", "背部", "4组x10次", listOf("俯身约45度", "背部发力拉向腹部", "肘部贴身", "顶峰收缩2秒"), "保持腰部中立，避免弯腰"),
        Exercise("坐姿下拉", "背部", "3组x12次", listOf("挺胸", "下拉至锁骨", "肩胛骨下回旋", "缓慢回放"), "不要过度后仰借力")
    ),
    "legs" to listOf(
        Exercise("杠铃深蹲", "腿部", "5组x8次", listOf("膝盖对准脚尖方向", "蹲至大腿平行地面", "重心在脚掌中后部", "核心收紧保持稳定"), "腰部必须保持中立，切勿弯腰"),
        Exercise("罗马尼亚硬拉", "腿部", "4组x10次", listOf("臀部向后推", "杠铃沿腿面下滑", "感受腘绳肌拉伸", "髋关节铰链运动"), "背部保持平直，不可弓背")
    ),
    "abs" to listOf(
        Exercise("卷腹", "腹部", "4组x20次", listOf("下背始终贴地", "用腹肌卷起上身", "不需要起太高", "双手放耳侧不要抱头"), "切勿双手抱头拉扯颈部"),
        Exercise("悬垂举腿", "腹部", "3组x12次", listOf("控制速度", "骨盆后倾", "举至与地面平行", "不要借力摆荡"), "握力不足可使用助力带")
    ),
    "shoulder" to listOf(
        Exercise("杠铃推举", "肩部", "4组x10次", listOf("核心收紧", "推至头顶正上方", "下放至锁骨位置", "不要过度后仰"), "肩关节有不适立即停止"),
        Exercise("侧平举", "肩部", "3组x15次", listOf("小臂微倾", "举至与肩同高", "小指侧略高", "控制速度"), "不要耸肩，重量不宜过大")
    ),
    "arms" to listOf(
        Exercise("杠铃弯举", "手臂", "3组x12次", listOf("大臂贴紧身体", "只动前臂", "顶峰收缩", "缓慢下放"), "不要借力甩举"),
        Exercise("绳索下压", "手臂", "3组x12次", listOf("大臂固定", "只动前臂", "完全伸直时挤压", "缓慢回放"), "手肘不要外展")
    )
)

val catNames = mapOf(
    "chest" to "胸部",
    "back" to "背部",
    "legs" to "腿部",
    "abs" to "腹部",
    "shoulder" to "肩部",
    "arms" to "手臂"
)

val catIcons = mapOf(
    "chest" to "💪",
    "back" to "🏋️",
    "legs" to "🦵",
    "abs" to "🔥",
    "shoulder" to "🏅",
    "arms" to "💪"
)

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
                items(exerciseData.keys.toList()) { key ->
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
                        Text(text = catIcons[key] ?: "", fontSize = 20.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = catNames[key] ?: "",
                            color = if (selectedCat == key) TextPrimary else TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        val exercises = exerciseData[selectedCat] ?: emptyList()
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
                Text(text = "${exercise.tips.size}个要点", color = TextSecondary, fontSize = 12.sp)
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
                    text = "${exercise.cat} · ${exercise.sets}",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }
        },
        text = {
            Column {
                Text(
                    text = "💡 姿势要点",
                    color = NeonBlue,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                exercise.tips.forEach { tip ->
                    Row(modifier = Modifier.padding(vertical = 2.dp)) {
                        Text(text = "• ", color = TextSecondary, fontSize = 14.sp)
                        Text(text = tip, color = TextPrimary, fontSize = 14.sp)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "⚠️ 注意事项",
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
                Text("知道了")
            }
        }
    )
}
