package com.cniekirk.traintimes.di

import com.cniekirk.traintimes.view.favourites.FavouritesFragment
import com.cniekirk.traintimes.view.main.StationSearchFragment
import com.cniekirk.traintimes.view.main.HomeFragment
import com.cniekirk.traintimes.view.main.ServiceDetailFragment
import com.cniekirk.traintimes.view.planner.JourneyPlannerFragment
import com.cniekirk.traintimes.view.planner.PlannerStationSearchFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeHomeFragment(): HomeFragment

    @ContributesAndroidInjector
    abstract fun contributeNearbyFragment(): JourneyPlannerFragment

    @ContributesAndroidInjector
    abstract fun contributeFavouritesFragment(): FavouritesFragment

    @ContributesAndroidInjector
    abstract fun contributeDepStationSearchFragment(): StationSearchFragment

    @ContributesAndroidInjector
    abstract fun contributePlannerDepStationSearchFragment(): PlannerStationSearchFragment

    @ContributesAndroidInjector
    abstract fun contributesServiceDetailFragment(): ServiceDetailFragment

}