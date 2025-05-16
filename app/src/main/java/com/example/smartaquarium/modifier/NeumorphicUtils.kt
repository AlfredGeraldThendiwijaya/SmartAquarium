package com.example.smartaquarium.ui.modifier

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer

fun Modifier.neumorphicSurface(
    backgroundColor: Color = Color(0xFFFAFAFA),
    cornerRadius: Dp = 20.dp,
    lightShadowColor: Color = Color.White,
    darkShadowColor: Color = Color(0xFFD1D9E6),
    offset: Dp = 6.dp,
    blur: Dp = 12.dp
): Modifier = this
    .graphicsLayer {
        shadowElevation = 1f
        shape = RoundedCornerShape(cornerRadius)
        clip = true
    }
    .drawBehind {
        val shadowColorLight = lightShadowColor.copy(alpha = 0.6f)
        val shadowColorDark = darkShadowColor.copy(alpha = 0.6f)
        val radiusPx = cornerRadius.toPx()
        val blurPx = blur.toPx()
        val offsetPx = offset.toPx()

        drawRoundRect(
            color = shadowColorLight,
            topLeft = Offset(-offsetPx, -offsetPx),
            size = size,
            cornerRadius = CornerRadius(radiusPx),
        )
        drawRoundRect(
            color = shadowColorDark,
            topLeft = Offset(offsetPx, offsetPx),
            size = size,
            cornerRadius = CornerRadius(radiusPx),
        )
    }
    .background(backgroundColor, RoundedCornerShape(cornerRadius))


