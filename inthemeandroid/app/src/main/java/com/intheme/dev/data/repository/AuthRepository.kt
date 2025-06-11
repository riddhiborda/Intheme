package com.intheme.dev.data.repository

import com.intheme.dev.data.apiservice.ApiService
import com.intheme.dev.data.model.request.LoginRequest
import com.intheme.dev.data.model.request.OtpVerificationRequest
import com.intheme.dev.data.model.response.BaseObjectResponse
import com.intheme.dev.data.model.response.BaseStringResponse
import com.intheme.dev.data.model.response.LoginResponse
import com.intheme.dev.data.model.response.OtpVerifyResponse
import com.intheme.dev.data.model.response.User
import com.intheme.dev.data.preference.PrefConstant.Companion.PREF_KEY_FCM_TOKEN
import com.intheme.dev.data.preference.PrefConstant.Companion.PREF_KEY_IS_LOGIN
import com.intheme.dev.data.preference.PrefConstant.Companion.PREF_KEY_TOKEN
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
class AuthRepository (val apiService: ApiService, val sharePreferenceManage: SharePreferenceManage) {

    //::::::::::::SharePreference::::::::::://
    fun getUserDetail(): User {
        val user = sharePreferenceManage.getPref(PREF_KEY_USER_DETAIL,"")
        return user.fromJson<User>()
    }

    fun setUserDetail(user: String){
        sharePreferenceManage.setPref(PREF_KEY_USER_DETAIL,user)
    }

    fun setToken(token: String){
        sharePreferenceManage.setPref(PREF_KEY_TOKEN,token)
    }

    fun getToken(): String {
        val token = sharePreferenceManage.getPref(PREF_KEY_TOKEN,"")
        return token
    }

    fun isLogin(): Boolean {
        val user = sharePreferenceManage.getPref(PREF_KEY_IS_LOGIN,false)
        return user
    }

    fun setIsLogin(isLogin: Boolean){
        sharePreferenceManage.setPref(PREF_KEY_IS_LOGIN,isLogin)
    }

    fun setLogoutUser(){
        sharePreferenceManage.clearPref()
    }

    //::::::::::::API Calling::::::::::://
    fun postLoginApi(loginResponse: LoginRequest): Flow<BaseObjectResponse<LoginResponse>> = flow {
        emit(apiService.postLoginApi(loginResponse))
    }.flowOn(Dispatchers.IO)

    fun postOTPVerificationApi(otpVerificationRequest: OtpVerificationRequest): Flow<BaseObjectResponse<OtpVerifyResponse>> = flow {
        emit(apiService.postVerifyOTPApi(otpVerificationRequest))
    }.flowOn(Dispatchers.IO)

    fun putEditProfileApi(id: String, name: RequestBody, email: RequestBody, mobileNo: RequestBody, profileImage: MultipartBody.Part?): Flow<BaseObjectResponse<User>> = flow {
        emit(apiService.putUpdateAdminApi(id,name,email,mobileNo,profileImage))
    }.flowOn(Dispatchers.IO)

    fun putLogoutUserApi(id: String): Flow<BaseStringResponse> = flow {
        emit(apiService.putLogoutUserApi(id))
    }.flowOn(Dispatchers.IO)
}