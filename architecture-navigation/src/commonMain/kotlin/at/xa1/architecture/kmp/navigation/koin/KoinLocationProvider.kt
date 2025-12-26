package at.xa1.architecture.kmp.navigation.koin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import at.xa1.architecture.kmp.navigation.ComposeLocation
import at.xa1.architecture.kmp.navigation.Coordinator
import at.xa1.architecture.kmp.navigation.CoordinatorLocation
import at.xa1.architecture.kmp.navigation.DefaultNavigator
import at.xa1.architecture.kmp.navigation.Location
import at.xa1.architecture.kmp.navigation.LocationId
import at.xa1.architecture.kmp.navigation.LocationProvider
import at.xa1.architecture.kmp.navigation.NavigationEntry
import at.xa1.architecture.kmp.navigation.Route
import org.koin.compose.getKoin
import org.koin.core.module.KoinDslMarker
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.bind
import kotlin.reflect.KClass


@Composable
fun getKoinLocationProvider(): LocationProvider {
    val koin = getKoin()
    return remember {
        val provider = KoinLocationProvider()
        koin.getAll<KoinLocationDefinition>().forEach {
            it.applyTo(provider)
        }
        provider
    }
}

@KoinDslMarker
fun Module.routes(definition: LocationDefinition.() -> Unit) {
    factory(
        named("${LocationId.new().value}"), // TODO better way to create unique names?
    ) {
        KoinLocationDefinition { provider ->
            LocationDefinition(provider, this).definition()
        }
    } bind KoinLocationDefinition::class
}

fun interface KoinLocationDefinition {
    fun applyTo(target: KoinLocationProvider)
}

class LocationDefinition(
    private val target: KoinLocationProvider,
    @PublishedApi
    internal val scope: Scope,
) {

    inline fun <reified C : Coordinator, reified T1 : Any> coordinator(
        routeType: RouteType, // TODO unify Rout type as KClass or reified generic type.
        noinline factory: (T1) -> C,
    ) {
        coordinator(routeType) {
            factory(scope.get<T1>())
        }
    }

    @PublishedApi
    internal fun coordinator(
        routeType: RouteType,
        factory: () -> Coordinator,
    ) {
        target.register(routeType) { navigationEntry ->
            val coordinator = factory()
            CoordinatorLocation(
                id = LocationId.new(),
                metadata = navigationEntry.metadata,
                navigator = DefaultNavigator(),
                route = navigationEntry.route,
                coordinator = coordinator,
            )
        }
    }

    inline fun <reified R : Route, reified VM : Any> composableWithViewModel(
        noinline composableLambda: @Composable (VM) -> Unit,
    ) {
        composable(
            routeType = R::class,
        ) {
            val viewModel = scope.get<VM>()
            composableLambda(viewModel)
        }
    }

    @PublishedApi
    internal fun composable(
        routeType: RouteType,
        composableLambda: @Composable () -> Unit,
    ) {
        target.register(routeType) { navigationEntry ->
            ComposeLocation(
                id = LocationId.new(),
                metadata = navigationEntry.metadata,
                composable = composableLambda,
            )
        }
    }
}

// TODO internal?
class KoinLocationProvider : LocationProvider {

    private val routes = HashMap<RouteType, KoinLocationFactory>()

    fun register(routeType: RouteType, locationFactory: KoinLocationFactory) {
        check(!routes.containsKey(routeType)) {
            "Route $routeType is already registered"
        }

        routes[routeType] = locationFactory
    }

    override fun provide(entry: NavigationEntry): Location {
        val factory = checkNotNull(routes[entry.route::class]) {
            "No location factory registered for route type: ${entry.route::class}. " +
                "Is the Koin module of the feature loaded?"
        }

        return factory(entry)
    }
}

// TODO internal?
typealias RouteType = KClass<out Route>
typealias KoinLocationFactory = (NavigationEntry) -> Location
