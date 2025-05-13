import java.io.FileInputStream
import java.util.Properties
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.mapsSecrets)
    alias(libs.plugins.compose.compiler)
    id("com.google.gms.google-services")
    id("com.google.protobuf").version("0.9.5")
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
        applicationId = "com.kastik.locationspoofer"
        minSdk = 25
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
            isMinifyEnabled = false
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
}

dependencies {
    implementation(libs.maps.compose.widgets)

    implementation(libs.androidx.navigation.compose)


    implementation(libs.maps.compose)
    implementation(libs.play.services.maps)
    implementation(libs.androidx.datastore.preferences)

    implementation(libs.accompanist.permissions)
    implementation(libs.places)

    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.datastore)
    implementation(libs.protobuf.javalite)
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.8.0"
    }
    generateProtoTasks {
        all().configureEach {
            builtins {
                maybeCreate("java").apply {
                    option("lite")
                }
            }
        }
    }
}