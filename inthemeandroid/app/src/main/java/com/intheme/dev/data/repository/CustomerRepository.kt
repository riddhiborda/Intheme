package com.intheme.dev.data.repository

import com.intheme.dev.data.apiservice.ApiService
import com.intheme.dev.data.model.request.BranchRequest
import com.intheme.dev.data.model.request.CustomerRequest
import com.intheme.dev.data.model.request.LoginRequest
import com.intheme.dev.data.model.request.OtpVerificationRequest
import com.intheme.dev.data.model.response.BaseListResponse
import com.intheme.dev.data.model.response.BaseObjectResponse
import com.intheme.dev.data.model.response.BaseStringResponse
import com.intheme.dev.data.model.response.BranchResponse
import com.intheme.dev.data.model.response.CustomerResponse
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
class CustomerRepository (val apiService: ApiService, val sharePreferenceManage: SharePreferenceManage) {

    //::::::::::::SharePreference::::::::::://
    fun getUserDetail(): User {
        val strLogin = sharePreferenceManage.getPref(PREF_KEY_USER_DETAIL,"")
        return strLogin.fromJson<User>()
    }

    fun setLogout() {
        sharePreferenceManage.clearPref()
    }

    //::::::::::::API Calling::::::::::://
    fun addAndUpdateCustomer(customer:CustomerRequest,strId:String=""): Flow<BaseStringResponse> = flow {
        if(strId.isEmpty()){
            emit(apiService.addCustomer(customerRequest =customer))
        }else{
            emit(apiService.updateCustomer(id = strId,customerRequest = customer))
        }
    }.flowOn(Dispatchers.IO)


    fun deleteCustomer(strId:String=""): Flow<BaseStringResponse> = flow {
        emit(apiService.deleteCustomer(id = strId))
    }.flowOn(Dispatchers.IO)


    fun getCustomerList(currentPage:Int=0): Flow<BaseObjectResponse<BaseListResponse<CustomerResponse>>> = flow {
        emit(apiService.getCustomerList(currentPage =currentPage, limit = 10 ))
    }.flowOn(Dispatchers.IO)



}