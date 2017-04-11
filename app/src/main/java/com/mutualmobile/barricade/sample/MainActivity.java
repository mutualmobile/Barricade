package com.mutualmobile.barricade.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import com.mutualmobile.barricade.Barricade;
import com.mutualmobile.barricade.sample.api.ChuckNorrisApiService;
import com.mutualmobile.barricade.sample.api.model.Joke;
import com.mutualmobile.barricade.sample.api.util.ApiUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

  private final String TAG = "BARRICADE_SAMPLE";
  private ChuckNorrisApiService chuckNorrisApiService;
  private ProgressBar progressBar;
  private TextView jokeTextView;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    initUi();
    chuckNorrisApiService = ApiUtils.getApiService();
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if(item.getItemId() == R.id.config) {
      Barricade.getInstance().launchConfigActivity(this);
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  private void initUi() {
    Switch barricadeSwitch = (Switch) findViewById(R.id.barricade_switch);
    checkChanged(barricadeSwitch.isChecked());
    barricadeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        checkChanged(isChecked);
      }
    });

    progressBar = (ProgressBar) findViewById(R.id.progress_bar);
    findViewById(R.id.get_joke_button).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        onJokeButtonClicked();
      }
    });
    jokeTextView = (TextView) findViewById(R.id.joke_text);
  }

  private void onJokeButtonClicked() {
    showProgress(true);
    chuckNorrisApiService.getRandomJoke().enqueue(new Callback<Joke>() {
      @Override public void onResponse(Call<Joke> call, Response<Joke> response) {
        if (response.isSuccessful()) {
          jokeTextView.setText(response.body().value);
        } else {
          jokeTextView.setText("Request failed : " + response.code());
        }
        showProgress(false);
      }

      @Override public void onFailure(Call<Joke> call, Throwable t) {
        Log.e(TAG, "(╯°□°)╯︵ ┻━┻", t);
        showProgress(false);
      }
    });
  }

  private void showProgress(boolean show) {
    progressBar.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
  }

  private void checkChanged(boolean isChecked) {
    Barricade.getInstance().setEnabled(isChecked);
  }

}
