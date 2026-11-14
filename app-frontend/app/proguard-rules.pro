# ProGuard para el frontend iOS-Rosemary (release ultra-ligero)
# Conserva el punto de entrada de Xposed (referenciado por assets/xposed_init)
-keep class com.rosemary.frontend.xposed.SystemUIHooks { *; }
-keep class de.robv.android.xposed.** { *; }

# Conserva las actividades declaradas en el manifest
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.preference.Preference

# Conserva serializables y parcelables
-keep class * implements android.os.Parcelable { *; }
-keep class * implements java.io.Serializable { *; }

# Compose / Kotlin: no ofuscar funciones @Composable ni propiedades @Model
-keep class kotlinx.coroutines.** { *; }
-keep class androidx.compose.** { *; }
-keep class androidx.lifecycle.** { *; }

# Mantiene anotaciones de Compose para recomposición
-keep @interface androidx.compose.runtime.Composable
-keep @interface androidx.compose.runtime.Stable
-keep @interface androidx.compose.runtime.Immutable

# Evita que R8 elimine recursos de layout/drawable usados por reflexión en Compose
-keepclassmembers class ** {
    @androidx.compose.ui.res.** *;
}

# Optimizaciones específicas para reducir tamaño
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-allowaccessmodification