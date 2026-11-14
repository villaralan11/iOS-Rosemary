package com.rosemary.frontend.ui

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Design tokens iOS calibrados al POCO M5s (1080x2400 @ 409 dpi, densidad 2.556).
 * Fuente de verdad numérica: ios-refs/ios_design_tokens.json
 *   - Modelo de springs: CASpringAnimation (damping/mass/stiffness) — headers
 *     CCUICASpringAnimationParameters.h de ControlCenterUI.framework.
 *   - Esquinas continuas: _setContinuousCornerRadius (CCUIContentModuleContentContainerView.h).
 *   - Proporciones home iOS (icono 60pt, margen 27pt, gap 27pt @ 375pt) escaladas a 422.5dp.
 */
object IOSTokens {

    // ===== Springs (presets CASpringAnimation <-> Compose) =====
    // dampingRatio = damping / (2 * sqrt(stiffness * mass)), mass = 1
    const val SPRING_PRESS_RATIO = 0.65f       // icon_press: squash rápido
    const val SPRING_PRESS_STIFF = 500f
    const val SPRING_RELEASE_RATIO = 0.82f     // icon_release: rebote sutil
    const val SPRING_RELEASE_STIFF = 380f
    const val SPRING_PAGE_RATIO = 1.0f         // page_swipe: críticamente amortizado
    const val SPRING_PAGE_STIFF = 300f
    const val SPRING_OVERLAY_OPEN_RATIO = 0.725f  // overlay_open: bouncy iOS 17 (CC)
    const val SPRING_OVERLAY_OPEN_STIFF = 170f
    const val SPRING_OVERLAY_CLOSE_RATIO = 1.0f   // overlay_close: sin rebote
    const val SPRING_OVERLAY_CLOSE_STIFF = 250f

    // ===== Grid del SpringBoard =====
    const val GRID_COLUMNS = 4
    const val GRID_ROWS = 6
    val iconSize = 64.dp
    val gridHMargin = 30.dp
    val gridHGap = 26.dp
    val iconLabelSize = 11.sp
    val iconLabelGap = 5.dp

    // ===== Dock =====
    val dockRadius = 30.dp
    val dockIconSize = 58.dp
    val dockHPadding = 16.dp
    val dockVPadding = 10.dp
    val dockBottomMargin = 22.dp

    // ===== Spotlight =====
    val spotlightTopPadding = 84.dp
    val spotlightRadius = 28.dp
    val spotlightHMargin = 16.dp
    val resultRowHeight = 56.dp
    val resultIconSize = 40.dp

    // ===== Control Center =====
    val ccPanelRadius = 32.dp
    val ccModuleRadius = 24.dp
    val ccModuleSpacing = 14.dp
    val ccToggleIconSize = 40.dp
    val ccMediaButtonSize = 52.dp

    // ===== Materiales (UIBlurEffect calibrado a 409 dpi) =====
    val frostThin = 32.dp   // systemUltraThinMaterial
    val frostRegular = 42.dp

    // ===== Liquid Glass (shader) =====
    const val GLASS_CHROMATIC_ABERRATION = 0.006f
    const val GLASS_SPECULAR_INTENSITY = 0.14f
}
