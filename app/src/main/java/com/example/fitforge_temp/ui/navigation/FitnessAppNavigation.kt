package com.example.listenapp.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.listenapp.ui.screen.*
import com.example.listenapp.ui.theme.BackgroundDeep

@Composable
fun FitnessAppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.Workout.route,
        Screen.Train.route,
        Screen.Profile.route
    )

    Scaffold(
        modifier = Modifier.background(BackgroundDeep),
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(
                    currentRoute = currentRoute ?: Screen.Home.route,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier
                .background(BackgroundDeep)
                .padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToPlank = { navController.navigate(Screen.Plank.route) },
                    onNavigateToPushup = { navController.navigate(Screen.Pushup.route) },
                    onNavigateToAbwheel = { navController.navigate(Screen.AbWheel.route) },
                    onNavigateToExercise = { navController.navigate(Screen.Exercise.route) },
                    onNavigateToWorkout = { navController.navigate(Screen.Workout.route) }
                )
            }
            composable(Screen.Workout.route) {
                WorkoutScreen()
            }
            composable(Screen.Train.route) {
                TrainScreen()
            }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    onNavigateToBodyData = { navController.navigate(Screen.BodyData.route) }
                )
            }
            composable(Screen.Plank.route) {
                PlankScreen(onBack = { navController.popBackStack() })
            }
            composable(Screen.Pushup.route) {
                PushupScreen(onBack = { navController.popBackStack() })
            }
            composable(Screen.AbWheel.route) {
                AbWheelScreen(onBack = { navController.popBackStack() })
            }
            composable(Screen.Exercise.route) {
                ExerciseScreen(onBack = { navController.popBackStack() })
            }
            composable(Screen.BodyData.route) {
                BodyDataScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
