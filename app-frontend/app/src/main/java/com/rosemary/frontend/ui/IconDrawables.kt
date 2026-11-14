package com.rosemary.frontend.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.TextStyle
import java.util.Locale
import kotlinx.coroutines.delay

/**
 * IconDrawables: iconografia 100% vectorial nativa (Canvas / Path / Stroke),
 * cero emojis en todo el proyecto.
 *
 * 1. Glifos del sistema (IosGlyph) para LockScreen, Control Center y Dynamic Island:
 *    candado cerrado/abierto, linterna, camara, sol, bocina con ondas, Wi-Fi,
 *    runa Bluetooth, media (prev/play/pause/next) y nota musical.
 * 2. Iconos de apps base estilo iOS (IOSAppIconKind) dibujados en el squircle:
 *    engranajes, auricular, burbuja, camara, brujula, nota doble, caratula de
 *    reloj con hora real (LocalTime.now()), tarjeta de calendario con fecha real
 *    (LocalDate.now()), flor, calculadora, carpeta y orbe LiquidGlass.
 */

// ============================ Glifos del sistema ============================

enum class IosGlyph {
    SUN, SPEAKER, WIFI, BLUETOOTH,
    PREV, PLAY, PAUSE, NEXT,
    TORCH, LOCK, LOCK_OPEN, CAMERA, MUSIC_NOTE,
}

@Composable
fun IosGlyphIcon(
    glyph: IosGlyph,
    size: Dp,
    color: Color = Color.White,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier.size(size)) {
        scale(this.size.minDimension / 24f, Offset.Zero) {
            drawGlyph24(glyph, color)
        }
    }
}

// Paths pre-construidos (espacio 24x24): cero allocaciones dentro de draw
private val glyphCone: Path by lazy {
    Path().apply {
        moveTo(4.2f, 9.4f); lineTo(8.8f, 9.4f); lineTo(13.6f, 4.9f)
        lineTo(13.6f, 19.1f); lineTo(8.8f, 14.6f); lineTo(4.2f, 14.6f); close()
    }
}
private val glyphBt: Path by lazy {
    Path().apply {
        moveTo(12f, 3.0f); lineTo(17.2f, 7.3f); lineTo(12f, 12f)
        lineTo(17.2f, 16.7f); lineTo(12f, 21.0f)
    }
}
private val glyphPrev: Path by lazy {
    Path().apply { moveTo(18.4f, 5.6f); lineTo(9.2f, 12f); lineTo(18.4f, 18.4f); close() }
}
private val glyphNext: Path by lazy {
    Path().apply { moveTo(5.6f, 5.6f); lineTo(14.8f, 12f); lineTo(5.6f, 18.4f); close() }
}
private val glyphPlay: Path by lazy {
    Path().apply { moveTo(8.6f, 5.0f); lineTo(19.0f, 12f); lineTo(8.6f, 19.0f); close() }
}
private val glyphNoteFlag: Path by lazy {
    Path().apply {
        moveTo(11.4f, 5.8f)
        cubicTo(16.8f, 6.6f, 17.8f, 9.6f, 16.6f, 12.2f)
        cubicTo(16.9f, 9.4f, 14.6f, 8.4f, 11.4f, 9.0f)
        close()
    }
}

private fun DrawScope.drawGlyph24(glyph: IosGlyph, color: Color) {
    when (glyph) {
        IosGlyph.SUN -> {
            drawCircle(color, radius = 4.4f, center = Offset(12f, 12f))
            for (i in 0 until 8) {
                rotate(i * 45f, pivot = Offset(12f, 12f)) {
                    drawLine(color, Offset(12f, 2.6f), Offset(12f, 5.2f), strokeWidth = 1.7f, cap = StrokeCap.Round)
                }
            }
        }

        IosGlyph.SPEAKER -> {
            drawPath(glyphCone, color)
            drawArc(
                color, startAngle = -52f, sweepAngle = 104f, useCenter = false,
                topLeft = Offset(13.6f - 3.8f, 12f - 3.8f), size = Size(7.6f, 7.6f),
                style = Stroke(width = 1.9f, cap = StrokeCap.Round),
            )
            drawArc(
                color, startAngle = -52f, sweepAngle = 104f, useCenter = false,
                topLeft = Offset(13.6f - 6.9f, 12f - 6.9f), size = Size(13.8f, 13.8f),
                style = Stroke(width = 1.9f, cap = StrokeCap.Round),
            )
        }

        IosGlyph.WIFI -> {
            val cx = 12f
            val cy = 19.2f
            for (i in 0 until 3) {
                val r = 3.2f + i * 3.2f
                drawArc(
                    color, startAngle = 220f, sweepAngle = 100f, useCenter = false,
                    topLeft = Offset(cx - r, cy - r), size = Size(r * 2, r * 2),
                    style = Stroke(width = 2.1f, cap = StrokeCap.Round),
                )
            }
            drawCircle(color, radius = 1.6f, center = Offset(cx, cy - 1.8f))
        }

        IosGlyph.BLUETOOTH -> {
            drawPath(glyphBt, color, style = Stroke(width = 2.0f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        }

        IosGlyph.PREV -> {
            drawRoundRect(color, topLeft = Offset(5.6f, 5.6f), size = Size(2.0f, 12.8f), cornerRadius = CornerRadius(1f))
            drawPath(glyphPrev, color)
        }

        IosGlyph.NEXT -> {
            drawRoundRect(color, topLeft = Offset(16.4f, 5.6f), size = Size(2.0f, 12.8f), cornerRadius = CornerRadius(1f))
            drawPath(glyphNext, color)
        }

        IosGlyph.PLAY -> drawPath(glyphPlay, color)

        IosGlyph.PAUSE -> {
            drawRoundRect(color, topLeft = Offset(6.8f, 5.6f), size = Size(3.2f, 12.8f), cornerRadius = CornerRadius(1.2f))
            drawRoundRect(color, topLeft = Offset(14.0f, 5.6f), size = Size(3.2f, 12.8f), cornerRadius = CornerRadius(1.2f))
        }

        IosGlyph.TORCH -> {
            drawRoundRect(color, topLeft = Offset(8.0f, 4.2f), size = Size(8.0f, 5.0f), cornerRadius = CornerRadius(1.2f))
            drawRoundRect(color, topLeft = Offset(9.7f, 10.2f), size = Size(4.6f, 9.8f), cornerRadius = CornerRadius(1.2f))
            drawRoundRect(color, topLeft = Offset(14.7f, 11.4f), size = Size(1.3f, 2.6f), cornerRadius = CornerRadius(0.6f))
        }

        IosGlyph.LOCK -> {
            drawArc(
                color, startAngle = 180f, sweepAngle = 180f, useCenter = false,
                topLeft = Offset(12f - 4.1f, 12f - 4.1f), size = Size(8.2f, 8.2f),
                style = Stroke(width = 2.2f, cap = StrokeCap.Round),
            )
            drawRoundRect(color, topLeft = Offset(6.4f, 10.6f), size = Size(11.2f, 9.2f), cornerRadius = CornerRadius(2.4f))
        }

        IosGlyph.LOCK_OPEN -> {
            // Arco levantado hacia la derecha: candado abierto
            drawArc(
                color, startAngle = -75f, sweepAngle = 150f, useCenter = false,
                topLeft = Offset(12f - 4.1f, 12f - 4.1f), size = Size(8.2f, 8.2f),
                style = Stroke(width = 2.2f, cap = StrokeCap.Round),
            )
            drawRoundRect(color, topLeft = Offset(6.4f, 10.6f), size = Size(11.2f, 9.2f), cornerRadius = CornerRadius(2.4f))
        }

        IosGlyph.CAMERA -> {
            drawRoundRect(color, topLeft = Offset(8.4f, 4.6f), size = Size(7.2f, 3.6f), cornerRadius = CornerRadius(1.2f))
            drawRoundRect(
                color, topLeft = Offset(2.8f, 6.8f), size = Size(18.4f, 12.6f),
                cornerRadius = CornerRadius(3.0f), style = Stroke(width = 2.0f),
            )
            drawCircle(color, radius = 3.7f, center = Offset(12f, 13.1f), style = Stroke(width = 2.0f))
            drawCircle(color, radius = 1.0f, center = Offset(17.8f, 9.4f))
        }

        IosGlyph.MUSIC_NOTE -> {
            rotate(-20f, pivot = Offset(9.2f, 17.8f)) {
                drawOval(color, topLeft = Offset(6.4f, 15.7f), size = Size(5.6f, 4.2f))
            }
            drawRect(color, topLeft = Offset(11.4f, 5.8f), size = Size(1.6f, 12.0f))
            drawPath(glyphNoteFlag, color)
        }
    }
}

// ======================== Iconos de apps base estilo iOS ========================

enum class IOSAppIconKind {
    SETTINGS, PHONE, MESSAGES, CAMERA, PHOTOS, SAFARI, MUSIC,
    CLOCK, CALCULATOR, FILES, CALENDAR, ORB,
}

data class IOSAppTheme(val kind: IOSAppIconKind, val background: Brush)

private fun v(c1: Long, c2: Long) = Brush.verticalGradient(listOf(Color(c1), Color(c2)))

val IOS_APP_THEMES: Map<String, IOSAppTheme> = mapOf(
    // Ajustes: acero metalico
    "com.android.settings" to IOSAppTheme(IOSAppIconKind.SETTINGS, v(0xFF9DA2AC, 0xFF5B616C)),
    // Telefono / Mensajes: verde esmeralda iOS (#34C759 -> #248A3D)
    "com.android.dialer" to IOSAppTheme(IOSAppIconKind.PHONE, v(0xFF34C759, 0xFF248A3D)),
    "com.google.android.dialer" to IOSAppTheme(IOSAppIconKind.PHONE, v(0xFF34C759, 0xFF248A3D)),
    "com.android.messaging" to IOSAppTheme(IOSAppIconKind.MESSAGES, v(0xFF5BE06B, 0xFF2FAE4C)),
    "com.google.android.apps.messaging" to IOSAppTheme(IOSAppIconKind.MESSAGES, v(0xFF5BE06B, 0xFF2FAE4C)),
    // Camara: metal claro
    "com.android.camera2" to IOSAppTheme(IOSAppIconKind.CAMERA, v(0xFFF4F4F8, 0xFFC6C7D2)),
    "com.google.android.GoogleCamera" to IOSAppTheme(IOSAppIconKind.CAMERA, v(0xFFF4F4F8, 0xFFC6C7D2)),
    // Fotos: blanco con flor multicolor
    "com.google.android.apps.photos" to IOSAppTheme(IOSAppIconKind.PHOTOS, v(0xFFFFFFFF, 0xFFE9E9F0)),
    // Navegador: brujula azul
    "com.android.chrome" to IOSAppTheme(IOSAppIconKind.SAFARI, v(0xFF2EA8FF, 0xFF0A5BD3)),
    // Musica: rojo/coral (#FA243C -> #FF5E3A)
    "com.google.android.apps.youtube.music" to IOSAppTheme(IOSAppIconKind.MUSIC, v(0xFFFA243C, 0xFFFF5E3A)),
    "com.android.music" to IOSAppTheme(IOSAppIconKind.MUSIC, v(0xFFFA243C, 0xFFFF5E3A)),
    // Reloj: caratula blanca
    "com.android.deskclock" to IOSAppTheme(IOSAppIconKind.CLOCK, v(0xFFFFFFFF, 0xFFE4E4EA)),
    "com.google.android.deskclock" to IOSAppTheme(IOSAppIconKind.CLOCK, v(0xFFFFFFFF, 0xFFE4E4EA)),
    // Calculadora: grafito
    "com.android.calculator2" to IOSAppTheme(IOSAppIconKind.CALCULATOR, v(0xFF2E2E33, 0xFF101014)),
    // Archivos: azul
    "com.android.documentsui" to IOSAppTheme(IOSAppIconKind.FILES, v(0xFF4FA8FF, 0xFF1B6BE8)),
    "com.google.android.documentsui" to IOSAppTheme(IOSAppIconKind.FILES, v(0xFF4FA8FF, 0xFF1B6BE8)),
    // Calendario: blanco con fecha real
    "com.android.calendar" to IOSAppTheme(IOSAppIconKind.CALENDAR, v(0xFFFFFFFF, 0xFFEFEFF4)),
    "com.google.android.calendar" to IOSAppTheme(IOSAppIconKind.CALENDAR, v(0xFFFFFFFF, 0xFFEFEFF4)),
    // Nuestro propio launcher: orbe de vidrio LiquidGlass
    "com.rosemary.frontend" to IOSAppTheme(IOSAppIconKind.ORB, v(0xFF2B2C4E, 0xFF12142E)),
)

fun iosAppThemeFor(packageName: String): IOSAppTheme? = IOS_APP_THEMES[packageName]

/** Superficie de icono iOS: squircle con gradiente + glifo vectorial (o cara viva). */
@Composable
fun IOSAppIconSurface(theme: IOSAppTheme, size: Dp, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(size)
            .clip(SquircleShape)
            .background(theme.background),
        contentAlignment = Alignment.Center,
    ) {
        when (theme.kind) {
            IOSAppIconKind.CLOCK -> LiveClockFace(Modifier.matchParentSize())
            IOSAppIconKind.CALENDAR -> LiveCalendarFace(Modifier.matchParentSize(), size)
            else -> Canvas(Modifier.matchParentSize()) {
                scale(this.size.minDimension / 24f, Offset.Zero) {
                    drawAppGlyph24(theme.kind)
                }
            }
        }
    }
}

private val appBubble: Path by lazy {
    Path().apply { addRoundRect(RoundRect(3.4f, 4.8f, 20.6f, 16.8f, CornerRadius(5.2f))) }
}
private val appBubbleTail: Path by lazy {
    Path().apply { moveTo(7.8f, 15.8f); lineTo(6.8f, 20.6f); lineTo(12.0f, 16.6f); close() }
}
private val appNeedleRed: Path by lazy {
    Path().apply {
        moveTo(16.7f, 7.3f); lineTo(13.1f, 13.1f); lineTo(10.9f, 10.9f); close()
    }
}
private val appNeedleWhite: Path by lazy {
    Path().apply {
        moveTo(7.3f, 16.7f); lineTo(13.1f, 13.1f); lineTo(10.9f, 10.9f); close()
    }
}
private val appNoteBeam: Path by lazy {
    Path().apply {
        moveTo(11.0f, 5.6f); lineTo(20.7f, 4.2f); lineTo(20.7f, 7.2f); lineTo(11.0f, 8.6f); close()
    }
}

private fun DrawScope.drawAppGlyph24(kind: IOSAppIconKind) {
    when (kind) {
        IOSAppIconKind.SETTINGS -> {
            val gear = Color(0xFFF5F6FA)
            drawGear(gear, cx = 9.6f, cy = 9.6f, rOuter = 5.6f, teeth = 8, ringW = 2.6f, toothW = 1.9f)
            drawGear(gear, cx = 16.4f, cy = 16.2f, rOuter = 4.1f, teeth = 8, ringW = 2.1f, toothW = 1.5f)
        }

        IOSAppIconKind.PHONE -> {
            drawArc(
                Color.White, startAngle = 190f, sweepAngle = -170f, useCenter = false,
                topLeft = Offset(12f - 7.4f, 11.4f - 7.4f), size = Size(14.8f, 14.8f),
                style = Stroke(width = 5.6f, cap = StrokeCap.Round),
            )
        }

        IOSAppIconKind.MESSAGES -> {
            drawPath(appBubble, Color.White)
            drawPath(appBubbleTail, Color.White)
        }

        IOSAppIconKind.CAMERA -> {
            val body = Color(0xFF33343C)
            val hole = Color(0xFFD8D9E2)
            drawRoundRect(body, topLeft = Offset(8.4f, 4.4f), size = Size(7.2f, 3.8f), cornerRadius = CornerRadius(1.2f))
            drawRoundRect(body, topLeft = Offset(2.8f, 6.6f), size = Size(18.4f, 12.8f), cornerRadius = CornerRadius(3.0f))
            drawCircle(hole, radius = 4.3f, center = Offset(12f, 13.0f))
            drawCircle(body, radius = 2.9f, center = Offset(12f, 13.0f), style = Stroke(width = 1.5f))
            drawCircle(body, radius = 1.1f, center = Offset(12f, 13.0f))
            drawCircle(hole, radius = 1.0f, center = Offset(17.8f, 9.2f))
        }

        IOSAppIconKind.PHOTOS -> {
            val petals = listOf(
                Color(0xFFFFC400), Color(0xFFFF8A00), Color(0xFFFF375F), Color(0xFFAF52DE),
                Color(0xFF5856D6), Color(0xFF007AFF), Color(0xFF32ADE6), Color(0xFF34C759),
            )
            for (i in 0 until 8) {
                rotate(i * 45f, pivot = Offset(12f, 12f)) {
                    drawOval(petals[i].copy(alpha = 0.72f), topLeft = Offset(9.9f, 3.9f), size = Size(4.2f, 7.6f))
                }
            }
        }

        IOSAppIconKind.SAFARI -> {
            val white = Color.White
            drawCircle(white, radius = 9.3f, center = Offset(12f, 12f), style = Stroke(width = 1.5f))
            for (i in 0 until 4) {
                rotate(i * 90f, pivot = Offset(12f, 12f)) {
                    drawLine(white, Offset(12f, 3.6f), Offset(12f, 5.4f), strokeWidth = 1.5f, cap = StrokeCap.Round)
                }
            }
            drawPath(appNeedleRed, Color(0xFFFF3B30))
            drawPath(appNeedleWhite, white)
        }

        IOSAppIconKind.MUSIC -> {
            val note = Color.White
            rotate(-16f, pivot = Offset(8.2f, 17.9f)) { drawOval(note, topLeft = Offset(5.2f, 15.6f), size = Size(6.0f, 4.6f)) }
            rotate(-16f, pivot = Offset(17.0f, 16.3f)) { drawOval(note, topLeft = Offset(14.0f, 14.0f), size = Size(6.0f, 4.6f)) }
            drawRect(note, topLeft = Offset(11.0f, 5.6f), size = Size(1.7f, 12.2f))
            drawRect(note, topLeft = Offset(19.0f, 4.2f), size = Size(1.7f, 12.2f))
            drawPath(appNoteBeam, note)
        }

        IOSAppIconKind.CALCULATOR -> {
            drawRoundRect(
                Color(0xFF58585E), topLeft = Offset(3.6f, 3.6f), size = Size(16.8f, 3.6f),
                cornerRadius = CornerRadius(1.2f),
            )
            val keyW = 3.6f
            val keyH = 2.7f
            val gapX = 0.8f
            val gapY = 0.8f
            for (row in 0 until 4) {
                for (col in 0 until 4) {
                    val x = 3.6f + col * (keyW + gapX)
                    val y = 8.6f + row * (keyH + gapY)
                    val keyColor = if (col == 3) Color(0xFFFF9F0A) else Color(0xFFB9B9C2).copy(alpha = 0.92f)
                    drawRoundRect(keyColor, topLeft = Offset(x, y), size = Size(keyW, keyH), cornerRadius = CornerRadius(0.9f))
                }
            }
        }

        IOSAppIconKind.FILES -> {
            drawRoundRect(Color.White, topLeft = Offset(2.8f, 5.2f), size = Size(8.0f, 4.2f), cornerRadius = CornerRadius(1.4f))
            drawRoundRect(Color.White, topLeft = Offset(2.8f, 7.0f), size = Size(18.4f, 11.8f), cornerRadius = CornerRadius(2.2f))
        }

        IOSAppIconKind.ORB -> {
            drawCircle(
                Brush.radialGradient(
                    listOf(Color(0xFFBFE0FF), Color(0xFF3D6FE0), Color(0xFF1A2150)),
                    center = Offset(12f, 12f),
                    radius = 7.6f,
                ),
                radius = 7.2f,
                center = Offset(12f, 12f),
            )
            drawCircle(
                Color.White.copy(alpha = 0.55f),
                radius = 7.2f,
                center = Offset(12f, 12f),
                style = Stroke(width = 1.1f),
            )
            drawCircle(Color.White.copy(alpha = 0.85f), radius = 1.7f, center = Offset(9.4f, 8.6f))
        }

        else -> { /* CLOCK y CALENDAR usan composables vivos, no este Canvas */ }
    }
}

private fun DrawScope.drawGear(
    color: Color,
    cx: Float,
    cy: Float,
    rOuter: Float,
    teeth: Int,
    ringW: Float,
    toothW: Float,
) {
    drawCircle(color, radius = rOuter, center = Offset(cx, cy), style = Stroke(width = ringW))
    for (i in 0 until teeth) {
        rotate(i * (360f / teeth), pivot = Offset(cx, cy)) {
            drawRoundRect(
                color,
                topLeft = Offset(cx - toothW / 2f, cy - rOuter - 0.9f),
                size = Size(toothW, 2.2f),
                cornerRadius = CornerRadius(toothW * 0.3f),
            )
        }
    }
    drawCircle(color, radius = ringW * 0.55f, center = Offset(cx, cy))
}

/** Caratula de reloj con manecillas conectadas a LocalTime.now() (en vivo). */
@Composable
private fun LiveClockFace(modifier: Modifier) {
    var now by remember { mutableStateOf(LocalTime.now()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1_000)
            now = LocalTime.now()
        }
    }
    Canvas(modifier) {
        val faceR = (this.size.minDimension / 2f) * 0.93f
        drawCircle(Color(0xFFFCFCFE), radius = faceR, center = center)
        val tickInner = faceR * 0.80f
        val tickOuter = faceR * 0.88f
        for (i in 0 until 12) {
            rotate(i * 30f, pivot = center) {
                drawLine(
                    color = if (i % 3 == 0) Color(0xFF1C1C1E) else Color(0xFF9A9AA2),
                    start = Offset(center.x, center.y - tickOuter),
                    end = Offset(center.x, center.y - tickInner),
                    strokeWidth = faceR * (if (i % 3 == 0) 0.06f else 0.035f),
                    cap = StrokeCap.Round,
                )
            }
        }
        val minute = now.minute
        val hourAngle = ((now.hour % 12) + minute / 60f) * 30f
        val minuteAngle = (minute + now.second / 60f) * 6f
        rotate(hourAngle, pivot = center) {
            drawLine(
                Color(0xFF1C1C1E),
                Offset(center.x, center.y + faceR * 0.10f),
                Offset(center.x, center.y - faceR * 0.46f),
                strokeWidth = faceR * 0.085f,
                cap = StrokeCap.Round,
            )
        }
        rotate(minuteAngle, pivot = center) {
            drawLine(
                Color(0xFF1C1C1E),
                Offset(center.x, center.y + faceR * 0.12f),
                Offset(center.x, center.y - faceR * 0.70f),
                strokeWidth = faceR * 0.06f,
                cap = StrokeCap.Round,
            )
        }
        drawCircle(Color(0xFFFF9500), radius = faceR * 0.05f, center = center)
    }
}

/** Tarjeta de calendario: dia de la semana rojo arriba + numero real (LocalDate.now()). */
@Composable
private fun LiveCalendarFace(modifier: Modifier, size: Dp) {
    var today by remember { mutableStateOf(LocalDate.now()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(60_000)
            today = LocalDate.now()
        }
    }
    Column(
        modifier = modifier.padding(top = size * 0.09f),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            today.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()).uppercase(),
            color = Color(0xFFE5342A),
            fontSize = (size.value * 0.17f).sp,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            today.dayOfMonth.toString(),
            color = Color(0xFF1C1C1E),
            fontSize = (size.value * 0.52f).sp,
            fontWeight = FontWeight.Light,
            modifier = Modifier.padding(top = size * 0.01f),
        )
    }
}
