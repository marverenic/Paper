package com.marverenic.reader.data

import android.content.SharedPreferences
import com.marverenic.reader.model.Timestamp

class PreferenceStore(private val prefs: SharedPreferences) {

    companion object {
        private const val USER_ID_KEY = "userId"
        private const val USER_TOKEN_KEY = "authToken"
        private const val REFRESH_TOKEN_KEY = "refreshToken"
        private const val USER_TOKEN_EXPIRATION_KEY = "authTokenExpirationTimestamp"
    }

    var userId: String?
        set(value) = prefs.edit().putString(USER_ID_KEY, value).apply()
        get() = prefs.getString(USER_ID_KEY, null)

    var authToken: String?
        set(value) = prefs.edit().putString(USER_TOKEN_KEY, value).apply()
        get() = prefs.getString(USER_TOKEN_KEY, null)

    var refreshToken: String?
        set(value) = prefs.edit().putString(REFRESH_TOKEN_KEY, value).apply()
        get() = prefs.getString(REFRESH_TOKEN_KEY, null)

    var authTokenExpirationTimestamp: Timestamp
        set(value) = prefs.edit().putLong(USER_TOKEN_EXPIRATION_KEY, value).apply()
        get() = prefs.getLong(USER_TOKEN_EXPIRATION_KEY, 0)

}