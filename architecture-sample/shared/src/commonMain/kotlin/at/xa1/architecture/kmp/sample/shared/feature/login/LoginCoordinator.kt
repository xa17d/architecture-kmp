package at.xa1.architecture.kmp.sample.shared.feature.login

import at.xa1.architecture.kmp.navigation.Coordinator
import at.xa1.architecture.kmp.navigation.Navigator
import at.xa1.architecture.kmp.navigation.stack

class LoginCoordinator(
    private val navigator: Navigator,
) : Coordinator {

    override fun start() {
        navigator.move(
            LoginForm(
                onLoginFailed = ::onLoginFailed,
                onLoginSucceeded = {
                    /* no need to handle explicitly */
                },
            ),
        )
    }

    private fun onLoginFailed() {
        navigator.stack {
            LoginError(
                onConfirm = ::close,
            )
        }
    }
}
