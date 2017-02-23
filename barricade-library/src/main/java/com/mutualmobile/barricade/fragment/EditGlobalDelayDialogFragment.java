package com.mutualmobile.barricade.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.mutualmobile.barricade.Barricade;
import com.mutualmobile.barricade.R;

/**
 * Dialog Fragment to edit global delay value for Barricade
 *
 * @author Mustafa Ali, 27/08/16.
 */
public class EditGlobalDelayDialogFragment extends AppCompatDialogFragment
    implements View.OnClickListener {

  private EditText delayValueEditText;
  private Barricade barricade;

  public EditGlobalDelayDialogFragment() {
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    getDialog().setTitle(R.string.title_edit_global_delay);
    View v = inflater.inflate(R.layout.dialog_edit_global_delay, container, false);

    delayValueEditText = (EditText) v.findViewById(R.id.delay_value_edittext);
    Button saveButton = (Button) v.findViewById(R.id.delay_save_btn);
    Button cancelButton = (Button) v.findViewById(R.id.delay_cancel_btn);

    saveButton.setOnClickListener(this);
    cancelButton.setOnClickListener(this);

    barricade = Barricade.getInstance();
    delayValueEditText.setText(String.valueOf(barricade.getDelay()));

    return v;
  }

  @Override public void onClick(View view) {
    if (view.getId() == R.id.delay_save_btn) {
      saveDelay();
    } else if (view.getId() == R.id.delay_cancel_btn) {
      dismiss();
    }
  }

  private void saveDelay() {
    String value = delayValueEditText.getText().toString();
    if (value.isEmpty()) {
      delayValueEditText.setError(getString(R.string.required));
    } else {
      barricade.setDelay(Long.valueOf(value));
      Toast.makeText(getActivity(), R.string.updated, Toast.LENGTH_LONG).show();
      dismiss();
    }
  }
}
