import com.fleeksoft.ksoup.Ksoup
import dev.burnoo.kspoon.Kspoon
import dev.burnoo.kspoon.annotation.Selector
import kotlinx.serialization.Serializable
import model.GithubProfile

private const val HTML_CONTENT = """
    <h1>burnoo - GitHub</h1>
    <img id="avatar" src="burnoo.png" />
    <ul class="interests">
        <li>KMP</li>
        <li>Android</li>
    </ul>
"""

@Serializable
data class FakeGitHubProfile(
    @Selector("h1", regex = "(.*) - GitHub")
    val username: String,
    @Selector("img[id=avatar]", attr = "abs:src")
    val avatarUrl: String,
    @Selector("li")
    val interests: List<String>,
    @Selector(".pinned-repositories")
    val pinnedRepositories: List<GithubProfile.PinnedRepository> = emptyList(),
)

fun simpleExample() {
    val kspoon = Kspoon {
        parse = { html -> Ksoup.parse(html, baseUri = "https://github.com/") }
        coerceInputValues = true
    }

    val profile = kspoon.parse<FakeGitHubProfile>(HTML_CONTENT)

    println("Display name: ${profile.username}")
    println("Avatar url: ${profile.avatarUrl}")
    println("Interests: ${profile.interests.joinToString()}")
    println("Pinned repositories:")
    profile.pinnedRepositories.forEach { println("${it.name} (${it.starNumbers} ⭐️)") }
}
