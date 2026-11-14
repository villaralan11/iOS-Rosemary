package com.rosemary.frontend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.rosemary.frontend.ui.SpringBoardScreen

/**
 * HOME del sistema (SpringBoard estilo iOS).
 *
 * Declarada en AndroidManifest.xml con:
 *   <category android:name="android.intent.category.HOME" />
 *   <category android:name="android.intent.category.DEFAULT" />
 *
 * Modo inmersivo total: edge-to-edge real, status bar y navigation bar
 * ocultas (reaparecen con swipe transitorio) — la UI dibuja su propia
 * status bar estilo iOS.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        setContent {
            SpringBoardScreen()
        }
    }
}
