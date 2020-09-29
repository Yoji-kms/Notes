package com.yoji.notes.authentication;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;

import java.util.Objects;

@SuppressWarnings("deprecation")
public class FingerprintUtils {
    private FingerprintUtils(){}

    public static boolean checkFingerprintCompatibility(@NonNull Context context){
        return FingerprintManagerCompat.from(context).isHardwareDetected();
    }

    public static SensorState checkSensorState(@NonNull Context context){
        if (checkFingerprintCompatibility(context)){
            KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
            if (!Objects.requireNonNull(keyguardManager).isKeyguardSecure()){
                return SensorState.NOT_BLOCKED;
            }
            if (!FingerprintManagerCompat.from(context).hasEnrolledFingerprints()){
                return SensorState.NO_FINGERPRINTS;
            }
            return SensorState.READY;
        }else{
            return SensorState.NOT_SUPPORTED;
        }
    }
    public static boolean isSensorStateAt(@NonNull SensorState sensorState, @NonNull Context context){
        return checkSensorState(context) == sensorState;
    }

    public static void enableFingerprint(boolean useFingerprint, Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences("fingerprint",
                Context.MODE_PRIVATE).edit();
        editor.putBoolean(Key.FINGERPRINT, useFingerprint);
        editor.apply();
    }

    public static boolean fingerprintEnabled(Context context) {
        return context.getSharedPreferences("fingerprint", Context.MODE_PRIVATE)
                .getBoolean(Key.FINGERPRINT, false);
    }
}
