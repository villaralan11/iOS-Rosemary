package com.rosemary.frontend.ui

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Design tokens iOS 27 calibrados al POCO M5s (1080x2400 @ 409 dpi, densidad 2.556).
 * Fuente de verdad numérica: ios-refs/ios_design_tokens.json v2
 *   - Modelo de springs: CASpringAnimation (damping/mass/stiffness) — headers
 *     CCUICASpringAnimationParameters.h de ControlCenterUI.framework.
 *   - Esquinas continuas: _setContinuousCornerRadius (CCUIContentModuleContentContainerView.h).
 *   - Proporciones home iOS (icono 60pt, margen 28pt, gap 28pt @ 375pt) escaladas a 422.5dp.
 *   - Squircle: superellipse n=5.0, corner_ratio=0.2381 (r = size * 0.2381), continuous_corner_factor=1.528655
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

    // iOS 27 nuevos presets
    const val SPRING_DI_EXPAND_RATIO = 0.62f    // dynamic_island_expand
    const val SPRING_DI_EXPAND_STIFF = 200f
    const val SPRING_DI_COLLAPSE_RATIO = 0.75f  // dynamic_island_collapse
    const val SPRING_DI_COLLAPSE_STIFF = 280f
    const val SPRING_UNLOCK_RATIO = 0.68f       // lockscreen_unlock
    const val SPRING_UNLOCK_STIFF = 180f
    const val SPRING_WIDGET_RATIO = 0.85f       // widget_transition
    const val SPRING_WIDGET_STIFF = 220f

    // ===== Grid del SpringBoard =====
    const val GRID_COLUMNS = 4
    const val GRID_ROWS = 6
    val iconSize = 64.dp
    val gridHMargin = 28.dp
    val gridHGap = 28.dp
    val iconLabelSize = 11.sp
    val iconLabelGap = 4.dp
    val iconLabelTracking = -0.015f  // em

    // ===== Dock =====
    val dockRadius = 28.dp
    val dockIconSize = 58.dp
    val dockPaddingTop = 8.dp
    val dockPaddingRight = 14.dp
    val dockPaddingBottom = 8.dp
    val dockPaddingLeft = 14.dp
    val dockBottomMargin = 20.dp
    // Aliases compatibilidad
    val dockHPadding = dockPaddingRight
    val dockVPadding = dockPaddingTop

    // ===== Spotlight =====
    val spotlightTopPadding = 80.dp
    val spotlightRadius = 26.dp
    val spotlightHMargin = 16.dp
    val resultRowHeight = 52.dp
    val resultIconSize = 38.dp

    // ===== Control Center =====
    val ccPanelRadius = 30.dp
    val ccModuleRadius = 22.dp
    val ccModuleSpacing = 12.dp
    val ccToggleIconSize = 38.dp
    val ccMediaButtonSize = 50.dp
    val ccSliderTrackHeight = 36.dp

    // ===== Dynamic Island =====
    val diCompactWidth = 232.dp
    val diCompactHeight = 34.dp
    val diCompactRadius = 17.dp
    val diExpandedWidth = 340.dp
    val diExpandedHeight = 156.dp
    val diExpandedRadius = 24.dp

    // ===== Lockscreen =====
    val lsClockSize = 96.sp
    val lsClockWeight = 200  // thin
    val lsClockTracking = -0.02f  // em
    val lsDateSize = 17.sp
    val lsDateWeight = 400  // regular
    val lsDateTracking = -0.01f  // em
    val lsButtonSize = 52.dp
    val lsButtonCornerRatio = 0.5f

    // ===== Materiales (UIBlurEffect calibrado a 409 dpi) =====
    val frostUltraThin = 20.dp   // systemUltraThinMaterial (reducido para memoria)
    val frostRegular = 20.dp     // systemMaterial (reducido para memoria)
    val frostThick = 30.dp       // systemThickMaterial (reducido para memoria)
    // Aliases compatibilidad
    val frostThin = frostUltraThin

    // ===== Liquid Glass (shader) =====
    const val GLASS_TINT_ALPHA = 0.12f
    const val GLASS_FROST_BLUR_DP = 20f
    const val GLASS_CHROMATIC_ABERRATION = 0.005f
    const val GLASS_SPECULAR_INTENSITY = 0.14f
    const val GLASS_SHIMMER_AMPLITUDE = 0.018f
    const val GLASS_SHIMMER_SPEED = 1.6f
    const val GLASS_REFRACTION_STRENGTH = 0.028f
    const val GLASS_REFRACTION_CURVATURE = 0.09f

    // ===== Colores (Display P3) =====
    const val COLOR_TINT_BASE = 0xFF0A1F2E
    const val COLOR_TINT_BASE_ALPHA = 0.12f
    const val COLOR_ACCENT_BLUE = 0xFF0066FF
    const val COLOR_ACCENT_GREEN = 0xFF30D158
    const val COLOR_ACCENT_ORANGE = 0xFFFF9F0A
    const val COLOR_ACCENT_RED = 0xFFFF453A
    const val COLOR_ACCENT_PURPLE = 0xFFBF5AF2
    const val COLOR_TEXT_PRIMARY = 0xFFFFFFFF
    const val COLOR_TEXT_SECONDARY = 0xCCFFFFFF
    const val COLOR_TEXT_TERTIARY = 0x99FFFFFF
    const val COLOR_SEPARATOR = 0x1AFFFFFF
    const val COLOR_GLASS_TINT = 0x5F0A1F2E
    const val COLOR_GLASS_SPECULAR = 0x33FFFFFF

    // ===== Curvas bezier iOS 27 =====
    // CubicBezierEasing(x1, y1, x2, y2)
    const val CURVE_IOS27_SPRING_X1 = 0.32f
    const val CURVE_IOS27_SPRING_Y1 = 0.0f
    const val CURVE_IOS27_SPRING_X2 = 0.48f
    const val CURVE_IOS27_SPRING_Y2 = 1.0f
    const val CURVE_IOS27_SNAPPY_X1 = 0.20f
    const val CURVE_IOS27_SNAPPY_Y1 = 0.0f
    const val CURVE_IOS27_SNAPPY_X2 = 0.38f
    const val CURVE_IOS27_SNAPPY_Y2 = 1.0f

    // ===== Squircle =====
    const val SQUIRCLE_N = 5.0f
    const val SQUIRCLE_CORNER_RATIO = 0.2381f
    const val SQUIRCLE_CONTINUOUS_FACTOR = 1.528655f

    // ===== Gestos =====
    const val GESTURE_SPOTLIGHT_THRESHOLD = 200
    const val GESTURE_CC_ZONE_HEIGHT = 240
    const val GESTURE_CC_MIN_DRAG = 140
    const val GESTURE_UNLOCK_THRESHOLD = 180

    // ===== Status Bar =====
    val statusBarHeight = 52.dp
    val statusBarFontSize = 17.sp
    val statusBarFontWeight = 600  // semibold
}