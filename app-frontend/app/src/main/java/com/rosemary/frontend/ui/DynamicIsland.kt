package com.rosemary.frontend.ui

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Dynamic Island iOS 27 (referencia: ios-refs/frames/dynamic_island_iphone18pro/).
 * Compacta: 232×34dp, corner 17dp (pill: 2 semicírculos 17dp + rect central 198dp).
 * Expandida: 340×156dp, corner 24dp.
 * Springs: expand 0.62/200, collapse 0.75/280.
 */
@Composable
fun DynamicIsland(
    isCharging: Boolean = false,
    batteryPct: Int = 100,
) {
    val visible = rememberAppVisible()
    var expanded by remember { mutableStateOf(false) }

    val springExpand = spring<Dp>(
        dampingRatio = IOSTokens.SPRING_DI_EXPAND_RATIO,
        stiffness = IOSTokens.SPRING_DI_EXPAND_STIFF,
    )
    val springCollapse = spring<Dp>(
        dampingRatio = IOSTokens.SPRING_DI_COLLAPSE_RATIO,
        stiffness = IOSTokens.SPRING_DI_COLLAPSE_STIFF,
    )
    val width by animateDpAsState(
        if (expanded) IOSTokens.diExpandedWidth else IOSTokens.diCompactWidth,
        animationSpec = if (expanded) springExpand else springCollapse,
        label = "islandW",
    )
    val height by animateDpAsState(
        if (expanded) IOSTokens.diExpandedHeight else IOSTokens.diCompactHeight,
        animationSpec = if (expanded) springExpand else springCollapse,
        label = "islandH",
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .statusBarsPadding()
            .padding(top = 6.dp),
    ) {
        Box(
            modifier = Modifier
                .width(width)
                .height(height)
                .clip(RoundedCornerShape(if (expanded) IOSTokens.diExpandedRadius else IOSTokens.diCompactRadius))
                .background(Color(0xFF07070A))
                // Rim especular superior (vidrio negro iluminado desde arriba)
                .background(
                    Brush.verticalGradient(
                        0f to Color.White.copy(alpha = 0.14f),
                        0.09f to Color.Transparent,
                    )
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) { expanded = !expanded }
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            if (expanded) {
                IslandExpandedContent(isCharging, batteryPct, visible)
            } else {
                // Reposo: lente de cámara a la derecha (pill compacta 232×34dp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(Color(0xFF10233A), Color(0xFF05070C)),
                                )
                            ),
                    )
                }
            }
        }
    }
}

@Composable
private fun IslandExpandedContent(isCharging: Boolean, batteryPct: Int, visible: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        // Carátula
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Brush.verticalGradient(listOf(Color(0xFFB06AB3), Color(0xFF4568DC)))),
            contentAlignment = Alignment.Center,
        ) { IosGlyphIcon(IosGlyph.MUSIC_NOTE, 26.dp, color = Color.White.copy(alpha = 0.95f)) }
        Column(Modifier.weight(1f)) {
            Text(
                "Rosemary Music",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                "Liquid Glass Sessions",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            // Onda de audio animada (solo cuando visible para ahorrar GPU)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                modifier = Modifier.padding(top = 6.dp),
            ) {
                if (visible) {
                    repeat(22) { i ->
                        val transition = rememberInfiniteTransition(label = "bars")
                        val h by transition.animateFloat(
                            initialValue = 0.25f,
                            targetValue = 1f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(450, delayMillis = (i % 7) * 60),
                                repeatMode = RepeatMode.Reverse,
                            ),
                            label = "bar$i",
                        )
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height((10.dp.value * (0.4f + h)).dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.85f)),
                        )
                    }
                } else {
                    repeat(22) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(4.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.5f)),
                        )
                    }
                }
            }
        }
        // Estado de batería al expandir (vectorial, con rayo si carga)
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            IslandBattery(level = batteryPct / 100f, charging = isCharging)
            Text("$batteryPct%", color = Color.White, fontSize = 11.sp)
        }
    }
}

/** Batería vectorial vertical: contorno + nivel + rayo de carga (sin emojis). */
@Composable
private fun IslandBattery(level: Float, charging: Boolean) {
    Canvas(Modifier.size(width = 15.dp, height = 23.dp)) {
        val w = size.width
        val h = size.height
        val bodyW = w * 0.66f
        drawRoundRect(
            color = Color.White.copy(alpha = 0.55f),
            topLeft = Offset(0f, 0f),
            size = Size(bodyW, h),
            cornerRadius = CornerRadius(w * 0.16f),
            style = Stroke(width = w * 0.055f),
        )
        drawRoundRect(
            color = Color.White.copy(alpha = 0.55f),
            topLeft = Offset(bodyW + w * 0.07f, h * 0.36f),
            size = Size(w * 0.14f, h * 0.28f),
            cornerRadius = CornerRadius(w * 0.06f),
        )
        val fh = (h - w * 0.18f) * level.coerceIn(0f, 1f)
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(w * 0.09f, h - fh - w * 0.09f),
            size = Size(bodyW - w * 0.18f, fh),
            cornerRadius = CornerRadius(w * 0.09f),
        )
        if (charging) {
            val bolt = Path().apply {
                moveTo(w * 0.58f, h * 0.14f)
                lineTo(w * 0.20f, h * 0.56f)
                lineTo(w * 0.40f, h * 0.56f)
                lineTo(w * 0.28f, h * 0.90f)
                lineTo(w * 0.74f, h * 0.42f)
                lineTo(w * 0.52f, h * 0.42f)
                close()
            }
            drawPath(bolt, Color(0xFFFFD60A))
        }
    }
}
