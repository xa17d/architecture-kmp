package at.xa1.architecture.kmp.navigation

import androidx.compose.runtime.Composable
import kotlin.random.Random
import kotlin.reflect.KClass

interface LocationProvider {
    fun provide(entry: NavigationEntry): Location
}

// TODO @JvmInline value
data class LocationId(val value: ULong) {
    companion object {
        private var counter = Random.nextLong().toULong()
        fun new(): LocationId {
            return LocationId(counter++)
        }
    }
}

interface Location {
    val id: LocationId
    val metadata: Metadata
}

class CoordinatorLocation(
    override val id: LocationId,
    override val metadata: Metadata,
    val navigator: Navigator,
    val coordinator: Coordinator,
    private val route: Route,
) : Location {
    override fun toString(): String {
        return "CoordinatorLocation(id=$id, coordinator=$coordinator, route=$route, metadata=$metadata)"
    }
}

class ComposeLocation(
    override val id: LocationId,
    override val metadata: Metadata,
    val composable: @Composable () -> Unit,
) : Location {
}

class DummyLocation(
    override val id: LocationId,
    override val metadata: Metadata = NoMetadata,
) : Location {
    override fun toString(): String {
        return "DummyLocation(id=$id, metadata=$metadata)"
    }
}

class MutableLocationProvider : LocationProvider {

    private val mapping = HashMap<KClass<out Route>, Type>()

    inline fun <reified T : Route> dummy() {
        addMapping(T::class, Type.DUMMY)
    }

    @PublishedApi
    internal fun addMapping(klass: KClass<out Route>, type: Type) {
        if (mapping.containsKey(klass)) {
            error("Mapping already present: $klass")
        }
        mapping[klass] = type
    }

    override fun provide(entry: NavigationEntry): Location {
        return when (mapping[entry.route::class]) {
            Type.COMPOSABLE -> TODO()
            Type.DUMMY -> DummyLocation(entry.locationId)
            Type.COORDINATOR -> TODO()
            null -> error("No mapping provided for ${entry.route}")
        }
    }

    enum class Type {
        COMPOSABLE,
        DUMMY,
        COORDINATOR
    }
}
