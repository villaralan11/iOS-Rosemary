package com.rosemary.frontend.ui

import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sign
import kotlin.math.sin
import kotlin.math.PI

/**
 * Squircle iOS 27: superellipse n=5 con corner_ratio=0.2381 (r = size * 0.2381)
 * continuous_corner_factor=1.528655 para esquinas continuas (transición suave de curvatura).
 * Fórmula: x^n + y^n = r^n donde n=5, r = min(w,h) * cornerRatio
 */
object SquircleShape : Shape {
    override fun createOutline(
        size: androidx.compose.ui.geometry.Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline = Outline.Generic(superellipsePath(size, IOSTokens.SQUIRCLE_CORNER_RATIO, IOSTokens.SQUIRCLE_N))
}

fun superellipsePath(
    size: androidx.compose.ui.geometry.Size,
    cornerRatio: Float = IOSTokens.SQUIRCLE_CORNER_RATIO,
    n: Float = IOSTokens.SQUIRCLE_N,
): Path {
    val w = size.width / 2f
    val h = size.height / 2f
    val halfSize = min(w, h)
    val r = halfSize * cornerRatio  // radio efectivo de la esquina
    val steps = 48
    val path = Path()
    
    for (i in 0 until steps) {
        val t = (i.toFloat() / steps) * 2.0 * PI
        val cosT = cos(t)
        val sinT = sin(t)
        
        // Superellipse: |x|^n + |y|^n = r^n
        // Para esquinas continuas, interpolamos entre el rectángulo y el superellipse
        val absCosT = abs(cosT.toFloat())
        val absSinT = abs(sinT.toFloat())
        
        // Factor de suavizado para esquinas continuas (iOS usa ~1.528)
        val continuousFactor = IOSTokens.SQUIRCLE_CONTINUOUS_FACTOR
        val xOffset = w * sign(cosT.toFloat()) * absCosT.pow(2f / n) * continuousFactor
        val yOffset = h * sign(sinT.toFloat()) * absSinT.pow(2f / n) * continuousFactor
        
        // Clamp al radio efectivo
        val x = (w + sign(cosT.toFloat()) * min(abs(xOffset), r))
        val y = (h + sign(sinT.toFloat()) * min(abs(yOffset), r))
        
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    return path
}
