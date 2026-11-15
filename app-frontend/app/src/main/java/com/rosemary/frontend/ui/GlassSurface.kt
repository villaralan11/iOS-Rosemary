package com.rosemary.frontend.ui

import android.graphics.Bitmap
import android.graphics.RenderEffect
import android.graphics.Shader
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

/**
 * Cache de proceso para el wallpaper frosted: UNA sola copia downsampled
 * (540x1200, RGB_565 = 1.2 MB vs 9.9 MB del ARGB_8888 full-res) reutilizada
 * por Dock, Spotlight, Control Center y LockScreen. Sin duplicados por capa.
 */
object WallpaperCache {
    @Volatile private var sourceRef: ImageBitmap? = null
    @Volatile private var cachedFrost: ImageBitmap? = null

    fun peek(source: ImageBitmap): ImageBitmap? =
        if (sourceRef === source) cachedFrost else null

    fun put(source: ImageBitmap, frost: ImageBitmap) {
        sourceRef = source
        cachedFrost = frost
    }
}

/**
 * Devuelve la version cacheada 540x1200 RGB_565 del wallpaper para capas de
 * blur; mientras se genera (IO, una sola vez), cae al original.
 */
@Composable
fun rememberFrostWallpaper(wallpaper: ImageBitmap): ImageBitmap {
    var frost by remember { mutableStateOf(WallpaperCache.peek(wallpaper)) }
    LaunchedEffect(wallpaper) {
        if (WallpaperCache.peek(wallpaper) != null) return@LaunchedEffect
        val result = withContext(Dispatchers.IO) {
            val w = 540
            val h = 1200
            val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565)
            val canvas = android.graphics.Canvas(bmp)
            val paint = android.graphics.Paint().apply { isFilterBitmap = true }
            canvas.drawBitmap(
                wallpaper.asAndroidBitmap(),
                null,
                android.graphics.RectF(0f, 0f, w.toFloat(), h.toFloat()),
                paint,
            )
            bmp.asImageBitmap()
        }
        WallpaperCache.put(wallpaper, result)
        frost = result
    }
    return frost ?: wallpaper
}

/**
 * Superficie de vidrio esmerilado real iOS 27: dibuja una copia ALINEADA del wallpaper
 * (idealmente la version cacheada de rememberFrostWallpaper) recortada a los
 * limites del componente y desenfocada con UN RenderEffect cacheado en remember
 * (cero allocaciones por frame). El contenido va como sibling y queda nitido.
 */
@Composable
fun GlassSurface(
    wallpaper: ImageBitmap,
    rootSize: Size,
    shape: androidx.compose.ui.graphics.Shape,
    tint: Color = Color(IOSTokens.COLOR_GLASS_TINT),
    frost: Dp = IOSTokens.frostUltraThin,
    content: @Composable () -> Unit,
) {
    var surfaceBounds by remember { mutableStateOf(Rect.Zero) }
    val density = LocalDensity.current
    // RenderEffect creado UNA vez por instancia (no por frame)
    val blurEffect = remember(frost, density) {
        with(density) {
            val r = frost.toPx()
            RenderEffect.createBlurEffect(r, r, Shader.TileMode.CLAMP).asComposeRenderEffect()
        }
    }
    Box(
        modifier = Modifier.onGloballyPositioned { surfaceBounds = it.boundsInRoot() },
    ) {
        // 1) Fondo frosted: wallpaper alineado al root, desenfocado solo esta capa
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(shape)
                .graphicsLayer { renderEffect = blurEffect },
        ) {
            Canvas(Modifier.matchParentSize()) {
                if (rootSize.width < 1f || rootSize.height < 1f) return@Canvas
                val s = maxOf(
                    rootSize.width / wallpaper.width,
                    rootSize.height / wallpaper.height,
                )
                val w = (wallpaper.width * s).roundToInt()
                val h = (wallpaper.height * s).roundToInt()
                // Centrado del Crop a pantalla completa, trasladado a coords locales
                val dx = (((rootSize.width - w) / 2f) - surfaceBounds.left).roundToInt()
                val dy = (((rootSize.height - h) / 2f) - surfaceBounds.top).roundToInt()
                drawImage(
                    image = wallpaper,
                    dstOffset = IntOffset(dx, dy),
                    dstSize = IntSize(w, h),
                )
            }
            Box(Modifier.matchParentSize().background(tint))
        }
        // 2) Capa LiquidGlass: refraccion + specular del propio vidrio (usar token v2)
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(shape)
                .background(Color(IOSTokens.COLOR_GLASS_TINT))
                .liquidGlass(blurDp = IOSTokens.GLASS_FROST_BLUR_DP.dp),
        )
        // 3) Contenido nitido
        content()
    }
}
