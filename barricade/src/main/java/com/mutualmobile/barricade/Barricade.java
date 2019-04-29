package com.mutualmobile.barricade;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import com.mutualmobile.barricade.activity.BarricadeActivity;
import com.mutualmobile.barricade.response.BarricadeResponse;
import com.mutualmobile.barricade.response.BarricadeResponseSet;
import com.mutualmobile.barricade.utils.AndroidAssetFileManager;
import com.mutualmobile.barricade.utils.AssetFileManager;
import com.mutualmobile.barricade.utils.BarricadeShakeListener;
import java.io.File;
import java.util.HashMap;
import java.util.logging.Logger;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.ResponseBody;

/**
 * Local server for your application.
 *
 * You should typically initialize it once, preferably in your Application class using Builder
 * class.
 */
public class Barricade {
  static final String TAG = "Barricade";
  private static final String ROOT_DIRECTORY = "barricade";
  private static final long DEFAULT_DELAY = 150;
  private static Barricade instance;

  private IBarricadeConfig barricadeConfig;
  private AssetFileManager fileManager;
  private long delay = DEFAULT_DELAY;

  private boolean enabled = false;

  /**
   * @return The singleton instance of the Barricade
   */
  public static Barricade getInstance() {
    if (instance == null) {
      throw new IllegalStateException("Barricade not installed, install using Builder");
    }
    return instance;
  }

  // Disable instance creation by using empty constructor
  private Barricade() {
  }

  private Barricade(AssetFileManager fileManager, IBarricadeConfig barricadeConfig, long delay, Application application) {
    this.barricadeConfig = barricadeConfig;
    this.fileManager = fileManager;
    this.delay = delay;

    if (application != null) {
      new BarricadeShakeListener(application);
    }
  }

  public boolean isEnabled() {
    return enabled;
  }

  /**
   * Builder class for Barricade
   */
  public static class Builder {
    private IBarricadeConfig barricadeConfig;
    private AssetFileManager fileManager;
    private long delay = DEFAULT_DELAY;
    private Application application;

    public Builder(Context context, IBarricadeConfig barricadeConfig) {
      this(barricadeConfig, new AndroidAssetFileManager(context));
    }

    public Builder(IBarricadeConfig barricadeConfig, AssetFileManager fileManager) {
      this.barricadeConfig = barricadeConfig;
      this.fileManager = fileManager;
    }

    public Builder enableShakeToStart(Application application) {
      this.application = application;
      return this;
    }

    public Builder setGlobalDelay(long delay) {
      this.delay = delay;
      return this;
    }

    /**
     * Create Barricade instance if not exist and return it
     * It should be called once in project, next call will be ignored
     *
     * @return Barricade instance
     */
    public Barricade install() {
      if (instance == null) {
        instance = new Barricade(fileManager, barricadeConfig, delay, application);
      } else {
        Logger.getLogger(TAG).info("Barricade already installed, install() will be ignored.");
      }
      return instance;
    }
  }

  /**
   * Returns a barricaded response for an endpoint
   *
   * @param chain OkHttp Interceptor chain
   * @param endpoint Endpoint that is being hit
   * @return Barricaded response (if available), null otherwise
   */
  okhttp3.Response getResponse(Interceptor.Chain chain, String endpoint) {
    BarricadeResponse barricadeResponse = barricadeConfig.getResponseForEndpoint(endpoint);
    if (barricadeResponse == null) {
      return null;
    }
    String fileResponse = getResponseFromFile(endpoint, barricadeResponse.responseFileName);
    return new okhttp3.Response.Builder().code(barricadeResponse.statusCode).message("Barricade OK")
        .request(chain.request())
        .protocol(Protocol.HTTP_1_0)
        .body(ResponseBody.create(MediaType.parse(barricadeResponse.contentType), fileResponse.getBytes()))
        .addHeader("content-type", barricadeResponse.contentType)
        .build();
  }

  public HashMap<String, BarricadeResponseSet> getConfig() {
    return barricadeConfig.getConfigs();
  }

  public long getDelay() {
    return delay;
  }

  private String getResponseFromFile(String endpoint, String variant) {
    String fileName = ROOT_DIRECTORY + File.separator + endpoint + File.separator + variant;
    return fileManager.getContentsOfFileAsString(fileName);
  }

  /**
   * Change Barricade status
   *
   * @param enabled true to enable, false otherwise
   */
  public Barricade setEnabled(boolean enabled) {
    this.enabled = enabled;
    return this;
  }

  /**
   * Set delay for responses sent by Barricade
   *
   * @param delay Delay in milliseconds
   */
  public Barricade setDelay(long delay) {
    this.delay = delay;
    return this;
  }

  /**
   * Change response to be returned for an endpoint
   *
   * @param endPoint The endpoint whose response you want to change. Use BarricadeConfig$EndPoints
   * to
   * get endpoint strings rather than passing string directly
   * @param defaultIndex The index of the response you want to get for endPoint. Use
   * BarricadeConfig$Responses to get responses for an endpoint instead of passing an int directly
   */
  public Barricade setResponse(String endPoint, int defaultIndex) {
    if (getConfig().containsKey(endPoint)) {
      getConfig().get(endPoint).defaultIndex = defaultIndex;
      return this;
    } else {
      throw new IllegalArgumentException(endPoint + " doesn't exist");
    }
  }

  /**
   * Resets any configuration changes done at run-time
   */
  public void reset() {
    HashMap<String, BarricadeResponseSet> configs = getConfig();
    for (String key : configs.keySet()) {
      BarricadeResponseSet set = configs.get(key);
      set.defaultIndex = set.originalDefaultIndex;
    }
    this.delay = DEFAULT_DELAY;
  }

  /**
   * Launches the Barricade configuration UI
   *
   * @param context Activity context
   */
  public void launchConfigActivity(Context context) {
    Intent intent = new Intent(context, BarricadeActivity.class);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT);
    } else {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }
    context.startActivity(intent);
  }
}
