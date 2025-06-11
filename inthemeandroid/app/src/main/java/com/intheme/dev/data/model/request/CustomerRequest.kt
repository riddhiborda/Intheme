package com.intheme.dev.data.model.request

import com.google.gson.annotations.SerializedName

data class CustomerRequest(

	@field:SerializedName("manager_mobile")
	val managerMobile: String? = null,

	@field:SerializedName("operator_email")
	val operatorEmail: String? = null,

	@field:SerializedName("mobileNo")
	val mobileNo: String? = null,

	@field:SerializedName("gstin")
	val gstin: String? = null,

	@field:SerializedName("manager_name")
	val managerName: String? = null,

	@field:SerializedName("operator_name")
	val operatorName: String? = null,

	@field:SerializedName("pin")
	val pin: Int? = null,

	@field:SerializedName("branch_id")
	val branchId: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("address_line_1")
	val addressLine1: String? = null,

	@field:SerializedName("address_line_3")
	val addressLine3: String? = null,

	@field:SerializedName("address_line_2")
	val addressLine2: String? = null,

	@field:SerializedName("manager_email")
	val managerEmail: String? = null,

	@field:SerializedName("email")
	val email: String? = null
)
