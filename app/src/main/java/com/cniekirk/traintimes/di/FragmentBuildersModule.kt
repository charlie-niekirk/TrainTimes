package com.cniekirk.traintimes.di

import com.cniekirk.traintimes.ui.main.StationSearchFragment
import com.cniekirk.traintimes.ui.main.HomeFragment
import com.cniekirk.traintimes.ui.main.ServiceDetailFragment
import com.cniekirk.traintimes.ui.planner.JourneyPlannerFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeHomeFragment(): HomeFragment

    @ContributesAndroidInjector
    abstract fun contributeNearbyFragment(): JourneyPlannerFragment

    @ContributesAndroidInjector
    abstract fun contributeDepStationSearchFragment(): StationSearchFragment

    @ContributesAndroidInjector
    abstract fun contributesServiceDetailFragment(): ServiceDetailFragment

}