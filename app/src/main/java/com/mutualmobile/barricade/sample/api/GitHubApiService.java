package com.mutualmobile.barricade.sample.api;

import com.mutualmobile.barricade.sample.api.model.Repo;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GitHubApiService {

  @GET("/users/{user}/repos") Call<List<Repo>> getUserRepos(@Path("user") String user);
}
