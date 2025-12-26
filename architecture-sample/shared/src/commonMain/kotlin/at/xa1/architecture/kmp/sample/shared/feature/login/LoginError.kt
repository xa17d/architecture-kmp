package at.xa1.architecture.kmp.sample.shared.feature.login

import at.xa1.architecture.kmp.navigation.Route

data class LoginError(
    val onConfirm: () -> Unit,
) : Route
