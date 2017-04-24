package com.mutualmobile.barricade.sample.api;

import com.mutualmobile.barricade.Barricade;
import com.mutualmobile.barricade.BarricadeConfig;
import com.mutualmobile.barricade.BarricadeInterceptor;
import com.mutualmobile.barricade.sample.api.model.Joke;
import com.mutualmobile.barricade.utils.TestAssetFileManager;
import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.google.common.truth.Truth.assertThat;

/**
 * Integration tests for the API with and without Barricade
 */
public class ChuckNorrisApiServiceTest {

  private static final String BASE_URL = "https://api.chucknorris.io";
  private static Barricade barricade;

  @BeforeClass public static void setup() {
    barricade = new Barricade.Builder(BarricadeConfig.getInstance(), new TestAssetFileManager()).install();
  }

  @After public void teardown() {
    barricade.reset();
  }

  @Test public void canFetchRandomJokeFromApi() throws IOException {
    barricade.setEnabled(false);
    Response<Joke> response = getApiService().getRandomJoke().execute();

    assertThat(response.isSuccessful()).isTrue();
    assertThat(response.code()).isEqualTo(200);
    assertThat(response.body()).isNotNull();

    Joke joke = response.body();
    assertThat(joke.id).isNotNull();
    assertThat(joke.id).isNotEmpty();
    assertThat(joke.value).isNotNull();
    assertThat(joke.value).isNotEmpty();
    assertThat(joke.iconUrl).isNotNull();
    assertThat(joke.iconUrl).isNotEmpty();
  }

  @Test public void canFetchRandomJokeFromBarricade() throws IOException {
    barricade.setEnabled(true);
    Response<Joke> response = getApiService().getRandomJoke().execute();

    assertThat(response.isSuccessful()).isTrue();
    assertThat(response.code()).isEqualTo(200);
    assertThat(response.body()).isNotNull();

    Joke joke = response.body();
    assertThat(joke.id).isNotNull();
    assertThat(joke.id).isNotEmpty();
    assertThat(joke.id).isEqualTo("gX6RQU9EQxC4oZd6kVeiSw");
    assertThat(joke.value).isNotNull();
    assertThat(joke.value).isNotEmpty();
    assertThat(joke.value).isEqualTo("When Chuck Norris was a kid, he made his mom eat her vegetables");
    assertThat(joke.iconUrl).isNotNull();
    assertThat(joke.iconUrl).isNotEmpty();
    assertThat(joke.iconUrl).isEqualTo("https://assets.chucknorris.host/img/avatar/chuck-norris.png");
  }

  @Test public void canSetBarricadeResponseAtRunTime() throws IOException {
    barricade.setEnabled(true).setResponse(BarricadeConfig.Endpoints.RANDOM, BarricadeConfig.Responses.Random.FAILURE);
    Response<Joke> response = getApiService().getRandomJoke().execute();
    assertThat(response.isSuccessful()).isFalse();
    assertThat(response.code()).isEqualTo(401);
  }

  private ChuckNorrisApiService getApiService() {
    HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
    httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

    OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(new BarricadeInterceptor()).addInterceptor(httpLoggingInterceptor).build();
    Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).client(okHttpClient).addConverterFactory(GsonConverterFactory.create()).build();
    return retrofit.create(ChuckNorrisApiService.class);
  }
}