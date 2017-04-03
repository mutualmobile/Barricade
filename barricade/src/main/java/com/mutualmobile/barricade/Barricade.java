package com.mutualmobile.barricade;

import android.content.Context;
import android.support.annotation.NonNull;
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
 *
 * @author Mustafa Ali, 12/07/16.
 */
public class Barricade {
  static final String TAG = "Barricade";
  private static final String ROOT_DIRECTORY = "barricade";
  private static final long DEFAULT_DELAY = 1500;
  private static Barricade instance;

  private IBarricadeConfig barricadeConfig;
  private AssetFileManager fileManager;
  private long delay = DEFAULT_DELAY;

  private boolean disabled = false;

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

  private Barricade(AssetFileManager fileManager, IBarricadeConfig barricadeConfig, long delay,
      Context context) {
    this.barricadeConfig = barricadeConfig;
    this.fileManager = fileManager;
    this.delay = delay;

    if (context != null) {
      new BarricadeShakeListener(context);
    }
  }

  public boolean isDisabled() {
    return disabled;
  }

  public void enable() {
    Logger.getLogger(TAG).info("Barricade enabled.");
    this.disabled = false;
  }

  public void disable() {
    Logger.getLogger(TAG).info("Barricade disabled.");
    this.disabled = true;
  }

  /**
   * Builder class for Barricade
   */
  public static class Builder {
    private IBarricadeConfig barricadeConfig;
    private AssetFileManager fileManager;
    private long delay = DEFAULT_DELAY;
    private Context context;

    public Builder(Context context, IBarricadeConfig barricadeConfig) {
      this(barricadeConfig, new AndroidAssetFileManager(context));
    }

    private Builder(IBarricadeConfig barricadeConfig, AssetFileManager fileManager) {
      this.barricadeConfig = barricadeConfig;
      this.fileManager = fileManager;
    }

    public Builder enableShakeToStart(Context context) {
      this.context = context;
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
        instance = new Barricade(fileManager, barricadeConfig, delay, context);
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
    return new okhttp3.Response.Builder().code(barricadeResponse.statusCode)
        .request(chain.request())
        .protocol(Protocol.HTTP_1_0)
        .body(ResponseBody.create(MediaType.parse(barricadeResponse.contentType),
            fileResponse.getBytes()))
        .addHeader("content-type", barricadeResponse.contentType)
        .build();
  }

  public HashMap<String, BarricadeResponseSet> getConfig() {
    return barricadeConfig.getConfigs();
  }

  public long getDelay() {
    return delay;
  }

  public void setDelay(long delay) {
    this.delay = delay;
  }

  @NonNull private String getResponseFromFile(String endpoint, String variant) {
    String fileName =
        ROOT_DIRECTORY + File.separator + endpoint + File.separator + variant + ".json";
    return fileManager.getContentsOfFileAsString(fileName);
  }

  /**
   * Methods for changing response and delay programmatically for testing
   */
  public Barricade enabled(boolean enabled) {
    this.disabled = !enabled;
    return this;
  }

  public Barricade withDelay(long delay) {
    this.delay = delay;
    return this;
  }

  public Barricade withResponse(String endPoint, int defaultIndex) {
    getConfig().get(endPoint).defaultIndex = defaultIndex;
    return this;
  }
}
