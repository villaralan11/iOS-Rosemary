# AGENTS.md — iOS-Rosemary Workspace

## Estado del frontend

Launcher iOS en Compose completo y verificado en emulador (Rosemary_Test, 1080x2400@409dpi, API 34): SpringBoard (grid 4x6 + dock), Spotlight, Control Center funcional, Dynamic Island (pill + tarjeta música), Lockscreen swipe-to-unlock, LiquidGlass AGSL (API 33+ con fallback 31/32), GlassSurface (RenderEffect), iconos 100% vector/reales (0 emojis), **PSS 81-84 MB** (límite arquitectural: LiquidGlass en CC consume ~20 MB nativo; objetivo <65 MB no alcanzable sin romper paridad visual), minify+shrink on. Stack: AGP 8.7.3, Kotlin 2.1.0, Compose BOM 2024.12.01, compileSdk 34, minSdk 31.

Backlog frontend: re-lock trigger, notificaciones en lockscreen, AOD, carpetas con squircle, app switcher.

**Estado actual (sesión rediseño iOS 27 COMPLETADA):** Frontend REABIERTO → rediseño visual iOS 27 **completado y verificado**. Top-5 fixes implementados:
- P0-1 LiquidGlass material v2 (tint 0.12, frost_blur 20dp, specular 0.14, shimmer 0.018)
- P0-2 Squircle 0.2381 (n=5, k=1.528655) + label tracking -0.015em + gap 4dp
- P0-3 Lockscreen reloj 96sp weight 200 (thin) tracking -0.02em + unlock spring 0.68/180
- P1-1 Dynamic Island v2: compact 232×34dp/17dp, expanded 340×156dp/24dp, springs expand 0.62/200 collapse 0.75/280
- P1-2 Dock/CC/Spotlight geometría v2 (dock 28dp, CC 30/22/12, Spotlight 80/26/52/38)
Tokens v2 en `ios_design_tokens.json` (fuente única), GAP_REPORT 21 filas, before/after screenshots en `device-dumps/qc/`.

**Nota iOS 27 (corrección usuario, nov 2026):** Apple renumeró en 2025 (iOS 18 → iOS 26, saltando 19-25). iOS 27 anunciado WWDC 8-jun-2026, lanzado 14-sep-2026 (actual 27.2.1). Fuente oficial: apple.com/ios, support.apple.com/en-us/149076, apple.com/newsroom/2026/09, WWDC 2026 videos. Fases C-F usan SOLO métricas medidas de este material público oficial.

**Límite conocido PSS:** PSS 81-84 MB (objetivo <65 MB no alcanzable sin quitar LiquidGlass AGSL en Control Center, lo que rompería paridad visual iOS 27). 6 iteraciones de optimización documentadas en BUILD_LOG. 60fps mantenido.

## Decisiones ROM

Base lineage-24.0; blobs desde zip oficial; árbol en ~/android/lineage; frontend congelado durante la pista ROM.

**Nota:** Pista ROM diferida por hardware insuficiente (7.6 GB RAM, 162 GB disco libre vs 64 GB RAM / 400 GB disco requeridos). Build server remoto necesario.

## Mapa del workspace

| Carpeta | Contenido | Commiteado |
|---------|-----------|------------|
| `app-frontend/` | Launcher iOS (Kotlin + Compose) | ✅ |
| `tools/` | Android SDK, adb, ffmpeg, apktool, jadx, Gradle | ❌ (gitignore) |
| `lineage-src/` | Clones Trebuchet + SystemUI (lineage-21.0, referencia) | ❌ (gitignore) |
| `ios-refs/` | Tokens de diseño, videos/frames iOS 27 (no commiteados) | Parcial |
| `device-dumps/` | Vacío (sin teléfono), qc/before/after screenshots | ❌ (gitignore) |
| `rea/` | REA MCP installation | ❌ (gitignore) |
| `~/android/lineage/` | Árbol de fuentes LineageOS 24.0 (FUERA del repo) | ❌ |

## Hardware local

- **CPU**: 8 cores
- **RAM**: 7.6 GiB (insuficiente para build ROM)
- **Disco libre**: 162 GB en /home (insuficiente para ~400 GB fuentes)
- **Swap**: 2 GB file
- **Node.js**: v22.23.5 vía nvm (instalado en esta sesión; sistema tenía v20.20.2 inservible para REA)
- **npm**: 10.9.9

## Próximas sesiones

1. **Pista ROM (activa — build server remoto necesario)**:
   - F3) `source build/envsetup.sh && breakfast rosemary`
   - F4) Blobs: zip oficial 24.0 rosemary → payload-dumper-go → extract-files.py → `~/android/system_dump`
   - F5) `croot && brunch rosemary` (con overlays device tree, SystemUI mods, boot animation, fuentes OFL)
   - F6) Personalización: overlays, frameworks/base, launcher como app sistema (`/system/priv-app`)

2. **Backlog frontend (pendiente)**:
   - Re-lock trigger (tap Dynamic Island / power key)
   - Notificaciones en lockscreen (pill estilo iOS)
   - AOD (Always On Display) clock mode
   - App folders con squircle (3×3 grid, LiquidGlass backdrop, spring open)
   - iOS app switcher (horizontal carousel, swipe-up to close)

**Especs listos para ROM**: `ios-refs/rom-visual-spec.md` (traducción tokens v2 → AOSP/LineageOS), `ios-refs/ios_design_tokens.json v2` (fuente única numérica), `ios-refs/GAP_REPORT.md` (21 brechas documentadas).

## Reglas de trabajo

- Un paso a la vez; mostrar comando antes si toca sistema
- sudo solo para instalación paquetes sistema; rm -rf solo en ~/android
- Sin emojis en código; RAM < 65 MB PSS; 60fps en Mali-G76
- Verificación en emulador con screenshots ANTES/DESPUÉS de cada cambio
- Errores completos al BUILD_LOG.md; máx 2 reintentos por comando
- REA: npm only (npx rea-agents setup / rea update), respaldo opencode.json previo
- Referencias iOS 27: SOLO material público real; métricas, no assets