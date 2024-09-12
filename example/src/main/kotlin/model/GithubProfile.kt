package model

import dev.burnoo.kspoon.annotation.Selector
import kotlinx.serialization.Serializable

@Serializable
data class GithubProfile(
    @Selector("title", regex = "(.*) · GitHub")
    val displayName: String,
    @Selector("meta[property=og:image]", attr = "content")
    val avatarUrl: String,
    @Selector("li.js-pinned-item-list-item")
    val pinnedRepositories: List<PinnedRepository>,
) {
    @Serializable
    data class PinnedRepository(
        @Selector("span.repo")
        val name: String,

        @Selector("a.pinned-item-meta", regex = "[0-9]+")
        val starNumbers: Int,
    )
}
