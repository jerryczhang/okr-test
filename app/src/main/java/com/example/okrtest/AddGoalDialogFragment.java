package com.example.okrtest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.okrtest.ui.main.Tab1Fragment;

public class AddGoalDialogFragment extends DialogFragment {
    private View view;

    public interface NoticeDialogListener {
       void onDialogPositiveClick(DialogFragment dialog);
    }

    private NoticeDialogListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (NoticeDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement NoticeDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        view = requireActivity().getLayoutInflater().inflate(R.layout.dialog_add_goal, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setPositiveButton(R.string.add_goal_dialog_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onDialogPositiveClick(AddGoalDialogFragment.this);
            }
        });
        builder.setNegativeButton(R.string.add_goal_dialog_negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AddGoalDialogFragment.this.getDialog().cancel();
            }
        });
        return builder.create();
    }
}
