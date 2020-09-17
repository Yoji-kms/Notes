package com.yoji.notes.authentication;

import android.app.KeyguardManager;
import android.content.Context;

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
}
