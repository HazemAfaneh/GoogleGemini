package com.hazem.googlegemini.app

sealed class ResultData<T> {
    data class Success<T>(val data: T) : ResultData<T>()
    data class Error<T>(val errorMessage: String) : ResultData<T>()
}

inline fun <reified T, reified A> ResultData<in T>.doIfSuccess(callback: (T) -> A): ResultData<A> {

    return when (val result = this) {
        is ResultData.Success -> ResultData.Success(callback(result.data as T))
        else -> this as ResultData<A>
    }
}

inline fun <T> ResultData<T>.doIfFailure(callback: (String) -> T): ResultData<T> {
    return when (val result = this) {
        is ResultData.Error -> {
            callback(result.errorMessage)
            result
        }

        else -> this as ResultData.Success
    }
}

