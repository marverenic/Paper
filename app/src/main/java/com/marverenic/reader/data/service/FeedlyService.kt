package com.marverenic.reader.data.service

import com.marverenic.reader.model.Category
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface FeedlyService {

    @GET("categories")
    fun getCategories(@Header("Authorization") token: String): Single<Response<List<Category>>>

}