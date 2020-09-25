package com.yoji.notes.authentication;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;
import androidx.core.widget.ImageViewCompat;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yoji.notes.NoteListActivity;
import com.yoji.notes.R;

import java.util.concurrent.Executor;

public class LoginActivity extends AuthenticationActivity {

    private StringBuilder enteredPinSb;

    private ImageView[] pinCircles = new ImageView[4];
    private Button[] numBtns = new Button[10];
    private TextView messageTxtView;

    private View.OnClickListener numBtnOnClickListener = v -> {
        enteredPinSb.append(((Button) v).getText().toString());
        if (enteredPinSb.length() == 4) {
            checkPin();
        }
        setPinCirclesColor();
    };

    private View.OnClickListener backspaceBtnOnClickListener = v -> {
        if (enteredPinSb.length() > 0) {
            enteredPinSb.setLength(enteredPinSb.length() - 1);
        }
        setPinCirclesColor();
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (hasNotSavedPin()) {
            createPin();
        }

        init();

        if (fingerprintEnabled()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                biometric();
            }else {
                messageTxtView.setVisibility(View.VISIBLE);
                fingerprint();
            }
        }else{
            messageTxtView.setVisibility(View.INVISIBLE);
        }
    }

    private void init() {
        enteredPinSb = new StringBuilder();
        pinCircles[0] = findViewById(R.id.pinCircle1Id);
        pinCircles[1] = findViewById(R.id.pinCircle2Id);
        pinCircles[2] = findViewById(R.id.pinCircle3Id);
        pinCircles[3] = findViewById(R.id.pinCircle4Id);

        numBtns[0] = findViewById(R.id.zeroBtnId);
        numBtns[1] = findViewById(R.id.oneBtnId);
        numBtns[2] = findViewById(R.id.twoBtnId);
        numBtns[3] = findViewById(R.id.threeBtnId);
        numBtns[4] = findViewById(R.id.fourBtnId);
        numBtns[5] = findViewById(R.id.fiveBtnId);
        numBtns[6] = findViewById(R.id.sixBtnId);
        numBtns[7] = findViewById(R.id.sevenBtnId);
        numBtns[8] = findViewById(R.id.eightBtnId);
        numBtns[9] = findViewById(R.id.nineBtnId);
        Button backspaceBtn = findViewById(R.id.backspaceBtnId);

        messageTxtView = findViewById(R.id.messageTxtViewId);

        for (Button numBtn : numBtns) {
            numBtn.setOnClickListener(numBtnOnClickListener);
        }
        backspaceBtn.setOnClickListener(backspaceBtnOnClickListener);
    }

    private void biometric() {
        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt biometricPrompt = new BiometricPrompt(LoginActivity.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                if (errorCode == BiometricPrompt.ERROR_USER_CANCELED) {
                    onBackPressed();
                }else if (errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                    Toast.makeText(getApplicationContext(),
                            "Authentication error: " + errString, Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(),
                        "Authentication succeeded!", Toast.LENGTH_SHORT).show();
                startMainActivity();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Authentication failed",
                        Toast.LENGTH_SHORT).show();
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login")
                .setSubtitle("Log in using biometric")
                .setNegativeButtonText("Cancel")
                .build();
        biometricPrompt.authenticate(promptInfo);
    }

    @SuppressWarnings("deprecation")
    private void fingerprint() {
        if (FingerprintUtils.isSensorStateAt(SensorState.READY, this)) {
            Toast.makeText(this, "Use fingerprint to login", Toast.LENGTH_SHORT).show();
            new FingerprintManagerCompat.AuthenticationCallback() {

                @Override
                public void onAuthenticationError(int errMsgId, CharSequence errString) {
                    Toast.makeText(LoginActivity.this, errString, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
                    Toast.makeText(LoginActivity.this, helpString, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
                    startMainActivity();
                }

                @Override
                public void onAuthenticationFailed() {
                    Toast.makeText(LoginActivity.this, "Try again", Toast.LENGTH_SHORT).show();
                }
            };
        }
    }

    private void setPinCirclesColor() {
        for (int i = 0; i < 4; i++) {
            setPinCircleColor(pinCircles[i], i < enteredPinSb.length());
        }
    }

    private void setPinCircleColor(ImageView pinCircle, boolean full) {
        if (full) {
            int FULL_COLOR = R.color.pin_circle_full;
            ImageViewCompat.setImageTintList(pinCircle, ColorStateList.valueOf(
                    ContextCompat.getColor(this, FULL_COLOR)));
        } else {
            int EMPTY_COLOR = R.color.pin_circle_empty;
            ImageViewCompat.setImageTintList(pinCircle, ColorStateList.valueOf(
                    ContextCompat.getColor(this, EMPTY_COLOR)));
        }
    }

    private void checkPin() {
        if (super.checkPin(enteredPinSb.toString())) {
            startMainActivity();
        } else {
            Toast.makeText(this, R.string.wrong_pin, Toast.LENGTH_SHORT).show();
            enteredPinSb.setLength(0);
            setPinCirclesColor();
        }
    }

    private void createPin() {
        Intent intent = new Intent(LoginActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    private void startMainActivity() {
        Intent intent = new Intent(LoginActivity.this, NoteListActivity.class);
        startActivity(intent);
    }
}