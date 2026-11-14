# INFORME DE BRECHAS — iOS 27 vs Frontend Actual
**Fecha**: 2026-11-14 | **Fuente**: ios_design_tokens.json v2 (medido de material oficial Apple) | **Frontend**: app-frontend/ (Compose, verificado en emulador Rosemary_Test 1080x2400@409dpi)

---

## Convenciones de la tabla
- **Geometría**: tamaños siempre **W×Hdp** (ancho × alto), paddings siempre **T/R/B/L** (top/right/bottom/left)
- **Etiqueta de origen** (columna "iOS 27"): **medido (frame N)** = extraído de keyframe PNG; **calibrado** = valor óptico ajustado a ojo en emulador contra frame de referencia (no medible desde video comprimido)
- **Fix Requerido**: describe lo que hacemos NOSOTROS (cambio de código), no lo que hace Apple
- **Dedupe**: filas que desembocan en el mismo cambio de código → marcadas como **relacionada: #X**

---

## Tabla de Brechas (20 filas)

| # | Elemento | iOS 27 (medido) | Actual (medido) | Brecha | Fix Requerido | Etiqueta |
|---|----------|-----------------|-----------------|--------|---------------|----------|
| 1 | **Squircle iconos (ecuación exacta)** | Superellipse n=5.0, corner_ratio=0.2381 (radio = 0.2381 × size), cubic_smoothing_k=1.528655 | n=5.0, corner_ratio=0.2237, k=1.528655 | **+6.4% más redondo** (ratio 0.2237→0.2381) | `Squircle.kt`: `cornerRatio=0.2381f`, `superellipseN=5.0f`, `continuousCornerFactor=1.528655f`. Fórmula: `x^n + y^n = r^n` con `r = size * 0.2381` | **medido (frame liquid_glass_oficial 0042)** |
| 2 | **Grid SpringBoard (margin/gap)** | margin L/R 28dp, gap H/V 28dp | margin 30dp, gap H 26dp / V 28dp | **Margin -2dp, Gap H +2dp** | `SpringBoardScreen.kt`: `hMargin=28.dp`, `hGap=28.dp`, `vGap=28.dp`, recalcular 4×6 | **medido (frame liquid_glass_oficial 0015)** |
| 3 | **Dock (corner, padding T/R/B/L, bottom margin)** | corner 28dp, pad T8/R14/B8/L14, bottom 20dp | corner 30dp, pad T10/R16/B10/L16, bottom 22dp | **Dock 10-15% más grande** | `SpringBoardScreen.kt` / `GlassSurface.kt`: `dockCorner=28.dp`, `dockPadding=PaddingValues(8,14,8,14).dp`, `bottomMargin=20.dp` | **medido (frame liquid_glass_oficial 0015)** |
| 4 | **Icon label (tracking, gap, maxLines)** | 11sp, tracking -0.015em, gap 4dp, 1 línea + ellipsis | 11sp, tracking 0, gap 5dp, 1 línea | **Tracking -0.015em, gap -1dp** | `SpringBoardScreen.kt`: `letterSpacing=-0.015.em`, `labelGap=4.dp`, `maxLines=1`, `ellipsis=true` | **medido (frame liquid_glass_oficial 0015)** |
| 5 | **Lockscreen reloj (font, weight, tracking, tamaño)** | SF Pro Rounded 96sp, **weight 200 (Thin)**, tracking -0.02em | Roboto ~80sp, weight 300, tracking 0 | **Fuente, peso, tracking, tamaño** | `LockScreen.kt`: `fontFamily=FontFamily.Default`, `fontWeight=FontWeight.Thin (200)`, `fontSize=96.sp`, `letterSpacing=-0.02.em`. **Relacionada: #16, #21** | **calibrado** (peso óptico contra frame lockscreen_animation 0003) |
| 6 | **Lockscreen fecha (font, weight, tracking)** | 17sp, weight 400 (Regular), tracking -0.01em | ~14sp, weight 400, tracking 0 | **Tamaño +3sp, tracking -0.01em** | `LockScreen.kt`: `fontSize=17.sp`, `fontWeight=FontWeight.Normal`, `letterSpacing=-0.01.em`. **Relacionada: #16** | **calibrado** |
| 7 | **Lockscreen botones (linterna/cámara)** | 52×52dp circular (ratio 0.5), GlassSurface liquid_glass | 56×56dp, corner ratio 0.25, GlassSurface standard | **-4dp, forma circular pura, material liquid_glass** | `LockScreen.kt`: `GlassCircleButton(size=52.dp, cornerRatio=0.5f, liquidGlass=true)` | **medido (frame lockscreen_animation 0003)** |
| 8 | **Dynamic Island compacta (W×H, corner)** | 232×34dp, corner 17dp (pill: 2 semicírculos + rect central) | ~220×36dp, corner 18dp | **H -2dp, corner -1dp** | `DynamicIsland.kt`: `compactSize=DpSize(232, 34)`, `cornerRadius=17.dp` | **medido (frame dynamic_island_iphone18pro 0001)** |
| 9 | **Dynamic Island expandida (W×H, corner, springs)** | 340×156dp, corner 24dp, expand spring 0.62/200, **collapse spring 0.75/280** | ~320×140dp, corner 22dp, expand 0.62/220, collapse N/A | **Dims +20×16dp, corner +2dp, expand más suave, NUEVO collapse** | `DynamicIsland.kt`: `expandedSize=DpSize(340, 156)`, `expandedCorner=24.dp`, `springExpand=Spring(0.62f, 200f)`, `springCollapse=Spring(0.75f, 280f)`. **Relacionada: #17** | **medido (frame dynamic_island_iphone18pro 0005) + calibrado (springs)** |
| 10 | **Control Center (panel corner, module corner, spacing)** | panel corner 30dp, module corner 22dp, spacing 12dp | panel 32dp, module 24dp, spacing 14dp | **Esquinas -2dp, spacing -2dp** | `ControlCenterOverlay.kt`: `panelCorner=30.dp`, `moduleCorner=22.dp`, `moduleSpacing=12.dp` | **medido (frame liquid_glass_oficial 0087)** |
| 11 | **Control Center toggles/iconos (W×H)** | toggle 38×38dp, media btn 50×50dp, slider track H 36dp | toggle 40×40dp, media 52×52dp, slider 38dp | **-2dp en todos los controles** | `ControlCenterOverlay.kt`: `toggleSize=38.dp`, `mediaBtnSize=50.dp`, `sliderTrackHeight=36.dp` | **medido (frame liquid_glass_oficial 0087)** |
| 12 | **Spotlight (panel top pad, corner, row H, icon W×H)** | top pad 80dp, corner 26dp, row 52dp, icon 38×38dp | top pad 84dp, corner 28dp, row 56dp, icon 40×40dp | **Más compacto: -4dp pad, -2dp corner, -4dp row** | `SpringBoardScreen.kt` (Spotlight): `panelTopPad=80.dp`, `panelCorner=26.dp`, `rowHeight=52.dp`, `iconSize=38.dp` | **medido (frame liquid_glass_oficial 0063)** |
| 13 | **LiquidGlass material (tint, blur, specular, shimmer)** | tint_alpha 0.12, frost_blur 28dp, specular 0.14, shimmer 0.018 | tint_alpha 0.08, frost_blur 32dp, specular 0.10, shimmer 0.012 | **Vidrio más visible, blur más nítido, borde +40%, shimmer +50%** | `LiquidGlassShader.kt` (AGSL uniforms) + `GlassSurface.kt` (RenderEffect blur): `tintAlpha=0.12f`, `frostBlurDp=28f`, `specularIntensity=0.14f`, `shimmerAmp=0.018f`. **Relacionada: #14** | **calibrado** (valores ópticos; video no permite medir refracción) |
| 14 | **UIBlurEffect (systemMaterial, systemThick)** | systemMaterial blur 38dp, systemThick 50dp | systemMaterial 42dp, systemThick 56dp | **Blur -4dp/-6dp (más nítido)** | `GlassSurface.kt`: `RenderEffect.createBlurEffect(radius=38f/50f)`. **Relacionada: #13** (mismo archivo, misma cadena RenderEffect) | **calibrado** |
| 15 | **Paleta colores (tint_base, accent_blue)** | tint_base #0A1F2E, accent #0066FF | tint_base #142E3A, accent #007AFF | **Tint más oscuro/azul, accent más saturado** | `DesignTokens.kt`: `tintBase=0xFF0A1F2E`, `accentBlue=0xFF0066FF`. Propagar a `IconDrawables.kt`, shaders | **medido (HTML apple.com/ios meta theme-color + frames liquid_glass)** |
| 16 | **Tipografía global (fallback FontFamily.Default + weight map)** | SF Pro Rounded: 100/200/300/400/500/600/700/800/900 | Roboto (Default): 100/300/400/500/700/900 | **Weight 200 (Thin) disponible via Default; pesos intermedios mapeados** | `Theme.kt` / `Typography.kt`: `fontFamily=FontFamily.Default`, `weightMap = mapOf(100 to Thin, 200 to Thin, 300 to Light, 400 to Normal, 500 to Medium, 600 to SemiBold, 700 to Bold, 800 to ExtraBold, 900 to Black)`. **Relacionada: #5, #6, #21** | **calibrado** (sin TTF Apple; decisión final en F) |
| 17 | **Spring presets (nuevos iOS 27)** | di_expand 0.62/200, di_collapse 0.75/280, unlock 0.68/180, widget 0.85/220 | Solo icon_press/release, page, overlay, indicator | **4 nuevos presets faltantes** | `DesignTokens.kt` + `SpringUtils.kt`: añadir `dynamicIslandExpand`, `dynamicIslandCollapse`, `lockscreenUnlock`, `widgetTransition`. **Relacionada: #9** | **calibrado** (valores iniciales; afinar a ojo en emulador) |
| 18 | **Curvas bezier (nuevas iOS 27)** | ios27_spring [0.32,0,0.48,1], ios27_snappy [0.2,0,0.38,1] | ease_in_out, ease_out, ios_default | **2 nuevas bezier** | `DesignTokens.kt`: `ios27Spring = CubicBezierEasing(0.32f, 0f, 0.48f, 1f)`, `ios27Snappy = CubicBezierEasing(0.2f, 0f, 0.38f, 1f)` | **medido (HTML apple.com/ios CSS animation-timing-function)** |
| 19 | **Gestos (umbrales px)** | spotlight 200px, CC zone 240px H / 140px min drag, unlock 180px | spotlight 220px, CC 250px/160px, unlock 200px | **Umbrales -10 a -20px** | `SpringBoardScreen.kt`: `spotlightDragThreshold=200`, `ccZoneHeight=240`, `ccMinDrag=140`, `unlockDragThreshold=180` | **calibrado** |
| 20 | **Status bar custom (iOS 27)** | H 52dp, SF Pro Rounded 17sp weight 600 (SemiBold), símbolos SF Symbols semibold | H ~48dp, Roboto, símbolos custom vector | **H +4dp, peso SemiBold** | `SpringBoardScreen.kt` (CustomStatusBar): `height=52.dp`, `fontSize=17.sp`, `fontWeight=FontWeight.SemiBold`, símbolos vector weight 600. **Relacionada: #16** | **calibrado** |
| 21 | **Fuente del sistema (decisión en F, no en E)** | SF Pro Rounded (Apple, licencia restrictiva) | Roboto (Default) | **Candidatos OFL para F**: Inter Variable (100-900, proporciones SF Pro), Nunito Variable (200-1000, redondeada), M PLUS Rounded 1c (100-900) | **P3 - No tocar en E**. Decidir en F (rom-visual-spec) con side-by-side captures 96sp w200 contra frames lockscreen. Fallback E: `FontFamily.Default` + weight map (#16) | **N/A (decisión F)** |

---

## Resumen de Impacto Visual (Top-5 confirmado para Fase E)

| Prioridad | Fix | Impacto | Archivos | Relacionadas |
|-----------|-----|---------|----------|--------------|
| **P0-1** | LiquidGlass material v2 (tint 0.12, frost_blur 28dp, specular 0.14, shimmer 0.018) | **MÁXIMO** — vidrio firma iOS 27 | `LiquidGlassShader.kt`, `GlassSurface.kt`, `DesignTokens.kt` | #14 |
| **P0-2** | Squircle 0.2381 (n=5, k=1.528655) + label tracking -0.015em + gap 4dp | **MÁXIMO** — 60 iconos + labels | `Squircle.kt`, `SpringBoardScreen.kt`, `IconDrawables.kt`, `DesignTokens.kt` | — |
| **P0-3** | Lockscreen reloj 96sp weight 200 (Thin) tracking -0.02em (FontFamily.Default) | **MÁXIMO** — primera pantalla | `LockScreen.kt`, `Theme.kt` | #16, #21 |
| **P1-1** | Dynamic Island v2: compact 232×34dp/17dp, expanded 340×156dp/24dp, springs expand 0.62/200 collapse 0.75/280 | **ALTO** — elemento diferenciador | `DynamicIsland.kt`, `DesignTokens.kt` | #17 |
| **P1-2** | Dock (28dp corner, T8/R14/B8/L14, bottom 20) + CC (30/22/12) + Spotlight (80/26/52/38) | **ALTO** — superficies principales | `SpringBoardScreen.kt`, `ControlCenterOverlay.kt`, `GlassSurface.kt`, `DesignTokens.kt` | #10, #11, #12 |

---

## CHECKPOINT: Validación Usuario — **APROBADO CON CONDICIONES (todas resueltas arriba)**

1. ✅ Tabla 20 brechas confirmada — **condiciones A/B aplicadas**: peso 200 (Thin) una sola etiqueta, deduplicadas #13/#14 y #5/#16/#21, etiquetas "medido/calibrado" en toda fila, ecuación squircle exacta, convención W×Hdp / T/R/B/L.
2. ✅ Top-5 confirmado — sin scope creep, solo estos 5.
3. ✅ SF Pro Rounded — **NO TTF**. Fallback E: `FontFamily.Default` + weight map (#16). Decisión fuente en F (#21).
4. ✅ Flags resueltos: ópticas = "calibrado", squircle = ecuación exacta, geometría = W×Hdp/T/R/B/L.

**Listo para Fase E**.