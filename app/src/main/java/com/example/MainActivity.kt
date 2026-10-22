package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MathViewModel
import com.example.ui.Screen
import com.example.ui.screens.*
import com.example.ui.theme.MathWhizTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: MathViewModel = viewModel()
            val isDark by viewModel.isDarkTheme.collectAsState()

            MathWhizTheme(darkTheme = isDark) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .windowInsetsPadding(WindowInsets.safeDrawing),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .widthIn(max = 768.dp) // Optimized for both tablets and phones
                    ) {
                        AppNavigation(viewModel = viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun AppNavigation(viewModel: MathViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()

    when (val screen = currentScreen) {
        is Screen.Onboarding -> OnboardingScreen(viewModel = viewModel)
        is Screen.Register -> RegistrationScreen(viewModel = viewModel)
        is Screen.Home -> HomeScreen(viewModel = viewModel)
        is Screen.LessonExercise -> LessonExerciseScreen(lesson = screen.lesson, viewModel = viewModel)
        is Screen.SpeedSprint -> SpeedSprintScreen(viewModel = viewModel)
        is Screen.GamesHub -> GamesArcadeHubScreen(viewModel = viewModel)
        is Screen.BalloonPopGame -> BalloonPopGameScreen(viewModel = viewModel)
        is Screen.PizzaChefGame -> PizzaChefGameScreen(viewModel = viewModel)
        is Screen.MemoryMatchGame -> MemoryMatchGameScreen(viewModel = viewModel)
        is Screen.BalanceScaleGame -> BalanceScaleGameScreen(viewModel = viewModel)
        is Screen.TimesTable -> TimesTableScreen(viewModel = viewModel)
        is Screen.Store -> RewardStoreScreen(viewModel = viewModel)
        is Screen.ParentDashboard -> ParentDashboardScreen(viewModel = viewModel)
        is Screen.Subscription -> SubscriptionScreen(viewModel = viewModel)
        is Screen.Updates -> UpdatesScreen(viewModel = viewModel)
        is Screen.Settings -> SettingsScreen(viewModel = viewModel)
    }
}
