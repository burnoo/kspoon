@file:Suppress("UnstableApiUsage")

rootProject.name = "kspoon"

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

include(":kspoon")
