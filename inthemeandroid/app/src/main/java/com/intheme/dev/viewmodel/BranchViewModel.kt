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
import com.intheme.dev.data.repository.AuthRepository
import com.intheme.dev.data.repository.BranchRepository
import com.intheme.dev.utils.ApiState
import com.intheme.dev.utils.NetworkHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BranchViewModel @Inject constructor(val branchRepository: BranchRepository, val newWorkHelper: NetworkHelper) : ViewModel(){

    fun setLogout() {
        branchRepository.setLogout()
    }

    private val createBranchStateFlow: MutableStateFlow<ApiState<BaseStringResponse>> = MutableStateFlow(ApiState.Empty)
    val mCreateBranchStateFlow: StateFlow<ApiState<BaseStringResponse>> = createBranchStateFlow
    fun createBranch(branchRequest: BranchRequest) = viewModelScope.launch {
        createBranchStateFlow.value = ApiState.Loading
        if(newWorkHelper.isNetworkConnected()){
            branchRepository.createBranch(branchRequest)
                .catch { e->
                    createBranchStateFlow.value = ApiState.Failure(false,e)
                }.collect {
                    createBranchStateFlow.value = ApiState.Success(it)
                }
        }else{
            createBranchStateFlow.value= ApiState.Failure(true,null)
        }
    }


    private val updateBranchStateFlow: MutableStateFlow<ApiState<BaseStringResponse>> = MutableStateFlow(ApiState.Empty)
    val mUpdateBranchStateFlow: StateFlow<ApiState<BaseStringResponse>> = updateBranchStateFlow
    fun updateBranch(id:String,branchRequest: BranchRequest) = viewModelScope.launch {
        updateBranchStateFlow.value = ApiState.Loading
        if(newWorkHelper.isNetworkConnected()){
            branchRepository.updateBranch(id,branchRequest)
                .catch { e->
                    updateBranchStateFlow.value = ApiState.Failure(false,e)
                }.collect {
                    updateBranchStateFlow.value = ApiState.Success(it)
                }
        }else{
            updateBranchStateFlow.value= ApiState.Failure(true,null)
        }
    }

    private val deleteBranchStateFlow: MutableStateFlow<ApiState<BaseStringResponse>> = MutableStateFlow(ApiState.Empty)
    val mDeleteBranchStateFlow: StateFlow<ApiState<BaseStringResponse>> = deleteBranchStateFlow
    fun deleteBranch(id:String) = viewModelScope.launch {
        deleteBranchStateFlow.value = ApiState.Loading
        if(newWorkHelper.isNetworkConnected()){
            branchRepository.deleteBranch(id)
                .catch { e->
                    deleteBranchStateFlow.value = ApiState.Failure(false,e)
                }.collect {
                    deleteBranchStateFlow.value = ApiState.Success(it)
                }
        }else{
            deleteBranchStateFlow.value= ApiState.Failure(true,null)
        }
    }


    private val getBranchListStateFlow: MutableStateFlow<ApiState<BaseObjectResponse<BaseListResponse<BranchResponse>>>> = MutableStateFlow(ApiState.Empty)
    val mGetBranchListStateFlow: StateFlow<ApiState<BaseObjectResponse<BaseListResponse<BranchResponse>>>> = getBranchListStateFlow
    fun getBranchList(currentPage:Int,limit:Int) = viewModelScope.launch {
        getBranchListStateFlow.value = ApiState.Loading
        if(newWorkHelper.isNetworkConnected()){
            branchRepository.getBranchList(currentPage,limit)
                .catch { e->
                    getBranchListStateFlow.value = ApiState.Failure(false,e)
                }.collect {
                    getBranchListStateFlow.value = ApiState.Success(it)
                }
        }else{
            getBranchListStateFlow.value= ApiState.Failure(true,null)
        }
    }
}