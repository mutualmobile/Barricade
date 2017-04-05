package com.mutualmobile.barricade.sample;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import com.mutualmobile.barricade.Barricade;
import com.mutualmobile.barricade.BarricadeInterceptor;
import com.mutualmobile.barricade.sample.api.GitHubApiService;
import com.mutualmobile.barricade.sample.api.model.Joke;
import com.mutualmobile.barricade.sample.api.model.Repo;
import java.util.ArrayList;
import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

  private final String TAG = "BARRICADE_SAMPLE";
  private GitHubApiService gitHubApiService;
  private ProgressDialog progressDialog;

  private TextView jokeTextView;
  private CardView cardInfo;
  private RecyclerView repoListView;
  private RepoListAdapter repoListAdapter;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    progressDialog = new ProgressDialog(this);
    progressDialog.setMessage("Fetching Data");

    initRetrofit();
    initRecyclerView();

    Switch barricadeSwitch = (Switch) findViewById(R.id.switch1);
    checkChanged(barricadeSwitch.isChecked());

    barricadeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        checkChanged(isChecked);
      }
    });

    jokeTextView = (TextView) findViewById(R.id.joke_text);
    cardInfo = (CardView) findViewById(R.id.card_info);

    findViewById(R.id.get_repos_button).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {

        showProgress(true);
        gitHubApiService.getUserRepos("google").enqueue(new Callback<List<Repo>>() {
          @Override public void onResponse(Call<List<Repo>> call, Response<List<Repo>> response) {
            Log.d(MainActivity.class.getCanonicalName(), "Got : " + response.body().size());
            showRepoList(response.body());
            showProgress(false);
          }

          @Override public void onFailure(Call<List<Repo>> call, Throwable t) {
            Log.e(TAG, "(╯°□°)╯︵ ┻━┻", t);
            showProgress(false);
          }
        });
      }
    });

    findViewById(R.id.get_joke_button).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {

        showProgress(true);
        gitHubApiService.getRandomChuckNorrisJoke().enqueue(new Callback<Joke>() {
          @Override public void onResponse(Call<Joke> call, Response<Joke> response) {
            if(response.isSuccessful()) {
              showJoke(response.body().value);
            }
            else {
              Log.e(TAG,"UnSuccessful Status Code Response: "+response.code());
            }
            showProgress(false);
          }

          @Override public void onFailure(Call<Joke> call, Throwable t) {
            Log.e(TAG, "(╯°□°)╯︵ ┻━┻", t);
            showProgress(false);
          }
        });
      }
    });
  }

  private void showProgress(boolean show) {
    if (progressDialog == null) {
      return;
    }

    if (show) {
      progressDialog.show();
    } else {
      progressDialog.dismiss();
    }
  }

  private void checkChanged(boolean isChecked) {
    if (isChecked) {
      Barricade.getInstance().enable();
    } else {
      Barricade.getInstance().disable();
    }
  }

  private void showRepoList(List<Repo> repoList) {
    cardInfo.setVisibility(View.GONE);
    repoListView.setVisibility(View.VISIBLE);
    repoListAdapter.setRepoList(repoList);
  }

  private void showJoke(String joke) {
    repoListView.setVisibility(View.GONE);
    cardInfo.setVisibility(View.VISIBLE);
    jokeTextView.setText(joke);
  }

  private void initRetrofit() {
    HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
    httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

    OkHttpClient okHttpClient =
        new OkHttpClient.Builder().addInterceptor(new BarricadeInterceptor())
            .addInterceptor(httpLoggingInterceptor)
            .build();

    Retrofit retrofit = new Retrofit.Builder().baseUrl("https://api.github.com")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build();

    gitHubApiService = retrofit.create(GitHubApiService.class);
  }

  private void initRecyclerView() {
    repoListView = (RecyclerView) findViewById(R.id.repo_list);
    repoListView.setLayoutManager(new LinearLayoutManager(this));
    repoListAdapter = new RepoListAdapter();
    repoListView.setAdapter(repoListAdapter);
  }
}
