package at.xa1.architecture.kmp.sample.shared.integration.post

import at.xa1.architecture.kmp.navigation.Route

data class PostStack(
    val onLogin: () -> Unit,
) : Route
