package com.intheme.dev.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import com.intheme.dev.data.repository.AuthRepository
import com.intheme.dev.data.repository.BranchRepository
import com.intheme.dev.data.repository.CustomerRepository
import com.intheme.dev.utils.ApiState
import com.intheme.dev.utils.NetworkHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomerViewModel @Inject constructor(val customerRepository: CustomerRepository, val newWorkHelper: NetworkHelper) : ViewModel(){

    fun getUserDetail(): User = customerRepository.getUserDetail()

    fun setLogout() {
        customerRepository.setLogout()
    }

    private val _createAndUpdateCustomerStateFlow: MutableStateFlow<ApiState<BaseStringResponse>> = MutableStateFlow(ApiState.Empty)
    val mCreateAndUpdateCustomerStateFlow: StateFlow<ApiState<BaseStringResponse>> = _createAndUpdateCustomerStateFlow

    fun addAndUpdateCustomer(customerRequest: CustomerRequest,strId:String="") = viewModelScope.launch {
        if (!newWorkHelper.isNetworkConnected()) {
            _createAndUpdateCustomerStateFlow.value = ApiState.Failure(isNetworkError = true, null)
            return@launch
        }
        Log.e("ApiState===>", "Id : $strId")
        customerRepository.addAndUpdateCustomer(customerRequest,strId)
            .onStart { _createAndUpdateCustomerStateFlow.value = ApiState.Loading }
            .catch { e -> _createAndUpdateCustomerStateFlow.value = ApiState.Failure(isNetworkError = false,  e)}
            .collect { response -> _createAndUpdateCustomerStateFlow.value = ApiState.Success(response) }
    }

    private val _deleteCustomerStateFlow: MutableStateFlow<ApiState<BaseStringResponse>> = MutableStateFlow(ApiState.Empty)
    val mDeleteCustomerStateFlow: StateFlow<ApiState<BaseStringResponse>> = _deleteCustomerStateFlow

    fun deleteCustomer(strId:String="") = viewModelScope.launch {
        if (!newWorkHelper.isNetworkConnected()) {
            _deleteCustomerStateFlow.value = ApiState.Failure(isNetworkError = true, null)
            return@launch
        }
        customerRepository.deleteCustomer(strId)
            .onStart { _deleteCustomerStateFlow.value = ApiState.Loading }
            .catch { e -> _deleteCustomerStateFlow.value = ApiState.Failure(isNetworkError = false,  e)}
            .collect { response -> _deleteCustomerStateFlow.value = ApiState.Success(response) }
    }


    private val _getCustomerListStateFlow: MutableStateFlow<ApiState<BaseObjectResponse<BaseListResponse<CustomerResponse>>>> = MutableStateFlow(ApiState.Empty)
    val mGetCustomerListStateFlow: StateFlow<ApiState<BaseObjectResponse<BaseListResponse<CustomerResponse>>>> = _getCustomerListStateFlow
    fun getCustomerList(currentPage:Int=0) = viewModelScope.launch {
        if (!newWorkHelper.isNetworkConnected()) {
            _getCustomerListStateFlow.value = ApiState.Failure(isNetworkError = true, null)
            return@launch
        }
        customerRepository.getCustomerList(currentPage)
            .onStart { _getCustomerListStateFlow.value = ApiState.Loading }
            .catch { e -> _getCustomerListStateFlow.value = ApiState.Failure(isNetworkError = false,  e)}
            .collect { response -> _getCustomerListStateFlow.value = ApiState.Success(response) }
    }

}