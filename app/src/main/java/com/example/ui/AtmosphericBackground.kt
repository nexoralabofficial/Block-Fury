package com.example.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.example.data.BoardTheme
import kotlin.random.Random

private data class AmbientParticle(
    val relX: Float,
    val relY: Float,
    val radius: Float,
    val speed: Float,
    val color: Color,
    val baseAlpha: Float
)

@Composable
fun AtmosphericBackground(
    theme: BoardTheme,
    reducedMotion: Boolean = false,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ambient_anim")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ambient_time"
    )

    // Pre-calculate deterministic background particles
    val particles = remember {
        val list = mutableListOf<AmbientParticle>()
        val rnd = Random(42)
        val colors = listOf(
            Color(0xFF38BDF8), // Cyan
            Color(0xFF818CF8), // Indigo
            Color(0xFFC084FC), // Purple
            Color(0xFFFBBF24)  // Gold
        )
        for (i in 0 until 24) {
            list.add(
                AmbientParticle(
                    relX = rnd.nextFloat(),
                    relY = rnd.nextFloat(),
                    radius = rnd.nextFloat() * 2.5f + 1.2f,
                    speed = rnd.nextFloat() * 0.4f + 0.2f,
                    color = colors[rnd.nextInt(colors.size)],
                    baseAlpha = rnd.nextFloat() * 0.25f + 0.12f
                )
            )
        }
        list
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // 1. Deep midnight-blue / indigo layered background
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        theme.appBackgroundStart,
                        Color(0xFF0A1026),
                        theme.appBackgroundEnd,
                        Color(0xFF030712)
                    )
                )
            )

            // 2. Center ambient radial backlight (giving board area a glow)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF1E3A8A).copy(alpha = 0.35f),
                        Color(0xFF0F172A).copy(alpha = 0.12f),
                        Color.Transparent
                    ),
                    center = Offset(w * 0.5f, h * 0.46f),
                    radius = w * 0.65f
                )
            )

            // 3. Faint subtle isometric grid / diagonal streaks
            val strokeColor = Color(0xFF38BDF8).copy(alpha = 0.035f)
            val gridStep = 48f
            var x = -h
            while (x < w + h) {
                drawLine(
                    color = strokeColor,
                    start = Offset(x, 0f),
                    end = Offset(x + h, h),
                    strokeWidth = 1f
                )
                x += gridStep * 2
            }

            // 4. Slowly floating particles
            val animOffset = if (reducedMotion) 0f else time
            for (p in particles) {
                val py = ((p.relY - animOffset * p.speed) % 1f + 1f) % 1f * h
                val px = (p.relX * w + Math.sin(py.toDouble() * 0.01).toFloat() * 10f) % w

                drawCircle(
                    color = p.color.copy(alpha = p.baseAlpha),
                    radius = p.radius,
                    center = Offset(px, py)
                )
            }
        }

        content()
    }
}
