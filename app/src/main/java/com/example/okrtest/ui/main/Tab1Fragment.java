package com.example.okrtest.ui.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.okrtest.InputTextDialog;
import com.example.okrtest.GoalDetailActivity;
import com.example.okrtest.GoalsAdapter;
import com.example.okrtest.R;

import java.util.ArrayList;
import java.util.Objects;

public class Tab1Fragment extends Fragment {
    private int numGoals;
    private ArrayList<String> goalNames = new ArrayList<String>();
    private GoalsAdapter goalsAdapter;
    private SharedPreferences sharedPreferences;

    public static final String EXTRA_GOAL_NAME = "com.example.okrtest.GOAL_NAME";

    @Override
    public void onStart() {
        super.onStart();
        loadGoals();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = Objects.requireNonNull(getActivity()).getPreferences(Context.MODE_PRIVATE);
    }
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_tab1, container, false);

        RecyclerView goalsRecyclerView = (RecyclerView) root.findViewById(R.id.goalsRecyclerView);
        final ImageView addGoal = (ImageView) root.findViewById(R.id.addGoalImageView);

        goalsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        goalsRecyclerView.scrollToPosition(0);

        goalsAdapter = new GoalsAdapter(this.getContext(), goalNames, new GoalsAdapter.ClickListener() {
            @Override
            public void onViewClicked(int position) {
                Intent intent = new Intent(getContext(), GoalDetailActivity.class);
                intent.putExtra(EXTRA_GOAL_NAME, goalNames.get(position));
                startActivity(intent);
            }
            @Override
            public void onItemClicked(int position, int id) {
                if (id == R.id.deleteGoalButton) {
                    deleteGoal(position);
                }
            }
        });
        goalsRecyclerView.setAdapter(goalsAdapter);

        addGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Resources res = getResources();
                String title = res.getString(R.string.add_goal_dialog_title);
                String positiveName = res.getString(R.string.add_goal_dialog_positive);
                String negativeName = res.getString(R.string.add_goal_dialog_negative);
                String hint = res.getString(R.string.add_goal_dialog_hint);
                DialogFragment addGoalDialog = new InputTextDialog(title, positiveName, negativeName, hint, new InputTextDialog.textDialogListener() {
                    @Override
                    public void onPositiveInput(String text) {
                        addGoal(text);
                    }
                });
                addGoalDialog.show(getParentFragmentManager(), "add_goal");
            }
        });

        Button clearSharedPrefButton = (Button) root.findViewById(R.id.clearSharedPrefButton);
        clearSharedPrefButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearSharedPreferences();
            }
        });
        return root;
    }

    public void addGoal(String name) {
        goalNames.add(name);
        goalsAdapter.notifyItemInserted(goalNames.size() - 1);
        ++numGoals;
        saveGoals();
    }

    private void deleteGoal(int position) {
        goalNames.remove(position);
        goalsAdapter.notifyItemRemoved(position);
        --numGoals;
        saveGoals();
    }

    private void saveGoals() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(getString(R.string.num_goals), numGoals);
        for (int i = 0; i < numGoals; ++i) {
            editor.putString(getString(R.string.goal) + i, goalNames.get(i));
        }
        editor.apply();
    }

    private void loadGoals() {
        int defaultNumGoals = 0;
        String defaultGoalName = "";
        numGoals = sharedPreferences.getInt(getString(R.string.num_goals), defaultNumGoals);
        goalNames.clear();
        for (int i = 0; i < numGoals; ++i) {
            String goalName = sharedPreferences.getString(getString(R.string.goal) + i, defaultGoalName);
            goalNames.add(goalName);
            goalsAdapter.notifyItemInserted(goalNames.size() - 1);
        }
    }

    private void clearSharedPreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
