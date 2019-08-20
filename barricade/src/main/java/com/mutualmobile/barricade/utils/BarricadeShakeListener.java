package com.mutualmobile.barricade.utils;

import android.app.Activity;
import android.app.Application;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import com.mutualmobile.barricade.Barricade;
import com.mutualmobile.barricade.activity.BarricadeActivity;

import static android.content.Context.SENSOR_SERVICE;

public class BarricadeShakeListener implements SensorEventListener, Application.ActivityLifecycleCallbacks {

	private final Application application;
	private static final int SHAKE_THRESHOLD = 1200;
	private long lastUpdate = 0;
	private float lastX, lastY, lastZ;

	private int shakeCount = 0;
	private boolean listenerRegistered;
	private SensorManager sensorManager;

	public BarricadeShakeListener(Application application) {
		this.application = application;
		this.sensorManager = (SensorManager) application.getSystemService(SENSOR_SERVICE);

		application.registerActivityLifecycleCallbacks(this);
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
				Barricade.getInstance().launchConfigActivity(application);
			}
			lastX = x;
			lastY = y;
			lastZ = z;
		}
	}

	public void enableShakeListener() {
		if (!listenerRegistered) {
			listenerRegistered =
					sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
							SensorManager.SENSOR_DELAY_GAME);
		}
	}

	public void disableShakeListener() {
		sensorManager.unregisterListener(this);
		listenerRegistered = false;
	}

	@Override public void onAccuracyChanged(Sensor sensor, int i) {

	}

	@Override public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

	}

	@Override public void onActivityStarted(Activity activity) {
		enableShakeListener();
	}

	@Override public void onActivityResumed(Activity activity) {
		enableShakeListener();
	}

	@Override public void onActivityPaused(Activity activity) {

	}

	@Override public void onActivityStopped(Activity activity) {
		if (activity.isTaskRoot() && !(activity instanceof BarricadeActivity)) {
			disableShakeListener();
		}
	}

	@Override public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

	}

	@Override public void onActivityDestroyed(Activity activity) {

	}
}
