package com.marverenic.reader.data.service

data class AuthCodeActivationRequest(
        val code: String,
        val client_id: String,
        val client_secret: String,
        val redirect_uri: String,
        val grant_type: String = "authorization_code"
)
