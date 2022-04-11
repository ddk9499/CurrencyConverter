package uz.dkamaloff.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import toothpick.ktp.KTP

class NamedScopeViewModelFactory(
    private val scopeName: Any,
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>) =
        if (KTP.isScopeOpen(scopeName)) {
            KTP.openScope(scopeName).getInstance(modelClass) as T
        } else {
            KTP.openRootScope().openSubScope(scopeName).getInstance(modelClass) as T
        }
}

/**
 * Created a viewModel with auto-generated scope.
 */
internal inline fun <reified T : ViewModel> ViewModelStoreOwner.viewModel(): Lazy<T> = lazy {
    ViewModelProvider(this, NamedScopeViewModelFactory(this::class.java)).get(T::class.java)
}
