package com.example.listenapp.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.listenapp.ui.theme.*

data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Home.route, Icons.Default.Home, "首页"),
    BottomNavItem(Screen.Workout.route, Icons.Default.FitnessCenter, "运动"),
    BottomNavItem(Screen.Train.route, Icons.Default.Assignment, "训练"),
    BottomNavItem(Screen.Profile.route, Icons.Default.Person, "我的")
)

@Composable
fun BottomNavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = BackgroundDeep.copy(alpha = 0.9f),
        shadowElevation = 0.dp
    ) {
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                bottomNavItems.forEach { item ->
                    val isSelected = currentRoute == item.route
                    Column(
                        modifier = Modifier
                            .clickable { onNavigate(item.route) }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .width(24.dp)
                                    .height(2.dp)
                                    .background(
                                        NeonBlue,
                                        androidx.compose.foundation.shape.RoundedCornerShape(1.dp)
                                    )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        } else {
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = if (isSelected) NeonBlue else TextSecondary,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = item.label,
                            color = if (isSelected) NeonBlue else TextSecondary,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .padding(horizontal = 20.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color.Transparent, NeonPurple, Color.Transparent)
                        )
                    )
            )
        }
    }
}
