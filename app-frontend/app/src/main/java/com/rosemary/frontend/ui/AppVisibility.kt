package com.rosemary.frontend.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

/**
 * Estado "app visible" (ON_START..ON_STOP): las animaciones continuas
 * (pulso del lockscreen, onda de audio de la Dynamic Island) y los tickers
 * de reloj solo corren mientras la app esta en foreground con pantalla viva.
 */
@Composable
fun rememberAppVisible(): Boolean {
    val owner = LocalLifecycleOwner.current
    var visible by remember { mutableStateOf(true) }
    DisposableEffect(owner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> visible = true
                Lifecycle.Event.ON_STOP -> visible = false
                else -> { }
            }
        }
        owner.lifecycle.addObserver(observer)
        onDispose { owner.lifecycle.removeObserver(observer) }
    }
    return visible
}
