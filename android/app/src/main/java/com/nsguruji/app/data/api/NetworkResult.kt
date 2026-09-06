package com.nsguruji.app.data.api

sealed class NetworkResult<out T> {
    data class Success<out T>(val data: T, val totalCount: Int = 0, val totalPages: Int = 1) : NetworkResult<T>()
    data class Error(val message: String, val throwable: Throwable? = null) : NetworkResult<Nothing>()
    object Loading : NetworkResult<Nothing>()
}
