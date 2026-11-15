package com.rosemary.frontend.ui

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.graphics.Shader
import android.os.Build
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * LiquidGlass — vidrio líquido estilo iOS en AGSL (Android Graphics Shading Language).
 *
 * API 33+ : RenderEffect chain = blur (frosted) -> RuntimeShader (refracción de bordes,
 *           aberración cromática sutil y tinte translúcido frío).
 * API 31/32: fallback limpio = solo blur (el tinte lo aporta el contenedor).
 *
 * Optimización 60fps: un solo pase, sin loops, 3 muestras de textura por píxel
 * (R/G/B) y matemática de bajo costo -> apto para Mali-G76 sin sobrecalentar.
 */
private const val LIQUID_GLASS_AGSL = """
uniform shader uContent;
uniform float2 uResolution;
uniform float uTime;
uniform float uStrength;
uniform float4 uTint;
uniform float uSpecular;

half4 main(float2 fragCoord) {
    float2 uv = fragCoord / uResolution;
    float2 center = uv - 0.5;
    float dist = length(center);
    float2 dir = dist > 0.0001 ? center / dist : float2(0.0, 0.0);

    // Refracción tipo lente gruesa (calibrado con frames del video oficial de Apple)
    float r = uStrength * (0.018 + 0.06 * dist * dist);
    float2 base = dir * r * uResolution;

    // Aberración cromática sutil: cada canal refracta con factor ligeramente distinto
    half4 cR = uContent.eval(fragCoord + base * 1.006);
    half4 cG = uContent.eval(fragCoord + base);
    half4 cB = uContent.eval(fragCoord + base * 0.994);
    float3 col = float3(cR.r, cG.g, cB.b);
    float alpha = float(cG.a);

    // Tinte translúcido frío estilo iOS
    col = mix(col, float3(uTint.r, uTint.g, uTint.b), float(uTint.a));

    // Iluminación direccional desde arriba (rim brillante arriba, borde denso abajo)
    float rim = smoothstep(0.22, 0.46, dist);
    float topLight = smoothstep(1.0, 0.0, uv.y);
    col += rim * uSpecular * (0.35 + 0.65 * topLight);
    col *= 1.0 - rim * 0.08 * (1.0 - topLight);

    // Brillo casi imperceptible animable
    float shimmer = 0.5 + 0.5 * sin(uTime * 1.5);
    col += 0.012 * shimmer;

    return half4(col, min(1.0, alpha + rim * 0.05));
}
"""

private val glassShaderSource: String = LIQUID_GLASS_AGSL

/**
 * Holder del efecto LiquidGlass: RuntimeShader + RenderEffect (blur chain)
 * creados UNA vez por instancia de Modifier. Por frame solo se actualiza el
 * uniform uResolution (operacion sin allocacion) y se re-asigna el efecto.
 */
private class LiquidGlassHolder(
    val composeEffect: androidx.compose.ui.graphics.RenderEffect?,
    private val shader: RuntimeShader?,
) {
    fun bindResolution(width: Float, height: Float) {
        val s = shader ?: return
        try {
            s.setFloatUniform("uResolution", width, height)
        } catch (_: Exception) { /* pre-layout: size 0, se re-intenta el proximo frame */ }
    }
}

/**
 * Modifier de vidrio líquido.
 * @param tint    tinte translúcido (se mezcla dentro del shader en API 33+)
 * @param strength intensidad de la refracción de bordes
 * @param blurDp  radio del blur frosted (encadena antes del shader)
 * @param timeSec segundos para animar el shimmer (0 = estático, ahorra GPU)
 */
fun Modifier.liquidGlass(
    tint: Color = Color(0x142E3A5F),
    strength: Float = 1f,
    blurDp: Dp = 32.dp,
    timeSec: Float = 0f,
): Modifier = composed {
    val density = androidx.compose.ui.platform.LocalDensity.current
    val specular = IOSTokens.GLASS_SPECULAR_INTENSITY
    // TODO el pipeline AGSL se construye una sola vez por instancia (cero alloc por frame)
    val holder = remember(tint, strength, blurDp, timeSec, density, specular) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val radius = with(density) { blurDp.toPx() }
                val shader = RuntimeShader(glassShaderSource)
                shader.setFloatUniform("uStrength", strength)
                shader.setFloatUniform("uTint", tint.red, tint.green, tint.blue, tint.alpha)
                shader.setFloatUniform("uTime", timeSec)
                shader.setFloatUniform("uSpecular", specular)
                val blur = RenderEffect.createBlurEffect(radius, radius, Shader.TileMode.CLAMP)
                val shaderEffect = RenderEffect.createRuntimeShaderEffect(shader, "uContent")
                // inner = blur, outer = shader (el shader refracta el contenido ya difuminado)
                LiquidGlassHolder(
                    RenderEffect.createChainEffect(shaderEffect, blur).asComposeRenderEffect(),
                    shader,
                )
            } else {
                // Fallback API 31–32: blur nativo + tinte de fondo del contenedor
                val radius = with(density) { blurDp.toPx() }
                LiquidGlassHolder(
                    RenderEffect.createBlurEffect(radius, radius, Shader.TileMode.CLAMP).asComposeRenderEffect(),
                    null,
                )
            }
        } catch (_: Exception) {
            LiquidGlassHolder(null, null)
        }
    }
    graphicsLayer {
        holder.bindResolution(size.width, size.height)
        renderEffect = holder.composeEffect
    }
}