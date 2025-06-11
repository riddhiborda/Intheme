package com.intheme.dev.data.model.response

import com.google.gson.annotations.SerializedName

data class OtpVerifyResponse(
	@field:SerializedName("tokens")
	val tokens: String? = null,

	@field:SerializedName("refreshToken")
	val refreshToken: String? = null,

	@field:SerializedName("user")
	val user: User? = null
)



