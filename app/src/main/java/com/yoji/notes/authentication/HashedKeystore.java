package com.yoji.notes.authentication;

import android.content.Context;
import android.content.SharedPreferences;

public class HashedKeystore implements Keystore {

    private SharedPreferences sharedPreferences;

    public HashedKeystore(Context context){

        sharedPreferences = context.getSharedPreferences("saved_pin", Context.MODE_PRIVATE);
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
}
