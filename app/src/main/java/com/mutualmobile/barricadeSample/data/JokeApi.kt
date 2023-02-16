package com.mutualmobile.barricadeSample.data

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mutualmobile.barricade.BarricadeInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object JokeApi {
    private val loggingInterceptor = HttpLoggingInterceptor()
        .setLevel(HttpLoggingInterceptor.Level.BODY)

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(BarricadeInterceptor())
        .build()

    private val gson: Gson = GsonBuilder().setLenient().create()

    val instance = Retrofit.Builder()
        .baseUrl("https://api.chucknorris.io")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
        .create<ApiService>()
}
