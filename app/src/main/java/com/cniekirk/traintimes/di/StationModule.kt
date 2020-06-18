package com.cniekirk.traintimes.di

import android.content.Context
import com.cniekirk.traintimes.data.remote.StationService
import com.cniekirk.traintimes.data.remote.TrackTimesService
import com.cniekirk.traintimes.repo.NreRepository
import com.cniekirk.traintimes.repo.NreRepositoryImpl
import com.cniekirk.traintimes.repo.StationRepo
import com.cniekirk.traintimes.repo.StationRepoImpl
import com.cniekirk.traintimes.utils.extensions.callFactory
import com.squareup.moshi.Moshi
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
 **/
@Module
class StationModule {

    @Provides
    @Singleton
    @Named("StationCache")
    fun provideCache(context: Context): Cache {
        return Cache(context.cacheDir, 10 * 1024 * 1024)
    }

    @Singleton
    @Provides
    @Named("StationOk")
    fun provideOkHttpClient(@Named("StationCache") cache: Cache): OkHttpClient {
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
    @Named("Station")
    fun provideRetrofit(@Named("StationOk") okHttpClient: Lazy<OkHttpClient>, @Named("StationTik") tikXml: TikXml): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://internal.nationalrail.co.uk/")
            .callFactory { okHttpClient.get().newCall(it) }
            .addConverterFactory(TikXmlConverterFactory.create(tikXml))
            .build()
    }

    @Singleton
    @Provides
    @Named("StationTik")
    fun provideTikXml(): TikXml {
        return TikXml.Builder()
            .writeDefaultXmlDeclaration(true)
            .exceptionOnUnreadXml(false)
            .build()
    }

    @Provides
    @Singleton
    fun provideStationService(@Named("Station") retrofit: Retrofit): StationService
            = retrofit.create(StationService::class.java)

    @Provides
    @Singleton
    fun provideNreRepository(stationRepoImpl: StationRepoImpl): StationRepo = stationRepoImpl

}