package com.cniekirk.traintimes.data.proto

import androidx.datastore.CorruptionException
import androidx.datastore.Serializer
import com.cniekirk.traintimes.model.Favourites
import com.cniekirk.traintimes.repo.CryptoRepository
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

class FavouritesSerializer @Inject constructor(
    private val cryptoRepository: CryptoRepository
): Serializer<Favourites> {

    override fun readFrom(input: InputStream): Favourites {
        return if (input.available() != 0) {
            try {
                Favourites.ADAPTER.decode(cryptoRepository.decrypt(input))
            } catch (exception: IOException) {
                throw CorruptionException("Cannot read proto", exception)
            }
        } else {
            Favourites(emptyList())
        }
    }

    override fun writeTo(t: Favourites, output: OutputStream) {
        cryptoRepository.encrypt(Favourites.ADAPTER.encode(t), output)
    }

}