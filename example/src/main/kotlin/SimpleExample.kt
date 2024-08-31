import dev.burnoo.ksoup.Kspoon
import model.FakeProfileLite

private const val HTML_CONTENT = """
    <h1>burnoo - GitHub</h1>
    <img id="avatar" src="burnoo.png" />
    <ul class="interests">
        <li>KMP</li>
        <li>Android</li>
    </ul>
"""

fun simpleExample() {
    val ksoup = Kspoon {
        parse = { html -> parse(html, baseUri = "https://github.com/") }
        coerceInputValues = true
    }

    val profile = ksoup.parse<FakeProfileLite>(HTML_CONTENT)

    println("Display name: ${profile.username}")
    println("Avatar url: ${profile.avatarUrl}")
    println("Interests: ${profile.interests.joinToString()}")
    println("Pinned repositories:")
    profile.pinnedRepositories.forEach { println("${it.name} (${it.starNumbers} ⭐️)") }
}
