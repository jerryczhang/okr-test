package com.example.okrtest.ui.main;

import android.app.Activity;
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
import com.example.okrtest.KRAdapter;
import com.example.okrtest.OutputTextDialog;
import com.example.okrtest.R;
import com.example.okrtest.RecyclerClickListener;
import com.example.okrtest.SaveManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class Tab1Fragment extends Fragment {
    private int numGoals;
    private ArrayList<String> goalNames = new ArrayList<>();
    private GoalsAdapter goalsAdapter;
    private SaveManager saveManager;

    public static final String EXTRA_GOAL_NAME = "com.example.okrtest.GOAL_NAME";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        saveManager = new SaveManager(getContext());
        loadGoals();
    }
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_tab1, container, false);

        RecyclerView goalsRecyclerView = (RecyclerView) root.findViewById(R.id.goalsRecyclerView);
        ImageView addGoal = (ImageView) root.findViewById(R.id.addGoalImageView);

        goalsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        goalsRecyclerView.scrollToPosition(0);

        goalsAdapter = new GoalsAdapter(this.getContext(), goalNames, new RecyclerClickListener() {
            @Override
            public void onViewClicked(int position) {
                Intent intent = new Intent(getContext(), GoalDetailActivity.class);
                intent.putExtra(EXTRA_GOAL_NAME, goalNames.get(position));
                startActivity(intent);
            }
            @Override
            public void onItemClicked(final int position, int id) {
                if (id == R.id.deleteGoalImageView) {
                    deleteGoal(position);
                } else if (id == R.id.goalNameTextView) {
                    String title = getString(R.string.rename_goal_dialog_title);
                    String positiveName = getString(R.string.rename_goal_dialog_positive);
                    String negativeName = getString(R.string.rename_goal_dialog_negative);
                    String hint = getString(R.string.rename_goal_dialog_hint);
                    DialogFragment renameKRDialog = new InputTextDialog(title, positiveName, negativeName, hint, new InputTextDialog.textDialogListener() {
                        @Override
                        public void onPositiveInput(String text) {
                            if (goalNames.contains(text)) {
                                String title = getString(R.string.goal_exists_dialog_title);
                                String message = getString(R.string.goal_exists_dialog_text);
                                String positiveName = getString(R.string.goal_exists_dialog_positive);
                                OutputTextDialog KRExistsDialog = new OutputTextDialog(title, message, positiveName);
                                KRExistsDialog.show(getParentFragmentManager(), "goal_exists");
                            } else {
                                renameGoal(position, text);
                            }
                        }
                    });
                    renameKRDialog.show(getParentFragmentManager(), "rename_goal");
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
                        if (goalNames.contains(text)) {
                            String title = getString(R.string.goal_exists_dialog_title);
                            String message = getString(R.string.goal_exists_dialog_text);
                            String positiveName = getString(R.string.goal_exists_dialog_positive);
                            OutputTextDialog KRExistsDialog = new OutputTextDialog(title, message, positiveName);
                            KRExistsDialog.show(getParentFragmentManager(), "goal_exists");
                        } else {
                            addGoal(text);
                        }
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
        saveManager.saveGoals(numGoals, goalNames);
    }

    private void deleteGoal(int position) {
        goalNames.remove(position);
        goalsAdapter.notifyItemRemoved(position);
        --numGoals;
        saveManager.deleteGoal(position);
        saveManager.saveGoals(numGoals, goalNames);
    }

    private void renameGoal(int position, String name) {
        String oldName = goalNames.get(position);
        goalNames.set(position, name);
        saveManager.renameGoal(position, oldName, name);
        saveManager.saveGoals(numGoals, goalNames);
        goalsAdapter.notifyItemChanged(position);
    }

    private void loadGoals() {
        SaveManager.SaveData saveData = saveManager.loadGoals();
        numGoals = saveData.getNumData();
        goalNames = (ArrayList<String>)saveData.getListData(0);
    }

    private void clearSharedPreferences() {
        File sharedPreferenceFile = new File("/data/data/com.example.okrtest/shared_prefs/");
        File[] listFiles = sharedPreferenceFile.listFiles();
        for (File file : listFiles) {
            file.delete();
        }
    }
}
