plugins {
    kotlin("jvm")
    alias(libs.plugins.kotlinx.serialization)
    application
}

dependencies {
    implementation(project(":kspoon"))
    implementation(libs.logback.classic)

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.contentNegotiation)
    implementation(libs.ktor.serialization.kotlinx)

    implementation(libs.retrofit.core)
    implementation(libs.retrofit.converter.kotlinxSerialization)
}

application {
    mainClass = "MainKt"
}
