package com.marverenic.reader.data

import android.net.Uri
import com.marverenic.reader.BuildConfig
import com.marverenic.reader.data.service.BASE_URL
import com.marverenic.reader.data.service.FeedlyService
import io.reactivex.Single

class FeedlyAuthenticationManager(
        private val service: FeedlyService,
        private val prefs: PreferenceStore
) : AuthenticationManager {


    private val clientId = BuildConfig.CLIENT_ID
    private val clientSecret = BuildConfig.CLIENT_SECRET
    private val redirectUri = "urn:ietf:wg:oauth:2.0:oob"

    override val loginUrl = BASE_URL + "auth/auth?response_type=code&client_id=$clientId&redirect_uri=${Uri.encode(redirectUri)}&scope=${Uri.encode("https://cloud.feedly.com/subscriptions")}"

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