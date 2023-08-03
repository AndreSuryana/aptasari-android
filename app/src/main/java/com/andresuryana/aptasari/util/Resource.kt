package com.andresuryana.aptasari.util

sealed class Resource<out T> {
    data class Success<out T>(val data: T?) : Resource<T>()
    data class Error<out T>(val message: String?, val messageRes: Int? = null) : Resource<T>()
}
