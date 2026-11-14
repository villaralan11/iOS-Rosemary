plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.rosemary.frontend"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.rosemary.frontend"
        // minSdk 31 (Android 12, base del POCO M5s / rosemary con LineageOS).
        // NOTA: RuntimeShader (AGSL) requiere API 33+; el código AGSL va protegido
        // con Build.VERSION checks y existe fallback con RenderEffect/blur en 31/32.
        minSdk = 31
        targetSdk = 34
        versionCode = 1
        versionName = "0.1.0-springboard"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(project(":xposed"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.animation)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // API de Xposed/LSPosed solo en tiempo de compilación (la inyecta el framework en runtime)
    compileOnly(files("libs/api-82.jar"))
}
