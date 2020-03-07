package com.cniekirk.traintimes.di

import android.content.Context
import com.cniekirk.traintimes.data.remote.TrackTimesService
import com.cniekirk.traintimes.data.remote.NREService
import com.cniekirk.traintimes.repo.NreRepository
import com.cniekirk.traintimes.repo.NreRepositoryImpl
import com.cniekirk.traintimes.utils.extensions.callFactory
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
@Module(includes = [ViewModelModule::class])
class NetworkRailModule {

    @Provides
    @Singleton
    fun provideCache(context: Context): Cache {
        return Cache(context.cacheDir, 10 * 1024 * 1024)
    }

    @Singleton
    @Provides
    @Named("NREOk")
    fun provideOkHttpClient(cache: Cache): OkHttpClient {
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
    fun provideRetrofit(@Named("NREOk") okHttpClient: Lazy<OkHttpClient>, tikXml: TikXml): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://lite.realtime.nationalrail.co.uk/")
            .callFactory { okHttpClient.get().newCall(it) }
            .addConverterFactory(TikXmlConverterFactory.create(tikXml))
            .build()
    }

    @Singleton
    @Provides
    fun provideTikXml(): TikXml {
        return TikXml.Builder()
            .writeDefaultXmlDeclaration(true)
            .exceptionOnUnreadXml(false)
            .build()
    }

    @Singleton
    @Provides
    @Named("JourneyPlan")
    fun provideJourneyRetrofit(@Named("NREOk") okHttpClient: Lazy<OkHttpClient>): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://livetracktimes.co.uk/")
            .callFactory { okHttpClient.get().newCall(it) }
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideJourneyPlannerService(@Named("JourneyPlan") retrofit: Retrofit): TrackTimesService
            = retrofit.create(TrackTimesService::class.java)

    @Provides
    @Singleton
    fun provideNreService(@Named("NRE") retrofit: Retrofit): NREService = retrofit.create(NREService::class.java)

    @Provides
    @Singleton
    fun provideNreRepository(nreRepositoryImpl: NreRepositoryImpl): NreRepository = nreRepositoryImpl
}