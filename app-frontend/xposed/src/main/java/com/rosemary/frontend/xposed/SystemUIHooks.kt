package com.rosemary.frontend.xposed

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * Punto de entrada del módulo Xposed (registrado en assets/xposed_init).
 *
 * Scope configurado en el manifest: com.android.systemui
 *
 * Roadmap de hooks sobre LineageOS 21 (rosemary):
 *  - StatusBar: translucidez + estilo de iconos iOS.
 *  - Keyguard: pantalla de bloqueo con blur y animaciones spring.
 *  - QS/Quick Settings: panel re-implementado estilo iOS (o passthrough
 *    al Centro de Control de este propio frontend).
 */
class SystemUIHooks : IXposedHookLoadPackage {

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != "com.android.systemui") return

        XposedBridge.log("[iOS-Rosemary] Proceso SystemUI detectado (${lpparam.processName})")

        try {
            // Placeholder de integración: el hook real se diseñará tras
            // el análisis con jadx/apktool de device-dumps/SystemUI.apk.
            val sysuiClassLoader = lpparam.classLoader
            XposedBridge.log("[iOS-Rosemary] ClassLoader de SystemUI disponible: $sysuiClassLoader")
        } catch (t: Throwable) {
            XposedBridge.log("[iOS-Rosemary] Error inicializando hooks: $t")
        }
    }
}
