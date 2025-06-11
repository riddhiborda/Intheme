package com.intheme.dev.data.model.request

import com.google.gson.annotations.SerializedName

data class OtpVerificationRequest(
	@field:SerializedName("email")
	var email: String? = null,
	@field:SerializedName("otp")
	var otp: String? = null,
	@field:SerializedName("token")
	var token: String? = null,
	@field:SerializedName("deviceId")
	var deviceId: String? = null
)
