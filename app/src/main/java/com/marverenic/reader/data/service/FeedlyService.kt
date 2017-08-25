package com.marverenic.reader.data.service

import com.marverenic.reader.model.Category
import com.marverenic.reader.model.Stream
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.*

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

    @POST("markers")
    fun markArticles(@Header("Authorization") token: String,
                     @Body request: ArticleMarkerRequest): Single<Response<Unit>>

}