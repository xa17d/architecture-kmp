package at.xa1.architecture.kmp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import kotlin.reflect.KClass

@Composable
fun <T> rememberViewModelScoped(
    key: String,
    calculation: @DisallowComposableCalls () -> T,
) : T {
    val viewModelStoreOwner = LocalViewModelStoreOwner.current ?: error("viewModelStore not available.")
    val viewModel = remember(viewModelStoreOwner) {
        val provider = ViewModelProvider.create(
            viewModelStoreOwner.viewModelStore,
            RememberViewModelScope.Factory(calculation),
        )
        val viewModel = provider[key, RememberViewModelScope::class]

        @Suppress("UNCHECKED_CAST")
        viewModel as RememberViewModelScope<T>
    }


    return viewModel.value
}

private class RememberViewModelScope<T>(
    val value: T,
) : ViewModel() {

    class Factory<T>(
        private val value: @DisallowComposableCalls () -> T,
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
            check(modelClass == RememberViewModelScope::class) {
                "Factory can only create RememberViewModelScope. modelClass: $modelClass"
            }

            val viewModel = RememberViewModelScope(value())

            @Suppress("UNCHECKED_CAST")
            return viewModel as T
        }
    }
}

