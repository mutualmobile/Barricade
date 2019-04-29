package com.mutualmobile.barricade.sample;

import android.app.Application;
import com.mutualmobile.barricade.Barricade;
import com.mutualmobile.barricade.BarricadeConfig;

public class BarricadeSampleApplication extends Application {
  @Override public void onCreate() {
    super.onCreate();
    new Barricade.Builder(this, BarricadeConfig.getInstance()).enableShakeToStart(this).install();
  }
}
