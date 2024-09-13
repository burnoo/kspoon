import dev.burnoo.kspoon.Kspoon
import model.GithubProfile
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface GitHubService {
    @GET("{username}")
    suspend fun getProfile(
        @Path("username") username: String,
    ): GithubProfile
}

suspend fun retrofitExample() {
    val retrofit = Retrofit.Builder()
        .addConverterFactory(
            Kspoon.toFormat().asConverterFactory("text/html; charset=UTF8".toMediaType()),
        )
        .baseUrl("https://github.com/")
        .build()
    val service = retrofit.create(GitHubService::class.java)

    val profile = service.getProfile("burnoo")

    println("Display name: ${profile.displayName}")
    println("Avatar url: ${profile.avatarUrl}")
    println("Pinned repositories:")
    profile.pinnedRepositories.forEach { println("${it.name} (${it.starNumbers} ⭐️)") }
}
