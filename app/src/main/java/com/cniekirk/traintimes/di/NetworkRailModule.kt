package com.cniekirk.traintimes.di

import android.content.Context
import com.cniekirk.traintimes.data.local.AppDb
import com.cniekirk.traintimes.data.remote.TrackTimesService
import com.cniekirk.traintimes.data.remote.NREService
import com.cniekirk.traintimes.model.adapter.SingleToArrayAdapter
import com.cniekirk.traintimes.model.getdepboard.local.Query
import com.cniekirk.traintimes.repo.NreRepository
import com.cniekirk.traintimes.repo.NreRepositoryImpl
import com.cniekirk.traintimes.utils.extensions.callFactory
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import dagger.Lazy
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

/**
 * @author Charlie Niekirk
 */
@Module
class NetworkRailModule {

    @Provides
    @Singleton
    @Named("NetworkCache")
    fun provideCache(context: Context): Cache {
        return Cache(context.cacheDir, 10 * 1024 * 1024)
    }

    @Singleton
    @Provides
    @Named("NREOk")
    fun provideOkHttpClient(@Named("NetworkCache") cache: Cache): OkHttpClient {
        val logger = HttpLoggingInterceptor()
        logger.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder()
            .callTimeout(7000, TimeUnit.MILLISECONDS)
            .addInterceptor(logger)
            .cache(cache)
            .build()
    }

    @Singleton
    @Provides
    @Named("NRE")
    fun provideRetrofit(@Named("NREOk") okHttpClient: Lazy<OkHttpClient>, @Named("Network") tikXml: TikXml): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://lite.realtime.nationalrail.co.uk/")
            .callFactory { okHttpClient.get().newCall(it) }
            .addConverterFactory(TikXmlConverterFactory.create(tikXml))
            .build()
    }

    @Singleton
    @Provides
    @Named("Network")
    fun provideTikXml(): TikXml {
        return TikXml.Builder()
            .writeDefaultXmlDeclaration(true)
            .exceptionOnUnreadXml(false)
            .build()
    }

    @Singleton
    @Provides
    @Named("cache")
    fun provideCacheMoshi(): Moshi {
        return Moshi.Builder()
            //.add(KotlinJsonAdapterFactory())
            .build()
    }

    @Singleton
    @Provides
    @Named("journey")
    fun provideJourneyMoshi(): Moshi {
        return Moshi.Builder()
            .add(SingleToArrayAdapter.INSTANCE)
            //.add(KotlinJsonAdapterFactory())
            .build()
    }

    @Singleton
    @Provides
    @Named("JourneyPlan")
    fun provideJourneyRetrofit(@Named("NREOk") okHttpClient: Lazy<OkHttpClient>, @Named("journey") moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://livetracktimes.co.uk/")
            .callFactory { okHttpClient.get().newCall(it) }
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Provides
    @Singleton
    fun provideJourneyPlannerService(@Named("JourneyPlan") retrofit: Retrofit): TrackTimesService
            = retrofit.create(TrackTimesService::class.java)

    @Singleton
    @Provides
    @Named("Net")
    fun provideAppDb(context: Context): AppDb {
        return AppDb.getInstance(context)
    }

    @Singleton
    @Provides
    fun provideRecentQueriesDao(@Named("Net") db: AppDb) = db.recentQueriesDao()

    @Singleton
    @Provides
    fun provideFavouritesDao(@Named("Net") db: AppDb) = db.favouritesDao()

    @Singleton
    @Provides
    fun provideAdapter(@Named("cache") moshi: Moshi): JsonAdapter<Query> {
        return moshi.adapter(Query::class.java)
    }

    @Provides
    @Singleton
    fun provideNreService(@Named("NRE") retrofit: Retrofit): NREService = retrofit.create(NREService::class.java)

    @Provides
    @Singleton
    fun provideNreRepository(nreRepositoryImpl: NreRepositoryImpl): NreRepository = nreRepositoryImpl
}