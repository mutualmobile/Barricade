package com.mutualmobile.barricade.utils

import android.content.Context
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.util.logging.Logger

/**
 * Implementation of [AssetFileManager] to allow reading files from assets folder in Android
 */
class AndroidAssetFileManager(private val applicationContext: Context) : AssetFileManager {
    private companion object {
        const val TAG = "AndroidAssetFileManager"
    }

    override fun getContentsOfFileAsString(fileName: String): String? {
        return try {
            val inputStream = getContentsOfFileAsStream(fileName)
            BufferedReader(inputStream?.reader()).readText()
        } catch (e: IOException) {
            Logger.getLogger(TAG).severe(e.message)
            null
        }
    }

    override fun getContentsOfFileAsStream(fileName: String): InputStream? {
        return try {
            applicationContext.assets.open(String.format("%s", fileName))
        } catch (e: IOException) {
            Logger.getLogger(TAG).severe(e.message)
            null
        }
    }
}
