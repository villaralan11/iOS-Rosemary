package com.rosemary.frontend.ui

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import android.view.KeyEvent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

/**
 * Centro de Control estilo iOS (base inicial): panel LiquidGlass desplegable
 * desde la esquina superior derecha, con tarjetas funcionales:
 *  - Brillo del sistema (Settings.System.SCREEN_BRIGHTNESS, requiere WRITE_SETTINGS)
 *  - Volumen multimedia (AudioManager STREAM_MUSIC)
 *  - Wi-Fi / Bluetooth (lectura de estado + toggle protegido)
 *  - Controles de reproducción (teclas de media vía AudioManager)
 */
@Composable
fun ControlCenterOverlay(
    expanded: Boolean,
    wallpaper: ImageBitmap,
    rootSize: Size,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current

    var brightness by remember { mutableFloatStateOf(0.5f) }
    var volume by remember { mutableFloatStateOf(0.5f) }
    var wifiOn by remember { mutableStateOf<Boolean?>(null) }
    var btOn by remember { mutableStateOf<Boolean?>(null) }

    val audioManager = remember { context.getSystemService(AudioManager::class.java) }
    val wifiManager = remember { context.getSystemService(WifiManager::class.java) }
    val btAdapter = remember {
        try { context.getSystemService(BluetoothManager::class.java)?.adapter } catch (_: Exception) { null }
    }

    val btPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) btOn = tryReadBtState(btAdapter)
    }

    LaunchedEffect(expanded) {
        if (!expanded) return@LaunchedEffect
        // Brillo actual (lectura sin permiso)
        brightness = try {
            Settings.System.getInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS) / 255f
        } catch (_: Exception) { 0.5f }
        // Volumen actual
        volume = try {
            audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat() /
                audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        } catch (_: Exception) { 0.5f }
        wifiOn = try { wifiManager?.isWifiEnabled } catch (_: Exception) { null }
        btOn = tryReadBtState(btAdapter)
    }

    AnimatedVisibility(
        visible = expanded,
        enter = fadeIn(spring()) + expandVertically(
            spring(
                dampingRatio = IOSTokens.SPRING_OVERLAY_OPEN_RATIO,
                stiffness = IOSTokens.SPRING_OVERLAY_OPEN_STIFF,
            )
        ),
        exit = fadeOut(spring()) + shrinkVertically(
            spring(
                dampingRatio = IOSTokens.SPRING_OVERLAY_CLOSE_RATIO,
                stiffness = IOSTokens.SPRING_OVERLAY_CLOSE_STIFF,
            )
        ),
    ) {
        // Fondo semitransparente que cubre toda la pantalla y cierra al tocar fuera
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.35f))
                .clickable { onDismiss() }
                .pointerInput(Unit) {
                    detectVerticalDragGestures(
                        onVerticalDrag = { _, amount ->
                            if (amount > 20f) onDismiss() // Swipe down to close
                        }
                    )
                },
        ) {
            // Panel de controles alineado arriba a la derecha
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp),
            ) {
                // Fondo frosted real (wallpaper alineado + blur) + vidrio; contenido nítido dentro
                GlassSurface(
                    wallpaper = wallpaper,
                    rootSize = rootSize,
                    shape = RoundedCornerShape(IOSTokens.ccPanelRadius),
                    tint = Color(0x8A121226),
                    frost = IOSTokens.frostRegular,
                ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(IOSTokens.ccModuleSpacing),
                ) {
            // ==== Brillo ====
            GlassCard {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(7.dp),
                    ) {
                        IosGlyphIcon(IosGlyph.SUN, 14.dp)
                        Text("Brillo", color = Color.White, fontSize = 13.sp)
                    }
                    Slider(
                        value = brightness,
                        onValueChange = { value ->
                            brightness = value
                            try {
                                if (Settings.System.canWrite(context)) {
                                    Settings.System.putInt(
                                        context.contentResolver,
                                        Settings.System.SCREEN_BRIGHTNESS,
                                        (value * 255).toInt().coerceIn(2, 255),
                                    )
                                }
                            } catch (_: Exception) { }
                        },
                        colors = SliderDefaults.colors(
                            thumbColor = Color.White,
                            activeTrackColor = Color.White,
                            inactiveTrackColor = Color.White.copy(alpha = 0.25f),
                        ),
                    )
                }
            }

            // ==== Volumen multimedia ====
            GlassCard {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(7.dp),
                    ) {
                        IosGlyphIcon(IosGlyph.SPEAKER, 14.dp)
                        Text("Volumen multimedia", color = Color.White, fontSize = 13.sp)
                    }
                    Slider(
                        value = volume,
                        onValueChange = { value ->
                            volume = value
                            try {
                                audioManager.setStreamVolume(
                                    AudioManager.STREAM_MUSIC,
                                    (value * audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC))
                                        .toInt().coerceIn(0, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)),
                                    0,
                                )
                            } catch (_: Exception) { }
                        },
                        colors = SliderDefaults.colors(
                            thumbColor = Color.White,
                            activeTrackColor = Color.White,
                            inactiveTrackColor = Color.White.copy(alpha = 0.25f),
                        ),
                    )
                }
            }

            // ==== Wi-Fi / Bluetooth ====
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ToggleCard(
                    modifier = Modifier.weight(1f),
                    glyph = IosGlyph.WIFI,
                    label = "Wi-Fi",
                    stateOn = wifiOn == true,
                    onToggle = { on ->
                        try {
                            @Suppress("DEPRECATION")
                            wifiManager?.setWifiEnabled(on)
                            wifiOn = on
                        } catch (_: Exception) {
                            wifiOn = try { wifiManager?.isWifiEnabled } catch (_: Exception) { null }
                        }
                    },
                )
                ToggleCard(
                    modifier = Modifier.weight(1f),
                    glyph = IosGlyph.BLUETOOTH,
                    label = "Bluetooth",
                    stateOn = btOn == true,
                    onToggle = { on ->
                        val granted = ContextCompat.checkSelfPermission(
                            context, Manifest.permission.BLUETOOTH_CONNECT
                        ) == PackageManager.PERMISSION_GRANTED
                        if (!granted) {
                            btPermissionLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT)
                            return@ToggleCard
                        }
                        try {
                            if (on) btAdapter?.enable() else btAdapter?.disable()
                            btOn = on
                        } catch (_: Exception) {
                            btOn = tryReadBtState(btAdapter)
                        }
                    },
                )
            }

            // ==== Controles de reproducción (teclas media) ====
            GlassCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    MediaButton(IosGlyph.PREV) { sendMediaKey(context, KeyEvent.KEYCODE_MEDIA_PREVIOUS) }
                    MediaButton(IosGlyph.PLAY) { sendMediaKey(context, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) }
                    MediaButton(IosGlyph.NEXT) { sendMediaKey(context, KeyEvent.KEYCODE_MEDIA_NEXT) }
                }
            }

            Text(
                "Desliza hacia abajo o toca fuera para cerrar",
                color = Color.White.copy(alpha = 0.4f),
                fontSize = 11.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
                }
            }
        }
        }
    }
}

@Composable
private fun GlassCard(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(IOSTokens.ccModuleRadius))
            .background(Color.White.copy(alpha = 0.07f))
            .padding(horizontal = 14.dp, vertical = 4.dp),
    ) { content() }
}

@Composable
private fun ToggleCard(
    modifier: Modifier = Modifier,
    glyph: IosGlyph,
    label: String,
    stateOn: Boolean,
    onToggle: (Boolean) -> Unit,
) {
    val scale by animateFloatAsState(
        targetValue = if (stateOn) 1f else 0.96f,
        animationSpec = spring(dampingRatio = 0.82f, stiffness = 380f),
        label = "toggleScale",
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = modifier
            .clip(RoundedCornerShape(IOSTokens.ccModuleRadius))
            .background(
                if (stateOn) Color(0x552E7D32) else Color.White.copy(alpha = 0.07f)
            )
            .clickable { onToggle(!stateOn) }
            .padding(vertical = 14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(IOSTokens.ccToggleIconSize)
                .scale(scale)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) { IosGlyphIcon(glyph, 18.dp) }
        Text(label, color = Color.White, fontSize = 12.sp)
    }
}

@Composable
private fun MediaButton(glyph: IosGlyph, onTap: () -> Unit) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.85f else 1f,
        animationSpec = spring(dampingRatio = 0.82f, stiffness = 380f),
        label = "mediaBtn",
    )
    Box(
        modifier = Modifier
            .size(IOSTokens.ccMediaButtonSize)
            .scale(scale)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.12f))
            .clickable {
                pressed = true
                onTap()
                pressed = false
            },
        contentAlignment = Alignment.Center,
    ) { IosGlyphIcon(glyph, 18.dp) }
}

private fun tryReadBtState(adapter: BluetoothAdapter?): Boolean? = try {
    adapter?.state == BluetoothAdapter.STATE_ON
} catch (_: Exception) { null }

private fun sendMediaKey(context: Context, keyCode: Int) {
    try {
        val am = context.getSystemService(AudioManager::class.java)
        am.dispatchMediaKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, keyCode))
        am.dispatchMediaKeyEvent(KeyEvent(KeyEvent.ACTION_UP, keyCode))
    } catch (_: Exception) { }
}
