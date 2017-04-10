package com.mutualmobile.barricade.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import com.mutualmobile.barricade.Barricade;

import static android.content.Context.SENSOR_SERVICE;

public class BarricadeShakeListener implements SensorEventListener {
  private Context mContext;
  private static final int SHAKE_THRESHOLD = 1200;
  private long lastUpdate = 0;
  private float lastX, lastY, lastZ;

  private int shakeCount = 0;

  public BarricadeShakeListener(Context context) {
    mContext = context;
    SensorManager sensorManager = (SensorManager) mContext.getSystemService(SENSOR_SERVICE);
    sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
  }

  @Override public void onSensorChanged(SensorEvent sensorEvent) {
    long curTime = System.currentTimeMillis();
    if ((curTime - lastUpdate) > 100) {
      long diffTime = (curTime - lastUpdate);
      lastUpdate = curTime;

      float x, y, z;
      x = sensorEvent.values[0];
      y = sensorEvent.values[1];
      z = sensorEvent.values[2];

      float speed = Math.abs(x + y + z - lastX - lastY - lastZ) / diffTime * 10000;

      if (speed > SHAKE_THRESHOLD) {
        shakeCount++;
      } else {
        shakeCount = 0;
      }
      if (shakeCount >= 2) {
        shakeCount = 0;
        Barricade.getInstance().launchConfigActivity(mContext);
      }
      lastX = x;
      lastY = y;
      lastZ = z;
    }
  }

  @Override public void onAccuracyChanged(Sensor sensor, int i) {

  }
}
