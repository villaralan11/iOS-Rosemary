package com.rosemary.frontend.ui

import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sign
import kotlin.math.sin
import kotlin.math.PI

/**
 * Squircle: superellipse estilo iOS (esquinas continuas, sin cambios de curvatura).
 * Exponente n=5 aproxima el superellipse usado por los iconos de iOS.
 */
object SquircleShape : Shape {
    override fun createOutline(
        size: androidx.compose.ui.geometry.Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline = Outline.Generic(superellipsePath(size))
}

private fun superellipsePath(size: androidx.compose.ui.geometry.Size): Path {
    val n = 5f
    val w = size.width / 2f
    val h = size.height / 2f
    val steps = 48
    val path = Path()
    for (i in 0 until steps) {
        val t = (i.toFloat() / steps) * 2.0 * PI
        val cosT = cos(t)
        val sinT = sin(t)
        val x = w + w * sign(cosT.toFloat()) * abs(cosT.toFloat()).pow(2f / n)
        val y = h + h * sign(sinT.toFloat()) * abs(sinT.toFloat()).pow(2f / n)
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    return path
}
