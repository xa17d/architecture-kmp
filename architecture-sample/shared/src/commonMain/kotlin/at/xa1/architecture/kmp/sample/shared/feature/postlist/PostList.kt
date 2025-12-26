package at.xa1.architecture.kmp.sample.shared.feature.postlist

import at.xa1.architecture.kmp.navigation.Route

data class PostList(
    val onNewPost: () -> Unit,
) : Route
