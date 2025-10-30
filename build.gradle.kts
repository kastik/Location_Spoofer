plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.mapsSecrets) apply true
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.firebaseCrashlytics) apply false
    // alias(libs.plugins.firebasePerf) apply false
    alias(libs.plugins.googleServices) apply false
    alias(libs.plugins.hiltAndroid) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.androidTest) apply false
    alias(libs.plugins.baselineprofile) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.protobuf) apply false
}

buildscript {
    dependencies {
        classpath(libs.secrets.gradle.plugin)
        classpath(libs.protobuf.gradle.plugin)
    }
}