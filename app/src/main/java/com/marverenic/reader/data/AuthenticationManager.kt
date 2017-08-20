package com.marverenic.reader.data

interface AuthenticationManager {

    fun isLoggedIn(): Boolean

    fun getFeedlyAuthToken(): String

}