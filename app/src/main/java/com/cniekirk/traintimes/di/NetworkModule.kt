package com.cniekirk.traintimes.di

import android.content.Context
import com.cniekirk.traintimes.data.remote.NREService
import com.cniekirk.traintimes.repo.NreRepository
import com.cniekirk.traintimes.repo.NreRepositoryImpl
import com.cniekirk.traintimes.utils.extensions.callFactory
import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * @author Charlie Niekirk
 */
@Module(includes = [ViewModelModule::class])
class NetworkModule {

    @Provides
    @Singleton
    fun provideCache(context: Context): Cache {
        return Cache(context.cacheDir, 10 * 1024 * 1024)
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(cache: Cache): OkHttpClient {
        val logger = HttpLoggingInterceptor()
        logger.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder()
            .callTimeout(10000, TimeUnit.MILLISECONDS)
            .addInterceptor(logger)
            .cache(cache)
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: dagger.Lazy<OkHttpClient>, tikXml: TikXml): Retrofit {
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

    @Provides
    @Singleton
    fun provideNreService(retrofit: Retrofit): NREService = retrofit.create(NREService::class.java)

    @Provides
    @Singleton
    fun provideNreRepository(nreRepositoryImpl: NreRepositoryImpl): NreRepository = nreRepositoryImpl
}