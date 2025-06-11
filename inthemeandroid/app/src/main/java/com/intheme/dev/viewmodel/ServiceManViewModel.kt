package com.intheme.dev.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import com.intheme.dev.data.repository.AuthRepository
import com.intheme.dev.data.repository.BranchAdminRepository
import com.intheme.dev.data.repository.BranchRepository
import com.intheme.dev.data.repository.ServiceManRepository
import com.intheme.dev.utils.ApiState
import com.intheme.dev.utils.NetworkHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class ServiceManViewModel @Inject constructor(val serviceManRepository: ServiceManRepository, val newWorkHelper: NetworkHelper) : ViewModel(){

    fun setLogout() {
        serviceManRepository.setLogout()
    }

    private val createServiceManStateFlow: MutableStateFlow<ApiState<BaseStringResponse>> = MutableStateFlow(ApiState.Empty)
    val mCreateServiceManStateFlow: StateFlow<ApiState<BaseStringResponse>> = createServiceManStateFlow
    fun createServiceMan(name: RequestBody, email: RequestBody, mobileNo: RequestBody, branchId: RequestBody, profileImage: MultipartBody.Part?) = viewModelScope.launch {
        createServiceManStateFlow.value = ApiState.Loading
        if(newWorkHelper.isNetworkConnected()){
            serviceManRepository.createServiceMan(name,email,mobileNo,branchId,profileImage)
                .catch { e->
                    createServiceManStateFlow.value = ApiState.Failure(false,e)
                }.collect {
                    createServiceManStateFlow.value = ApiState.Success(it)
                }
        }else{
            createServiceManStateFlow.value= ApiState.Failure(true,null)
        }
    }


    private val updateServiceManStateFlow: MutableStateFlow<ApiState<BaseStringResponse>> = MutableStateFlow(ApiState.Empty)
    val mUpdateServiceManStateFlow: StateFlow<ApiState<BaseStringResponse>> = updateServiceManStateFlow
    fun updateServiceMan(id:String,name: RequestBody, email: RequestBody, mobileNo: RequestBody, branchId: RequestBody, profileImage: MultipartBody.Part?) = viewModelScope.launch {
        updateServiceManStateFlow.value = ApiState.Loading
        if(newWorkHelper.isNetworkConnected()){
            serviceManRepository.updateServiceMan(id,name,email,mobileNo,branchId,profileImage)
                .catch { e->
                    updateServiceManStateFlow.value = ApiState.Failure(false,e)
                }.collect {
                    updateServiceManStateFlow.value = ApiState.Success(it)
                }
        }else{
            updateServiceManStateFlow.value= ApiState.Failure(true,null)
        }
    }

    private val deleteServiceManStateFlow: MutableStateFlow<ApiState<BaseStringResponse>> = MutableStateFlow(ApiState.Empty)
    val mDeleteServiceManStateFlow: StateFlow<ApiState<BaseStringResponse>> = deleteServiceManStateFlow
    fun deleteServiceMan(id:String) = viewModelScope.launch {
        deleteServiceManStateFlow.value = ApiState.Loading
        if(newWorkHelper.isNetworkConnected()){
            serviceManRepository.deleteServiceMan(id)
                .catch { e->
                    deleteServiceManStateFlow.value = ApiState.Failure(false,e)
                }.collect {
                    deleteServiceManStateFlow.value = ApiState.Success(it)
                }
        }else{
            deleteServiceManStateFlow.value= ApiState.Failure(true,null)
        }
    }


    private val getServiceManStateFlow: MutableStateFlow<ApiState<BaseObjectResponse<BaseListResponse<User>>>> = MutableStateFlow(ApiState.Empty)
    val mGetServiceManListStateFlow: StateFlow<ApiState<BaseObjectResponse<BaseListResponse<User>>>> = getServiceManStateFlow
    fun getServiceManList(currentPage:Int,limit:Int) = viewModelScope.launch {
        getServiceManStateFlow.value = ApiState.Loading
        if(newWorkHelper.isNetworkConnected()){
            serviceManRepository.getServiceManList(currentPage,limit)
                .catch { e->
                    getServiceManStateFlow.value = ApiState.Failure(false,e)
                }.collect {
                    getServiceManStateFlow.value = ApiState.Success(it)
                }
        }else{
            getServiceManStateFlow.value= ApiState.Failure(true,null)
        }
    }


}