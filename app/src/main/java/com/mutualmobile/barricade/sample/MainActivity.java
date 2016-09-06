package com.mutualmobile.barricade.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import com.mutualmobile.barricade.sample.api.GitHubApiService;
import com.mutualmobile.barricade.sample.api.model.Repo;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

  private final String TAG = "BARRICADE_SAMPLE";
  private GitHubApiService gitHubApiService;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Retrofit retrofit = new Retrofit.Builder().baseUrl("https://api.github.com").addConverterFactory(GsonConverterFactory.create()).build();

    gitHubApiService = retrofit.create(GitHubApiService.class);

    findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        gitHubApiService.getUserRepos("mutualmobile").enqueue(new Callback<List<Repo>>() {
          @Override public void onResponse(Call<List<Repo>> call, Response<List<Repo>> response) {
            Log.d(MainActivity.class.getCanonicalName(), "Got : " + response.body().size());
          }

          @Override public void onFailure(Call<List<Repo>> call, Throwable t) {
            Log.e(TAG, "(╯°□°)╯︵ ┻━┻", t);
          }
        });
      }
    });
  }
}
