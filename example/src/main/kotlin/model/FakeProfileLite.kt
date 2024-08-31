package model

import dev.burnoo.ksoup.annotation.Selector
import kotlinx.serialization.Serializable

@Serializable
data class FakeProfileLite(
    @Selector("h1", regex = "(.*) - GitHub")
    val username: String,
    @Selector("img[id=avatar]", attr = "abs:src")
    val avatarUrl: String,
    @Selector("li")
    val interests : List<String>,
    @Selector(".pinned-repositories")
    val pinnedRepositories: List<GithubProfile.PinnedRepository> = emptyList(),
)
