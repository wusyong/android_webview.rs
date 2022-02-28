plugins {
    id("com.android.application")
    id("rustPlugin")
    id("org.jetbrains.kotlin.android")
}

android {
    compileSdk = 31
    defaultConfig {
        applicationId = "com.example.w"
        minSdk = 24
        targetSdk = 31
        versionCode = 1
        versionName = "1.0"
    }
    sourceSets.getByName("main") {
        // Vulkan validation layers
        val ndkHome = System.getenv("NDK_HOME")
        jniLibs.srcDir("${ndkHome}/sources/third_party/vulkan/src/build-android/jniLibs")
    }
    buildTypes {
        getByName("debug") {
            isDebuggable = true
            isJniDebuggable = true
            isMinifyEnabled = false
            packagingOptions {
                jniLibs.keepDebugSymbols.add("*/arm64-v8a/*.so")
                jniLibs.keepDebugSymbols.add("*/armeabi-v7a/*.so")
                jniLibs.keepDebugSymbols.add("*/x86/*.so")
                jniLibs.keepDebugSymbols.add("*/x86_64/*.so")
            }
        }
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
    flavorDimensions.add("abi")
    productFlavors {
        create("arm64") {
            dimension = "abi"
            ndk {
                abiFilters += listOf("arm64-v8a")
            }
        }
        create("arm") {
            dimension = "abi"
            ndk {
                abiFilters += listOf("arm64-v8a")
                abiFilters += listOf("armeabi-v7a")
            }
        }
        create("x86") {
            dimension = "abi"
            ndk {
                abiFilters += listOf("x86")
            }
        }
        create("x86_64") {
            dimension = "abi"
            ndk {
                abiFilters += listOf("x86_64")
            }
        }
    }

    assetPacks += mutableSetOf()
    buildFeatures {
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

rust {
    rootDirRel = "../../../"
    targets = listOf("aarch64", "armv7", "i686", "x86_64")
    arches = listOf("arm64", "arm", "x86", "x86_64")
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("androidx.startup:startup-runtime:1.0.0")
    implementation("com.google.android.material:material:1.5.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.3")
    implementation("androidx.webkit:webkit:1.4.0")
}

afterEvaluate {
    android.applicationVariants.all {
        val buildType = "${buildType.name.capitalize()}"
        productFlavors.forEach {
            val archAndBuildType = name.capitalize()
            tasks["merge${archAndBuildType}JniLibFolders"].dependsOn(tasks["rustBuild${archAndBuildType}"])
        }
    }
}
