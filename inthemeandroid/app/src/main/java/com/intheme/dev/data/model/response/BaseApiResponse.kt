package com.intheme.dev.data.model.response
import com.google.gson.annotations.SerializedName




data class BaseListResponse<T>(
    @field:SerializedName("data")
    val data: List<T>? = null,
    @field:SerializedName("lastPage")
    val lastPage: Int? = null,
    @field:SerializedName("totalCount")
    val totalCount: Int? = null,
)

data class BaseObjectResponse<T>(
    @field:SerializedName("message") val message: String,
    @field:SerializedName("data") val data: T,
    @field:SerializedName("code") val status: Int
)

data class BaseStringResponse(
    @field:SerializedName("code") val code: Int,
    @field:SerializedName("message") val message: String,
)

