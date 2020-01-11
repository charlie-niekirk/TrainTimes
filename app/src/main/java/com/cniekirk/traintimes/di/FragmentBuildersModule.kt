package com.cniekirk.traintimes.di

import com.cniekirk.traintimes.ui.main.StationSearchFragment
import com.cniekirk.traintimes.ui.main.HomeFragment
import com.cniekirk.traintimes.ui.nearby.NearbyFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeHomeFragment(): HomeFragment

    @ContributesAndroidInjector
    abstract fun contributeNearbyFragment(): NearbyFragment

    @ContributesAndroidInjector
    abstract fun contributeDepStationSearchFragment(): StationSearchFragment

}