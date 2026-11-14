package com.rosemary.frontend.ui

import android.app.ActivityOptions
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.View
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.rosemary.frontend.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.text.Collator
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** App instalada del sistema (patrón Trebuchet: ACTION_MAIN + CATEGORY_LAUNCHER). */
data class AppEntry(
    val label: String,
    val packageName: String,
    val component: ComponentName,
    val icon: ImageBitmap?,
)

/**
 * Carga real de apps instaladas (UI thread-safe): consulta con PackageManager
 * como LoaderCursor/AppInfo de Trebuchet, iconos decodificados en IO.
 */
@Composable
fun rememberInstalledApps(): List<AppEntry> {
    val context = LocalContext.current
    var apps by remember { mutableStateOf<List<AppEntry>>(emptyList()) }
    LaunchedEffect(Unit) {
        val result = withContext(Dispatchers.IO) {
            val pm = context.packageManager
            val query = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
            val collator = Collator.getInstance(Locale.getDefault())
            pm.queryIntentActivities(query, 0).mapNotNull { ri ->
                val info = ri.activityInfo ?: return@mapNotNull null
                // Patrón Trebuchet (AppInfo.makeLaunchIntent): componente manual
                val cn = ComponentName(info.packageName, info.name)
                val label = try { ri.loadLabel(pm).toString() } catch (_: Exception) { info.packageName }
                val bmp = try {
                    val d: Drawable = ri.loadIcon(pm)
                    // Reescalado exacto a 144x144 px en IO: suficiente para iconSize 60dp@2.4x,
                    // evita bitmaps gigantes por app en el Native Heap
                    d.toBitmap(144, 144).asImageBitmap()
                } catch (_: Exception) { null }
                AppEntry(label, info.packageName, cn, bmp)
            }.sortedWith(compareBy(collator) { it.label.lowercase() })
        }
        apps = result
    }
    return apps
}

/** Lanzamiento con scale-up desde el icono (patrón Trebuchet ActivityContext L400-427). */
fun launchApp(context: Context, app: AppEntry, source: View, x: Int, y: Int) {
    val intent = Intent(Intent.ACTION_MAIN)
        .addCategory(Intent.CATEGORY_LAUNCHER)
        .setComponent(app.component)
        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
    try {
        val opts = ActivityOptions.makeScaleUpAnimation(source, x, y, 0, 0).toBundle()
        context.startActivity(intent, opts)
    } catch (e: Exception) {
        try { context.startActivity(intent) } catch (_: Exception) { }
    }
}

private const val PAGE_COLUMNS = IOSTokens.GRID_COLUMNS
private const val PAGE_ROWS = IOSTokens.GRID_ROWS
private const val PAGE_SIZE = PAGE_COLUMNS * PAGE_ROWS

/**
 * SpringBoard estilo iOS 26: wallpaper real de borde a borde, status bar iOS
 * propia (barras del sistema ocultas), Dynamic Island, dock frosted real,
 * Spotlight por arrastre hacia abajo y Centro de Control arriba a la derecha.
 */
@Composable
fun SpringBoardScreen() {
    val context = LocalContext.current
    val view = LocalView.current
    val apps = rememberInstalledApps()
    val wallpaper = ImageBitmap.imageResource(R.drawable.wallpaper_ios)
    // Version downsampled cacheada (540x1200 RGB_565) para TODAS las capas de blur:
    // una sola textura de 1.2 MB en vez de copias full-res de 9.9 MB por superficie
    val frostWallpaper = rememberFrostWallpaper(wallpaper)

    var spotlightOpen by remember { mutableStateOf(false) }
    var controlCenterOpen by remember { mutableStateOf(false) }
    var locked by remember { mutableStateOf(true) }
    var rootSize by remember { mutableStateOf(Size.Zero) }

    val pages = remember(apps) { apps.chunked(PAGE_SIZE) }
    val pagerState = rememberPagerState(pageCount = { maxOf(1, pages.size) })
    val dockApps = remember(apps) { apps.take(4) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned { rootSize = it.size.let { s -> Size(s.width.toFloat(), s.height.toFloat()) } }
            // Gesto de arrastre hacia abajo en el escritorio = Spotlight
            // (solo si no está bloqueado)
            .pointerInput(locked) {
                if (locked) return@pointerInput
                var totalDrag = 0f
                detectVerticalDragGestures(
                    onVerticalDrag = { _, amount -> totalDrag += amount },
                    onDragEnd = {
                        if (totalDrag > IOSTokens_gesturesSpotlightMinDrag) spotlightOpen = true
                        totalDrag = 0f
                    },
                    onDragCancel = { totalDrag = 0f },
                )
            },
    ) {
        // Wallpaper edge-to-edge (el vidrio ya tiene luz y color real que refractar)
        Image(
            bitmap = wallpaper,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize(),
        )
        // Scrim superior/inferior sutil para contraste de la UI
        Box(
            Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        0f to Color.Black.copy(alpha = 0.35f),
                        0.18f to Color.Transparent,
                        0.82f to Color.Transparent,
                        1f to Color.Black.copy(alpha = 0.45f),
                    )
                ),
        )

        Column(Modifier.fillMaxSize()) {
            // Status bar iOS propia (barras del sistema ocultas en MainActivity)
            IOSStatusBar()

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) { page ->
                SpringBoardPage(
                    apps = pages.getOrNull(page) ?: emptyList(),
                    onAppTap = { app, x, y -> launchApp(context, app, view, x, y) },
                )
            }

            // Indicador de página con spring
            Row(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(7.dp),
            ) {
                repeat(maxOf(1, pages.size)) { index ->
                    val selected = pagerState.currentPage == index
                    val dotScale by animateFloatAsState(
                        targetValue = if (selected) 1.4f else 1f,
                        animationSpec = spring(
                            dampingRatio = IOSTokens.SPRING_RELEASE_RATIO,
                            stiffness = IOSTokens.SPRING_RELEASE_STIFF,
                        ),
                        label = "dot",
                    )
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .scale(dotScale)
                            .clip(CircleShape)
                            .background(
                                if (selected) Color.White else Color.White.copy(alpha = 0.35f)
                            ),
                    )
                }
            }

            Spacer(Modifier.height(IOSTokens.dockBottomMargin + 92.dp)) // espacio del dock flotante
        }

        // Dock flotante: fondo frosted REAL (wallpaper alineado + blur) + iconos nítidos
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = IOSTokens.dockBottomMargin),
        ) {
            GlassSurface(
                wallpaper = frostWallpaper,
                rootSize = rootSize,
                shape = RoundedCornerShape(IOSTokens.dockRadius),
                tint = Color(0x33141C30),
                frost = IOSTokens.frostThin,
            ) {
                Row(
                    modifier = Modifier.padding(
                        horizontal = IOSTokens.dockHPadding,
                        vertical = IOSTokens.dockVPadding,
                    ),
                    horizontalArrangement = Arrangement.spacedBy(IOSTokens.gridHGap),
                ) {
                    dockApps.forEach { app ->
                        SpringAppIcon(
                            app = app,
                            iconSize = IOSTokens.dockIconSize,
                            showLabel = false,
                            onAppTap = { app, x, y -> launchApp(context, app, view, x, y) },
                        )
                    }
                }
            }
        }

        // Zona de gesto: esquina superior derecha = Centro de Control
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .fillMaxWidth(0.5f)
                .height(150.dp)
                .pointerInput(Unit) {
                    var totalDrag = 0f
                    detectVerticalDragGestures(
                        onVerticalDrag = { _, amount -> totalDrag += amount },
                        onDragEnd = {
                            if (totalDrag > 120f) controlCenterOpen = true
                            totalDrag = 0f
                        },
                        onDragCancel = { totalDrag = 0f },
                    )
                },
        )

        // Overlay Spotlight (arrastre hacia abajo)
        SpotlightOverlay(
            visible = spotlightOpen,
            apps = apps,
            wallpaper = frostWallpaper,
            rootSize = rootSize,
            onDismiss = { spotlightOpen = false },
            onAppTap = { app, x, y ->
                spotlightOpen = false
                launchApp(context, app, view, x, y)
            },
        )

        ControlCenterOverlay(
            expanded = controlCenterOpen,
            wallpaper = frostWallpaper,
            rootSize = rootSize,
            onDismiss = { controlCenterOpen = false },
        )

        // Dynamic Island (siempre encima, centrada bajo el borde superior)
        Box(Modifier.align(Alignment.TopCenter)) {
            DynamicIsland()
        }

        // Lockscreen iOS: capa superior, swipe-up para desbloquear
        androidx.compose.animation.AnimatedVisibility(
            visible = locked,
            enter = androidx.compose.animation.fadeIn(),
            exit = androidx.compose.animation.fadeOut(
                androidx.compose.animation.core.spring(stiffness = 240f)
            ) + androidx.compose.animation.slideOutVertically(
                animationSpec = androidx.compose.animation.core.spring(dampingRatio = 1f, stiffness = 260f),
            ) { it / 4 },
        ) {
            LockScreen(
                wallpaper = frostWallpaper,
                onUnlock = { locked = false },
            )
        }
    }
}

private const val IOSTokens_gesturesSpotlightMinDrag = 200f

@Composable
internal fun IOSStatusBar() {
    val visible = rememberAppVisible()
    var now by remember { mutableStateOf(System.currentTimeMillis()) }
    LaunchedEffect(visible) {
        if (!visible) return@LaunchedEffect
        now = System.currentTimeMillis()
        while (true) {
            delay(15_000)
            now = System.currentTimeMillis()
        }
    }
    val time = remember(now) {
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(now))
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = IOSTokens.gridHMargin)
            .height(36.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            time,
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.4.sp,
        )
        Spacer(Modifier.weight(1f))
        // Señal: 4 barras crecientes
        Canvas(Modifier.size(width = 13.dp, height = 12.dp)) {
            val barW = 2.dp.toPx()
            val gap = 1.4.dp.toPx()
            for (i in 0..3) {
                val h = size.height * ((i + 1) / 4f)
                drawRoundRect(
                    color = Color.White,
                    topLeft = Offset(i * (barW + gap), size.height - h),
                    size = Size(barW, h),
                    cornerRadius = CornerRadius(barW / 2f),
                )
            }
        }
        Spacer(Modifier.width(6.dp))
        // Wi-Fi: 3 arcos
        Canvas(Modifier.size(width = 16.dp, height = 12.dp)) {
            val cx = size.width / 2f
            val cy = size.height
            for (i in 0..2) {
                val r = (size.width / 2f) * ((i + 1) / 3f)
                drawArc(
                    color = Color.White,
                    startAngle = 220f,
                    sweepAngle = 100f,
                    useCenter = false,
                    topLeft = Offset(cx - r, cy - r),
                    size = Size(r * 2, r * 2),
                    style = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round),
                )
            }
            drawCircle(Color.White, radius = 1.1.dp.toPx(), center = Offset(cx, cy - 1.dp.toPx()))
        }
        Spacer(Modifier.width(6.dp))
        // Batería: contorno + llenado + nub
        Canvas(Modifier.size(width = 26.dp, height = 13.dp)) {
            val r = 4.dp.toPx()
            val bodyW = size.width - 3.dp.toPx()
            drawRoundRect(
                color = Color.White.copy(alpha = 0.45f),
                topLeft = Offset(0f, 0f),
                size = Size(bodyW, size.height),
                cornerRadius = CornerRadius(r),
                style = Stroke(width = 1.2.dp.toPx()),
            )
            drawRoundRect(
                color = Color.White,
                topLeft = Offset(1.6.dp.toPx(), 1.6.dp.toPx()),
                size = Size((bodyW - 3.2.dp.toPx()) * 0.82f, size.height - 3.2.dp.toPx()),
                cornerRadius = CornerRadius(r * 0.7f),
            )
            drawRoundRect(
                color = Color.White.copy(alpha = 0.45f),
                topLeft = Offset(size.width - 2.dp.toPx(), size.height * 0.32f),
                size = Size(2.dp.toPx(), size.height * 0.36f),
                cornerRadius = CornerRadius(1.dp.toPx()),
            )
        }
    }
}

/** Una página del SpringBoard: cuadrícula fija 4x6. */
@Composable
private fun SpringBoardPage(
    apps: List<AppEntry>,
    onAppTap: (AppEntry, Int, Int) -> Unit,
) {
    val rows = apps.chunked(PAGE_COLUMNS)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = IOSTokens.gridHMargin)
            .padding(top = 10.dp),
    ) {
        repeat(PAGE_ROWS) { rowIndex ->
            val rowApps = rows.getOrNull(rowIndex) ?: emptyList()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(IOSTokens.gridHGap, Alignment.Start),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                repeat(PAGE_COLUMNS) { colIndex ->
                    val app = rowApps.getOrNull(colIndex)
                    if (app != null) {
                        SpringAppIcon(
                            app = app,
                            iconSize = IOSTokens.iconSize,
                            showLabel = true,
                            onAppTap = onAppTap,
                        )
                    } else {
                        Spacer(Modifier.size(IOSTokens.iconSize))
                    }
                }
            }
        }
    }
}

/**
 * Icono de app unificado (Priority 1/2 del proyecto):
 * 1. Tema nativo vectorial para apps base estilo iOS (IOSAppIcons).
 * 2. Icono REAL del app instalado (loadIcon -> ImageBitmap) sobre fondo suave,
 *    con padding interno y recorte limpio en squircle (sin emojis ni bordes).
 * 3. Inicial tipografica como ultimo recurso.
 */
@Composable
fun AppIconImage(app: AppEntry, size: Dp, modifier: Modifier = Modifier) {
    val theme = iosAppThemeFor(app.packageName)
    Box(
        modifier = modifier
            .size(size)
            .clip(SquircleShape),
        contentAlignment = Alignment.Center,
    ) {
        when {
            theme != null -> IOSAppIconSurface(theme, size, Modifier.matchParentSize())
            app.icon != null -> Box(
                Modifier
                    .matchParentSize()
                    .background(Brush.verticalGradient(listOf(Color(0xFFFDFDFF), Color(0xFFE7E7EF)))),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    bitmap = app.icon,
                    contentDescription = app.label,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .matchParentSize()
                        .padding(size * 0.17f),
                )
            }
            else -> Box(
                Modifier
                    .matchParentSize()
                    .background(Color(0xFF3A3A46)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    app.label.take(1).uppercase(),
                    color = Color.White,
                    fontSize = (size.value * 0.42f).sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

/** Icono estilo iOS: squircle continuo + icono vectorial nativo o app real. */
@Composable
fun SpringAppIcon(
    app: AppEntry,
    iconSize: Dp,
    showLabel: Boolean,
    onAppTap: (AppEntry, Int, Int) -> Unit,
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.82f else 1f,
        animationSpec = spring(
            dampingRatio = IOSTokens.SPRING_PRESS_RATIO,
            stiffness = IOSTokens.SPRING_PRESS_STIFF,
        ),
        label = "iconSpring",
    )
    var topLeft by remember { mutableStateOf(IntOffset.Zero) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(IOSTokens.iconLabelGap),
        modifier = Modifier
            .width(iconSize)
            .onGloballyPositioned { coords ->
                val b = coords.boundsInRoot()
                topLeft = IntOffset(b.left.toInt(), b.top.toInt())
            }
            .pointerInput(app.packageName) {
                detectTapGestures(
                    onPress = {
                        pressed = true
                        tryAwaitRelease()
                        pressed = false
                    },
                    onTap = { onAppTap(app, topLeft.x, topLeft.y) },
                )
            },
    ) {
        Box(
            modifier = Modifier
                .size(iconSize)
                .scale(scale)
                .clip(SquircleShape),
            contentAlignment = Alignment.Center,
        ) {
            AppIconImage(app = app, size = iconSize, modifier = Modifier.matchParentSize())
        }
        if (showLabel) {
            Text(
                app.label,
                color = Color.White,
                fontSize = IOSTokens.iconLabelSize,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = androidx.compose.ui.text.TextStyle(
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = 0.65f),
                        blurRadius = 8f,
                        offset = Offset(0f, 1f),
                    ),
                ),
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
        }
    }
}

/** Spotlight: barra de búsqueda con vidrio esmerilado real y filtrado instantáneo. */
@Composable
private fun SpotlightOverlay(
    visible: Boolean,
    apps: List<AppEntry>,
    wallpaper: ImageBitmap,
    rootSize: Size,
    onDismiss: () -> Unit,
    onAppTap: (AppEntry, Int, Int) -> Unit,
) {
    var query by remember { mutableStateOf("") }
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(spring()) + scaleIn(
            initialScale = 0.92f,
            animationSpec = spring(
                dampingRatio = IOSTokens.SPRING_OVERLAY_OPEN_RATIO,
                stiffness = IOSTokens.SPRING_OVERLAY_OPEN_STIFF,
            ),
        ),
        exit = fadeOut(spring()) + scaleOut(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.35f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) { onDismiss },
        ) {
            val filtered = remember(query, apps) {
                if (query.isBlank()) emptyList()
                else apps.filter { it.label.contains(query, ignoreCase = true) }.take(12)
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = IOSTokens.spotlightTopPadding,
                        start = IOSTokens.spotlightHMargin,
                        end = IOSTokens.spotlightHMargin,
                    ),
            ) {
                GlassSurface(
                    wallpaper = wallpaper,
                    rootSize = rootSize,
                    shape = RoundedCornerShape(IOSTokens.spotlightRadius),
                    tint = Color(0x660B0B18),
                    frost = IOSTokens.frostThin,
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        TextField(
                            value = query,
                            onValueChange = { query = it },
                            singleLine = true,
                            placeholder = { Text("Buscar apps", color = Color.White.copy(alpha = 0.5f)) },
                            colors = TextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = Color.White,
                                focusedContainerColor = Color.White.copy(alpha = 0.08f),
                                unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                            ),
                            modifier = Modifier.fillMaxWidth(),
                        )
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 320.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                        ) {
                            items(filtered, key = { it.packageName }) { app ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .clickable { onAppTap(app, 0, 0) }
                                        .padding(10.dp),
                                ) {
                                    AppIconImage(app = app, size = IOSTokens.resultIconSize)
                                    Text(app.label, color = Color.White, fontSize = 15.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
