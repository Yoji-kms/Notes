package com.yoji.notes.authentication;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

public final class CryptoUtils {
    private static final String KEY_STORE = "AndroidKeyStore";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final String SALT = "hGCdelAk63";

    private static byte[] iv;

    private CryptoUtils() {
    }

    private static SecretKey generateSecretKey() {
        final KeyGenerator keyGenerator;
        try {
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES,
                    KEY_STORE);
            keyGenerator.init(new KeyGenParameterSpec.Builder(Key.ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .build());
            return keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException | NoSuchProviderException |
                InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getIv() {
        return Base64.encodeToString(iv, Base64.DEFAULT);
    }

    static String encryptText(final String textToEncrypt) {
        final Cipher cipher;
        try {
            cipher = Cipher.getInstance(TRANSFORMATION);

            cipher.init(Cipher.ENCRYPT_MODE, generateSecretKey());
            iv = cipher.getIV();
            byte[] encryption = cipher.doFinal((textToEncrypt + SALT).getBytes(StandardCharsets.UTF_8));

            return (Base64.encodeToString(encryption, Base64.DEFAULT));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static SecretKey getSecretKey() {
        try {
            KeyStore keyStore = KeyStore.getInstance(KEY_STORE);
            keyStore.load(null);
            return ((KeyStore.SecretKeyEntry) keyStore.getEntry(Key.ALIAS, null)).getSecretKey();
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableEntryException |
                CertificateException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    static String decryptData(String encodedString, String savedIv) {
        final Cipher cipher;
        try {
            cipher = Cipher.getInstance(TRANSFORMATION);
            byte[] decodedIv = Base64.decode(savedIv, Base64.DEFAULT);
            final GCMParameterSpec spec = new GCMParameterSpec(128, decodedIv);
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), spec);
            byte[] encryptedData = Base64.decode(encodedString, Base64.DEFAULT);
            String decryptedData = new String(cipher.doFinal(encryptedData), StandardCharsets.UTF_8);
            return decryptedData.replace(SALT, "");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException |
                InvalidAlgorithmParameterException | InvalidKeyException | BadPaddingException |
                IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }
}
