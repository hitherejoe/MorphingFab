import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.composeCompiler)
}

// A shell. Everything worth looking at is in :sample's commonMain.
android {
    namespace = "cards.sleevd.morphingfab.sample.android"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "cards.sleevd.morphingfab.sample"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "0.1.0"
    }

    buildFeatures { compose = true }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin { compilerOptions { jvmTarget.set(JvmTarget.JVM_17) } }

// Compose itself arrives transitively through activity-compose, which has to expose the runtime
// for `setContent` to take a @Composable lambda. Nothing else is needed to render SampleApp().
dependencies {
    implementation(project(":sample"))
    implementation(libs.androidx.activity.compose)
}
