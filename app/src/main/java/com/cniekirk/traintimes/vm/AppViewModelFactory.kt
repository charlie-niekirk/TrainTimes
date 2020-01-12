package com.cniekirk.traintimes.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

/**
 * ViewModelProvider.Factory to be provided with a map of valid ViewModels by Dagger
 */
@Singleton
class AppViewModelFactory @Inject constructor(
    private val creators: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
): ViewModelProvider.Factory {

    /**
     * Create the ViewModel instance if possible
     *
     * @param modelClass: The ViewModel class
     * @return The ViewModel instance of type [T]
     */
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        val creator = creators[modelClass] ?: creators.entries.firstOrNull {
            modelClass.isAssignableFrom(it.key)
        }?.value ?: throw IllegalArgumentException("Unknown model class $modelClass") as Throwable

        @Suppress("UNCHECKED_CAST")
        return creator.get() as T

    }

}