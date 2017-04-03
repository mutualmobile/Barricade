package com.mutualmobile.barricade.sample;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import com.mutualmobile.barricade.Barricade;
import com.mutualmobile.barricade.BarricadeConfig;
import com.mutualmobile.barricade.BarricadeInterceptor;
import com.mutualmobile.barricade.sample.api.GitHubApiService;
import com.mutualmobile.barricade.sample.api.model.Repo;
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

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    progressDialog = new ProgressDialog(this);
    progressDialog.setMessage("Fetching Data");

    init();

    Switch barricadeSwitch = (Switch) findViewById(R.id.switch1);
    checkChanged(barricadeSwitch.isChecked());

    barricadeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        checkChanged(isChecked);
      }
    });

    final TextView textView = (TextView) findViewById(R.id.textView);

    findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        showProgress(true);
        gitHubApiService.getUserRepos("google").enqueue(new Callback<List<Repo>>() {
          @Override public void onResponse(Call<List<Repo>> call, Response<List<Repo>> response) {
            Log.d(MainActivity.class.getCanonicalName(), "Got : " + response.body().size());

            textView.setText(response.body().toString());

            showProgress(false);
          }

          @Override public void onFailure(Call<List<Repo>> call, Throwable t) {
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

  private void init() {
    HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
    httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

    OkHttpClient okHttpClient =
        new OkHttpClient.Builder().addInterceptor(new BarricadeInterceptor())
            .addInterceptor(httpLoggingInterceptor)
            .build();

    Retrofit retrofit = new Retrofit.Builder().baseUrl("https://api.github.com")
        .client(okHttpClient).addConverterFactory(GsonConverterFactory.create()).build();

    gitHubApiService = retrofit.create(GitHubApiService.class);
  }
}
