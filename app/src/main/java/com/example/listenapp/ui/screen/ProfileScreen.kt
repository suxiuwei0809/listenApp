package com.example.listenapp.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.WaterDrop
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
fun ProfileScreen(
    onNavigateToBodyData: () -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDeep),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            ProfileHeader()
        }

        item {
            ProfileStats()
        }

        item {
            MenuSection(
                items = listOf(
                    MenuItem(Icons.Default.MonitorWeight, "身体数据", "体重、体脂率等", onNavigateToBodyData),
                    MenuItem(Icons.Default.WaterDrop, "喝水提醒", "每30分钟提醒", null),
                    MenuItem(Icons.Default.DirectionsWalk, "计步设置", "每日步数目标", null),
                    MenuItem(Icons.Default.Notifications, "打卡提醒", "运动提醒设置", null)
                )
            )
        }

        item {
            MenuSection(
                items = listOf(
                    MenuItem(Icons.Default.Palette, "主题设置", "深色/浅色主题", null),
                    MenuItem(Icons.Default.Info, "关于", "版本 1.0.0", null)
                )
            )
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
fun ProfileHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(
                    colors = listOf(NeonPink.copy(alpha = 0.15f), NeonBlue.copy(alpha = 0.15f))
                )
            )
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Brush.horizontalGradient(GradientPinkPurple)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(36.dp)
                )
            }
            Column {
                Text(
                    text = "健身达人",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(NeonBlue.copy(alpha = 0.2f))
                        .border(1.dp, NeonBlue.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Lv.12 运动健将",
                        color = NeonBlue,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileStats() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .offset(y = (-30).dp),
        colors = CardDefaults.cardColors(containerColor = GlassBackground),
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            ProfileStatItem(value = "256", label = "运动天数")
            ProfileStatItem(value = "128h", label = "运动时长")
            ProfileStatItem(value = "52kg", label = "累计减重")
        }
    }
}

@Composable
fun ProfileStatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = NeonBlue,
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

data class MenuItem(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val label: String,
    val desc: String,
    val onClick: (() -> Unit)?
)

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
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { item.onClick?.invoke() }
                        .padding(14.dp, 15.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(NeonPink.copy(alpha = 0.15f))
                            .border(1.dp, NeonPink.copy(alpha = 0.2f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = null,
                            tint = NeonPink,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.label,
                            color = TextPrimary,
                            fontSize = 15.sp
                        )
                        Text(
                            text = item.desc,
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = TextSecondary
                    )
                }
                if (index < items.size - 1) {
                    HorizontalDivider(
                        color = Color.White.copy(alpha = 0.05f),
                        modifier = Modifier.padding(start = 64.dp)
                    )
                }
            }
        }
    }
}
