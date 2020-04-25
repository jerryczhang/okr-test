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
import androidx.fragment.app.Fragment;

import com.example.okrtest.ui.main.Tab1Fragment;

public class AddGoalDialogFragment extends DialogFragment {
    private View view;
    private Tab1Fragment parent;

    public AddGoalDialogFragment(Tab1Fragment parent) {
        this.parent = parent;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        view = requireActivity().getLayoutInflater().inflate(R.layout.dialog_add_goal, null);
        final EditText goalNameEditText = (EditText) view.findViewById(R.id.goalNameEditText);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setPositiveButton(R.string.add_goal_dialog_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                parent.addGoal(goalNameEditText.getText().toString());
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
