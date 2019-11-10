package com.cniekirk.traintimes.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cniekirk.traintimes.ui.main.HomeViewModel
import com.cniekirk.traintimes.ui.nearby.NearbyViewModel
import com.cniekirk.traintimes.vm.AppViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    abstract fun bindHomeViewModel(homeViewModel: HomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NearbyViewModel::class)
    abstract fun bindNearbyViewModel(nearbyViewModel: NearbyViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(appViewModelFactory: AppViewModelFactory): ViewModelProvider.Factory

}