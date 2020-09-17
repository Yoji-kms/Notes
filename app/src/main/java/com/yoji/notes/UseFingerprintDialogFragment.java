package com.yoji.notes;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;

public class UseFingerprintDialogFragment extends DialogFragment implements
        DialogInterface.OnClickListener {

    private FragmentResultListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        String title;
        String message;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            title = getString(R.string.dialog_title_biometric);
            message = getString(R.string.dialog_message_biometric);
        } else {
            title = getString(R.string.dialog_title_fingerprint);
            message = getString(R.string.dialog_message_fingerprint);
        }
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(getString(R.string.yes), this)
                .setNegativeButton(getString(R.string.no), this);
        return adb.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case Dialog.BUTTON_POSITIVE:
                listener.onFinishDialog(true);
                dismiss();
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                listener.onFinishDialog(false);
                dismiss();
                break;
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (FragmentResultListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement FragmentResultListener");
        }
    }
}