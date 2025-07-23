plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.smartaquarium"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.smartaquarium"
        minSdk = 31
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:latest.release"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth-ktx") // Firebase Authentication
    implementation("com.google.android.gms:play-services-auth:21.3.0") // Google Sign-In
    implementation("io.coil-kt:coil-compose:2.4.0")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    // UI dan Compose
    implementation("androidx.compose.material3:material3:1.3.1")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.compose.material)
    implementation("com.google.accompanist:accompanist-swiperefresh:0.30.1")
    implementation("com.github.CanHub:Android-Image-Cropper:4.4.0")




    // Grafik dan Chart
    implementation("com.github.PhilJay:MPAndroidChart:3.1.0")
    implementation("com.github.codersalman:Gauge-Library-Android:0.0.1")
    implementation ("com.patrykandpatrick.vico:compose:2.1.3")
    implementation("com.patrykandpatrick.vico:core:2.1.3")
    implementation("com.patrykandpatrick.vico:compose-m3:2.1.3")



    // Google Maps
    implementation(libs.play.services.maps)


    // Coroutines (versi terbaru)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Number Picker
    implementation(libs.numberpicker)
    implementation(libs.firebase.database.ktx)
    implementation(libs.androidx.room.runtime.android)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.protolite.well.known.types)


    implementation ("androidx.camera:camera-camera2:1.3.1")
    implementation ("androidx.camera:camera-lifecycle:1.3.1")
    implementation ("androidx.camera:camera-view:1.3.1")
    implementation ("androidx.camera:camera-extensions:1.3.1")
    implementation(libs.androidx.appcompat)
    implementation("androidx.compose.foundation:foundation:1.8.3") // Atau versi Compose yang sesuai


    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
