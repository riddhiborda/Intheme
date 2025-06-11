package com.intheme.dev.data.model.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(
	@field:SerializedName("otp")
	val otp: Int? = null
)
