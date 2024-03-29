import java.io.FileInputStream
import java.util.Properties

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.mapsSecrets)
    id("com.google.gms.google-services")
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
    compileSdk = 34

    defaultConfig {
        applicationId = "com.kastik.locationspoofer"
        minSdk = 24
        targetSdk = 34
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
        kotlinCompilerExtensionVersion = "1.5.3"
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
    implementation(libs.androidx.datastore.preferences)

    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)
    implementation(platform("com.google.firebase:firebase-bom:32.4.0"))
    implementation("com.google.firebase:firebase-analytics-ktx")
}