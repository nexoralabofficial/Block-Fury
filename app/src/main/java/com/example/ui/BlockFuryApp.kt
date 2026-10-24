package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.GameMode
import com.example.viewmodel.GameViewModel

enum class AppScreen {
    SPLASH,
    MAIN_MENU,
    GAMEPLAY
}

@Composable
fun BlockFuryApp(
    viewModel: GameViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    var currentScreen by remember { mutableStateOf(AppScreen.SPLASH) }
    val lifecycleOwner = LocalLifecycleOwner.current

    // Lifecycle observer for managing sound and timers
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    viewModel.audioManager.onPause()
                }
                Lifecycle.Event.ON_RESUME -> {
                    viewModel.audioManager.onResume()
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Crossfade(
        targetState = currentScreen,
        label = "screen_crossfade",
        modifier = modifier.fillMaxSize()
    ) { screen ->
        when (screen) {
            AppScreen.SPLASH -> {
                SplashScreen(
                    audioManager = viewModel.audioManager,
                    onSplashFinished = {
                        currentScreen = AppScreen.MAIN_MENU
                    }
                )
            }
            AppScreen.MAIN_MENU -> {
                MainMenuScreen(
                    preferences = viewModel.preferences,
                    audioManager = viewModel.audioManager,
                    onStartGame = { mode ->
                        viewModel.startNewGame(mode)
                        currentScreen = AppScreen.GAMEPLAY
                    }
                )
            }
            AppScreen.GAMEPLAY -> {
                GamePlayScreen(
                    viewModel = viewModel,
                    onNavigateHome = {
                        viewModel.audioManager.playMenuMusic()
                        currentScreen = AppScreen.MAIN_MENU
                    }
                )
            }
        }
    }
}
