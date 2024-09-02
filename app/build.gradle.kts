import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.application)
    alias(libs.plugins.buildkonfig)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.detekt)
    alias(libs.plugins.kotest.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kover)
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

    jvm("desktop") {
        compilations {
            val main by getting

            val integrationTest by compilations.creating {
                defaultSourceSet {
                    dependencies {
                        implementation(
                            main.compileDependencyFiles + main.output.classesDirs
                        )
                        implementation(libs.kotest.runner.junit5)
                    }
                }

                // Source: https://youtrack.jetbrains.com/issue/KTIJ-23114#focus=Comments-27-8518506.0-0
                associateWith(main)

                tasks.register<Test>("desktopIntegrationTest") {
                    group = LifecycleBasePlugin.VERIFICATION_GROUP

                    classpath = compileDependencyFiles + runtimeDependencyFiles + output.allOutputs
                    testClassesDirs = output.classesDirs

                    useJUnitPlatform()
                }
            }
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "App"
            isStatic = true

            export(libs.decompose)
            export(libs.essenty.lifecycle)
            export(libs.mvikotlin.logging)
            export(libs.mvikotlin.main)
        }
    }


    /*    TODO uncomment when web platform will be supported
        @OptIn(ExperimentalWasmDsl::class)
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
                api(libs.decompose)
                api(libs.essenty.lifecycle)
                api(libs.mvikotlin.logging)
                api(libs.mvikotlin.main)

                implementation(compose.runtime)
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)
                implementation(libs.compose.placeholder.material3)
                implementation(libs.coroutines.core)
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
                implementation(libs.sqldelight.driver.android)
            }
        }

        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(libs.ktor.client.okhttp)
                implementation(libs.coroutines.swing)
                implementation(libs.sqldelight.driver.jvm)
            }
        }

        val iosMain by getting {
            dependencies {
                implementation(libs.ktor.client.darwin)
                implementation(libs.sqldelight.driver.native)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(libs.kotest.assertions.core)
                implementation(libs.kotest.framework.engine)
                implementation(libs.turbine)
            }
        }

        val desktopTest by getting {
            dependencies {
                implementation(libs.kotest.runner.junit5)
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
        testImplementation(libs.kotest.runner.junit5)

        debugImplementation(libs.compose.jetpack.tooling)
    }

    @Suppress("UnstableApiUsage")
    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
    }
}

/* TODO uncomment when web platform will be supported
compose.experimental {
    web.application {}
}*/

buildkonfig {
    packageName = "hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki"

    defaultConfigs {
        val apiKey: String = gradleLocalProperties(rootDir).getProperty("THE_ONE_API_KEY")

        require(apiKey.isNotEmpty()) {
            "Register on https://the-one-api.dev/ to obtain an API key, and place it in " +
                    "local.properties with the following key: THE_ONE_API_KEY.\n" +
                    "So in your local.properties file you should have a line like this:" +
                    "THE_ONE_API_KEY=<your_api_key>"
        }

        buildConfigField(STRING, "THE_ONE_API_KEY", apiKey)
    }
}

sqldelight {
    databases {
        create("SqlDelightDatabase") {
            packageName.set("hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.sqldelight")
        }
    }
}

detekt {
    buildUponDefaultConfig = true
    config.setFrom("$rootDir/detekt.yml")
    parallel = true
}

dependencies {
    detektPlugins(libs.detekt.compose)
}

tasks.named<Test>("desktopTest") {
    useJUnitPlatform()
}