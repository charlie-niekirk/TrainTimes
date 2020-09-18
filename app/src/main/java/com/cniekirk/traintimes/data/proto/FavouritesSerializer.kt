package com.cniekirk.traintimes.data.proto

import androidx.datastore.CorruptionException
import androidx.datastore.Serializer
import com.cniekirk.traintimes.model.Favourites
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object FavouritesSerializer : Serializer<Favourites> {
    override fun readFrom(input: InputStream): Favourites {
        try {
            return Favourites.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read protobuf.", exception)
        }
    }
    override fun writeTo(t: Favourites, output: OutputStream) = t.writeTo(output)
}