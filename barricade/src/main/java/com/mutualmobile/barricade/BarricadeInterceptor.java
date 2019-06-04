package com.mutualmobile.barricade;

import java.io.IOException;
import java.util.Iterator;
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
      String endpoint = request.url().encodedPath();
      String queryString = getParams(request);
      Response response = null;
      if(!queryString.isEmpty()){
        response = barricade.getResponseForParams(chain, endpoint,queryString);
      }else {
        response = barricade.getResponse(chain, endpoint);
      }
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

  private String getParams(Request request){
    String requestType = request.method();
    switch (requestType.trim().toUpperCase()){
      case "PUT" :
      case "POST" : {
        //Todo : fetch parameters from post request
        return "";
      }
      case "GET" :
      default : {
       return getFormattedQuery(request);
      }
    }
  }


  private String getFormattedQuery(Request request){
    Iterator<String>  paramIterator = request.url().queryParameterNames().iterator();
    StringBuilder query = new StringBuilder();
    while(paramIterator.hasNext()){
      String paramName =paramIterator.next();
      List<String>values = request.url().queryParameterValues(paramName);
      for(String value: values){
        query.append(paramName).append("=").append(value).append("&");
      }
    }
    return query.toString();
  }
}
