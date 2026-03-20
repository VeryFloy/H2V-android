package com.h2v.messenger.ui.theme

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.glassBackground(
    cornerRadius: Dp = 16.dp,
    alpha: Float = 0.94f
): Modifier = this
    .clip(RoundedCornerShape(cornerRadius))
    .background(
        Brush.verticalGradient(
            listOf(
                H2VColors.SurfaceGlass.copy(alpha = alpha),
                H2VColors.Surface.copy(alpha = alpha * 0.95f),
                H2VColors.Background.copy(alpha = alpha)
            )
        ),
        RoundedCornerShape(cornerRadius)
    )

fun Modifier.glassCard(
    cornerRadius: Dp = 16.dp
): Modifier = this
    .glassBackground(cornerRadius)
    .border(
        width = 0.5.dp,
        brush = Brush.verticalGradient(
            listOf(
                H2VColors.GlassWhite15,
                H2VColors.GlassWhite05,
                Color.Transparent
            )
        ),
        shape = RoundedCornerShape(cornerRadius)
    )

fun Modifier.specularTopLine(): Modifier = this.drawBehind {
    drawLine(
        brush = Brush.horizontalGradient(
            listOf(
                Color.Transparent,
                H2VColors.GlassWhite05,
                H2VColors.SpecularHighlight.copy(alpha = 0.15f),
                H2VColors.SpecularHighlight.copy(alpha = 0.15f),
                H2VColors.GlassWhite05,
                Color.Transparent
            )
        ),
        start = Offset(0f, 0f),
        end = Offset(size.width, 0f),
        strokeWidth = 1f
    )
}

fun Modifier.glowEffect(
    color: Color = H2VColors.AccentBlue,
    radius: Dp = 40.dp
): Modifier = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )
    this.drawBehind {
        drawCircle(
            color = color.copy(alpha = glowAlpha),
            radius = radius.toPx(),
            center = center
        )
    }
}

fun Modifier.glassSurface(): Modifier = this
    .background(
        Brush.verticalGradient(
            listOf(
                H2VColors.SurfaceGlass,
                H2VColors.Background
            )
        )
    )
    .specularTopLine()
