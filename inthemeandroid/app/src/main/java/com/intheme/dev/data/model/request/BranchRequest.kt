package com.intheme.dev.data.model.request

import com.google.gson.annotations.SerializedName

data class BranchRequest(

	@field:SerializedName("branch_name")
	var branchName: String? = null,

	@field:SerializedName("branch_location")
	var branchLocation: String? = null
)
