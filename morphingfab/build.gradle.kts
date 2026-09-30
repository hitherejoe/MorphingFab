import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeCompiler)
}

// Compose and nothing else. Anything that would pull in an icon pack, a string loader or a
// reduced-motion preference belongs in a parameter, not in here.
kotlin {
    androidTarget { compilerOptions { jvmTarget.set(JvmTarget.JVM_17) } }

    // No iosX64: Compose Multiplatform no longer ships artifacts for it, so declaring the target
    // would only produce a source set whose dependencies can't resolve.
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        // `api`, not `implementation`: Rect, Modifier, Color, Dp and the MaterialTheme colours all
        // appear in the public signatures, so consumers need them on the compile classpath.
        commonMain.dependencies {
            api(libs.compose.runtime)
            api(libs.compose.foundation)
            api(libs.compose.material3)
            api(libs.compose.ui)
        }

        // Only for the actual BackHandler, which delegates to OnBackPressedDispatcher.
        androidMain.dependencies { implementation(libs.androidx.activity.compose) }
    }
}

android {
    namespace = "cards.sleevd.morphingfab"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig { minSdk = libs.versions.minSdk.get().toInt() }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
