package at.xa1.architecture.kmp.sample.shared.common.post

import at.xa1.architecture.kmp.sample.shared.common.user.NoUserLoggedInException
import at.xa1.architecture.kmp.sample.shared.common.user.UserService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

interface PostService {
    val latestPosts: Flow<List<Post>>
    suspend fun post(message: String): Result<Unit>
}

class RandomFakePostService(
    private val randomPostScope: CoroutineScope,
    private val userService: UserService,
    private val random: Random = Random,
    private val randomPostGenerator: RandomPostGenerator = RandomPostGenerator(random),
) : PostService {
    private val _latestPosts = MutableStateFlow(randomPostGenerator.createList(MAX_LATEST_POSTS))

    override val latestPosts: Flow<List<Post>> = flow {
        simulateLoading()
        emitAll(_latestPosts)
    }

    override suspend fun post(message: String): Result<Unit> {
        val user = userService.currentUser.value ?: return Result.failure(
            NoUserLoggedInException(),
        )

        simulateLoading()

        addPost(
            Post(
                author = user.username,
                message = message,
            ),
        )

        return Result.success(Unit)
    }

    private fun addPost(
        post: Post,
    ) {
        _latestPosts.update { previousPosts ->
            (previousPosts + post).take(MAX_LATEST_POSTS)
        }
    }

    private suspend fun simulateLoading() {
        delay(random.nextLong(100, 1000))
    }

    private suspend fun addRandomPosts() {
        while (true) {
            delay(random.nextLong(1_000, 10_000))
            addPost(randomPostGenerator.create())
        }
    }

    init {
        randomPostScope.launch { addRandomPosts() }
    }


    companion object {
        private const val MAX_LATEST_POSTS = 20
    }
}

class RandomPostGenerator(private val random: Random) {
    fun create(): Post {
        return Post(
            author = authors.random(random),
            message = messages.random(random),
        )
    }

    fun createList(count: Int): List<Post> = buildList {
        repeat(count) {
            add(create())
        }
    }

    private val authors = listOf(
        "Alice",
        "Bob",
        "Charlie",
        "Dave",
    )

    private val messages = listOf(
        "Hi there!",
        "Hello, world!",
        "This is awesome!",
        "What's up?",
    )
}
