package com.intheme.dev.data.model.response

import com.google.gson.annotations.SerializedName

data class BranchResponse(
	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("deletedAt")
	val deletedAt: Any? = null,

	@field:SerializedName("branch_name")
	val branchName: String? = null,

	@field:SerializedName("branch_location")
	val branchLocation: String? = null,

	@field:SerializedName("_id")
	val id: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)
