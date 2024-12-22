package asm2.moob.movieapp.util

import java.net.UnknownHostException
import java.net.SocketTimeoutException
import retrofit2.HttpException
import java.io.IOException

object NetworkUtil {
    fun getErrorMessage(error: Throwable): String {
        return when (error) {
            is UnknownHostException -> "No internet connection. Please check your network and try again."
            is SocketTimeoutException -> "Connection timed out. Please try again."
            is IOException -> "Network error. Please check your connection and try again."
            is HttpException -> "Server error. Please try again later."
            else -> "An unexpected error occurred. Please try again."
        }
    }
} 