plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.shoppi"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.shoppi"
        minSdk = 21
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

    buildFeatures{
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.gms:play-services-cast-framework:21.5.0")
    dependencies {
        implementation ("org.slf4j:slf4j-simple:1.7.30")
        implementation ("org.slf4j:slf4j-api:1.7.32")
        implementation ("com.github.bumptech.glide:glide:4.12.0")
        annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")
        implementation ("com.google.android.material:material:1.12.0")
        implementation ("androidx.appcompat:appcompat:1.6.1")
        implementation ("androidx.constraintlayout:constraintlayout:2.1.4")
        implementation ("androidx.legacy:legacy-support-v4:1.0.0")
        testImplementation ("junit:junit:4.13.2")
        androidTestImplementation ("androidx.test.ext:junit:1.1.5")
        androidTestImplementation ("androidx.test.espresso:espresso-core:3.5.1")

        implementation ("com.itextpdf:itext7-core:7.1.15")

    }
}