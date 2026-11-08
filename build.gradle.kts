plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "br.com.minimallauncher"
    compileSdk = 35

    defaultConfig {
        applicationId = "br.com.minimallauncher"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }
}
