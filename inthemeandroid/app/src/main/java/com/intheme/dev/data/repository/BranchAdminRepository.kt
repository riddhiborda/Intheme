package com.intheme.dev.data.repository

import com.intheme.dev.data.apiservice.ApiService
import com.intheme.dev.data.model.request.BranchRequest
import com.intheme.dev.data.model.request.LoginRequest
import com.intheme.dev.data.model.request.OtpVerificationRequest
import com.intheme.dev.data.model.response.BaseListResponse
import com.intheme.dev.data.model.response.BaseObjectResponse
import com.intheme.dev.data.model.response.BaseStringResponse
import com.intheme.dev.data.model.response.BranchResponse
import com.intheme.dev.data.model.response.LoginResponse
import com.intheme.dev.data.model.response.OtpVerifyResponse
import com.intheme.dev.data.model.response.User
import com.intheme.dev.data.preference.SharePreferenceManage
import com.intheme.dev.data.preference.PrefConstant.Companion.PREF_KEY_USER_DETAIL
import com.intheme.dev.utils.fromJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Singleton


@Singleton
class BranchAdminRepository (val apiService: ApiService, val sharePreferenceManage: SharePreferenceManage) {

    //::::::::::::SharePreference::::::::::://
    fun getUserDetail(): User {
        val strLogin = sharePreferenceManage.getPref(PREF_KEY_USER_DETAIL,"")
        return  strLogin.fromJson<User>()
    }

    fun setLogout() {
        sharePreferenceManage.clearPref()
    }

    //::::::::::::API Calling::::::::::://
    fun createBranchAdmin(name: RequestBody, email: RequestBody, mobileNo: RequestBody,branchId: RequestBody, profileImage: MultipartBody.Part?): Flow<BaseStringResponse> = flow {
        emit(apiService.createBranchAdmin(name,email,mobileNo,branchId,profileImage))
    }.flowOn(Dispatchers.IO)

    fun updateBranchAdmin(id:String,name: RequestBody, email: RequestBody, mobileNo: RequestBody,branchId: RequestBody, profileImage: MultipartBody.Part?): Flow<BaseStringResponse> = flow {
        emit(apiService.updateBranchAdmin(id,name,email,mobileNo,branchId,profileImage))
    }.flowOn(Dispatchers.IO)

    fun deleteBranchAdmin(id:String): Flow<BaseStringResponse> = flow {
        emit(apiService.deleteBranchAdmin(id))
    }.flowOn(Dispatchers.IO)

    fun getBranchAdminList(currentPage:Int,limit:Int): Flow<BaseObjectResponse<BaseListResponse<User>>> = flow {
        emit(apiService.getBranchAdminList(currentPage =currentPage, limit = limit ))
    }.flowOn(Dispatchers.IO)

}