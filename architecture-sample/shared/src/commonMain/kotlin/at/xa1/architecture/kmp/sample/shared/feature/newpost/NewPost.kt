package at.xa1.architecture.kmp.sample.shared.feature.newpost

import at.xa1.architecture.kmp.navigation.Route

data class NewPost(
    val onClose: () -> Unit,
    val onPosted: () -> Unit,
) : Route
