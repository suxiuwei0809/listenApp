package com.example.fitforge.ui.screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitforge.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.*

// ---- Shockwave data ----
data class ShockwaveState(
    val createdAtMs: Long,
    val delayMs: Int
)

// ---- Particle data ----
data class ParticleState(
    val createdAtMs: Long,
    val angle: Float,
    val dist: Float,
    val particleColor: Color,
    val delayMs: Int,
    val size: Float
)

private const val SHOCKWAVE_DURATION = 800L
private const val PARTICLE_DURATION = 700L

// ============================================================
// Counter button - all visual effects
// ============================================================

@Composable
fun CounterButton(
    count: Int,
    color: Color,
    modifier: Modifier = Modifier,
    size: Dp = 150.dp,
    onIncrement: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current

    // ---- Breath ring ----
    val breathTransition = rememberInfiniteTransition(label = "breath")
    val breathAlpha by breathTransition.animateFloat(
        initialValue = 0.12f, targetValue = 0.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ), label = "breathAlpha"
    )
    val breathSpread by breathTransition.animateFloat(
        initialValue = 2f, targetValue = 14f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ), label = "breathSpread"
    )

    // ---- Number bounce animation ----
    val numberScale = remember { Animatable(1f) }
    var prevCount by remember { mutableIntStateOf(count) }
    LaunchedEffect(count) {
        if (count > prevCount) {
            numberScale.snapTo(1f)
            numberScale.animateTo(1.35f, tween(140, easing = EaseOutCubic))
            numberScale.animateTo(
                1f, spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        }
        prevCount = count
    }

    // ---- Press feedback ----
    val pressScale = remember { Animatable(1f) }

    // ---- Shockwave & particle lists ----
    val shockwaves = remember { mutableStateListOf<ShockwaveState>() }
    val particles = remember { mutableStateListOf<ParticleState>() }

    // Drive Canvas redraw via refresh tick
    var frameTick by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(16) // ~60fps
            frameTick++
            // Cleanup expired effects
            val now = System.currentTimeMillis()
            shockwaves.removeAll { now - it.createdAtMs > SHOCKWAVE_DURATION + it.delayMs + 200 }
            particles.removeAll { now - it.createdAtMs > PARTICLE_DURATION + it.delayMs + 200 }
        }
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // ---- Breath ring layer ----
        Canvas(modifier = Modifier.fillMaxSize()) {
            val drawSize = this.size // DrawScope.size: Size
            val center = Offset(drawSize.width / 2, drawSize.height / 2)
            val radius = drawSize.width / 2
            val breathPx = with(density) { breathSpread.dp.toPx() }
            drawCircle(
                color = color.copy(alpha = breathAlpha),
                radius = radius + breathPx,
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )
            drawCircle(
                color = color.copy(alpha = breathAlpha * 0.4f),
                radius = radius + breathPx * 1.6f,
                center = center,
                style = Stroke(width = 1.dp.toPx())
            )
        }

        // ---- Shockwave + particle layer ----
        val now = System.currentTimeMillis()
        Canvas(modifier = Modifier.fillMaxSize()) {
            val drawSize = this.size
            val cx = drawSize.width / 2
            val cy = drawSize.height / 2
            val halfSize = drawSize.width / 2

            // Shockwave
            shockwaves.forEach { sw ->
                val elapsed = (now - sw.createdAtMs - sw.delayMs).coerceIn(0L, SHOCKWAVE_DURATION)
                val progress = elapsed.toFloat() / SHOCKWAVE_DURATION.toFloat()
                val ringRadius = halfSize + progress * halfSize * 2.5f
                val alpha = (1f - progress) * 0.7f
                val strokeW = (1f - progress) * 2.5f
                drawCircle(
                    color = color.copy(alpha = alpha),
                    radius = ringRadius,
                    center = Offset(cx, cy),
                    style = Stroke(width = strokeW.dp.toPx())
                )
            }

            // Particles
            particles.forEach { p ->
                val elapsed = (now - p.createdAtMs - p.delayMs).coerceIn(0L, PARTICLE_DURATION)
                val progress = elapsed.toFloat() / PARTICLE_DURATION.toFloat()
                val dx = cos(p.angle) * p.dist * progress
                val dy = sin(p.angle) * p.dist * progress
                val particleSize = p.size * (1f - progress * 0.8f)
                // Glow
                drawCircle(
                    color = p.particleColor.copy(alpha = (1f - progress) * 0.6f),
                    radius = particleSize * 2.5f,
                    center = Offset(cx + dx, cy + dy)
                )
                // Particle
                drawCircle(
                    color = p.particleColor.copy(alpha = 1f - progress),
                    radius = particleSize,
                    center = Offset(cx + dx, cy + dy)
                )
            }
        }

        // ---- Main button ----
        Box(
            modifier = Modifier
                .size(size)
                .scale(pressScale.value)
                .clip(CircleShape)
                .background(Color(0xFF0C0C1E))
                .border(1.5.dp, GlassBorder, CircleShape)
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            val pressed = event.changes.any { it.pressed }
                            if (pressed && pressScale.value > 0.9f) {
                                scope.launch {
                                    pressScale.animateTo(
                                        0.88f,
                                        spring(dampingRatio = 0.35f, stiffness = 600f)
                                    )
                                }
                            }
                            if (!pressed && pressScale.value < 1f) {
                                scope.launch {
                                    pressScale.animateTo(
                                        1f,
                                        spring(
                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                            stiffness = Spring.StiffnessLow
                                        )
                                    )
                                }
                            }
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            // Top highlight
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.1f),
                                Color.Transparent
                            ),
                            center = Offset(0.35f * size.value, 0.35f * size.value),
                            radius = size.value * 0.65f
                        )
                    )
            )

            // Count number (with bounce animation)
            Text(
                text = count.toString(),
                color = Color.White,
                fontSize = (size.value * 0.4f).sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.scale(numberScale.value)
            )
        }

        // ---- Transparent click layer (over button) ----
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    val tickMs = System.currentTimeMillis()
                    onIncrement()

                    // 3 shockwave layers
                    for (i in 0 until 3) {
                        shockwaves.add(ShockwaveState(tickMs, i * 40))
                    }

                    // 18 particles
                    val pColors = listOf(color, Color.White, NeonPurple, color.copy(alpha = 0.8f))
                    for (i in 0 until 18) {
                        val angle = (PI.toFloat() * 2f / 18f) * i +
                                (Math.random().toFloat() * 0.3f)
                        val halfPx = with(density) { size.toPx() / 2f }
                        val dist = halfPx + (Math.random().toFloat() * halfPx * 2.5f)
                        particles.add(
                            ParticleState(
                                createdAtMs = tickMs,
                                angle = angle,
                                dist = dist,
                                particleColor = pColors[i % pColors.size],
                                delayMs = (Math.random() * 150).toInt(),
                                size = 3f + Math.random().toFloat() * 6f
                            )
                        )
                    }
                }
        )
    }
}

// ============================================================
// Full-screen flash effect
// ============================================================

@Composable
fun ScreenFlash(
    visible: Boolean,
    onDone: () -> Unit
) {
    if (!visible) return
    val alpha = remember { Animatable(0.12f) }
    LaunchedEffect(Unit) {
        alpha.snapTo(0.12f)
        delay(80)
        alpha.animateTo(0f, tween(180, easing = EaseOutCubic))
        onDone()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White.copy(alpha = alpha.value))
    )
}

// ============================================================
// Screen shake effect
// ============================================================

@Composable
fun ScreenShake(
    visible: Boolean,
    onDone: () -> Unit,
    content: @Composable (Modifier) -> Unit
) {
    val shakeOffset = remember { Animatable(0f) }
    LaunchedEffect(visible) {
        if (visible) {
            val sequence = floatArrayOf(-4f, 4f, -3f, 3f, -2f, 2f, -1f, 1f, 0f)
            for (offset in sequence) {
                shakeOffset.snapTo(offset)
                delay(35)
            }
            shakeOffset.snapTo(0f)
            onDone()
        }
    }
    content(Modifier.offset { IntOffset(shakeOffset.value.toInt(), 0) })
}

// ============================================================
// Standalone animated count (for scenes without full button)
// ============================================================

@Composable
fun AnimatedCount(
    count: Int,
    color: Color = Color.White,
    fontSize: androidx.compose.ui.unit.TextUnit = 56.sp,
    fontWeight: FontWeight = FontWeight.Bold,
    modifier: Modifier = Modifier
) {
    val scale = remember { Animatable(1f) }
    var prev by remember { mutableIntStateOf(count) }
    LaunchedEffect(count) {
        if (count > prev) {
            scale.snapTo(1f)
            scale.animateTo(1.35f, tween(140, easing = EaseOutCubic))
            scale.animateTo(
                1f, spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        }
        prev = count
    }
    Text(
        text = count.toString(),
        color = color,
        fontSize = fontSize,
        fontWeight = fontWeight,
        textAlign = TextAlign.Center,
        modifier = modifier.scale(scale.value)
    )
}
