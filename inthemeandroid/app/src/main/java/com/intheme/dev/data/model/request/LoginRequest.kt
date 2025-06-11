package com.intheme.dev.data.model.request

import com.google.gson.annotations.SerializedName

data class LoginRequest(
	@field:SerializedName("email")
	var email: String? = null
)
