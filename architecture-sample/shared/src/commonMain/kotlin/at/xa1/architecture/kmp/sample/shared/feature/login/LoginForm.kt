package at.xa1.architecture.kmp.sample.shared.feature.login

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import at.xa1.architecture.kmp.navigation.Route
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class LoginForm(
    val onLoginFailed: () -> Unit,
    val onLoginSucceeded: () -> Unit,
) : Route

class LoginFormViewModel {
    val state: StateFlow<Unit> = MutableStateFlow(Unit)
    fun dispatch(action: Unit) {
    }
}

@Composable
fun LoginFormView(
    state: Unit,
    dispatch: (Unit) -> Unit,
) {
    Text("Login Form")
}
