import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.application)
    alias(libs.plugins.buildkonfig)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.sqldelight)
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = libs.versions.java.get()
            }
        }
    }

    jvm("desktop")

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "TheLordOfTheRingsCharacterWiki"
            isStatic = true
        }
    }


    /*    @OptIn(ExperimentalWasmDsl::class)
        wasmJs {
            moduleName = "theLordOfTheRingsCharacterWiki"
            browser {
                commonWebpackConfig {
                    outputFileName = "theLordOfTheRingsCharacterWiki.js"
                    devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                        static = (static ?: mutableListOf()).apply {
                            // Serve sources to debug inside browser
                            add(project.projectDir.path)
                        }
                    }
                }
            }
            binaries.executable()
        }*/

    applyDefaultHierarchyTemplate()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)
                implementation(libs.compose.placeholder.material3)
                implementation(libs.coroutines.core)
                implementation(libs.decompose)
                implementation(libs.decompose.compose.extension)
                implementation(libs.kermit)
                implementation(libs.kermit.koin)
                implementation(libs.koin.core)
                implementation(libs.ktor.client.contentNegotiation)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.logging)
                implementation(libs.ktor.client.serialization.kotlinxJson)
                implementation(libs.material3.windowSizeClass)
                implementation(libs.mvikotlin.core)
                implementation(libs.mvikotlin.coroutines)
                implementation(libs.mvikotlin.logging)
                implementation(libs.mvikotlin.main)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        val nonAndroidMain by creating {
            dependsOn(commonMain)

            dependencies {
                implementation(libs.sqldelight.coroutines)
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(libs.androidx.activity.compose)
                implementation(libs.androidx.lifecycle.compose)
                implementation(libs.androidx.splashScreen)
                implementation(libs.compose.jetpack.preview)
                implementation(libs.coroutines.android)
                implementation(libs.koin.android)
                implementation(libs.ktor.client.okhttp)
                implementation(libs.room.ktx)
                implementation(libs.room.runtime)
            }
        }

        val desktopMain by getting {
            dependsOn(nonAndroidMain)

            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(libs.ktor.client.okhttp)
                implementation(libs.coroutines.swing)
                implementation(libs.sqldelight.jvm.driver)
            }
        }

        val iosMain by getting {
            dependsOn(nonAndroidMain)

            dependencies {
                implementation(libs.ktor.client.darwin)
                implementation(libs.sqldelight.native.driver)
            }
        }
    }
}

dependencies {
    val androidKspConfiguration = "kspAndroid"

    add(androidKspConfiguration, libs.room.compiler)
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

    applicationVariants.all {
        val variantName = name
        sourceSets {
            getByName("main") {
                kotlin.srcDir(File("build/generated/ksp/$variantName/kotlin"))
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
    }
}

/*compose.experimental {
    web.application {}
}*/

buildkonfig {
    packageName = "hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki"

    defaultConfigs {
        val apiKey: String = gradleLocalProperties(rootDir).getProperty("THE_ONE_API_KEY")

        require(apiKey.isNotEmpty()) {
            "Register on https://the-one-api.dev/ to obtain an API key, and place it in local.properties with the following key: THE_ONE_API_KEY"
        }

        buildConfigField(STRING, "THE_ONE_API_KEY", apiKey)
    }
}

ksp {
    arg("KOIN_CONFIG_CHECK","true")
}

sqldelight {
    databases {
        create("SqlDelightDatabase") {
            packageName.set("hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki")
        }
    }
}
