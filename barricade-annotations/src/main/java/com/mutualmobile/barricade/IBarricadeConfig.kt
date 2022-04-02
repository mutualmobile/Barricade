package com.mutualmobile.barricade

import com.mutualmobile.barricade.response.BarricadeResponse
import com.mutualmobile.barricade.response.BarricadeResponseSet

/**
 * Contract for a Barricade configuration
 */
interface IBarricadeConfig {
    fun getConfigs(): HashMap<String, BarricadeResponseSet>
    fun getResponseForEndpoint(endpoint: String): BarricadeResponse?
}
