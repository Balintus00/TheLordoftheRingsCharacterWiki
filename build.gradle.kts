import io.gitlab.arturbosch.detekt.Detekt

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.buildkonfig) apply false
    alias(libs.plugins.compose.multiplatform) apply false
    alias(libs.plugins.detekt)
    alias(libs.plugins.kotest.multiplatform) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kover) apply false
    alias(libs.plugins.sqldelight) apply false
}

// https://github.com/detekt/detekt/issues/3663#issuecomment-999866611
allprojects {
    tasks.register("detektAll") {
        dependsOn(tasks.withType<Detekt>())

        group = LifecycleBasePlugin.VERIFICATION_GROUP
    }

    tasks.configureEach {
        if (name == LifecycleBasePlugin.BUILD_TASK_NAME) {
            dependsOn(tasks.withType<Detekt>())
        }
    }

    tasks.withType<Detekt>().configureEach {
        exclude { it.file.path.contains("build") }
    }
}