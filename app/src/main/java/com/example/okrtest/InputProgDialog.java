package com.example.okrtest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.lang.ref.WeakReference;
import java.util.Objects;

public class InputProgDialog extends DialogFragment {
    private String title;
    private String positiveName;
    private String negativeName;
    private int hintNum;
    private int hintDen;
    private WeakReference<progDialogListener> listenerRef;

    public InputProgDialog(String title,
                           String positiveName,
                           String negativeName,
                           int hintNum,
                           int hintDen,
                           progDialogListener listener) {
        this.title = title;
        this.positiveName = positiveName;
        this.negativeName = negativeName;
        this.hintNum = hintNum;
        this.hintDen = hintDen;
        listenerRef = new WeakReference<>(listener);
    }

    public interface progDialogListener {
        void onPositiveInput(int num, int den);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = requireActivity().getLayoutInflater().inflate(R.layout.dialog_input_prog, null);
        final EditText numEditText = (EditText) view.findViewById(R.id.numEditText);
        final EditText denEditText = (EditText) view.findViewById(R.id.denEditText);
        TextView titleTextView = (TextView) view.findViewById(R.id.titleTextView);
        titleTextView.setText(title);

        numEditText.setText(String.valueOf(hintNum));
        denEditText.setText(String.valueOf(hintDen));
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setPositiveButton(positiveName, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int num = Integer.parseInt(numEditText.getText().toString());
                int den = Integer.parseInt(denEditText.getText().toString());
                listenerRef.get().onPositiveInput(num, den);
            }
        });
        builder.setNegativeButton(negativeName, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Objects.requireNonNull(InputProgDialog.this.getDialog()).cancel();
            }
        });
        return builder.create();
    }
}
