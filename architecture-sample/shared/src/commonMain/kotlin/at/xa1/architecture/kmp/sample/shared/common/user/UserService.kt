package at.xa1.architecture.kmp.sample.shared.common.user

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface UserService {
    val currentUser: StateFlow<UserInfo?>

    suspend fun login(username: String, password: String)
    suspend fun logout()
}

class NoUserLoggedInException : Exception("No user is logged in.")
class LoginException(message: String, cause: Throwable? = null) : Exception(message, cause)

data class UserInfo(
    // TODO rename ID?
    val username: String,
)

class FakeUserService : UserService {

    private val _currentUser = MutableStateFlow<UserInfo?>(null)
    override val currentUser: StateFlow<UserInfo?>
        get() = _currentUser


    override suspend fun login(username: String, password: String) {
        if (!password.endsWith("!")) {
            throw LoginException(
                "Password must end with '!'. " +
                    "In FakeUserService, any username can login as long as the password ends with '!'.",
            )
        }

        _currentUser.value = UserInfo(
            username = username,
        )
    }

    override suspend fun logout() {
        _currentUser.value = null
    }
}
