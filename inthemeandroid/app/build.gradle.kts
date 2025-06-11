plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.googleServices)
}

android {
    namespace = "com.intheme.dev"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.intheme.dev"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    val devApiBaseURL: String by project
    val prodApiBaseURL: String by project

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("debug")
            isDebuggable = true
            isMinifyEnabled = false
            buildConfigField("String", "api_base_url", devApiBaseURL)
        }
        release {
            isMinifyEnabled = true
            isDebuggable = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"),"proguard-rules.pro")
            buildConfigField("String", "api_base_url", prodApiBaseURL)
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    // Add KSP settings here
    ksp {
        arg("debuggable", "true") // Enable KSP debugging
    }

}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.process)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)

    implementation(libs.fragment.ktx)
    implementation(libs.activity.ktx)

    //Api calling lib
    implementation(libs.retrofit)
    implementation(libs.convertor.gson)

    implementation(platform(libs.okhttp.bom))
    implementation(libs.okhttp.module)
    implementation(libs.okhttp.logging.interceptor)

    implementation(libs.gson)

    //Glide For Image
    implementation(libs.glide.main)
    ksp(libs.glide.compiler)

    //For Size
    implementation(libs.sdp)
    implementation(libs.ssp)

    implementation(libs.roundedimageview)

    implementation(libs.animation)
    implementation(libs.playServicesBase)

    implementation(libs.swiperefreshlayout)
    implementation(libs.shimmerView)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.fcm)

    implementation(libs.firebase.directboot)
}