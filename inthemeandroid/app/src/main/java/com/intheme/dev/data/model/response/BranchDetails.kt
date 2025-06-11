package com.intheme.dev.data.model.response

import com.google.gson.annotations.SerializedName

data class BranchDetails(
	@field:SerializedName("branchId")
	val branchId: String? = null,
	@field:SerializedName("branch_name")
	val branchName: String? = null,
	@field:SerializedName("branch_location")
	val branchLocation: String? = null
)
