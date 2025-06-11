package com.intheme.dev.data.model.response

import com.google.gson.annotations.SerializedName

data class EquipmentResponse(

	@field:SerializedName("couple")
	val couple: String? = null,

	@field:SerializedName("equipment_images")
	val equipmentImages: ArrayList<EquipmentImagesItem> = arrayListOf(),

	@field:SerializedName("mechine")
	val mechine: String? = null,

	@field:SerializedName("intack")
	val intack: String? = null,

	@field:SerializedName("isActive")
	val isActive: Boolean? = null,

	@field:SerializedName("model_no")
	val modelNo: String? = null,

	@field:SerializedName("airend")
	val airend: String? = null,

	@field:SerializedName("oil")
	val oil: String? = null,

	@field:SerializedName("thermostat")
	val thermostat: String? = null,

	@field:SerializedName("customerBy")
	val customerBy: CustomerBy? = null,

	@field:SerializedName("temprature_sensor")
	val tempratureSensor: String? = null,

	@field:SerializedName("radiator")
	val radiator: String? = null,

	@field:SerializedName("supplier")
	val supplier: String? = null,

	@field:SerializedName("model")
	val model: String? = null,

	@field:SerializedName("fan_motor")
	val fanMotor: String? = null,

	@field:SerializedName("controller")
	val controller: String? = null,

	@field:SerializedName("main_motor")
	val mainMotor: String? = null,

	@field:SerializedName("oil_filter")
	val oilFilter: String? = null,

	@field:SerializedName("air_filter")
	val airFilter: String? = null,

	@field:SerializedName("inverter")
	val inverter: String? = null,

	@field:SerializedName("separator")
	val separator: String? = null,

	@field:SerializedName("mpv")
	val mpv: String? = null,

	@field:SerializedName("nrv")
	val nrv: String? = null,

	@field:SerializedName("passsword")
	val passsword: String? = null,

	@field:SerializedName("createdBy")
	val createdBy: CreatedBy? = null,

	@field:SerializedName("mechine_history")
	val mechineHistory: String? = null,

	@field:SerializedName("old_service_provider")
	val oldServiceProvider: String? = null,

	@field:SerializedName("pressure_sensor")
	val pressureSensor: String? = null,

	@field:SerializedName("_id")
	val id: String? = null,

	@field:SerializedName("mfg_year")
	val mfgYear: String? = null,

	@field:SerializedName("blockBy")
	val blockBy: BlockBy? = null,

	@field:SerializedName("serial_no")
	val serialNo: String? = null
)

data class CreatedBy(

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("role")
	val role: String? = null,

	@field:SerializedName("id")
	val id: Any? = null
)

data class CustomerBy(

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("mobileNo")
	val mobileNo: Any? = null,

	@field:SerializedName("email")
	val email: Any? = null
)

data class BlockBy(

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("role")
	val role: String? = null,

	@field:SerializedName("id")
	val id: Any? = null
)

data class EquipmentImagesItem(

	@field:SerializedName("image_url")
	val imageUrl: String? = null,

	@field:SerializedName("_id")
	val id: String? = null
)
