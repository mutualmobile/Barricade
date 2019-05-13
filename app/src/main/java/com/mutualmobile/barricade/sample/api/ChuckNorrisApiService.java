package com.mutualmobile.barricade.sample.api;

import com.mutualmobile.barricade.annotation.Barricade;
import com.mutualmobile.barricade.annotation.Params;
import com.mutualmobile.barricade.annotation.QueryParams;
import com.mutualmobile.barricade.annotation.Response;
import com.mutualmobile.barricade.sample.api.model.Joke;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ChuckNorrisApiService {

  @GET("/jokes/random") @Barricade(endpoint = "/jokes/random", responses = {
      @Response(fileName = "success.json",isDefault = true),
      @Response(fileName = "failure.json", statusCode = 401)
  }) Call<Joke> getRandomJoke();



  @GET("/jokes/random")
  @Barricade(endpoint = "/jokes/random",
      queryParams = {
        @QueryParams( params = {@Params(name = "test",value = "success")} ,
          response = @Response(fileName = "success.json",isDefault = true)),
        @QueryParams( params = {@Params(name = "test",value = "failure")} ,
          response = @Response(fileName = "failure.json", statusCode = 401))
      },
      responses = {
      @Response(fileName = "success.json",isDefault = true),
      @Response(fileName = "failure.json", statusCode = 401)
  }) Call<Joke> getRandomJoke(@Query("test")String test);

  @GET("/jokes/categories") Call<List<String>> getJokeCategories();
}
