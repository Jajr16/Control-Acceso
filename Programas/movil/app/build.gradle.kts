plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("kotlin-kapt")
}


kapt {
    javacOptions {
        // These options are normally set automatically via the Hilt Gradle plugin, but we
        // set them manually to workaround a bug in the Kotlin 1.5.20
        option("-Adagger.fastInit=ENABLED")
        option("-Adagger.hilt.android.internal.disableAndroidSuperclassValidation=true")
    }
}

android {
    namespace = "com.example.prueba3"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.prueba3"
        minSdk = 25
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    packagingOptions {
        exclude("META-INF/gradle/incremental.annotation.processors")
        exclude("META-INF/AL2.0")
        exclude("META-INF/LGPL2.1")
    }
}

dependencies {

// PERMISOS
    implementation("io.coil-kt:coil-compose:2.5.0")
    implementation(libs.accompanist.permissions)
    implementation(libs.face.detection)

    //implementation("com.github.chrisbanes:photoview:2.3.0")
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)

    implementation(libs.androidx.compose.material3.material3.v120.x2) // Material 3
    implementation(libs.ui)
    //noinspection GradleDependency
    implementation(libs.androidx.material) // Material Design
    implementation (libs.androidx.navigation.compose.v273)

    implementation(libs.retrofit)

    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)
    implementation(libs.okhttp)
    //noinspection UseTomlInstead
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.benchmark.macro)
    implementation(libs.androidx.espresso.core)
    implementation(libs.vision.common)
    implementation(libs.play.services.mlkit.face.detection)
    implementation(libs.play.services.mlkit.barcode.scanning)
    implementation(libs.androidx.ui.test.android)
    implementation(libs.firebase.messaging.ktx)
    implementation(libs.androidx.room.ktx)
    implementation(libs.transport.api)
    implementation(libs.androidx.compose.material)
    implementation(libs.play.services.wearable)
    testImplementation(libs.junit)
    implementation(libs.material3)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)

    implementation(libs.face.detection.v1615) // O la última versión
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2.v102)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view.v100)

    implementation(libs.face.detection.v1604)
    implementation(kotlin("script-runtime"))

    implementation ("androidx.compose.material:material:1.5.4")
    implementation ("androidx.compose.material:material-icons-extended:1.5.4")

//    IMPORTAR FIREBASE
    kapt(libs.hilt.android.compiler)
    implementation(libs.kotlinStdlib)
    implementation(libs.hiltAndroid)
    kapt(libs.hiltCompiler)

    implementation(platform("com.google.firebase:firebase-bom:33.10.0"))
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.google.firebase.crashlytics.ktx)
    implementation(libs.google.firebase.messaging.ktx)
    implementation(libs.firebase.analytics)

    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation(libs.accompanist.insets)
    implementation(libs.hilt.android.compiler)
    kapt("com.google.dagger:hilt-android-compiler:2.44")

    implementation("androidx.multidex:multidex:2.0.1")
    implementation("androidx.localbroadcastmanager:localbroadcastmanager:1.1.0")

}