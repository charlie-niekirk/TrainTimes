package com.cniekirk.traintimes.di

import android.content.Context
import com.cniekirk.traintimes.data.local.AppDb
import com.cniekirk.traintimes.data.remote.CRSService
import com.cniekirk.traintimes.repo.CrsRepository
import com.cniekirk.traintimes.repo.CrsRepositoryImpl
import com.cniekirk.traintimes.utils.extensions.callFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
class CRSModule {

    @Singleton
    @Provides
    @Named("CRSOk")
    fun provideOkHttpClient(): OkHttpClient {
        val logger = HttpLoggingInterceptor()
        logger.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder()
            .callTimeout(10000, TimeUnit.MILLISECONDS)
            .addInterceptor(logger)
            .build()
    }

    @Singleton
    @Provides
    @Named("CRS")
    fun provideRetrofit(@Named("CRSOk") okHttpClient: dagger.Lazy<OkHttpClient>): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://www.nationalrail.co.uk")
            .callFactory { okHttpClient.get().newCall(it) }
            .build()
    }

    @Singleton
    @Provides
    fun provideAppDb(context: Context): AppDb {
        return AppDb.getInstance(context)
    }

    @Singleton
    @Provides
    fun provideCrsDao(db: AppDb) = db.crsDao()

    @Singleton
    @Provides
    fun provideCrsService(@Named("CRS") retrofit: Retrofit): CRSService = retrofit.create(CRSService::class.java)

    @Provides
    @Singleton
    fun provideCrsRepository(crsRepository: CrsRepositoryImpl): CrsRepository = crsRepository

}