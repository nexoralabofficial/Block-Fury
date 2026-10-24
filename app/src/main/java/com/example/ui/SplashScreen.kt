package com.example.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.GameAudioManager
import com.example.model.CubeColor
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    audioManager: GameAudioManager,
    onSplashFinished: () -> Unit
) {
    val scale = remember { Animatable(0.4f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        audioManager.playSound(GameAudioManager.SoundEffect.STARTUP)
        scale.animateTo(
            targetValue = 1.0f,
            animationSpec = tween(700, easing = FastOutSlowInEasing)
        )
        alpha.animateTo(
            targetValue = 1.0f,
            animationSpec = tween(500)
        )
        delay(1200)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF060B1E), Color(0xFF0F1A42), Color(0xFF030712))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.scale(scale.value)
        ) {
            // 4 glossy cubes clustered together
            Canvas(modifier = Modifier.size(160.dp)) {
                val pad = 6.dp.toPx()
                val cubeSize = (size.minDimension - pad * 3) / 2f
                val cr = CornerRadius(cubeSize * 0.22f, cubeSize * 0.22f)

                // Top Left: Red
                drawGlossyCube(
                    color = CubeColor.RED,
                    topLeft = Offset(pad, pad),
                    size = Size(cubeSize, cubeSize),
                    cornerRadius = cr
                )
                // Top Right: Yellow
                drawGlossyCube(
                    color = CubeColor.YELLOW,
                    topLeft = Offset(pad * 2 + cubeSize, pad),
                    size = Size(cubeSize, cubeSize),
                    cornerRadius = cr
                )
                // Bottom Left: Green
                drawGlossyCube(
                    color = CubeColor.GREEN,
                    topLeft = Offset(pad, pad * 2 + cubeSize),
                    size = Size(cubeSize, cubeSize),
                    cornerRadius = cr
                )
                // Bottom Right: Blue
                drawGlossyCube(
                    color = CubeColor.BLUE,
                    topLeft = Offset(pad * 2 + cubeSize, pad * 2 + cubeSize),
                    size = Size(cubeSize, cubeSize),
                    cornerRadius = cr
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "BLOCK FURY",
                fontSize = 36.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = 3.sp
            )

            Text(
                text = "CUBE MASTER",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF38BDF8),
                letterSpacing = 6.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "PLACE. SMASH. DOMINATE.",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFACC15),
                letterSpacing = 2.sp
            )
        }
    }
}
