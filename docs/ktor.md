# Ktor integration

## Installation
Add following dependencies to your `build.gradle.kts`/`build.gradle` file:

```kotlin
dependencies {
    
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    implementation("io.ktor:kotlinx-serialization-core:$ktor_version")
    
    implementation("dev.burnoo.kspoon:kspoon:$kspoon_version")
}
```

## Usage
Install Ktor content negotiation plugin on `HttpClient` and register `KotlinxSerializationConverter` with `Kspoon.toFormat()` for HTML content type:

```kotlin
val kspoon = Kspoon {
    // Configure Kspoon
}
val client = HttpClient {
    install(ContentNegotiation) {
        register(ContentType.Text.Html, KotlinxSerializationConverter(kspoon.toFormat()))
    }
}
```

Now for "text/html" content type, when `.body()` method is called on the response, kspoon will parse the HTML content:

```kotlin
@Serializable
data class GithubProfile(
    @Selector("title", regex = "(.*) · GitHub")
    val displayName: String,
    @Selector("meta[property=og:image]", attr = "content")
    val avatarUrl: String,
)

val profile = client.get<Page>("https://github.com/burnoo").body()
println(profile) // GithubProfile(displayName=burnoo (Bruno Wieczorek), avatarUrl=https://avatars.githubusercontent.com/u/17478192?v=4)
```
