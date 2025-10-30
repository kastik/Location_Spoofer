import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.proto
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.mapsSecrets)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.firebaseCrashlytics)
    // alias(libs.plugins.firebasePerf)
    alias(libs.plugins.googleServices)
    alias(libs.plugins.protobuf)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hiltAndroid)
    alias(libs.plugins.baselineprofile)
}

android {
    val keystoreFile = layout.projectDirectory.file("keystore.config")

    signingConfigs {
        create("release") {
            val propsProvider = providers.fileContents(keystoreFile)
                .asText
                .map { text ->
                    Properties().apply { load(text.reader()) }
                }

            val releaseStoreFileProvider = propsProvider.map { props ->
                file(props.getProperty("releaseKeystoreFile"))
            }

            storeFile = releaseStoreFileProvider.get()
            storePassword = propsProvider.map {
                it.getProperty("releaseStorePassword")
            }.get()
            keyAlias = propsProvider.map {
                it.getProperty("releaseKeyAlias")
            }.get()
            keyPassword = propsProvider.map {
                it.getProperty("releaseKeyPassword")
            }.get()

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
        proguardFiles(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro"
        )
    }

    buildTypes {
        release {
            isMinifyEnabled = false
             signingConfig = signingConfigs.getByName("release")

        }
        debug {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("debug")
            applicationIdSuffix = ".debug"
            firebaseCrashlytics {
                mappingFileUploadEnabled = false
            }
            googleServices {
                disableVersionCheck = true
            }
        }
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

    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildToolsVersion = "36.0.0"
    sourceSets["main"].proto {
        srcDirs("src/main/proto", "src/main/proto/google")
    }
}

dependencies {
    implementation("javax.annotation:javax.annotation-api:1.3.2")

    //Generic
    implementation(libs.core.ktx)
    implementation(libs.activity.compose)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.serialization.protobuf)


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
    //implementation(libs.google.firebase.perf)

    //Google Maps
    implementation(libs.maps.compose)
    implementation(libs.play.services.maps)
    implementation(libs.androidx.lifecycle.service)
    implementation(libs.play.services.location)
    //implementation(libs.androidx.profileinstaller)
    //"baselineProfile"(project(":baselineprofile"))

    //DI
    ksp(libs.hilt.android.compiler)
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)

    //Coroutines
    implementation(libs.kotlinx.coroutines.android)

    //GRPC
    implementation(libs.grpc.core)
    implementation(libs.grpc.context)
    implementation(libs.grpc.okhttp)


    //Network
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.squareup.moshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.logging.interceptor)

    implementation(project(":proto"))

}



secrets {
    propertiesFileName = "secrets.properties"
    defaultPropertiesFileName = "defaults.properties"
}
