package com.marverenic.reader.data.service

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

const val BASE_URL = "https://cloud.feedly.com/v3/"

fun createFeedlyApi(): FeedlyService = Retrofit.Builder()
        .apply {
            baseUrl(BASE_URL)
            addConverterFactory(MoshiConverterFactory.create(createMoshi()))
            addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
        }
        .build()
        .create(FeedlyService::class.java)

private fun createMoshi(): Moshi {
    return Moshi.Builder()
            .add(Unit::class.java, object : JsonAdapter<Unit>() {
                override fun fromJson(reader: JsonReader) = Unit

                override fun toJson(writer: JsonWriter, value: Unit?) {
                    if (value != null) {
                        writer.beginObject()
                        writer.endObject()
                    }
                }
            })
            .build()
}