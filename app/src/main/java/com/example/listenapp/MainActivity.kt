package com.example.listenapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.listenapp.ui.navigation.FitnessAppNavigation
import com.example.listenapp.ui.theme.FitnessAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FitnessAppTheme {
                FitnessAppNavigation()
            }
        }
    }
}
