package com.yoji.notes;

public interface Keystore {
    boolean hasNotSavedPin ();
    boolean checkPin(String enteredPin);
    void saveNewPin(String newPin);
}
