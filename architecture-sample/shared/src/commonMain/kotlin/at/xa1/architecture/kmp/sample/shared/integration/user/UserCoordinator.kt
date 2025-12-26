package at.xa1.architecture.kmp.sample.shared.integration.user

import at.xa1.architecture.kmp.navigation.Coordinator
import at.xa1.architecture.kmp.navigation.CoordinatorScope
import at.xa1.architecture.kmp.navigation.Navigator
import at.xa1.architecture.kmp.navigation.ScopeContext
import at.xa1.architecture.kmp.sample.shared.common.user.UserInfo
import at.xa1.architecture.kmp.sample.shared.common.user.UserService
import at.xa1.architecture.kmp.sample.shared.feature.login.Login
import at.xa1.architecture.kmp.sample.shared.feature.profile.UserProfile
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class UserCoordinator(
    private val navigator: Navigator,
    private val userService: UserService,
    private val scope: CoordinatorScope,
) : Coordinator {
    override fun start() {
        userService.currentUser
            .onEach { userInfo -> onUserChanged(userInfo) }
            .launchIn(scope)
    }

    private fun onUserChanged(userInfo: UserInfo?) {
        if (userInfo != null) {
            userLoggedIn(userInfo)
        } else {
            noUserLoggedIn()
        }
    }

    private fun userLoggedIn(userInfo: UserInfo) {
        navigator.move(
            UserProfile,
            metadata = ScopeContext.add(UserScopeContext(userInfo))
        )
    }

    private fun noUserLoggedIn() {
        navigator.move(Login)
    }
}
