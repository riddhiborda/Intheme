package com.intheme.dev.utils

sealed class ApiState<out T : Any> {
    data object Loading : ApiState<Nothing>()
    data class Failure(val isNetworkError: Boolean,val msg:Throwable?) : ApiState<Nothing>()
    data class Success<out T : Any>(val data:T) : ApiState<T>()
    data object Empty : ApiState<Nothing>()
}

