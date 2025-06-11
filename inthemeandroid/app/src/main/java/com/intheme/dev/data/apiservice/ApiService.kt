package com.intheme.dev.data.apiservice


import com.intheme.dev.data.apiservice.ApiConstant.Companion.ADD_BRANCH_ADMIN_END_POINT
import com.intheme.dev.data.apiservice.ApiConstant.Companion.ADD_BRANCH_END_POINT
import com.intheme.dev.data.apiservice.ApiConstant.Companion.ADD_CUSTOMER_END_POINT
import com.intheme.dev.data.apiservice.ApiConstant.Companion.ADD_SERVICE_MAN_END_POINT
import com.intheme.dev.data.apiservice.ApiConstant.Companion.DELETE_BRANCH_ADMIN_END_POINT
import com.intheme.dev.data.apiservice.ApiConstant.Companion.DELETE_BRANCH_END_POINT
import com.intheme.dev.data.apiservice.ApiConstant.Companion.DELETE_CUSTOMER_END_POINT
import com.intheme.dev.data.apiservice.ApiConstant.Companion.DELETE_EQUIPMENT_END_POINT
import com.intheme.dev.data.apiservice.ApiConstant.Companion.DELETE_SERVICE_MAN_END_POINT
import com.intheme.dev.data.apiservice.ApiConstant.Companion.GET_BRANCH_ADMIN_LIST_END_POINT
import com.intheme.dev.data.apiservice.ApiConstant.Companion.GET_BRANCH_LIST_END_POINT
import com.intheme.dev.data.apiservice.ApiConstant.Companion.GET_CUSTOMER_LIST_END_POINT
import com.intheme.dev.data.apiservice.ApiConstant.Companion.GET_EQUIPMENT_LIST_END_POINT
import com.intheme.dev.data.apiservice.ApiConstant.Companion.GET_SERVICE_MAN_LIST_END_POINT
import com.intheme.dev.data.apiservice.ApiConstant.Companion.LOGIN_API_END_POINT
import com.intheme.dev.data.apiservice.ApiConstant.Companion.LOGOUT_API_END_POINT
import com.intheme.dev.data.apiservice.ApiConstant.Companion.PARAM_BRANCH_ID
import com.intheme.dev.data.apiservice.ApiConstant.Companion.PARAM_EMAIL
import com.intheme.dev.data.apiservice.ApiConstant.Companion.PARAM_ID
import com.intheme.dev.data.apiservice.ApiConstant.Companion.PARAM_LIMIT
import com.intheme.dev.data.apiservice.ApiConstant.Companion.PARAM_MOBILE_NO
import com.intheme.dev.data.apiservice.ApiConstant.Companion.PARAM_NAME
import com.intheme.dev.data.apiservice.ApiConstant.Companion.PARAM_PAGE
import com.intheme.dev.data.apiservice.ApiConstant.Companion.REFRESH_TOKEN_END_POINT
import com.intheme.dev.data.apiservice.ApiConstant.Companion.UPDATE_ADMIN_END_POINT
import com.intheme.dev.data.apiservice.ApiConstant.Companion.UPDATE_BRANCH_ADMIN_END_POINT
import com.intheme.dev.data.apiservice.ApiConstant.Companion.UPDATE_BRANCH_END_POINT
import com.intheme.dev.data.apiservice.ApiConstant.Companion.UPDATE_CUSTOMER_END_POINT
import com.intheme.dev.data.apiservice.ApiConstant.Companion.UPDATE_SERVICE_MAN_END_POINT
import com.intheme.dev.data.apiservice.ApiConstant.Companion.VERIFY_OTP_END_POINT
import com.intheme.dev.data.model.request.BranchRequest
import com.intheme.dev.data.model.request.CustomerRequest
import com.intheme.dev.data.model.request.LoginRequest
import com.intheme.dev.data.model.request.OtpVerificationRequest
import com.intheme.dev.data.model.request.RefreshTokenRequest
import com.intheme.dev.data.model.response.BaseListResponse
import com.intheme.dev.data.model.response.BaseObjectResponse
import com.intheme.dev.data.model.response.BaseStringResponse
import com.intheme.dev.data.model.response.BranchResponse
import com.intheme.dev.data.model.response.CustomerResponse
import com.intheme.dev.data.model.response.EquipmentResponse
import com.intheme.dev.data.model.response.LoginResponse
import com.intheme.dev.data.model.response.OtpVerifyResponse
import com.intheme.dev.data.model.response.User

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap


interface ApiService {

    @POST(LOGIN_API_END_POINT)
    suspend fun postLoginApi(@Body loginRequest: LoginRequest): BaseObjectResponse<LoginResponse>

    @POST(VERIFY_OTP_END_POINT)
    suspend fun postVerifyOTPApi(@Body otpVerificationRequest: OtpVerificationRequest): BaseObjectResponse<OtpVerifyResponse>

    @PUT("$LOGOUT_API_END_POINT/{$PARAM_ID}")
    suspend fun putLogoutUserApi(@Path(PARAM_ID) id: String): BaseStringResponse

    @POST(REFRESH_TOKEN_END_POINT)
    fun refreshToken(@Body request: RefreshTokenRequest): Call<BaseObjectResponse<OtpVerifyResponse>>

    @Multipart
    @PUT("$UPDATE_ADMIN_END_POINT/{$PARAM_ID}")
    suspend fun putUpdateAdminApi(
        @Path(PARAM_ID) id: String,
        @Part(PARAM_NAME) name: RequestBody,
        @Part(PARAM_EMAIL) email: RequestBody,
        @Part(PARAM_MOBILE_NO) mobileNo: RequestBody,
        @Part image: MultipartBody.Part?
    ): BaseObjectResponse<User>

    @GET(GET_BRANCH_LIST_END_POINT)
    suspend fun getBranchList(@Query(PARAM_PAGE) currentPage:Int, @Query(PARAM_LIMIT) limit:Int): BaseObjectResponse<BaseListResponse<BranchResponse>>

    @POST(ADD_BRANCH_END_POINT)
    suspend fun createBranch(@Body branchRequest: BranchRequest): BaseStringResponse

    @PUT("$UPDATE_BRANCH_END_POINT/{$PARAM_ID}")
    suspend fun updateBranch(@Path(PARAM_ID) id: String, @Body branchRequest: BranchRequest): BaseStringResponse

    @DELETE("$DELETE_BRANCH_END_POINT/{$PARAM_ID}")
    suspend fun deleteBranch(@Path(PARAM_ID) id: String): BaseStringResponse


    @GET(GET_BRANCH_ADMIN_LIST_END_POINT)
    suspend fun getBranchAdminList(@Query(PARAM_PAGE) currentPage:Int, @Query(PARAM_LIMIT) limit:Int): BaseObjectResponse<BaseListResponse<User>>

    @Multipart
    @POST(ADD_BRANCH_ADMIN_END_POINT)
    suspend fun createBranchAdmin(
        @Part(PARAM_NAME) name: RequestBody,
        @Part(PARAM_EMAIL) email: RequestBody,
        @Part(PARAM_MOBILE_NO) mobileNo: RequestBody,
        @Part(PARAM_BRANCH_ID) branchId: RequestBody,
        @Part image: MultipartBody.Part?
    ): BaseStringResponse

    @Multipart
    @PUT("$UPDATE_BRANCH_ADMIN_END_POINT/{$PARAM_ID}")
    suspend fun updateBranchAdmin(
        @Path(PARAM_ID) id: String,
        @Part(PARAM_NAME) name: RequestBody,
        @Part(PARAM_EMAIL) email: RequestBody,
        @Part(PARAM_MOBILE_NO) mobileNo: RequestBody,
        @Part(PARAM_BRANCH_ID) branchId: RequestBody,
        @Part image: MultipartBody.Part?
    ): BaseStringResponse

    @DELETE("$DELETE_BRANCH_ADMIN_END_POINT/{$PARAM_ID}")
    suspend fun deleteBranchAdmin(@Path(PARAM_ID) id: String): BaseStringResponse


    @GET(GET_SERVICE_MAN_LIST_END_POINT)
    suspend fun getServiceManList(@Query(PARAM_PAGE) currentPage:Int, @Query(PARAM_LIMIT) limit:Int): BaseObjectResponse<BaseListResponse<User>>

    @Multipart
    @POST(ADD_SERVICE_MAN_END_POINT)
    suspend fun createServiceMan(
        @Part(PARAM_NAME) name: RequestBody,
        @Part(PARAM_EMAIL) email: RequestBody,
        @Part(PARAM_MOBILE_NO) mobileNo: RequestBody,
        @Part(PARAM_BRANCH_ID) branchId: RequestBody,
        @Part image: MultipartBody.Part?
    ): BaseStringResponse

    @Multipart
    @PUT("$UPDATE_SERVICE_MAN_END_POINT/{$PARAM_ID}")
    suspend fun updateServiceMan(
        @Path(PARAM_ID) id: String,
        @Part(PARAM_NAME) name: RequestBody,
        @Part(PARAM_EMAIL) email: RequestBody,
        @Part(PARAM_MOBILE_NO) mobileNo: RequestBody,
        @Part(PARAM_BRANCH_ID) branchId: RequestBody,
        @Part image: MultipartBody.Part?
    ): BaseStringResponse

    @DELETE("$DELETE_SERVICE_MAN_END_POINT/{$PARAM_ID}")
    suspend fun deleteServiceMan(@Path(PARAM_ID) id: String): BaseStringResponse

    @POST(ADD_CUSTOMER_END_POINT)
    suspend fun addCustomer(@Body customerRequest: CustomerRequest): BaseStringResponse

    @PUT("$UPDATE_CUSTOMER_END_POINT/{$PARAM_ID}")
    suspend fun updateCustomer(@Path(PARAM_ID) id: String,@Body customerRequest: CustomerRequest): BaseStringResponse

    @DELETE("$DELETE_CUSTOMER_END_POINT/{$PARAM_ID}")
    suspend fun deleteCustomer(@Path(PARAM_ID) id: String): BaseStringResponse

    @GET(GET_CUSTOMER_LIST_END_POINT)
    suspend fun getCustomerList(@Query(PARAM_PAGE) currentPage:Int, @Query(PARAM_LIMIT) limit:Int): BaseObjectResponse<BaseListResponse<CustomerResponse>>

    @GET(GET_EQUIPMENT_LIST_END_POINT)
    suspend fun getEquipmentList(@QueryMap params: Map<String, Any>): BaseObjectResponse<BaseListResponse<EquipmentResponse>>

    @DELETE("$DELETE_EQUIPMENT_END_POINT/{$PARAM_ID}")
    suspend fun deleteEquipment(@Path(PARAM_ID) id: String): BaseStringResponse

}