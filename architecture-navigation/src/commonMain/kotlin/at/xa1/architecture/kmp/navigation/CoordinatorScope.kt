package at.xa1.architecture.kmp.navigation

import kotlinx.coroutines.CoroutineScope

interface CoordinatorScope : CoroutineScope

private class CoordinatorScopeWrapper(
    private val delegate: CoroutineScope,
) : CoordinatorScope, CoroutineScope by delegate

fun CoordinatorScope(delegate: CoroutineScope): CoordinatorScope =
    CoordinatorScopeWrapper(delegate)
