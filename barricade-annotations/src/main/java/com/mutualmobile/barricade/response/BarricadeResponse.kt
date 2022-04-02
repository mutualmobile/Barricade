package com.mutualmobile.barricade.response

data class BarricadeResponse(
    val statusCode: Int,
    val responseFileName: String,
    val contentType: String
)
