# AGENTS.md — iOS-Rosemary Workspace

## Estado del frontend

Launcher iOS en Compose completo y verificado en emulador (Rosemary_Test, 1080x2400@409dpi, API 34): SpringBoard (grid 4x6 + dock), Spotlight, Control Center funcional, Dynamic Island (pill + tarjeta música), Lockscreen swipe-to-unlock, LiquidGlass AGSL (API 33+ con fallback 31/32), GlassSurface (RenderEffect), iconos 100% vector/reales (0 emojis), PSS 83.7 MB, minify+shrink on. Stack: AGP 8.7.3, Kotlin 2.1.0, Compose BOM 2024.12.01, compileSdk 34, minSdk 31.

Backlog frontend: re-lock trigger, notificaciones en lockscreen, AOD, carpetas con squircle, app switcher.

**Estado actual (sesión rediseño iOS 27):** Frontend REABIERTO para rediseño visual con paridad a iOS 27. Fase A completada: REA operativo con Node.js ≥ 22.23.4. Fases B-F pendientes.

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

1. **Reiniciar opencode** → REA hereda Node v22.23.5
2. **Fase A2**: `rea update` + verificar `rea --help`
3. **Fase B**: Evidencia estado actual (screenshots before + auditoría REA APK)
4. **Fase C**: Referencia iOS 27 real → tokens v2
5. **Fase D**: Informe de brechas ≥15 filas
6. **Fase E**: Top-5 fixes mayor impacto visual
7. **Fase F**: Puente ROM (rom-visual-spec.md)
8. **Pista ROM (diferida)**: F3-F6 cuando haya build server remoto
   - F3) `source build/envsetup.sh && breakfast rosemary`
   - F4) Blobs: zip oficial 24.0 rosemary → payload-dumper-go → extract-files.py
   - F5) `croot && brunch rosemary`
   - F6) Personalización: overlays, frameworks/base, launcher app sistema

## Reglas de trabajo

- Un paso a la vez; mostrar comando antes si toca sistema
- sudo solo para instalación paquetes sistema; rm -rf solo en ~/android
- Sin emojis en código; RAM < 65 MB PSS; 60fps en Mali-G76
- Verificación en emulador con screenshots ANTES/DESPUÉS de cada cambio
- Errores completos al BUILD_LOG.md; máx 2 reintentos por comando
- REA: npm only (npx rea-agents setup / rea update), respaldo opencode.json previo
- Referencias iOS 27: SOLO material público real; métricas, no assets