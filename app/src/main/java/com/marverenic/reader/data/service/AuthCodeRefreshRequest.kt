package com.marverenic.reader.data.service

data class AuthCodeRefreshRequest(
        val refresh_token: String,
        val client_id: String,
        val client_secret: String,
        val grant_type: String = "refresh_token"
)
