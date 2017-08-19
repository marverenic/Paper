package com.marverenic.reader.data.service

import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

const val BASE_URL = "https://cloud.feedly.com/v3/"

fun createFeedlyApi() = Retrofit.Builder()
        .apply {
            baseUrl(BASE_URL)
            addConverterFactory(MoshiConverterFactory.create())
            addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
        }
        .build()
        .create(FeedlyService::class.java)
