# Retrofit integration

## Installation
Add following dependencies to your `build.gradle.kts`/`build.gradle` file:

```kotlin
dependencies {
    implementation("com.squareup.retrofit2:retrofit:$retrofit_version")
    implementation("com.squareup.retrofit2:converter-kotlinx-serialization:$retrofit_version")

    implementation("dev.burnoo.kspoon:kspoon:$kspoon_version")
    // or any other variant
}
```

## Usage
Add `ConverterFactory` to your `Retrofit` instance. Use `asConverterFactory()` extension function on `Ksoup.toFormat()`:

```kotlin
val baseUrl = "https://github.com/"
val kspoon = Kspoon {
    parse = { html -> Ksooup.parse(html, baseUri = baseUrl) }
}

val retrofit = Retrofit.Builder()
    .addConverterFactory(
        kspoon.toFormat().asConverterFactory("text/html; charset=UTF8".toMediaType())
    )
    .baseUrl(baseUrl)
    .build()
```

Now for "text/html" content type, parsing will be done by kspoon for Retrofit services:

```kotlin
@Serializable
data class GithubProfile(
    @Selector("title", regex = "(.*) · GitHub")
    val displayName: String,
    @Selector("meta[property=og:image]", attr = "content")
    val avatarUrl: String,
)

interface GitHubService {
    @GET("{username}")
    suspend fun getProfile(@Path("username") username: String): GithubProfile
}

val service = retrofit.create(GitHubService::class.java)
val profile = service.getProfile("burnoo")
println(profile) // GithubProfile(displayName=burnoo (Bruno Wieczorek), avatarUrl=https://avatars.githubusercontent.com/u/17478192?v=4)
```

