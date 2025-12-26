package at.xa1.architecture.kmp.navigation

import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.runtime.snapshots.SnapshotStateList

interface Navigator { // Should there be only one implementation?
    val backStack: List<NavigationEntry>
    fun move(route: Route, metadata: Metadata = NoMetadata)
    fun stack(route: Route, metadata: Metadata = NoMetadata)

    // TODO replace replace/push? goTo/add?

    fun addBackStackChangedListener(listener: NavigatorBackStackChangedListener)
    fun removeBackStackChangedListener(listener: NavigatorBackStackChangedListener)
}

typealias NavigatorBackStackChangedListener = (Navigator) -> Unit

inline fun Navigator.stack(crossinline route: RouteBuilder.() -> Route) {
    TODO()
}

interface RouteBuilder {
    fun setMetadata(key: String, value: Any)
    fun close()
}


data class NavigationEntry(
    val locationId: LocationId = LocationId.new(),
    val route: Route,
    val metadata: Metadata = NoMetadata,
)

class DefaultNavigator : Navigator {
    private val _backStack = SnapshotStateList<NavigationEntry>()
    private val _listeners = ListenerList<Navigator>()

    override val backStack: List<NavigationEntry>
        get() = _backStack

    private inline fun editBackStack(action: () -> Unit) {
        val snapshot = Snapshot.takeMutableSnapshot()
        val result = try {
            snapshot.enter {
                action()
            }
            snapshot.apply()
        } finally {
            snapshot.dispose()
        }

        if (!result.succeeded) {
            // TODO throw exception? Option to check?
        }

        // TODO should this even be snapshot based?
        _listeners.notifyAll(this)
    }

    override fun move(
        route: Route,
        metadata: Metadata,
    ) {
        editBackStack {
            _backStack.clear()
            _backStack.add(
                NavigationEntry(
                    route = route,
                    metadata = metadata,
                ),
            )
        }
    }

    override fun stack(
        route: Route,
        metadata: Metadata,
    ) {
        editBackStack {
            _backStack.add(
                NavigationEntry(
                    route = route,
                    metadata = metadata,
                ),
            )
        }
    }

    override fun addBackStackChangedListener(listener: NavigatorBackStackChangedListener) {
        _listeners.add(listener)
    }

    override fun removeBackStackChangedListener(listener: NavigatorBackStackChangedListener) {
        _listeners.remove(listener)
    }
}
