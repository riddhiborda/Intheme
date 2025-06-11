package com.intheme.dev.data.model.request

import com.google.gson.annotations.SerializedName

data class RefreshTokenRequest(
    @field:SerializedName("refreshToken")
    var refreshToken: String? = null
)
