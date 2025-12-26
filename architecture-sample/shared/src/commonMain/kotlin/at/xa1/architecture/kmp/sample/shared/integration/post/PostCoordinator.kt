package at.xa1.architecture.kmp.sample.shared.integration.post

import at.xa1.architecture.kmp.navigation.Coordinator
import at.xa1.architecture.kmp.navigation.Navigator
import at.xa1.architecture.kmp.navigation.stack
import at.xa1.architecture.kmp.sample.shared.common.user.UserService
import at.xa1.architecture.kmp.sample.shared.feature.postlist.PostList
import at.xa1.architecture.kmp.sample.shared.feature.newpost.NewPost

class PostCoordinator(
    private val navigator: Navigator,
    private val userService: UserService,
    private val args: PostStack,
) : Coordinator {
    override fun start() {
        navigator.move(
            PostList(
                onNewPost = ::newPost,
            ),
        )
    }

    private fun newPost() {
        if (userService.currentUser.value == null) {
            args.onLogin()
        } else {
            navigator.stack {
                NewPost(
                    onClose = ::close,
                    onPosted = ::close,
                )
            }
        }
    }
}
