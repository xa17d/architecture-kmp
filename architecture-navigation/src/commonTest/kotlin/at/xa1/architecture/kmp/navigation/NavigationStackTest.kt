package at.xa1.architecture.kmp.navigation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class NavigationStackTest {
    @Test
    fun `navigation stack`() {
        val s = NavigationStack(
            object : LocationProvider {
                override fun provide(entry: NavigationEntry): Location {
                    return when (entry.route::class) {
                        TestRoute::class -> DummyLocation(LocationId.new())
                        TestCoordinatorR::class -> {
                            val nav = DefaultNavigator()
                            val coord = TestCoordinator(nav).apply {
                                start()
                            }
                            CoordinatorLocation(
                                id = LocationId.new(),
                                metadata = NoMetadata,
                                navigator = nav,
                                coordinator = coord,
                                route = entry.route,
                            )
                        }

                        else -> error("unknown type")
                    }
                }
            },
            TestCoordinatorR(),
        )

        //assertEquals(1, s.backStack.size)
        //assertIs<DummyLocation>(s.backStack[0])



        s.backStack.forEach {
            println(it)
        }
    }
}

data class TestRoute(val id: String = "") : Route
data class TestCoordinatorR(val id: String = "") : Route

class TestCoordinator(
    val navigator: Navigator,
) : Coordinator {
    override fun start() {
        navigator.move(TestRoute("hey"))
    }
}
