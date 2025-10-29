import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.library")
    alias(libs.plugins.kotlinAndroid)
}

android {
    namespace = "com.kastik.locationspoofer.test"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_17
        }
    }

    testOptions {
        unitTests.isIncludeAndroidResources = true
    }
}

dependencies {
    implementation(project(":locationSpoofer"))
    implementation(libs.androidx.junit.ktx)

    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
    testImplementation("app.cash.turbine:turbine:1.1.0")
    testImplementation("io.mockk:mockk:1.13.9")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testImplementation(kotlin("test"))
}
