package com.example.fitforge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.fitforge.ui.navigation.FitnessAppNavigation
import com.example.fitforge.ui.theme.FitnessAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Splash Screen - 确保开屏展示至少 2s 且不超过 3s
        val splashScreen = installSplashScreen()
        
        // 记录开屏开始时间，控制最短展示时长
        var splashReady = false
        val minSplashMs = 2000L // 最短停留 2 秒
        val startTime = System.currentTimeMillis()

        splashScreen.setKeepOnScreenCondition {
            if (!splashReady) {
                val elapsed = System.currentTimeMillis() - startTime
                if (elapsed >= minSplashMs) {
                    splashReady = true
                }
            }
            !splashReady
        }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FitnessAppTheme {
                FitnessAppNavigation()
            }
        }
    }
}
