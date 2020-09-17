package com.yoji.notes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

public class SettingsActivity extends AppCompatActivity implements FragmentResultListener, Keystore {

    private EditText newPinEdtTxt;
    private EditText confirmPinEdtTxt;
    private Button saveBtn;

    private SharedPreferences sharedPreferences;

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
        if (!checkPin(enteredPin)) {
            saveNewPin(enteredPin);

            if (!fingerprintEnabled()) {
                DialogFragment dialogFragment = new UseFingerprintDialogFragment();
                dialogFragment.show(getSupportFragmentManager(), "dialog_tag");
            }
        }else {
            Toast.makeText(this, R.string.match_pin, Toast.LENGTH_SHORT).show();
            newPinEdtTxt.setText("");
            confirmPinEdtTxt.setText("");
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

        ToggleButton showNewPinBtn = findViewById(R.id.showNewPinBtnId);
        ToggleButton showConfirmPinBtn = findViewById(R.id.showConfirmPinBtnId);
        saveBtn = findViewById(R.id.saveBtnId);
        Button cancelBtn = findViewById(R.id.cancelBtnId);

        cancelBtn.setEnabled(!hasNotSavedPin());

        showNewPinBtn.setOnCheckedChangeListener(showNewPinBtnOnCheckedChangeListener);
        showConfirmPinBtn.setOnCheckedChangeListener(showConfirmPinBtnOnCheckedChangeListener);

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
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Key.FINGERPRINT, useFingerprint);
        editor.apply();
        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean hasNotSavedPin() {
        sharedPreferences = getSharedPreferences("saved_pin", MODE_PRIVATE);
        String savedPin = sharedPreferences.getString(Key.PIN, "");
        return savedPin.equals("");
    }

    @Override
    public boolean checkPin(String pin) {
        String encodedPin = sharedPreferences.getString(Key.PIN, "");
        String savedIv = sharedPreferences.getString(Key.IV, "");
        String decodedPin = CryptoUtils.decryptData(encodedPin, savedIv);
        return pin.equals(decodedPin);
    }

    @Override
    public void saveNewPin(String newPin) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String encodedPin = CryptoUtils.encryptText(newPin);
        editor.putString(Key.PIN, encodedPin);
        editor.putString(Key.IV, CryptoUtils.getIv());
        editor.apply();
    }

    private boolean fingerprintEnabled() {
        sharedPreferences = getSharedPreferences("saved_pin", MODE_PRIVATE);
        return sharedPreferences.getBoolean(Key.FINGERPRINT, false);
    }
}