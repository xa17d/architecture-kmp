package at.xa1.architecture.kmp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import at.xa1.architecture.kmp.navigation.koin.getKoinLocationProvider

@Composable
fun rememberKoinNavigationStack(
    rootRoute: Route,
    /**
     * Needs to be unique in the ViewModelStore to allow proper state restoring across configuration changes.
     */
    navigationDisplayId: String = "DEFAULT",
): NavigationStack {
    val locationProvider = getKoinLocationProvider()
    val navigationStack = rememberViewModelScoped("$NAV_DISPLAY_VIEWMODEL_KEY_REFIX$navigationDisplayId") {
        NavigationStack(
            locationProvider = locationProvider,
            rootRoute = rootRoute,
        )
    }

    return navigationStack
}

data object DefaultRoute : Route

@Composable
fun NavDisplay(
    navigationStack: NavigationStack = rememberKoinNavigationStack(DefaultRoute),
) {


    /**
     *  TODO expose parameters?
     *     backStack: List<T>,
     *     modifier: Modifier = Modifier,
     *     contentAlignment: Alignment = Alignment.TopStart,
     *     onBack: () -> Unit = {
     *         if (backStack is MutableList<T>) {
     *             backStack.removeLastOrNull()
     *         }
     *     },
     *     entryDecorators: List<NavEntryDecorator<T>> =
     *         listOf(rememberSaveableStateHolderNavEntryDecorator()),
     *     sceneStrategy: SceneStrategy<T> = SinglePaneSceneStrategy(),
     *     sizeTransform: SizeTransform? = null,
     *     transitionSpec: AnimatedContentTransitionScope<Scene<T>>.() -> ContentTransform =
     *         defaultTransitionSpec(),
     *     popTransitionSpec: AnimatedContentTransitionScope<Scene<T>>.() -> ContentTransform =
     *         defaultPopTransitionSpec(),
     *     predictivePopTransitionSpec:
     *         AnimatedContentTransitionScope<Scene<T>>.(
     *             @NavigationEvent.SwipeEdge Int
     *         ) -> ContentTransform =
     *         defaultPredictivePopTransitionSpec(),
     *     entryProvider: (key: T) -> NavEntry<T>,
     */

    val backStack = remember { mutableStateOf(navigationStack.backStack) }
    DisposableEffect(backStack) {
        val listener: (NavigationStack) -> Unit = { stack ->
            backStack.value = stack.backStack
        }
        navigationStack.addBackStackChangedListener(listener)

        onDispose {
            navigationStack.removeBackStackChangedListener(listener)
        }
    }

    println(">>> ${backStack.value}")

    NavDisplay(
        backStack = backStack.value,
        // TODO? onBack = { backStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
            // TODO add KoinScope entry decorator
            NavEntryDecorator(
                onPop = { contentKey -> println(">>> onPop $contentKey") },
                decorate = { entry ->
                    entry.Content()
                },
            ),
        ),
        entryProvider = { location ->
            NavEntry(
                key = location,
                contentKey = location.id.toString(),
                metadata = location.metadata,
                content = when (location) { // TODO move out of lambda
                    is ComposeLocation -> {
                        val composable = location.composable
                        @Composable { _: Location ->
                            composable()
                        }
                    }

                    else -> {
                        // TODO should empty content be skipped entirely?
                        @Composable { _: Location -> }
                    }
                },
            )
        },
    )
}

private const val NAV_DISPLAY_VIEWMODEL_KEY_REFIX = "at.xa1.architecture.kmp.navigation.NavDisplayViewModelId:"
