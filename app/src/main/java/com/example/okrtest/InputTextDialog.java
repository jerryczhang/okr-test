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

import com.example.okrtest.ui.main.Tab1Fragment;

import java.lang.ref.WeakReference;
import java.util.Objects;

public class InputTextDialog extends DialogFragment {
    private String title;
    private String positiveName;
    private String negativeName;
    private String hint;
    private WeakReference<textDialogListener> listenerRef;

    public InputTextDialog(String title,
                           String positiveName,
                           String negativeName,
                           String hint,
                           textDialogListener listener) {
        this.title = title;
        this.positiveName = positiveName;
        this.negativeName = negativeName;
        this.hint = hint;
        listenerRef = new WeakReference<>(listener);
    }

    public interface textDialogListener {
        void onPositiveInput(String text);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = requireActivity().getLayoutInflater().inflate(R.layout.dialog_input_text, null);
        final EditText inputEditText = (EditText) view.findViewById(R.id.inputEditText);
        TextView titleTextView = (TextView) view.findViewById(R.id.titleTextView);
        titleTextView.setText(title);

        inputEditText.setHint(hint);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setPositiveButton(positiveName, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listenerRef.get().onPositiveInput(inputEditText.getText().toString());
            }
        });
        builder.setNegativeButton(negativeName, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Objects.requireNonNull(InputTextDialog.this.getDialog()).cancel();
            }
        });
        return builder.create();
    }
}
