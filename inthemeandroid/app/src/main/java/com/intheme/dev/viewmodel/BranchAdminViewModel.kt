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
class BranchAdminViewModel @Inject constructor(val branchAdminRepository: BranchAdminRepository, val newWorkHelper: NetworkHelper) : ViewModel(){

    fun setLogout() {
        branchAdminRepository.setLogout()
    }

    private val createBranchAdminStateFlow: MutableStateFlow<ApiState<BaseStringResponse>> = MutableStateFlow(ApiState.Empty)
    val mCreateBranchAdminStateFlow: StateFlow<ApiState<BaseStringResponse>> = createBranchAdminStateFlow
    fun createBranchAdmin(name: RequestBody, email: RequestBody, mobileNo: RequestBody, branchId: RequestBody, profileImage: MultipartBody.Part?) = viewModelScope.launch {
        createBranchAdminStateFlow.value = ApiState.Loading
        if(newWorkHelper.isNetworkConnected()){
            branchAdminRepository.createBranchAdmin(name,email,mobileNo,branchId,profileImage)
                .catch { e->
                    createBranchAdminStateFlow.value = ApiState.Failure(false,e)
                }.collect {
                    createBranchAdminStateFlow.value = ApiState.Success(it)
                }
        }else{
            createBranchAdminStateFlow.value= ApiState.Failure(true,null)
        }
    }


    private val updateBranchAdminStateFlow: MutableStateFlow<ApiState<BaseStringResponse>> = MutableStateFlow(ApiState.Empty)
    val mUpdateBranchAdminStateFlow: StateFlow<ApiState<BaseStringResponse>> = updateBranchAdminStateFlow
    fun updateBranchAdmin(id:String,name: RequestBody, email: RequestBody, mobileNo: RequestBody, branchId: RequestBody, profileImage: MultipartBody.Part?) = viewModelScope.launch {
        updateBranchAdminStateFlow.value = ApiState.Loading
        if(newWorkHelper.isNetworkConnected()){
            branchAdminRepository.updateBranchAdmin(id,name,email,mobileNo,branchId,profileImage)
                .catch { e->
                    updateBranchAdminStateFlow.value = ApiState.Failure(false,e)
                }.collect {
                    updateBranchAdminStateFlow.value = ApiState.Success(it)
                }
        }else{
            updateBranchAdminStateFlow.value= ApiState.Failure(true,null)
        }
    }

    private val deleteBranchAdminStateFlow: MutableStateFlow<ApiState<BaseStringResponse>> = MutableStateFlow(ApiState.Empty)
    val mDeleteBranchAdminStateFlow: StateFlow<ApiState<BaseStringResponse>> = deleteBranchAdminStateFlow
    fun deleteBranchAdmin(id:String) = viewModelScope.launch {
        deleteBranchAdminStateFlow.value = ApiState.Loading
        if(newWorkHelper.isNetworkConnected()){
            branchAdminRepository.deleteBranchAdmin(id)
                .catch { e->
                    deleteBranchAdminStateFlow.value = ApiState.Failure(false,e)
                }.collect {
                    deleteBranchAdminStateFlow.value = ApiState.Success(it)
                }
        }else{
            deleteBranchAdminStateFlow.value= ApiState.Failure(true,null)
        }
    }


    private val getBranchAdminListStateFlow: MutableStateFlow<ApiState<BaseObjectResponse<BaseListResponse<User>>>> = MutableStateFlow(ApiState.Empty)
    val mGetBranchAdminListStateFlow: StateFlow<ApiState<BaseObjectResponse<BaseListResponse<User>>>> = getBranchAdminListStateFlow
    fun getBranchAdminList(currentPage:Int,limit:Int) = viewModelScope.launch {
        getBranchAdminListStateFlow.value = ApiState.Loading
        if(newWorkHelper.isNetworkConnected()){
            branchAdminRepository.getBranchAdminList(currentPage,limit)
                .catch { e->
                    getBranchAdminListStateFlow.value = ApiState.Failure(false,e)
                }.collect {
                    getBranchAdminListStateFlow.value = ApiState.Success(it)
                }
        }else{
            getBranchAdminListStateFlow.value= ApiState.Failure(true,null)
        }
    }


}