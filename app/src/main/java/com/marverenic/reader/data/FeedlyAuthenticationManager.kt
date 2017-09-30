package com.marverenic.reader.data

import com.marverenic.reader.data.service.FeedlyService
import io.reactivex.Single

class FeedlyAuthenticationManager(
        private val service: FeedlyService,
        private val prefs: PreferenceStore
) : AuthenticationManager {

    // TODO: Check if a user has revoked our auth token
    override fun isLoggedIn() = Single.just(prefs.userId != null)

    override fun getFeedlyAuthToken(): Single<String> {
        TODO("not implemented")
    }

    override fun getFeedlyUserId() = prefs.userId?.let { Single.just(it) }
            ?: throw IllegalStateException("User is not logged in")

    override fun logIn(callbackUrl: String) {
        TODO("not implemented")
    }

}