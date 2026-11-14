plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.rosemary.frontend.xposed"
    compileSdk = 34

    defaultConfig {
        minSdk = 31
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // API de Xposed clásica (compatible con LSPosed). Solo compilación:
    // el framework la proporciona en runtime dentro de cada proceso hookeado.
    compileOnly(files("../app/libs/api-82.jar"))
}
