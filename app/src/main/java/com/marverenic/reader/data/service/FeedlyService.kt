package com.marverenic.reader.data.service

import com.marverenic.reader.model.Category
import com.marverenic.reader.model.Stream
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface FeedlyService {

    @GET("categories")
    fun getCategories(@Header("Authorization") token: String): Single<Response<List<Category>>>

    @GET("streams/{streamId}/contents")
    fun getStream(@Header("Authorization") token: String,
                  @Path("streamId") streamId: String,
                  @Query("count") count: Int): Single<Response<Stream>>

    @GET("streams/{streamId}/contents")
    fun getStreamContinuation(@Header("Authorization") token: String,
                              @Path("streamId") streamId: String,
                              @Query("continuation") continuation: String,
                              @Query("count") count: Int): Single<Response<Stream>>

}