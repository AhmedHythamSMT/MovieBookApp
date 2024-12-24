package asm2.moob.movieapp.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "https://api.themoviedb.org/3/"
    private const val API_KEY = "1f54bd990f1cdfb230adb312546d765d"  // Your TMDB API key
    private const val ACCESS_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIxZjU0YmQ5OTBmMWNkZmIyMzBhZGIzMTI1NDZkNzY1ZCIsInN1YiI6IjY1OGVmNzQ3YjdiNjlkMDk3MzM5OTRiYiIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.kzK3H8nWXgEUj8qRKGqzkFcN1YHQVbYDxDRZHsHU9RM"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()
            val url = original.url.newBuilder()
                .addQueryParameter("api_key", API_KEY)
                .build()

            val request = original.newBuilder()
                .header("Authorization", "Bearer $ACCESS_TOKEN")
                .url(url)
                .build()

            chain.proceed(request)
        }
        .addInterceptor(loggingInterceptor)
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val movieApi: MovieApi = retrofit.create(MovieApi::class.java)
} 