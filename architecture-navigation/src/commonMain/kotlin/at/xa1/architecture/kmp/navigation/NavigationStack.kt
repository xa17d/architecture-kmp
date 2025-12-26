package at.xa1.architecture.kmp.navigation

import androidx.compose.runtime.snapshots.SnapshotStateList


class NavigationStack(
    private val locationProvider: LocationProvider,
    private val rootRoute: Route,
) {
    val backStack: List<Location>
        get() = _backStack

    private val _listeners = ListenerList<NavigationStack>()

    private val _trackedLocations = HashMap<LocationId, Location>()
    private val _backStack = SnapshotStateList<Location>()

    private val rootNavigator = DefaultNavigator().apply {
        move(rootRoute)
        addBackStackChangedListener(::navigatorChangedListener)
    }

    init {
        refreshStack()
    }

    private fun navigatorChangedListener(navigator: Navigator) {
        refreshStack()
    }

    private fun refreshStack() {
        val stack = buildList {
            addRecursive(rootNavigator.backStack)
        }

        _backStack.clear()
        _backStack.addAll(stack)

        val removedLocations = _trackedLocations.keys - stack.map { it.id }.toSet()
        removedLocations.forEach { removedLocationId ->
            val removedLocation = checkNotNull(_trackedLocations.remove(removedLocationId)) {
                "Internal error: removed location not found in tracked locations, which should be impossible. " +
                    "-> bug in navigation stack implementation"
            }

            //removedLocation.dispose()

            if (removedLocation is CoordinatorLocation) {
                removedLocation.navigator.removeBackStackChangedListener(::navigatorChangedListener)
            }
        }

        _listeners.notifyAll(this)
    }

    private fun MutableList<Location>.addRecursive(backStack: List<NavigationEntry>) {
        backStack.forEach { entry ->
            addRecursive(entry)
        }
    }

    private fun createLocation(entry: NavigationEntry): Location {
        val location = locationProvider.provide(entry)

        if (location is CoordinatorLocation) {
            location.navigator.addBackStackChangedListener(::navigatorChangedListener)
        }

        return location
    }

    private fun MutableList<Location>.addRecursive(entry: NavigationEntry) {
        val location = _trackedLocations.getOrPut(entry.locationId) {
            createLocation(entry)
        }
        addRecursive(location)
    }

    private fun MutableList<Location>.addRecursive(location: Location) {
        add(location)
        if (location is CoordinatorLocation) {
            location.navigator.backStack.forEach { entry ->
                val location = _trackedLocations.getOrPut(entry.locationId) {
                    locationProvider.provide(entry)
                }

                if (location is CoordinatorLocation) {
                    addRecursive(location)
                } else {
                    add(location)
                }
            }
        }
    }

    fun addBackStackChangedListener(listener: (NavigationStack) -> Unit) {
        _listeners.add(listener)
    }

    fun removeBackStackChangedListener(listener: (NavigationStack) -> Unit) {
        _listeners.remove(listener)
    }
}
