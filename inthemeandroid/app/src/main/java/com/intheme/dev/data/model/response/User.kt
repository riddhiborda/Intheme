package com.intheme.dev.data.model.response

import com.google.gson.annotations.SerializedName

data class User(
	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("role")
	val role: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("mobileNo")
	val mobileNo: String? = null,

	@field:SerializedName("_id")
	val id: String? = null,

	@field:SerializedName("isActive")
	val isActive: Boolean? = null,

	@field:SerializedName("branchDetails")
	val branchDetails: BranchDetails? = null,

	@field:SerializedName("email")
	val email: String? = null
)