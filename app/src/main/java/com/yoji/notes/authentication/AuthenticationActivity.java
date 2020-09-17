package com.yoji.notes.authentication;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AuthenticationActivity extends AppCompatActivity implements Keystore {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences("saved_pin", MODE_PRIVATE);
    }

    @Override
    public boolean hasNotSavedPin() {
        String savedPin = sharedPreferences.getString(Key.PIN, "");
        return savedPin.equals("");
    }

    @Override
    public boolean checkPin(String enteredPin) {
        String encodedSavedPin = sharedPreferences.getString(Key.PIN, "");
        String savedIv = sharedPreferences.getString(Key.IV, "");
        String decodedSavedPin = CryptoUtils.decryptData(encodedSavedPin, savedIv);
        return enteredPin.equals(decodedSavedPin);
    }

    @Override
    public void saveNewPin(String newPin) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String encodedPin = CryptoUtils.encryptText(newPin);
        editor.putString(Key.PIN, encodedPin);
        editor.putString(Key.IV, CryptoUtils.getIv());
        editor.apply();
    }

    public void enableFingerprint(boolean useFingerprint){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Key.FINGERPRINT, useFingerprint);
        editor.apply();
    }

    public boolean fingerprintEnabled() {
        return sharedPreferences.getBoolean(Key.FINGERPRINT, false);
    }
}
