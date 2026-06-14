package com.example.listenapp.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Workout : Screen("workout")
    data object Train : Screen("train")
    data object Profile : Screen("profile")
    data object Plank : Screen("plank")
    data object Pushup : Screen("pushup")
    data object AbWheel : Screen("abwheel")
    data object Exercise : Screen("exercise")
    data object BodyData : Screen("bodydata")
}
