package com.mutualmobile.barricade.response

/**
 * Contains all responses for a barricaded endpoint along with other metadata.
 */
data class BarricadeResponseSet(
    val responses: List<BarricadeResponse>,
    var defaultIndex: Int,
    val originalDefaultIndex: Int = defaultIndex
)
