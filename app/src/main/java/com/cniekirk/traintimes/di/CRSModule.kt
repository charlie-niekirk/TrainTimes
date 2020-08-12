package com.cniekirk.traintimes.di

import android.content.Context
import com.cniekirk.traintimes.data.local.AppDb
import com.cniekirk.traintimes.data.remote.CRSService
import com.cniekirk.traintimes.repo.CrsRepository
import com.cniekirk.traintimes.repo.CrsRepositoryImpl
import com.cniekirk.traintimes.utils.extensions.callFactory
import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
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
            .callTimeout(5000, TimeUnit.MILLISECONDS)
            .addInterceptor(logger)
            .build()
    }

    @Singleton
    @Provides
    @Named("CRS")
    fun provideRetrofit(@Named("CRSOk") okHttpClient: dagger.Lazy<OkHttpClient>, @Named("CRSTik") tikXml: TikXml): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://lite.realtime.nationalrail.co.uk/")
            .callFactory { okHttpClient.get().newCall(it) }
            .addConverterFactory(TikXmlConverterFactory.create(tikXml))
            .build()
    }

    @Singleton
    @Provides
    @Named("CRSTik")
    fun provideTikXml(): TikXml {
        return TikXml.Builder()
            .writeDefaultXmlDeclaration(true)
            .exceptionOnUnreadXml(false)
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