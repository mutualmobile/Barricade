package com.mutualmobile.barricade;

import android.support.annotation.NonNull;
import com.mutualmobile.barricade.response.BarricadeResponse;
import com.mutualmobile.barricade.response.BarricadeResponseSet;
import com.mutualmobile.barricade.utils.AssetFileManager;
import java.io.File;
import java.util.HashMap;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.ResponseBody;

/**
 * Local server for your application.
 *
 * You should typically initialize it once, preferably in your Application class by calling one of the init() methods.
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

  /**
   * Initializes the barricade for your application. You should typically call this only once, preferably in your application class.
   *
   * @param barricadeConfig Barricade configuration that is code generated
   * @param fileManager Implementation of {@link AssetFileManager} that can read response files
   * @param delay Default delay in milliseconds before returning a barricaded response
   */
  public static Barricade init(IBarricadeConfig barricadeConfig, AssetFileManager fileManager, long delay) {
    if (instance == null) {
      instance = new Barricade(fileManager, barricadeConfig, delay);
    }
    return instance;
  }

  /**
   * Initializes the barricade for your application. You should typically call this only once, preferably in your application class.
   *
   * @param barricadeConfig Barricade configuration that is code generated
   * @param fileManager Implementation of {@link AssetFileManager} that can read response files
   */
  public static Barricade init(IBarricadeConfig barricadeConfig, AssetFileManager fileManager) {
    return init(barricadeConfig, fileManager, DEFAULT_DELAY);
  }

  /**
   * @return The singleton instance of the Barricade
   */
  public static Barricade getInstance() {
    if (instance == null) {
      throw new IllegalStateException("Barricade not initialized, call init() first");
    }
    return instance;
  }

  private Barricade() {
  }

  private Barricade(AssetFileManager fileManager, IBarricadeConfig barricadeConfig, long delay) {
    this.barricadeConfig = barricadeConfig;
    this.fileManager = fileManager;
    this.delay = delay;
  }

  /**
   * Returns a barricaded response for an endpoint
   *
   * @param chain OkHttp Interceptor chain
   * @param endpoint Endpoint that is being hit
   * @return Barricaded response (if available), null otherwise
   */
  public okhttp3.Response getResponse(Interceptor.Chain chain, String endpoint) {
    BarricadeResponse barricadeResponse = barricadeConfig.getResponseForEndpoint(endpoint);
    if (barricadeResponse == null) {
      return null;
    }
    String fileResponse = getResponseFromFile(endpoint, barricadeResponse.responseFileName);
    return new okhttp3.Response.Builder().code(barricadeResponse.statusCode)
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

  public void setDelay(long delay) {
    this.delay = delay;
  }

  @NonNull private String getResponseFromFile(String endpoint, String variant) {
    String fileName = ROOT_DIRECTORY + File.separator + endpoint + File.separator + variant + ".json";
    return fileManager.getContentsOfFileAsString(fileName);
  }
}
