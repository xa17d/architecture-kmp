package at.xa1.architecture.kmp.sample.shared.integration.app

import at.xa1.architecture.kmp.navigation.Coordinator
import at.xa1.architecture.kmp.navigation.Navigator
import at.xa1.architecture.kmp.sample.shared.feature.login.LoginForm

class AppCoordinator(
    private val navigator: Navigator,
) : Coordinator {
    init { // TODO call onStart callbacks.
        navigator.move(LoginForm(onLoginFailed = {}, onLoginSucceeded = {}))
    }
}
