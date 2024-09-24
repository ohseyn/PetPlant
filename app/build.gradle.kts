plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.petplant"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.petplant"
        minSdk = 23
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation ("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation ("androidx.recyclerview:recyclerview:1.2.1")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation (platform("com.google.firebase:firebase-bom:33.1.2"))
    implementation ("com.google.firebase:firebase-analytics")
    implementation ("com.google.firebase:firebase-auth:21.1.0")
    implementation ("com.google.firebase:firebase-firestore")
    implementation ("com.google.firebase:firebase-storage")
    implementation ("com.google.firebase:firebase-messaging:23.0.0")
}

apply {
    plugin("com.google.gms.google-services")
}