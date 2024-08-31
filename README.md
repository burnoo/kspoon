# kspoon 🥄

Annotation based HTML to Kotlin parser + ktor converter, [jspoon](https://github.com/DroidsOnRoids/jspoon/]) successor.

Work in progress... 🚧

<details>

<summary>API usage peek</summary>

```kotlin
@Serializable
data class Page(
    @Selector("title", regex = "(.*) · GitHub")
    val displayName: String,
    @Selector("meta[property=og:image]", attr = "content")
    val avatarUrl: String,
)

val client = HttpClient(CIO) {
    install(ContentNegotiation) {
        register(ContentType.Text.Html, KotlinxSerializationConverter(Kspoon.toFormat()))
    }
}
val page = client.get("https://github.com/burnoo").body<Page>()
println(page)
// Page(displayName=burnoo (Bruno Wieczorek), avatarUrl=https://avatars.githubusercontent.com/u/17478192?v=4?s=400)
```

</details>
