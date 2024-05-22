plugins {
    id("com.android.application")
    id("io.objectbox")
    id("realm-android")
}

android {
    namespace = "io.objectbox.performanceapp"
    compileSdk = 34 // Android 14

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    defaultConfig {
        applicationId = "io.objectbox.performanceapp"
        minSdk = 19 // Android 4.4
        targetSdk = 30 // Android 11
        versionCode = 1
        versionName = "1.0"

        javaCompileOptions {
            annotationProcessorOptions {
                arguments(mapOf("room.schemaLocation" to "$projectDir/schemas"))
            }
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("debug")
        }
        create("releaseDebugCert") {
            initWith(getByName("release"))
            // Just to use without checkjni
            signingConfig = signingConfigs.getByName("debug")
        }
        create("debugJniNoDebug") {
            initWith(getByName("debug"))
            // Just to use without checkjni
            isJniDebuggable = false
        }
    }
}

// Print deprecation warnings like Kotlin
tasks.withType(JavaCompile::class).configureEach {
    options.isDeprecation = true
}

dependencies {
    implementation("androidx.room:room-runtime:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")
    implementation("org.greenrobot:greendao:3.3.0")
    implementation("org.greenrobot:essentials:3.1.0")
}
