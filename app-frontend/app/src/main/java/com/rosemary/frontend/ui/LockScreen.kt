package com.rosemary.frontend.ui

import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.TextStyle

/**
 * Lockscreen estilo iOS 26 (referencia: ios-refs/frames/lockscreen/):
 *  - fecha pequeña semibold + reloj gigante translúcido (wallpaper-matched)
 *  - botones circulares de vidrio: linterna (funcional, torch) y cámara
 *  - hint "Desliza para desbloquear" + home indicator
 *  - swipe-up con threshold -> spring críticamente amortizado + fade -> onUnlock
 */
@Composable
fun LockScreen(
    wallpaper: ImageBitmap,
    onUnlock: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val fullHeight = context.resources.displayMetrics.heightPixels.toFloat()
    val visible = rememberAppVisible()

    val offsetY = remember { Animatable(0f) }
    var torchOn by remember { mutableStateOf(false) }
    // Reloj/fecha en vivo solo cuando la app está visible (pantalla encendida)
    var now by remember { mutableStateOf(LocalTime.now()) }
    var today by remember { mutableStateOf(LocalDate.now()) }
    LaunchedEffect(visible) {
        if (!visible) return@LaunchedEffect
        now = LocalTime.now()
        today = LocalDate.now()
        while (true) {
            delay(1_000)
            if (!visible) break
            now = LocalTime.now()
        }
    }
    LaunchedEffect(visible) {
        if (!visible) return@LaunchedEffect
        while (true) {
            delay(60_000)
            if (!visible) break
            today = LocalDate.now()
        }
    }
    val time = remember(now) { String.format("%02d:%02d", now.hour, now.minute) }
    val date = remember(today) {
        today.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("es")).replaceFirstChar { it.uppercase() } +
        ", " + today.dayOfMonth + " " +
        today.month.getDisplayName(TextStyle.FULL, Locale("es"))
    }
    // Pulso del hint solo cuando visible (cero GPU en background)
    val hintAlpha = if (visible) {
        val transition = rememberInfiniteTransition(label = "hint")
        transition.animateFloat(
            initialValue = 0.45f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
            label = "hintAlpha",
        )
    } else {
        // Estado estático cuando no visible
        mutableStateOf(1f)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .graphicsLayer {
                translationY = offsetY.value // negativo = hacia arriba (convensión Compose)
                alpha = (1f + offsetY.value / (fullHeight * 0.45f)).coerceIn(0f, 1f)
            }
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onVerticalDrag = { _, amount ->
                        scope.launch { offsetY.snapTo(offsetY.value + amount) }
                    },
                    onDragEnd = {
                        if (offsetY.value < -240f) {
                            scope.launch {
                                offsetY.animateTo(-fullHeight, spring(dampingRatio = 1f, stiffness = 260f))
                                onUnlock()
                            }
                        } else {
                            scope.launch {
                                offsetY.animateTo(0f, spring(dampingRatio = 0.7f, stiffness = 320f))
                            }
                        }
                    },
                    onDragCancel = {
                        scope.launch { offsetY.animateTo(0f, spring(dampingRatio = 0.7f, stiffness = 320f)) }
                    },
                )
            },
    ) {
        // Wallpaper con blur suave + scrim (lockscreen oscurecido)
        Image(
            bitmap = wallpaper,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer {
                    val r = 22.dp.toPx()
                    renderEffect = android.graphics.RenderEffect
                        .createBlurEffect(r, r, android.graphics.Shader.TileMode.CLAMP)
                        .asComposeRenderEffect()
                },
        )
        Box(
            Modifier
                .matchParentSize()
                .background(Color.Black.copy(alpha = 0.42f)),
        )

        Column(Modifier.fillMaxSize()) {
            IOSStatusBar()
            // Icono de candado centrado (vectorial)
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                contentAlignment = Alignment.Center,
            ) {
                IosGlyphIcon(IosGlyph.LOCK, 14.dp)
            }
            // Fecha + reloj gigante
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 26.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    date.replaceFirstChar { it.uppercase() },
                    color = Color.White.copy(alpha = 0.92f),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    time,
                    color = Color.White.copy(alpha = 0.80f),
                    fontSize = 118.sp,
                    fontWeight = FontWeight.Light,
                    letterSpacing = (-2).sp,
                    style = androidx.compose.ui.text.TextStyle(
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.25f),
                            blurRadius = 24f,
                        ),
                    ),
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }

        // Botones de vidrio: linterna (izquierda) / cámara (derecha)
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 44.dp)
                .padding(bottom = 64.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            GlassCircleButton(
                glyph = IosGlyph.TORCH,
                active = torchOn,
                onTap = { torchOn = toggleTorch(context, !torchOn) },
            )
            GlassCircleButton(
                glyph = IosGlyph.CAMERA,
                active = false,
                onTap = { launchCamera(context) },
            )
        }

        // Hint de desbloqueo
        Text(
            "Desliza hacia arriba para desbloquear",
            color = Color.White.copy(alpha = hintAlpha.value * 0.85f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 38.dp),
        )

        // Home indicator
        Canvas(
            Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 10.dp)
                .width(120.dp)
                .height(4.dp),
        ) {
            drawRoundRect(
                color = Color.White.copy(alpha = 0.85f),
                cornerRadius = CornerRadius(2.dp.toPx()),
            )
        }
    }
}

@Composable
private fun GlassCircleButton(
    glyph: IosGlyph,
    active: Boolean,
    onTap: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(
                if (active) Color.White.copy(alpha = 0.42f) else Color.White.copy(alpha = 0.14f)
            )
            .clickable { onTap() },
        contentAlignment = Alignment.Center,
    ) {
        IosGlyphIcon(glyph, 21.dp)
        // Rim especular superior (vidrio iluminado)
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(CircleShape)
                .background(
                    Brush.verticalGradient(
                        0f to Color.White.copy(alpha = 0.18f),
                        0.12f to Color.Transparent,
                    )
                ),
        )
    }
}

/** Toggle del torch real (linterna) vía CameraManager. */
private fun toggleTorch(context: Context, on: Boolean): Boolean = try {
    val cm = context.getSystemService(CameraManager::class.java)
    val cameraId = cm.cameraIdList.first { id ->
        cm.getCameraCharacteristics(id)
            .get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
    }
    cm.setTorchMode(cameraId, on)
    on
} catch (_: Exception) { false }

private fun launchCamera(context: Context) = try {
    context.startActivity(
        Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
            .setPackage("com.android.camera2")
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
    )
} catch (_: Exception) { }
