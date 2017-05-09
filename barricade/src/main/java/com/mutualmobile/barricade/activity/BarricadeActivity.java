package com.mutualmobile.barricade.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.mutualmobile.barricade.Barricade;
import com.mutualmobile.barricade.R;
import com.mutualmobile.barricade.adapter.BarricadeEndpointsRVAdapter;
import com.mutualmobile.barricade.adapter.BarricadeResponsesRVAdapter;
import com.mutualmobile.barricade.response.BarricadeResponseSet;
import java.util.HashMap;
import java.util.Locale;

/**
 * Activity to view the Barricade configuration and change default responses for endpoints. This
 * should typically be used to easily change the
 * configuration during testing and not included in production builds. Note that any changes are
 * currently not persisted across app restarts.
 * If you want to persist them, use annotations parameters instead.
 */
public class BarricadeActivity extends AppCompatActivity
    implements BarricadeEndpointsRVAdapter.EndpointClickedListener, BarricadeResponsesRVAdapter.EndpointResponseClickedListener {

  private HashMap<String, BarricadeResponseSet> barricadeConfig;

  private ActionBar actionBar;
  private RecyclerView endpointsRecyclerView;
  private RecyclerView responsesRecyclerView;

  private BarricadeEndpointsRVAdapter endpointsRVAdapter;
  private BarricadeResponsesRVAdapter responsesRVAdapter;
  private RelativeLayout container;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_barricade);

    initUi();
    barricadeConfig = Barricade.getInstance().getConfig();
    setEndpointsView();
  }

  private void initUi() {
    actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
    }
    endpointsRecyclerView = (RecyclerView) findViewById(R.id.endpoint_rv);
    responsesRecyclerView = (RecyclerView) findViewById(R.id.endpoint_responses_rv);

    endpointsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    responsesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    container = (RelativeLayout) findViewById(R.id.activity_barricade);
  }

  private void setEndpointsView() {
    setToolbarTitle(getString(R.string.title_activity));
    endpointsRVAdapter = new BarricadeEndpointsRVAdapter(barricadeConfig, this);
    endpointsRecyclerView.setAdapter(endpointsRVAdapter);
    endpointsRecyclerView.setVisibility(View.VISIBLE);
    responsesRecyclerView.setVisibility(View.GONE);
  }

  private void setResponsesView(String endpoint) {
    setToolbarTitle(endpoint);
    BarricadeResponseSet responseSet = barricadeConfig.get(endpoint);
    responsesRVAdapter = new BarricadeResponsesRVAdapter(endpoint, responseSet.responses, responseSet.defaultIndex, this);
    responsesRecyclerView.setAdapter(responsesRVAdapter);
    endpointsRecyclerView.setVisibility(View.GONE);
    responsesRecyclerView.setVisibility(View.VISIBLE);
  }

  private void setToolbarTitle(String title) {
    if (actionBar != null) {
      actionBar.setTitle(title);
    }
  }

  @Override public void onBackPressed() {
    if (endpointsRecyclerView.getVisibility() == View.GONE) {
      setEndpointsView();
    } else {
      super.onBackPressed();
    }
  }

  @Override public void onEndpointClicked(String endpoint) {
    setResponsesView(endpoint);
  }

  @Override public void onResponseClicked(String endpoint, int index) {
    barricadeConfig.get(endpoint).defaultIndex = index;
    setEndpointsView();
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    int itemId = item.getItemId();
    if (itemId == android.R.id.home) {
      onBackPressed();
      return true;
    } else if (itemId == R.id.menu_delay) {
      showEditDialog();
      return true;
    } else if (itemId == R.id.menu_reset) {
      showResetDialog();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  private void showEditDialog() {
    View dialogView =
        getLayoutInflater().inflate(R.layout.dialog_edit_global_delay, container, false);

    final EditText delayEditText = (EditText) dialogView.findViewById(R.id.delay_value_edittext);
    delayEditText.setText(String.format(Locale.US, "%d", Barricade.getInstance().getDelay()));

    new AlertDialog.Builder(this).setView(dialogView)
        .setTitle(R.string.edit_delay)
        .setPositiveButton(R.string.set, new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialogInterface, int i) {
            String value = delayEditText.getText().toString();
            if (value.isEmpty()) {
              delayEditText.setError(getString(R.string.required));
            } else {
              Barricade.getInstance().setDelay(Long.parseLong(value));
              Toast.makeText(BarricadeActivity.this, R.string.updated, Toast.LENGTH_LONG).show();
            }
          }
        })
        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialogInterface, int i) {

          }
        })
        .show();
  }

  private void showResetDialog() {
    new AlertDialog.Builder(this).setMessage(getString(R.string.reset_message))
        .setCancelable(true)
        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialogInterface, int i) {
            Barricade.getInstance().reset();
            endpointsRVAdapter.notifyDataSetChanged();
          }
        })
        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialogInterface, int i) {

          }
        })
        .create()
        .show();
  }
}
