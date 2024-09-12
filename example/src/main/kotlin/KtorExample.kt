import dev.burnoo.kspoon.Kspoon
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.KotlinxSerializationConverter
import model.GithubProfile

suspend fun ktorExample() {
    val client = HttpClient {
        install(ContentNegotiation) {
            register(ContentType.Text.Html, KotlinxSerializationConverter(Kspoon.toFormat()))
        }
    }

    val profile = client.get("https://github.com/burnoo").body<GithubProfile>()

    println("Display name: ${profile.displayName}")
    println("Avatar url: ${profile.avatarUrl}")
    println("Pinned repositories:")
    profile.pinnedRepositories.forEach { println("${it.name} (${it.starNumbers} ⭐️)") }
}
