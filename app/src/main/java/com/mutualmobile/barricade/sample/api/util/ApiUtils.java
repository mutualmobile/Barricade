package com.mutualmobile.barricade.sample.api.util;

import com.mutualmobile.barricade.BarricadeInterceptor;
import com.mutualmobile.barricade.sample.api.ChuckNorrisApiService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiUtils {

  private static OkHttpClient okHttpClient;

  public static ChuckNorrisApiService getApiService() {
    HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
    httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

    okHttpClient = new OkHttpClient.Builder().addInterceptor(new BarricadeInterceptor()).addInterceptor(httpLoggingInterceptor).build();

    Retrofit retrofit =
        new Retrofit.Builder().baseUrl("https://api.chucknorris.io").client(okHttpClient).addConverterFactory(GsonConverterFactory.create()).build();

    return retrofit.create(ChuckNorrisApiService.class);
  }

  public static OkHttpClient getOkHttpClient() {
    return okHttpClient;
  }
}
