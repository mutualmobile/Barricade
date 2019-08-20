package com.mutualmobile.barricade.sample;

import android.app.Application;
import com.mutualmobile.barricade.Barricade;
import com.mutualmobile.barricade.BarricadeConfig;

public class BarricadeSampleApplication extends Application {
  @Override public void onCreate() {
    super.onCreate();
    Barricade barricade =new Barricade.Builder(this, BarricadeConfig.getInstance()).install();
    barricade.enableShakeListener(this);
  }
}
