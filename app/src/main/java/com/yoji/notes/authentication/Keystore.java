package com.yoji.notes.authentication;

public interface Keystore {
    boolean hasNotSavedPin ();
    boolean checkPin(String enteredPin);
    void saveNewPin(String newPin);
}
