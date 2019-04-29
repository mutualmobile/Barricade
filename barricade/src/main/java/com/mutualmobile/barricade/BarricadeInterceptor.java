package com.mutualmobile.barricade;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * OkHttp interceptor that maps all outgoing requests to their barricaded responses (if available)
 */
public class BarricadeInterceptor implements Interceptor {

  private Barricade barricade = Barricade.getInstance();

  @Override public Response intercept(Chain chain) throws IOException {
    if (barricade.isEnabled()) {
      Request request = chain.request();
      List<String> pathSegments = request.url().pathSegments();
      String endpoint = pathSegments.get(pathSegments.size() - 1);

      Response response = barricade.getResponse(chain, endpoint);
      if (response != null) {
        try {
          Thread.sleep(barricade.getDelay());
        } catch (InterruptedException e) {
          Logger.getLogger(Barricade.TAG).severe(e.getMessage());
        }
        return response;
      } else {
        Logger.getLogger(Barricade.TAG).severe("No response found, making actual request");
        return chain.proceed(request);
      }
    } else {
      return chain.proceed(chain.request());
    }
  }
}
