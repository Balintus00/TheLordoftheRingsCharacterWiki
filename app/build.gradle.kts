plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = libs.versions.java.get()
            }
        }
    }

    sourceSets {
        val androidMain by getting {
            dependencies {
                implementation(libs.compose.jetpack.preview)
                implementation(libs.androidx.activity.compose)
            }
        }
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)
                implementation(libs.decompose)
                implementation(libs.decompose.compose.extension)
                implementation(libs.material3.windowSizeClass)
            }
        }
    }
}

android {
    namespace = "hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki"
    compileSdk = libs.versions.android.sdk.compile.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki"
        minSdk = libs.versions.android.sdk.min.get().toInt()
        targetSdk = libs.versions.android.sdk.compile.get().toInt()
        versionCode = 1
        versionName = libs.versions.theLordOfTheRingsCharacterWiki.get()

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
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
        val javaVersionEnumName = "VERSION_${libs.versions.java.get()}"

        sourceCompatibility = JavaVersion.valueOf(javaVersionEnumName)
        targetCompatibility = JavaVersion.valueOf(javaVersionEnumName)
    }
    dependencies {
        debugImplementation(libs.compose.jetpack.tooling)
    }
}
