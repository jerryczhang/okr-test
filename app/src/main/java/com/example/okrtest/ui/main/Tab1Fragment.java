package com.example.okrtest.ui.main;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.okrtest.AddGoalDialogFragment;
import com.example.okrtest.GoalsAdapter;
import com.example.okrtest.R;

import java.util.ArrayList;
import java.util.Arrays;

public class Tab1Fragment extends Fragment implements AddGoalDialogFragment.NoticeDialogListener {
    private ArrayList<String> goalNames;
    private GoalsAdapter goalsAdapter;
    private ImageView addGoal;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_tab1, container, false);

        Resources res = getResources();
        goalNames = new ArrayList<String>(Arrays.asList(res.getStringArray(R.array.goal_names)));

        RecyclerView goalsRecyclerView = (RecyclerView) root.findViewById(R.id.goalsRecyclerView);

        goalsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        goalsRecyclerView.scrollToPosition(0);

        goalsAdapter = new GoalsAdapter(this.getContext(), goalNames);
        goalsRecyclerView.setAdapter(goalsAdapter);

        addGoal = (ImageView) root.findViewById(R.id.addGoalImageView);
        addGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment addGoalDialogFragment = new AddGoalDialogFragment();
                addGoalDialogFragment.show(getParentFragmentManager(), "add_goal");
            }
        });

        return root;
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        goalNames.add("Goal " + (goalNames.size() + 1));
        goalsAdapter.notifyItemInserted(goalNames.size() - 1);
    }
}
