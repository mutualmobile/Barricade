package com.mutualmobile.barricadeSample.states

sealed class ResponseState<out T> {
    object Empty : ResponseState<Nothing>()
    object Loading : ResponseState<Nothing>()
    class Success<T>(val data: T) : ResponseState<T>()
    class Failure(val reason: String) : ResponseState<Nothing>()
}
