package com.mutualmobile.barricade

import android.content.Context
import android.content.Intent
import android.os.Build
import com.mutualmobile.barricade.Barricade.Builder
import com.mutualmobile.barricade.utils.AndroidAssetFileManager
import com.mutualmobile.barricade.utils.AssetFileManager
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.io.File
import java.util.logging.Logger

/**
 * Local server for your application.
 *
 * You should typically initialize it once, preferably in your Application class using [Builder]
 * class.
 */
class Barricade private constructor(
    private val barricadeConfig: IBarricadeConfig,
    private val fileManager: AssetFileManager
) {
    /**
     * Change barricade status
     * */
    var isEnabled: Boolean = false

    /**
     * How long should it take before returning API response.
     * */
    var delay: Long = DEFAULT_DELAY

    companion object {
        const val TAG = "Barricade"
        private val logger = Logger.getLogger(TAG)
        private const val DEFAULT_DELAY: Long = 150
        private const val ROOT_DIRECTORY = "barricade"
        private var instance: Barricade? = null

        /**
         * @return The singleton instance of the Barricade
         * */
        fun getInstance(): Barricade {
            instance?.let { nnInstance ->
                return nnInstance
            } ?: run {
                throw IllegalStateException("Barricade not installed, install using Builder")
            }
        }
    }

    class Builder constructor(
        context: Context,
        private val barricadeConfig: IBarricadeConfig
    ) {
        private var delay: Long = DEFAULT_DELAY
        private var assetFileManager = AndroidAssetFileManager(context)

        fun setDelay(delay: Long) = apply { this.delay = delay }

        fun install() {
            instance?.let {
                logger.info("Barricade already installed, install() will be ignored.")
            } ?: run {
                instance = Barricade(
                    barricadeConfig = barricadeConfig,
                    fileManager = assetFileManager
                )
                instance?.delay = delay
            }
        }
    }

    /**
     * Returns a barricaded response for an endpoint
     *
     * @param chain OkHttp Interceptor chain
     * @param endpoint Endpoint that is being hit
     * @return Barricaded response (if available), null otherwise
     */
    fun getResponse(
        chain: Interceptor.Chain,
        endpoint: String
    ): Response? {
        barricadeConfig.getResponseForEndpoint(endpoint)?.let { nnBarricadeResponse ->
            val fileResponse: String? =
                getResponseFromFile(endpoint, nnBarricadeResponse.responseFileName)
            fileResponse?.let { nnFileResponse ->
                return Response.Builder()
                    .code(nnBarricadeResponse.statusCode)
                    .message("Barricade OK")
                    .request(chain.request())
                    .protocol(Protocol.HTTP_1_0)
                    .body(
                        nnFileResponse
                            .toResponseBody(
                                nnBarricadeResponse
                                    .contentType
                                    .toMediaTypeOrNull()
                            )
                    )
                    .addHeader("content-type", nnBarricadeResponse.contentType)
                    .build()
            }
        }
        return null
    }

    fun getConfig() = barricadeConfig.getConfigs()

    /**
     * Change response to be returned for an endpoint
     *
     * @param endpoint The endpoint whose response you want to change. Use BarricadeConfig$EndPoints
     * to
     * get endpoint strings rather than passing string directly
     * @param defaultIndex The index of the response you want to get for endPoint. Use
     * BarricadeConfig$Responses to get responses for an endpoint instead of passing an int directly
     */
    fun setResponse(endpoint: String, defaultIndex: Int): Barricade {
        if (getConfig().containsKey(endpoint)) {
            getConfig()[endpoint]?.defaultIndex = defaultIndex
            return this
        } else {
            throw IllegalArgumentException("$endpoint doesn't exist")
        }
    }

    /**
     * Resets any configuration changes done at run-time
     */
    fun reset() {
        getConfig().values.forEach { barricadeResponseSet ->
            barricadeResponseSet.defaultIndex = barricadeResponseSet.originalDefaultIndex
        }
        delay = DEFAULT_DELAY
    }

    private fun getResponseFromFile(endpoint: String, variant: String): String? {
        val fileName = ROOT_DIRECTORY + File.separator + endpoint + File.separator + variant
        return fileManager.getContentsOfFileAsString(fileName)
    }

    fun launchConfigActivity(context: Context) {
        val intent = Intent(context, BarricadeConfigActivity::class.java)
        intent.flags += Intent.FLAG_ACTIVITY_MULTIPLE_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.flags += Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT
        }
        context.startActivity(intent)
    }
}
