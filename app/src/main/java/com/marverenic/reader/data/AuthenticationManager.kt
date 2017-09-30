package com.marverenic.reader.data

import io.reactivex.Single

interface AuthenticationManager {

    val loginUrl: String

    fun isLoggedIn(): Single<Boolean>

    fun getFeedlyAuthToken(): Single<String>

    fun getFeedlyUserId(): Single<String>

    fun logIn(callbackUrl: String)

}