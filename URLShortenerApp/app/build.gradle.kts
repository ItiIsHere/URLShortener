// Build.gradle
plugins {
    id("com.android.application") version "8.8.0"
    id("org.jetbrains.kotlin.android") version "1.9.0"
    id("org.jetbrains.kotlin.kapt") version "2.1.20"
    alias(libs.plugins.google.services)

}

android {
    namespace = "com.example.urlshortener"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.urlshortenerapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(libs.monitor)
    implementation(libs.ext.junit)
    androidTestImplementation(libs.junit.junit)
    // ROOM
    dependencies {
        // Firebase
        implementation(platform("com.google.firebase:firebase-bom:32.8.1")) // Usa la versión más reciente
        implementation("com.google.firebase:firebase-auth")
        implementation("com.google.android.gms:play-services-auth:21.1.1") // Para Google Sign-In

        // Room
        implementation("androidx.room:room-runtime:2.6.1")
        kapt("androidx.room:room-compiler:2.6.1")
        implementation("androidx.room:room-ktx:2.6.1") // Para corrutinas

        // Retrofit
        implementation("com.squareup.retrofit2:retrofit:2.9.0")
        implementation("com.squareup.retrofit2:converter-gson:2.9.0")

        // AndroidX
        implementation("androidx.appcompat:appcompat:1.6.1")
        implementation("androidx.constraintlayout:constraintlayout:2.1.4")
        implementation("androidx.activity:activity-ktx:1.8.0")
        implementation("com.google.android.material:material:1.11.0")
        implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
        implementation("androidx.recyclerview:recyclerview:1.3.2")

        // Kotlin
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

        implementation ("com.google.android.gms:play-services-auth-api-phone:18.0.1")
    }

}
