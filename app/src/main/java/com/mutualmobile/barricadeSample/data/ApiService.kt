package com.mutualmobile.barricadeSample.data

import com.mutualmobile.barricade.annotation.Barricade
import com.mutualmobile.barricadeSample.data.models.JokeResponse
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("/jokes/random")
    @Barricade(
        responses = [
            com.mutualmobile.barricade.annotation.Response(
                fileName = "success.json",
                isDefault = true
            ),
            com.mutualmobile.barricade.annotation.Response(
                fileName = "failure.json",
                statusCode = 404
            )
        ]
    )
    suspend fun getRandomJoke(): Response<JokeResponse>

    @GET("/jokes/categories")
    suspend fun getJokeCategories(): Response<List<String>>
}
