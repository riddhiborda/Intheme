package com.intheme.dev.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intheme.dev.data.model.request.LoginRequest
import com.intheme.dev.data.model.request.OtpVerificationRequest
import com.intheme.dev.data.model.response.BaseObjectResponse
import com.intheme.dev.data.model.response.LoginResponse
import com.intheme.dev.data.model.response.OtpVerifyResponse
import com.intheme.dev.data.model.response.User
import com.intheme.dev.data.preference.PrefConstant.Companion.PREF_KEY_USER_DETAIL
import com.intheme.dev.data.repository.AuthRepository
import com.intheme.dev.utils.ApiState
import com.intheme.dev.utils.NetworkHelper
import com.intheme.dev.utils.fromJson
import com.intheme.dev.utils.toJson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(val authRepository: AuthRepository, val newWorkHelper: NetworkHelper) : ViewModel(){

    fun getUserDetail(): User = authRepository.getUserDetail()

    fun setUserDetail(user: User){
        val strUser:String = user.toJson()
        authRepository.setUserDetail(strUser)
    }

    fun setToken(token:String){
        authRepository.setToken(token)
    }

    fun isLogin(): Boolean = authRepository.isLogin()

    fun setIsLogin(isLogin: Boolean){
        authRepository.setIsLogin(isLogin)
    }

    private val loginStateFlow: MutableStateFlow<ApiState<BaseObjectResponse<LoginResponse>>> = MutableStateFlow(ApiState.Empty)
    val mLoginStateFlow: StateFlow<ApiState<BaseObjectResponse<LoginResponse>>> = loginStateFlow
    fun postLogin(loginRequest: LoginRequest) = viewModelScope.launch {
        loginStateFlow.value = ApiState.Loading
        if(newWorkHelper.isNetworkConnected()){
            authRepository.postLoginApi(loginRequest)
                .catch { e->
                    loginStateFlow.value = ApiState.Failure(false,e)
                }.collect {
                    loginStateFlow.value = ApiState.Success(it)
                }
        }else{
            loginStateFlow.value= ApiState.Failure(true,null)
        }
    }


    private val resendOTPStateFlow: MutableStateFlow<ApiState<BaseObjectResponse<LoginResponse>>> = MutableStateFlow(ApiState.Empty)
    val mResendOTPStateFlow: StateFlow<ApiState<BaseObjectResponse<LoginResponse>>> = resendOTPStateFlow
    fun postResendOTP(loginRequest: LoginRequest) = viewModelScope.launch {
        resendOTPStateFlow.value = ApiState.Loading
        if(newWorkHelper.isNetworkConnected()){
            authRepository.postLoginApi(loginRequest)
                .catch { e->
                    resendOTPStateFlow.value = ApiState.Failure(false,e)
                }.collect {
                    resendOTPStateFlow.value = ApiState.Success(it)
                }
        }else{
            resendOTPStateFlow.value= ApiState.Failure(true,null)
        }
    }


    private val verificationOTPStateFlow: MutableStateFlow<ApiState<BaseObjectResponse<OtpVerifyResponse>>> = MutableStateFlow(ApiState.Empty)
    val mVerificationOTPStateFlow: StateFlow<ApiState<BaseObjectResponse<OtpVerifyResponse>>> = verificationOTPStateFlow
    fun postVerificationOTP(otpVerificationRequest: OtpVerificationRequest) = viewModelScope.launch {
        verificationOTPStateFlow.value = ApiState.Loading
        if(newWorkHelper.isNetworkConnected()){
            authRepository.postOTPVerificationApi(otpVerificationRequest)
                .catch { e->
                    verificationOTPStateFlow.value = ApiState.Failure(false,e)
                }.collect {
                    verificationOTPStateFlow.value = ApiState.Success(it)
                }
        }else{
            verificationOTPStateFlow.value= ApiState.Failure(true,null)
        }
    }


}