package com.yoji.notes.authentication;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({Key.PIN, Key.FINGERPRINT, Key.IV, Key.ALIAS})
public @interface Key{
    String PIN = "key_pin";
    String FINGERPRINT = "key_fingerprint";
    String IV = "key_iv";
    String ALIAS = "key_alias";
}

