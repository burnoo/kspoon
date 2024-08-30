plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlinx.serialization)
}

kotlin.sourceSets.commonMain {
    dependencies {
        implementation(libs.kotlinx.serialization.core)
        implementation(libs.ksoup)
    }
}
