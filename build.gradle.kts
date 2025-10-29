plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.mapsSecrets) apply true
    alias(libs.plugins.compose.compiler) apply false
    id("com.google.firebase.crashlytics") version "3.0.6" apply false
    //id("com.google.firebase.firebase-perf") version "2.0.1" apply false
    id("com.google.gms.google-services") version "4.4.3" apply false
    id("com.google.dagger.hilt.android") version "2.57" apply false
    id("com.google.devtools.ksp") version "2.2.0-2.0.2" apply false
    alias(libs.plugins.android.test) apply false
    alias(libs.plugins.baselineprofile) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
    alias(libs.plugins.android.library) apply false
}

buildscript {
    dependencies {
        classpath(libs.secrets.gradle.plugin)
        classpath(libs.protobuf.gradle.plugin)
    }
}