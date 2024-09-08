import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import java.util.Properties

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.dokka)
    `maven-publish`
    signing
}

group = "dev.burnoo.kspoon"
version = "0.0.1-SNAPSHOT"

data class Variant(
    val type: Type,
    val ksoupDependency: Provider<MinimalExternalModuleDependency>,
    val suffix: String,
) {
    enum class Type { Default, Korlibs, Ktor2, Okio }
}

val currentVariant = when (properties["variant"]?.toString()) {
    "korlibs" -> Variant(Variant.Type.Korlibs, libs.ksoup.korlibs, "-korlibs")
    "ktor2" -> Variant(Variant.Type.Ktor2, libs.ksoup.ktor2, "-ktor2")
    "okio" -> Variant(Variant.Type.Okio, libs.ksoup.okio, "-okio")
    else -> Variant(Variant.Type.Default, libs.ksoup.default, "")
}

kotlin {
    explicitApi()

    jvm()

    js {
        browser()
        nodejs()
    }
    if (currentVariant.type !in setOf(Variant.Type.Ktor2, Variant.Type.Okio)) {
        @OptIn(ExperimentalWasmDsl::class)
        wasmJs {
            browser()
            nodejs()
            d8()
        }
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
    commonMainApi(currentVariant.ksoupDependency)
    commonMainImplementation(libs.kotlinx.serialization.core)

    commonTestImplementation(libs.kotlin.test)
    commonTestImplementation(libs.kotlinx.datetime)
    commonTestImplementation(libs.kotest.assertions)
}

// Publishing configuration below
val currentProperties = rootProject.file("local.properties")
    .run { if (exists()) Properties().apply { load(reader()) } else properties }

val isRelease: Boolean
    get() = currentProperties["isRelease"]?.toString()?.toBoolean() == true

tasks.withType<DokkaTask>().configureEach {
    if (isRelease) {
        moduleName = moduleName.get() + currentVariant.suffix
        moduleVersion = moduleVersion.get().replace("-SNAPSHOT", "")
    }
}

extensions.findByType<PublishingExtension>()?.apply {
    repositories {
        maven {
            name = "sonatype"
            val repositoryId = currentProperties["sonatypeStagingRepositoryId"]?.toString()
            url = uri(
                if (repositoryId != null) {
                    "https://s01.oss.sonatype.org/service/local/staging/deployByRepositoryId/$repositoryId/"
                } else {
                    "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
                },
            )
            credentials {
                username = currentProperties["sonatypeUsername"]?.toString()
                password = currentProperties["sonatypePassword"]?.toString()
            }
        }
        maven {
            name = "sonatypeSnapshot"
            url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
            credentials {
                username = currentProperties["sonatypeUsername"]?.toString()
                password = currentProperties["sonatypePassword"]?.toString()
            }
        }
    }
    publications.withType<MavenPublication>().configureEach {
        val publication = this
        val dokkaJar = project.tasks.register("${publication.name}DokkaJar", Jar::class) {
            archiveClassifier.set("javadoc")
            from(tasks.named("dokkaHtml"))
            archiveBaseName.set("${archiveBaseName.get()}-${publication.name}")
        }
        artifact(dokkaJar)
        val libraryName = artifactId.replace(project.name, project.name + currentVariant.suffix)
        artifactId = libraryName
        pom {
            name = libraryName
            description = "Annotation based HTML to Kotlin parser, jspoon successor"
            url = "https://github.com/burnoo/ksoup"
            if (isRelease) {
                version = version.replace("-SNAPSHOT", "")
            }

            licenses {
                license {
                    name = "The Apache License, Version 2.0"
                    url = "https://www.apache.org/licenses/LICENSE-2.0"
                }
            }

            developers {
                developer {
                    id = "burnoo"
                    name = "Bruno Wieczorek"
                    url = "https://burnoo.dev"
                    email = "bruno.wieczorek@gmail.com"
                }
            }

            scm {
                connection = "scm:git:git@github.com:burnoo/ksoup.git"
                url = "https://github.com/burnoo/ksoup"
                tag = "HEAD"
            }
        }
    }
}

signing {
    val key = currentProperties["signingKey"]?.toString()?.replace("\\n", "\n")
    val password = currentProperties["signingPassword"]?.toString()
    useInMemoryPgpKeys(key, password)
    sign(publishing.publications)
}
