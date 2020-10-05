package com.yoji.notes.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.yoji.notes.App;
import com.yoji.notes.R;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity implements FragmentResultListener {

    private EditText newPinEdtTxt;
    private EditText confirmPinEdtTxt;
    private Button saveBtn;

    private TextWatcher confirmPinTxtWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String newPin = newPinEdtTxt.getText().toString().trim();
            String confirmPin = confirmPinEdtTxt.getText().toString().trim();

            saveBtn.setEnabled(confirmPin.length() == 4 && confirmPin.equals(newPin));
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    private CompoundButton.OnCheckedChangeListener showNewPinBtnOnCheckedChangeListener =
            (buttonView, isChecked) -> showPassword(isChecked, newPinEdtTxt);

    private CompoundButton.OnCheckedChangeListener showConfirmPinBtnOnCheckedChangeListener =
            (buttonView, isChecked) -> showPassword(isChecked, confirmPinEdtTxt);

    private View.OnClickListener saveBtnOnClickListener = v -> {
        String enteredPin = confirmPinEdtTxt.getText().toString().trim();
        if (App.getKeystore().checkPin(enteredPin)){
            Toast.makeText(this, R.string.pin_match, Toast.LENGTH_SHORT)
                    .show();
            newPinEdtTxt.setText("");
            confirmPinEdtTxt.setText("");
        }else {
            App.getKeystore().saveNewPin(enteredPin);

            if (!FingerprintUtils.fingerprintEnabled(this)
                    && FingerprintUtils.isSensorStateAt(SensorState.READY, this)) {
                DialogFragment dialogFragment = new UseFingerprintDialogFragment();
                dialogFragment.show(getSupportFragmentManager(), "dialog_tag");
            } else {
                finish();
            }
        }
    };

    private View.OnClickListener cancelBtnOnClickListener = v -> finish();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        init();
    }

    private void init() {
        newPinEdtTxt = findViewById(R.id.newPinEdtTxtId);
        confirmPinEdtTxt = findViewById(R.id.confirmPinEdtTxtId);
        Toolbar toolbar = findViewById(R.id.toolbarId);

        ToggleButton showNewPinBtn = findViewById(R.id.showNewPinBtnId);
        ToggleButton showConfirmPinBtn = findViewById(R.id.showConfirmPinBtnId);
        saveBtn = findViewById(R.id.saveBtnId);
        Button cancelBtn = findViewById(R.id.cancelBtnId);

        cancelBtn.setEnabled(!App.getKeystore().hasNotSavedPin());

        showNewPinBtn.setOnCheckedChangeListener(showNewPinBtnOnCheckedChangeListener);
        showConfirmPinBtn.setOnCheckedChangeListener(showConfirmPinBtnOnCheckedChangeListener);

        toolbar.setTitle(R.string.settings);
        setSupportActionBar(toolbar);
        if (!App.getKeystore().hasNotSavedPin()) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        }

        saveBtn.setOnClickListener(saveBtnOnClickListener);
        cancelBtn.setOnClickListener(cancelBtnOnClickListener);

        confirmPinEdtTxt.addTextChangedListener(confirmPinTxtWatcher);
    }

    private void showPassword(boolean hide, EditText passwordEdtTxt) {
        if (hide) {
            passwordEdtTxt.setTransformationMethod(null);
        } else {
            passwordEdtTxt.setTransformationMethod(new PasswordTransformationMethod());
        }
    }

    @Override
    public void onFinishDialogListener(boolean useFingerprint) {
        FingerprintUtils.enableFingerprint(useFingerprint, this);
        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}