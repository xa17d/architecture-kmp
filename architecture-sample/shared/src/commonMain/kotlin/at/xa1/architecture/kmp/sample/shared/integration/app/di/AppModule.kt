package at.xa1.architecture.kmp.sample.shared.integration.app.di

import androidx.compose.runtime.collectAsState
import at.xa1.architecture.kmp.navigation.DefaultNavigator
import at.xa1.architecture.kmp.navigation.DefaultRoute
import at.xa1.architecture.kmp.navigation.Navigator
import at.xa1.architecture.kmp.navigation.koin.routes
import at.xa1.architecture.kmp.sample.shared.feature.login.LoginForm
import at.xa1.architecture.kmp.sample.shared.feature.login.LoginFormView
import at.xa1.architecture.kmp.sample.shared.feature.login.LoginFormViewModel
import at.xa1.architecture.kmp.sample.shared.integration.app.AppCoordinator
import org.koin.dsl.module

val appModule = module {
    routes {
        coordinator(DefaultRoute::class, ::AppCoordinator)
        composableWithViewModel<LoginForm, LoginFormViewModel> { viewModel ->
            LoginFormView(
                viewModel.state.collectAsState().value,
                viewModel::dispatch,
            )
        }
    }

    factory<Navigator> { DefaultNavigator() }
}
