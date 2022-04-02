package com.mutualmobile.barricadeSample

import android.app.Application
import com.mutualmobile.barricade.Barricade
import com.mutualmobile.barricade.BarricadeConfig

class B2Application : Application() {
    override fun onCreate() {
        super.onCreate()
        Barricade.Builder(this, BarricadeConfig.getInstance()).install()
    }
}
