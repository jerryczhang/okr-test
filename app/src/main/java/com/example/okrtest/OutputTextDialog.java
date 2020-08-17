package com.example.okrtest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.preference.PreferenceManager;

import java.lang.ref.WeakReference;
import java.util.Objects;

public class OutputTextDialog extends DialogFragment {
    private String title;
    private String positiveName;
    private String negativeName;
    private String text;
    private WeakReference<OutputTextListener> listenerRef;

    public OutputTextDialog(String title, String text, String positiveName) {
        this.title = title;
        this.text = text;
        this.positiveName = positiveName;
        this.negativeName = "";
    }

    public OutputTextDialog(String title, String text, String positiveName, String negativeName, OutputTextListener listener) {
        this.title = title;
        this.text = text;
        this.positiveName = positiveName;
        this.negativeName = negativeName;
        this.listenerRef = new WeakReference<>(listener);
    }

    public interface OutputTextListener {
        void onPositiveInput();
        void onNegativeInput();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(getContext()));
        boolean isDark = prefs.getBoolean(getString(R.string.dark_mode_key), false);
        AlertDialog.Builder builder;
        if (isDark) {
            builder = new AlertDialog.Builder(getActivity(), R.style.DarkDialog);
        } else {
            builder = new AlertDialog.Builder(getActivity());
        }
        builder.setTitle(title).setMessage(text);
        builder.setPositiveButton(positiveName, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (listenerRef != null) {
                    listenerRef.get().onPositiveInput();
                }
            }
        });
        builder.setNegativeButton(negativeName, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (listenerRef != null) {
                    listenerRef.get().onNegativeInput();
                }
            }
        });
        return builder.create();
    }
}
