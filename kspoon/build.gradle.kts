import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlinx.serialization)
}

group = "dev.burnoo.kspoon"
version = "0.0.1-SNAPSHOT"

kotlin {
    jvm()

    js {
        browser()
        nodejs()
    }
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        nodejs()
        d8()
    }

    linuxX64()
    linuxArm64()
    tvosArm64()
    tvosX64()
    tvosSimulatorArm64()
    macosX64()
    macosArm64()
    iosArm64()
    iosSimulatorArm64()
    iosX64()
    mingwX64()
}

tasks.withType<Test> { useJUnitPlatform() }

dependencies {
    commonMainImplementation(libs.kotlinx.serialization.core)
    commonMainImplementation(libs.ksoup)

    commonTestImplementation(libs.kotlin.test)
    commonTestImplementation(libs.kotlinx.datetime)
    commonTestImplementation(libs.kotest.assertions)
}
