package asm2.moob.movieapp.util

import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

object NetworkUtil {
    fun getErrorMessage(error: Throwable): String {
        return when (error) {
            is HttpException -> {
                when (error.code()) {
                    401 -> "Unauthorized access. Please check your API key."
                    404 -> "Resource not found."
                    429 -> "Too many requests. Please try again later."
                    500 -> "Server error. Please try again later."
                    else -> "Network error occurred. Please try again."
                }
            }
            is SocketTimeoutException -> "Connection timed out. Please check your internet connection."
            is UnknownHostException -> "Unable to connect to server. Please check your internet connection."
            is IOException -> "Network error occurred. Please check your internet connection."
            else -> error.message ?: "An unexpected error occurred."
        }
    }
} 