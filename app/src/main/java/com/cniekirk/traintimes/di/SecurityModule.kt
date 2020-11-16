package com.cniekirk.traintimes.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.createDataStore
import com.cniekirk.traintimes.data.proto.FavouritesSerializer
import com.cniekirk.traintimes.model.Favourites
import com.cniekirk.traintimes.repo.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import java.security.KeyStore
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
class SecurityModule {

    companion object Names {
        const val KEY_NAME = "Key Name"
        const val KEY_STORE_NAME = "Key Store Name"

        const val ANDROID_KEY_STORE_TYPE = "AndroidKeyStore"
        const val SIMPLE_DATA_KEY_NAME = "SimpleDataKey"
    }

    @Provides
    fun provideKeyStore(): KeyStore =
        KeyStore.getInstance(ANDROID_KEY_STORE_TYPE).apply { load(null) }

    @Provides
    @Named(KEY_NAME)
    fun providesKeyName(): String = SIMPLE_DATA_KEY_NAME

    @Provides
    @Named(KEY_STORE_NAME)
    fun providesKeyStoreName(): String = ANDROID_KEY_STORE_TYPE

    @Provides
    @Singleton
    fun provideFavouritesDataStore(@ApplicationContext context: Context, cryptoRepository: CryptoRepository): DataStore<Favourites> {
        return context.createDataStore(
            "favourite_routes.pb",
            serializer = FavouritesSerializer(cryptoRepository)
        )
    }

    @Provides
    @Singleton
    fun provideProtoRepository(dataStore: DataStore<Favourites>): ProtoRepository
            = ProtoRepositoryImpl(dataStore)

    @Provides
    @Singleton
    fun provideCipherRepository(aesCipherRepositoryImpl: AESCipherRepositoryImpl): CipherRepository = aesCipherRepositoryImpl

    @Provides
    @Singleton
    fun provideCryptoRepository(cryptoRepositoryImpl: CryptoRepositoryImpl): CryptoRepository = cryptoRepositoryImpl

}