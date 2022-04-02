package com.mutualmobile.barricade

import okhttp3.Interceptor
import okhttp3.Response
import java.util.logging.Logger

/**
 * OkHttp interceptor that maps all outgoing requests to their barricaded responses (if available)
 */
class BarricadeInterceptor : Interceptor {
    companion object {
        private val barricade = Barricade.getInstance()
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        if (barricade.isEnabled) {
            val request = chain.request()
            val pathSegments = request.url.pathSegments
            val endpoint = pathSegments[pathSegments.lastIndex]
            val response = barricade.getResponse(chain, endpoint)
            response?.let {
                try {
                    Thread.sleep(barricade.delay)
                } catch (e: InterruptedException) {
                    Logger.getLogger(Barricade.TAG).severe(e.message)
                }
                return response
            } ?: run {
                Logger.getLogger(Barricade.TAG).severe("No response found, making actual request")
                return chain.proceed(request)
            }
        } else {
            return chain.proceed(chain.request())
        }
    }
}
