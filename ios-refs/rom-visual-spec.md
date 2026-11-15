# ROM VISUAL SPEC — iOS 27 Look & Feel para LineageOS 24 (rosemary)
**Fuente**: ios_design_tokens.json v2 (métricas medidas de material oficial Apple iOS 27)
**Objetivo**: Que el dispositivo se vea/comporte como iPhone FUERA de la app launcher (SystemUI, boot, overlays, tipografía sistema, launcher como app sistema).

---

## 1. Overlays Device Tree (device/xiaomi/rosemary/overlay)

### Frameworks-res overlay (frameworks/base/core/res/res/values)
```xml
<!-- config.xml -->
<dimen name="status_bar_height">52dp</dimen>           <!-- iOS 27: 52dp vs 48dp actual -->
<dimen name="navigation_bar_height">0dp</dimen>        <!-- Oculta; gestos iOS -->
<dimen name="status_bar_icon_size">17dp</dimen>        <!-- SF Symbols semibold -->
<integer name="config_statusBarComponent">0</integer>  <!-- Deshabilita status bar AOSP -->
<integer name="config_navBarComponent">0</integer>     <!-- Deshabilita nav bar AOSP -->
```

### SystemUI overlay (packages/apps/SystemUI/res/values)
```xml
<!-- dimens.xml -->
<dimen name="qs_panel_corner_radius">30dp</dimen>              <!-- CC panel -->
<dimen name="qs_tile_corner_radius">22dp</dimen>               <!-- CC módulos -->
<dimen name="qs_tile_spacing">12dp</dimen>                     <!-- CC spacing -->
<dimen name="qs_slider_track_height">36dp</dimen>              <!-- Brillo/Vol -->
<dimen name="qs_icon_size">38dp</dimen>                        <!-- Toggle icons -->
<dimen name="qs_media_button_size">50dp</dimen>                <!-- Media buttons -->
<dimen name="lockscreen_clock_size">96sp</dimen>               <!-- Lockscreen reloj -->
<dimen name="lockscreen_clock_weight">200</dimen>              <!-- Thin -->
<dimen name="lockscreen_clock_tracking">-0.02</dimen>          <!-- em -->
<dimen name="lockscreen_date_size">17sp</dimen>
<dimen name="lockscreen_date_weight">400</dimen>
<dimen name="lockscreen_date_tracking">-0.01</dimen>
<dimen name="lockscreen_button_size">52dp</dimen>              <!-- Linterna/Cámara -->
<dimen name="dynamic_island_compact_width">232dp</dimen>
<dimen name="dynamic_island_compact_height">34dp</dimen>
<dimen name="dynamic_island_compact_radius">17dp</dimen>
<dimen name="dynamic_island_expanded_width">340dp</dimen>
<dimen name="dynamic_island_expanded_height">156dp</dimen>
<dimen name="dynamic_island_expanded_radius">24dp</dimen>
```

### Colors (frameworks/base/core/res/res/values/colors.xml)
```xml
<color name="ios_tint_base">#0A1F2E</color>
<color name="ios_tint_base_alpha">#1E0A1F2E</color>  <!-- 12% -->
<color name="ios_accent_blue">#0066FF</color>
<color name="ios_accent_green">#30D158</color>
<color name="ios_accent_orange">#FF9F0A</color>
<color name="ios_accent_red">#FF453A</color>
<color name="ios_accent_purple">#BF5AF2</color>
<color name="ios_text_primary">#FFFFFF</color>
<color name="ios_text_secondary">#CCFFFFFF</color>
<color name="ios_text_tertiary">#99FFFFFF</color>
<color name="ios_separator">#1AFFFFFF</color>
<color name="ios_glass_tint">#5F0A1F2E</color>
<color name="ios_glass_specular">#33FFFFFF</color>
```

### Blur materials (frameworks/base/core/res/res/values/dimens.xml)
```xml
<dimen name="blur_ultra_thin">20dp</dimen>    <!-- systemUltraThinMaterial (E: 20dp ajustado por PSS; C: 28dp medido) -->
<dimen name="blur_regular">20dp</dimen>      <!-- systemMaterial (E: 20dp ajustado por PSS; C: 38dp medido) -->
<dimen name="blur_thick">30dp</dimen>        <!-- systemThickMaterial (E: 30dp ajustado por PSS; C: 50dp medido) -->
```
**Nota**: Valores base iOS 27 medidos (28/38/50dp) ajustados a 20/20/30dp en E por límite PSS (<65 MB). La capa ROM debe usar los valores E (20/20/30dp) para paridad visual con el launcher.
```

---

## 2. SystemUI (packages/apps/SystemUI)

### Status Bar (com.android.systemui.statusbar)
- **Altura**: 52dp (vs 48dp)
- **Fuente**: Roboto Thin (weight 200) 17sp tracking 0 (fallback) / SF Pro Rounded si se incluye
- **Iconos**: SF Symbols semibold (weight 600) 17dp — migrar drawables vectoriales
- **Ocultación**: `BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE` (ya en launcher)

### Quick Settings / Control Center (com.android.systemui.qs)
- **Panel**: corner 30dp, margin 12dp, frostRegular (38dp blur)
- **Tiles/Módulos**: corner 22dp, spacing 12dp, frostUltraThin (28dp blur)
- **Sliders**: track height 36dp, thumb 24dp, colors iOS accent
- **Toggles**: 38dp, iconos SF Symbols outline
- **Media**: 50dp botones, carátula 52dp corner 14dp
- **Animaciones**: springs iOS 27 (expand 0.62/200, collapse 0.75/280)

### Lockscreen (com.android.systemui.keyguard)
- **Reloj**: 96sp weight 200 (Thin) tracking -0.02em, shadow 0 2px 8px #80000000
- **Fecha**: 17sp weight 400 tracking -0.01em
- **Botones**: 52dp circulares, liquidGlass (tint #5F0A1F2E, frost 28dp)
- **Desbloqueo**: swipe-up threshold 180px, spring 0.68/180
- **Wallpaper**: blur 22dp + scrim #66000000

### Dynamic Island (NUEVO — requiere servicio SystemUI)
- **Compacta**: 232×34dp, corner 17dp (pill: 2 semicírculos 17dp + rect 198dp)
- **Expandida**: 340×156dp, corner 24dp
- **Springs**: expand 0.62/200, collapse 0.75/280
- **Rim especular**: alpha 0.14 top gradient
- **Integración**: MediaSession + BatteryService + CameraManager (torch)

---

## 3. Boot Animation (device/xiaomi/rosemary/bootanimation.zip)

### Especificación
- **Resolución**: 1080×2400 @ 409dpi
- **FPS**: 60
- **Duración**: ~2.5s (loop until boot complete)
- **Parte 1 (0-1.2s)**: Apple logo fade-in con liquidGlass shimmer (0.018 amp, 1.6 speed)
- **Parte 2 (1.2-2.5s)**: Logo hold + sutil pulse (spring 0.85/220)
- **Colores**: P3, background #000000, logo #FFFFFF con glass_tint #5F0A1F2E

### Archivos
```
bootanimation.zip
├── desc.txt          # 1080 2400 60
├── part0/            # 72 frames (1.2s @ 60fps)
│   ├── 00001.png
│   └── ...
├── part1/            # 78 frames (1.3s @ 60fps, loop=0)
│   ├── 00001.png
│   └── ...
└── part2/            # 0 frames (clean exit)
```

---

## 4. Launcher como App Sistema (preinstalado en /system/priv-app)

### AndroidManifest.xml (app sistema)
```xml
<manifest package="com.rosemary.frontend"
    android:sharedUserId="android.uid.system">
    <application
        android:persistent="true"
        android:supportsRtl="true"
        android:theme="@style/Theme.iOSLauncher">
        <activity
            android:name=".MainActivity"
            android:launchMode="singleTask"
            android:clearTaskOnLaunch="true"
            android:stateNotNeeded="true"
            android:resumeWhilePausing="true"
            android:configChanges="keyboard|keyboardHidden|navigation|orientation|screenLayout|uiMode|screenSize|smallestScreenSize|density"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.HOME" />
                <category android:name="android.intent.category.DEFAULT" />
            </intent-filter>
        </activity>
    </application>
</manifest>
```

### Theme.iOSLauncher (res/values/themes.xml)
```xml
<style name="Theme.iOSLauncher" parent="android:Theme.Material.Light.NoActionBar">
    <item name="android:windowTranslucentStatus">true</item>
    <item name="android:windowTranslucentNavigation">true</item>
    <item name="android:windowDrawsSystemBarBackgrounds">false</item>
    <item name="android:statusBarColor">@android:color/transparent</item>
    <item name="android:navigationBarColor">@android:color/transparent</item>
    <item name="android:windowLightStatusBar">false</item>
    <item name="android:windowLightNavigationBar">false</item>
</style>
```

### Preinstalación (device/xiaomi/rosemary/preinstalled-packages.mk)
```makefile
PRODUCT_PACKAGES += \
    iOSRosemaryLauncher

PRODUCT_SYSTEM_DEFAULT_PROPERTIES += \
    ro.sys.fw.dex2oat_thread_count=8 \
    dalvik.vm.dex2oat-threads=8
```

---

## 5. Tipografía del Sistema (Fonts)

### Decisión: **NO usar SF Pro TTF** (licencia Apple restrictiva, repo público)
### Candidatos OFL para `frameworks/base/data/fonts/`:

| Fuente | Rango peso | Proporciones | Uso recomendado |
|--------|------------|--------------|-----------------|
| **Inter Variable** | 100-900 | Muy cercanas a SF Pro | UI general, labels, system font |
| **Nunito Variable** | 200-1000 | Redondeada, geométrica | Reloj lockscreen (200), display |
| **M PLUS Rounded 1c** | 100-900 | Redondeada, CJK ready | Reloj lockscreen, fecha |

### Implementación (fonts.xml)
```xml
<family name="sans-serif">
    <font weight="100" style="normal">Inter-Thin.ttf</font>
    <font weight="200" style="normal">Inter-ExtraLight.ttf</font>
    <font weight="300" style="normal">Inter-Light.ttf</font>
    <font weight="400" style="normal">Inter-Regular.ttf</font>
    <font weight="500" style="normal">Inter-Medium.ttf</font>
    <font weight="600" style="normal">Inter-SemiBold.ttf</font>
    <font weight="700" style="normal">Inter-Bold.ttf</font>
    <font weight="800" style="normal">Inter-ExtraBold.ttf</font>
    <font weight="900" style="normal">Inter-Black.ttf</font>
</family>
<family name="sans-serif-rounded">
    <font weight="200" style="normal">Nunito-ExtraLight.ttf</font>
    <font weight="300" style="normal">Nunito-Light.ttf</font>
    <font weight="400" style="normal">Nunito-Regular.ttf</font>
    <font weight="500" style="normal">Nunito-Medium.ttf</font>
    <font weight="600" style="normal">Nunito-SemiBold.ttf</font>
    <font weight="700" style="normal">Nunito-Bold.ttf</font>
</family>
```

### Mapeo pesos iOS 27 → Android
| iOS weight | Nombre iOS | Android weight | Fuente |
|------------|------------|----------------|--------|
| 100 | UltraLight | 100 | Inter-Thin |
| 200 | Thin | 200 | Inter-ExtraLight / Nunito-ExtraLight |
| 300 | Light | 300 | Inter-Light |
| 400 | Regular | 400 | Inter-Regular |
| 500 | Medium | 500 | Inter-Medium |
| 600 | Semibold | 600 | Inter-SemiBold |
| 700 | Bold | 700 | Inter-Bold |
| 800 | Heavy | 800 | Inter-ExtraBold |
| 900 | Black | 900 | Inter-Black |

**Lockscreen reloj**: `sans-serif-rounded` weight 200 (Nunito ExtraLight) 96sp tracking -0.02em

---

## 6. Squircle Consistency (SystemUI + Launcher)

### Ecuación única (ambas capas)
```kotlin
// Superellipse n=5
// r = min(w,h) * 0.2381
// continuous_corner_factor = 1.528655
// Fórmula: x^n + y^n = r^n
```

### Aplicación
| Componente | Radio (ratio) | Implementación |
|------------|---------------|---------------|
| Iconos launcher | 0.2381 (64dp → 15.2dp) | `SquircleShape` Compose |
| Dock launcher | 28dp fijo | `RoundedCornerShape(28.dp)` |
| CC Panel | 30dp | `RoundedCornerShape(30.dp)` |
| CC Tiles | 22dp | `RoundedCornerShape(22.dp)` |
| Lockscreen botones | 0.5 (52dp → 26dp) | `CircleShape` |
| Dynamic Island compacta | 17dp | `RoundedCornerShape(17.dp)` |
| Dynamic Island expandida | 24dp | `RoundedCornerShape(24.dp)` |
| Spotlight panel | 26dp | `RoundedCornerShape(26.dp)` |

---

## 7. Color Palette (Display P3) — System-wide

```xml
<!-- frameworks/base/core/res/res/values/colors.xml -->
<color name="system_bg">#000000</color>
<color name="system_surface">#1C1C1E</color>
<color name="system_surface_elevated">#2C2C2E</color>
<color name="system_tint_base">#0A1F2E</color>
<color name="system_accent_blue">#0066FF</color>
<color name="system_accent_green">#30D158</color>
<color name="system_accent_orange">#FF9F0A</color>
<color name="system_accent_red">#FF453A</color>
<color name="system_accent_purple">#BF5AF2</color>
<color name="system_text_primary">#FFFFFF</color>
<color name="system_text_secondary">#CCFFFFFF</color>
<color name="system_text_tertiary">#99FFFFFF</color>
<color name="system_separator">#1AFFFFFF</color>
<color name="system_glass_tint">#5F0A1F2E</color>
<color name="system_glass_specular">#33FFFFFF</color>
```

---

## 8. Springs & Curves (SystemUI Animations)

```xml
<!-- frameworks/base/core/res/res/values/dimens.xml -->
<!-- CASpringAnimation → Android Spring(dampingRatio, stiffness) -->
<item name="spring_icon_press" type="dimen">0.65,500</item>
<item name="spring_icon_release" type="dimen">0.82,380</item>
<item name="spring_page_swipe" type="dimen">1.0,300</item>
<item name="spring_overlay_open" type="dimen">0.725,170</item>
<item name="spring_overlay_close" type="dimen">1.0,250</item>
<item name="spring_indicator_dot" type="dimen">0.82,380</item>
<item name="spring_di_expand" type="dimen">0.62,200</item>
<item name="spring_di_collapse" type="dimen">0.75,280</item>
<item name="spring_unlock" type="dimen">0.68,180</item>
<item name="spring_widget" type="dimen">0.85,220</item>
```

### Curvas Bezier (CAMediaTimingFunction)
```xml
<item name="curve_ios27_spring" type="string">0.32,0,0.48,1</item>
<item name="curve_ios27_snappy" type="string">0.2,0,0.38,1</item>
```

---

## 9. LiquidGlass Material (SystemUI RenderEffect)

### Parámetros AGSL (para SurfaceFlinger / RenderThread)
```glsl
// uniforms v2
tint_alpha = 0.12
frost_blur_dp = 20
specular_intensity = 0.14
shimmer_amplitude = 0.018
shimmer_speed = 1.6
refraction_strength = 0.028
refraction_curvature = 0.09
chromatic_aberration = 0.005
tint_color = #0A1F2E
```
**Nota**: Valor base iOS 27 medido 28dp (C); E ajustado a 20dp por límite PSS. Shared AGSL shader (singleton) Dock+CC en launcher.

### Cadena RenderEffect (API 31+)
```kotlin
// Inner: blur(frost_blur_dp=20dp) → Outer: RuntimeShader(LiquidGlass)
// Fallback API 31-32: solo blur nativo (vivo en LiquidGlassShader.kt:187-189)
```
**Nota**: Shared AGSL shader (singleton) entre Dock y CC en launcher.

---

## 10. Gestos (SystemUI Touch Zones)

| Gesture | Zona | Threshold | Acción |
|---------|------|-----------|--------|
| Spotlight | Centro superior (y < 200px) | 200px drag down | Abrir Spotlight |
| Control Center | Top-end (x > 540px, y < 240px) | 140px drag down | Abrir CC |
| Unlock | Lockscreen (y > 1200px) | 180px drag up | Desbloquear |
| Dynamic Island | Tap en pill (y < 100px) | Tap | Expandir/colapsar |

---

## 11. Checklist de Implementación (F4-F6)

### F4: Blobs + extract-files.py
- [ ] Descargar zip oficial LineageOS 24 rosemary
- [ ] `payload-dumper-go` extraer super/system/vendor/product/boot/dtbo
- [ ] Montar .img en `~/android/system_dump`
- [ ] `./extract-files.py ~/android/system_dump`

### F5: Build ROM
- [ ] `croot && breakfast rosemary`
- [ ] Aplicar overlays device tree (sección 1)
- [ ] Modificar SystemUI (sección 2)
- [ ] Boot animation (sección 3)
- [ ] Fuentes OFL (sección 5)
- [ ] `croot && brunch rosemary`

### F6: Personalización
- [ ] Preinstalar launcher en `/system/priv-app`
- [ ] Configurar como HOME default
- [ ] Verificar en hardware real (cuando POCO M5s disponible)
- [ ] Screenshots device-dumps/qc/device/

---

## 12. Validación Visual (Paridad iOS 27)

| Componente | Métrica clave | Tolerancia |
|------------|---------------|------------|
| Lockscreen reloj | 96sp w200 tracking -0.02em | ±2sp, ±0.005em |
| Dock corner | 28dp | ±1dp |
| CC panel corner | 30dp | ±1dp |
| CC module corner | 22dp | ±1dp |
| Dynamic Island compacta | 232×34dp / 17dp | ±2dp / ±1dp |
| Dynamic Island expandida | 340×156dp / 24dp | ±4dp / ±2dp |
| Squircle iconos | ratio 0.2381 n=5 | ±0.005 |
| LiquidGlass tint_alpha | 0.12 | ±0.01 |
| LiquidGlass frost_blur | 28dp | ±2dp |
| Colores tint_base | #0A1F2E | Exacto hex |
| Colores accent_blue | #0066FF | Exacto hex |

---

**Fin del spec** — Este documento es la entrada para F4 (parches locales) y F5 (build ROM). Todas las métricas derivan de `ios_design_tokens.json v2` medido de fuentes oficiales Apple iOS 27.