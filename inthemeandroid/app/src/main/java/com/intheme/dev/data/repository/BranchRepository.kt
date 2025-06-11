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
import javax.inject.Singleton


@Singleton
class BranchRepository (val apiService: ApiService, val sharePreferenceManage: SharePreferenceManage) {

    //::::::::::::SharePreference::::::::::://
    fun getUserDetail(): User {
        val strLogin = sharePreferenceManage.getPref(PREF_KEY_USER_DETAIL,"")
        return  strLogin.fromJson<User>()
    }

    fun setLogout() {
        sharePreferenceManage.clearPref()
    }

    //::::::::::::API Calling::::::::::://
    fun createBranch(branchRequest: BranchRequest): Flow<BaseStringResponse> = flow {
        emit(apiService.createBranch(branchRequest))
    }.flowOn(Dispatchers.IO)

    fun updateBranch(id:String,branchRequest: BranchRequest): Flow<BaseStringResponse> = flow {
        emit(apiService.updateBranch(id,branchRequest))
    }.flowOn(Dispatchers.IO)

    fun deleteBranch(id:String): Flow<BaseStringResponse> = flow {
        emit(apiService.deleteBranch(id))
    }.flowOn(Dispatchers.IO)

    fun getBranchList(currentPage:Int,limit:Int): Flow<BaseObjectResponse<BaseListResponse<BranchResponse>>> = flow {
        emit(apiService.getBranchList(currentPage =currentPage, limit = limit ))
    }.flowOn(Dispatchers.IO)

}