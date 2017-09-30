package com.marverenic.reader.data

import com.marverenic.reader.BuildConfig
import io.reactivex.Single

class DevAuthenticationManager: AuthenticationManager {

    override val loginUrl: String
        get() = throw UnsupportedOperationException()

    private val authToken: String? = BuildConfig.DEV_OAUTH_TOKEN
    private val userId: String? = BuildConfig.DEV_USER_ID

    override fun isLoggedIn() = Single.just(true)

    override fun getFeedlyAuthToken(): Single<String> {
        return authToken.takeIf { !it.isNullOrBlank() }?.let { Single.just(it) }
                ?: throw RuntimeException("Invalid dev oauth key")
    }

    override fun getFeedlyUserId(): Single<String> {
        return userId.takeIf { !it.isNullOrBlank() }?.let { Single.just(it) }
                ?: throw RuntimeException("Invalid dev username")
    }

    override fun logIn(callbackUrl: String) {
        throw UnsupportedOperationException()
    }

}
