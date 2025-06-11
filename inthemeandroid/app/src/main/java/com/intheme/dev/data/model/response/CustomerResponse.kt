package com.intheme.dev.data.model.response

import com.google.gson.annotations.SerializedName

data class CustomerResponse(

	@field:SerializedName("manager_mobile")
	val managerMobile: String? = null,

	@field:SerializedName("operator_email")
	val operatorEmail: String? = null,

	@field:SerializedName("mobileNo")
	val mobileNo: Long? = null,

	@field:SerializedName("gstin")
	val gstin: String? = null,

	@field:SerializedName("isActive")
	val isActive: Boolean? = null,

	@field:SerializedName("manager_name")
	val managerName: String? = null,

	@field:SerializedName("operator_name")
	val operatorName: String? = null,

	@field:SerializedName("pin")
	val pin: Int? = null,

	@field:SerializedName("createdBy")
	val createdBy: User? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("address_line_1")
	val addressLine1: String? = null,

	@field:SerializedName("address_line_3")
	val addressLine3: String? = null,

	@field:SerializedName("_id")
	val id: String? = null,

	@field:SerializedName("address_line_2")
	val addressLine2: String? = null,

	@field:SerializedName("manager_email")
	val managerEmail: String? = null,

	@field:SerializedName("blockBy")
	val blockBy: User? = null,

	@field:SerializedName("branchDetails")
	val branchDetails: BranchDetails? = null,

	@field:SerializedName("email")
	val email: String? = null
)






