import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.proto
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.mapsSecrets)
    alias(libs.plugins.compose.compiler)
    id("com.google.firebase.crashlytics")
    id("com.google.firebase.firebase-perf")
    id("com.google.gms.google-services")
    id("com.google.protobuf")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

android {
    val prop = Properties()
    prop.load(FileInputStream(file("keystore.config")))
    signingConfigs {
        getByName("debug") {
            storeFile = file(prop.getProperty("debugKeystoreFile"))
            storePassword = prop.getProperty("debugStorePassword")
            keyAlias = prop.getProperty("debugKeyAlias")
            keyPassword = prop.getProperty("debugKeyPassword")
        }
        create("release") {
            storeFile = file(prop.getProperty("releaseKeystoreFile"))
            storePassword = prop.getProperty("releaseStorePassword")
            keyAlias = prop.getProperty("releaseKeyAlias")
            keyPassword = prop.getProperty("releaseKeyPassword")
        }
    }
    namespace = "com.kastik.locationspoofer"
    compileSdk = 36

    defaultConfig {
        buildFeatures {
            buildConfig = true
        }
        applicationId = "com.kastik.locationspoofer"
        minSdk = 26
        targetSdk = 36
        versionCode = 2
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        multiDexEnabled = false
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            applicationIdSuffix = ".debug"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        compileOptions {
            jvmTarget
        }
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        //kotlinCompilerExtensionVersion = "1.5.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    sourceSets["main"].proto {
        srcDirs("src/main/proto","src/main/proto/google")
    }
}

dependencies {
    implementation("javax.annotation:javax.annotation-api:1.3.2")

    //Generic
    implementation(libs.core.ktx)
    implementation(libs.activity.compose)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.androidx.material.icons.extended)

    //Generic Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)
    implementation(libs.material)
    implementation(libs.maps.compose.widgets)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.accompanist.permissions)

    //Storage
    implementation(libs.androidx.datastore)
    implementation(libs.androidx.datastore.preferences)

    //Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics.ktx)

    //Google Maps
    implementation(libs.maps.compose)
    implementation(libs.play.services.maps)
    implementation(libs.places)

    //DI
    ksp(libs.hilt.android.compiler)
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)

    //Coroutines
    implementation(libs.kotlinx.coroutines.android)

    //GRPC
    implementation(libs.grpc.core)
    implementation(libs.grpc.context)
    implementation(libs.grpc.stub)
    implementation(libs.grpc.okhttp)
    implementation(libs.grpc.protobuf)

    //Protobuf models
    implementation(libs.proto.google.common.protos)

    //Network
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.squareup.moshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.logging.interceptor)


    //Testing
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    testImplementation(libs.junit)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)

}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.24.0"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.61.0"
        }
        id("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:1.4.1:jdk8@jar"
        }
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                // Add this to generate Java code
                //maybeCreate("kotlin")
                maybeCreate("java")
            }
            task.plugins {
                id("grpc")
                //id("grpckt")
            }
        }
    }
}

secrets {
    // To add your Maps API key to this project:
    // 1. If the secrets.properties file does not exist, create it in the same folder as the local.properties file.
    // 2. Add this line, where YOUR_API_KEY is your API key:
    //        MAPS_API_KEY=YOUR_API_KEY
    propertiesFileName = "secrets.properties"

    // A properties file containing default secret values. This file can be
    // checked in version control.
    //defaultPropertiesFileName = "local.defaults.properties" TODO
}
