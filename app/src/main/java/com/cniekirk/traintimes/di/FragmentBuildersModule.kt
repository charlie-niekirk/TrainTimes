package com.cniekirk.traintimes.di

import com.cniekirk.traintimes.ui.favourites.FavouritesFragment
import com.cniekirk.traintimes.ui.main.DepBoardResultsFragment
import com.cniekirk.traintimes.ui.main.StationSearchFragment
import com.cniekirk.traintimes.ui.main.HomeFragment
import com.cniekirk.traintimes.ui.main.ServiceDetailFragment
import com.cniekirk.traintimes.ui.planner.JourneyPlannerFragment
import com.cniekirk.traintimes.ui.planner.JourneyPlannerResultsFragment
import com.cniekirk.traintimes.ui.planner.PlannerStationSearchFragment
import com.cniekirk.traintimes.ui.station.StationDetailFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeHomeFragment(): HomeFragment

    @ContributesAndroidInjector
    abstract fun contributeDepBoardResultsFragment(): DepBoardResultsFragment

    @ContributesAndroidInjector
    abstract fun contributeNearbyFragment(): JourneyPlannerFragment

    @ContributesAndroidInjector
    abstract fun contributeFavouritesFragment(): FavouritesFragment

    @ContributesAndroidInjector
    abstract fun contributeDepStationSearchFragment(): StationSearchFragment

    @ContributesAndroidInjector
    abstract fun contributePlannerDepStationSearchFragment(): PlannerStationSearchFragment

    @ContributesAndroidInjector
    abstract fun contributesJourneyPlannerResultsFragment(): JourneyPlannerResultsFragment

    @ContributesAndroidInjector
    abstract fun contributesServiceDetailFragment(): ServiceDetailFragment

    @ContributesAndroidInjector
    abstract fun contributeStationDetailFragment(): StationDetailFragment

}