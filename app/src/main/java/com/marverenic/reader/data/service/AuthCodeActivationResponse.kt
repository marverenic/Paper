package com.marverenic.reader.data.service

import com.marverenic.reader.model.Seconds

data class AuthCodeActivationResponse(
        val id: String,
        val access_token: String,
        val refresh_token: String,
        val expires_in: Seconds,
        val plan: String
)
