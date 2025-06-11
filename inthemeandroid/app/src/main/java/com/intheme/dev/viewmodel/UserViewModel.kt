package com.intheme.dev.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intheme.dev.data.model.request.LoginRequest
import com.intheme.dev.data.model.request.OtpVerificationRequest
import com.intheme.dev.data.model.response.BaseObjectResponse
import com.intheme.dev.data.model.response.BaseStringResponse
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
class UserViewModel @Inject constructor(val authRepository: AuthRepository, val newWorkHelper: NetworkHelper) : ViewModel(){
    //::::::::::::SharePreference::::::::::://
    fun getUserDetail(): User = authRepository.getUserDetail()

    fun setUserDetail(user: User){
        val strUser:String = user.toJson()
        authRepository.setUserDetail(strUser)
    }

    fun logoutUser() = authRepository.setLogoutUser()

    private val logoutStateFlow: MutableStateFlow<ApiState<BaseStringResponse>> = MutableStateFlow(ApiState.Empty)
    val mLogoutStateFlow: StateFlow<ApiState<BaseStringResponse>> = logoutStateFlow
    fun postLogout(id: String) = viewModelScope.launch {
        logoutStateFlow.value = ApiState.Loading
        if(newWorkHelper.isNetworkConnected()){
            authRepository.putLogoutUserApi(id)
                .catch { e->
                    logoutStateFlow.value = ApiState.Failure(false,e)
                }.collect {
                    logoutStateFlow.value = ApiState.Success(it)
                }
        }else{
            logoutStateFlow.value= ApiState.Failure(true,null)
        }
    }


    private val updateProfileStateFlow: MutableStateFlow<ApiState<BaseObjectResponse<User>>> = MutableStateFlow(ApiState.Empty)
    val mUpdateProfileStateFlow: StateFlow<ApiState<BaseObjectResponse<User>>> = updateProfileStateFlow
    fun putEditProfile(id: String, name: RequestBody, email: RequestBody, mobileNo: RequestBody, profileImage: MultipartBody.Part?) = viewModelScope.launch {
        updateProfileStateFlow.value = ApiState.Loading
        if(newWorkHelper.isNetworkConnected()){
            authRepository.putEditProfileApi(id,name,email,mobileNo,profileImage)
                .catch { e->
                    updateProfileStateFlow.value = ApiState.Failure(false,e)
                }.collect {
                    updateProfileStateFlow.value = ApiState.Success(it)
                }
        }else{
            updateProfileStateFlow.value= ApiState.Failure(true,null)
        }
    }
}