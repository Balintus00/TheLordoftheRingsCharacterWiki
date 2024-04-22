@file:Suppress("UnstableApiUsage")


pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/kotlin/p/wasm/experimental")
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/kotlin/p/wasm/experimental")
    }
}

// TODO: After Jellyfish is released, it can be renamed to TheLordOfTheRingsCharacterWiki
//  https://youtrack.jetbrains.com/issue/IDEA-317606/Changing-only-the-case-of-the-Gradle-root-project-name-causes-exception-while-importing-project-java.lang.IllegalStateException
rootProject.name = "TheLordoftheRingsCharacterWiki"
include(":app")
 