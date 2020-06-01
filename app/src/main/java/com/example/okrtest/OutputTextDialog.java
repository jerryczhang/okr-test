package com.example.okrtest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

public class OutputTextDialog extends DialogFragment {
    private String title;
    private String positiveName;
    private String text;

    public OutputTextDialog(String title, String text, String positiveName) {
        this.title = title;
        this.text = text;
        this.positiveName = positiveName;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title).setMessage(text).setPositiveButton(positiveName, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        return builder.create();
    }
}
