import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidLibrary)
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
    // Core
    testImplementation(project(":locationSpoofer"))
    testImplementation(project(":proto"))

    // Kotlin test utilities
    testImplementation(kotlin("test"))
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)

    // JUnit
    testImplementation(libs.junit.platform.suite)

    // AndroidX / DataStore
    testImplementation(libs.androidx.junit.ktx)
    testImplementation(libs.androidx.datastore)

}


tasks.withType<Test>().configureEach {
    // ensures JUnit5 platform runs kotlin.test and MockK together
    useJUnitPlatform()
}
