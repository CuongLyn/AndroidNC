plugins {
    alias(libs.plugins.android.application)

    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.mypets"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.mypets"
        minSdk = 24
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation(libs.androidx.work.runtime)
    implementation(libs.firebase.messaging)
    implementation(libs.mpandroidchart)
    implementation(libs.material.calendarview)
    implementation(libs.threetenabp)

    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)

    implementation(libs.okhttp)
    implementation(libs.json)
    implementation(libs.commons.io)
    implementation (libs.glide)
    annotationProcessor (libs.glide.compiler)

    implementation(platform("com.google.firebase:firebase-bom:33.12.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-database")
    implementation ("androidx.work:work-runtime:2.9.0")
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")


}