package com.example.okrtest;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.media.tv.TvContract;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
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
import com.example.okrtest.SwipeCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MainActivity extends AppCompatActivity {
    private int numGoals;
    private ArrayList<String> goalNames = new ArrayList<>();
    private GoalsAdapter goalsAdapter;
    private SaveManager saveManager;

    private ArrayList<Integer> nums = new ArrayList<>();
    private ArrayList<Integer> dens = new ArrayList<>();

    public static final String EXTRA_GOAL_NAME = "com.example.okrtest.GOAL_NAME";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        saveManager = new SaveManager(MainActivity.this);
        loadGoals();

        setContentView(R.layout.fragment_tab1);

        final RecyclerView goalsRecyclerView = (RecyclerView) findViewById(R.id.goalsRecyclerView);
        Button addGoal = (Button) findViewById(R.id.addGoalButton);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.goalProgressBar);

        goalsRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        goalsRecyclerView.scrollToPosition(0);

        goalsAdapter = new GoalsAdapter(MainActivity.this, goalNames, new RecyclerClickListener() {
            @Override
            public void onViewClicked(int position) {
                Intent intent = new Intent(MainActivity.this, GoalDetailActivity.class);
                intent.putExtra(EXTRA_GOAL_NAME, goalNames.get(position));
                startActivity(intent);
            }
            @Override
            public void onItemClicked(final int position, int id) {
            }
        });
        goalsRecyclerView.setAdapter(goalsAdapter);
        loadProg();
        goalsAdapter.setGoalProg(nums, dens);

        SwipeCallback swipeCallback = new SwipeCallback(MainActivity.this, new SwipeCallback.SwipeListener() {
            @Override
            public void onMove(int fromPosition, int toPosition) {
                String goalName = goalNames.get(fromPosition);
                if (toPosition < fromPosition) {
                    goalNames.add(toPosition, goalName);
                    goalNames.remove(fromPosition + 1);
                } else {
                    goalNames.add(toPosition + 1, goalName);
                    goalNames.remove(fromPosition);
                }
                saveManager.saveGoals(numGoals, goalNames);
                goalsAdapter.notifyItemMoved(fromPosition, toPosition);
            }

            @Override
            public void onSwipeLeft(final int position) {
                String title = getString(R.string.delete_goal_dialog_title);
                String message = "Delete \"" + goalNames.get(position) + "\"?";
                String positiveName = getString(R.string.delete_goal_dialog_positive);
                String negativeName = getString(R.string.delete_goal_dialog_negative);
                OutputTextDialog deleteGoalDialog = new OutputTextDialog(title, message, positiveName, negativeName, new OutputTextDialog.OutputTextListener() {
                    @Override
                    public void onPositiveInput() {
                        deleteGoal(position);
                    }

                    @Override
                    public void onNegativeInput() {
                    }
                });
                deleteGoalDialog.show(getSupportFragmentManager(), "delete_goal");
                goalsAdapter.notifyItemChanged(position);
            }

            @Override
            public void onSwipeRight(final int position) {
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
                            KRExistsDialog.show(getSupportFragmentManager(), "goal_exists");
                        } else {
                            renameGoal(position, text);
                        }
                    }
                });
                renameKRDialog.show(getSupportFragmentManager(), "rename_goal");
                goalsAdapter.notifyItemChanged(position);
            }
        });
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeCallback);
        itemTouchHelper.attachToRecyclerView(goalsRecyclerView);

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
                            KRExistsDialog.show(getSupportFragmentManager(), "goal_exists");
                        } else {
                            addGoal(text);
                        }
                    }
                });
                addGoalDialog.show(getSupportFragmentManager(), "add_goal");
            }
        });

        Button clearSharedPrefButton = (Button) findViewById(R.id.clearSharedPrefButton);
        clearSharedPrefButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearSharedPreferences();
            }
        });
    }

    public void addGoal(String name) {
        goalNames.add(name);
        nums.add(0);
        dens.add(1);
        goalsAdapter.setGoalProg(nums, dens);
        goalsAdapter.notifyItemInserted(goalNames.size() - 1);
        ++numGoals;
        saveManager.saveGoals(numGoals, goalNames);
    }

    private void deleteGoal(int position) {
        goalNames.remove(position);
        nums.remove(position);
        dens.remove(position);
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

    private void loadProg() {
        for (String goalName : goalNames) {
            SaveManager.SaveData saveData = saveManager.loadKRs(goalName);
            ArrayList<Integer> KRNums = (ArrayList<Integer>) saveData.getListData(1);
            ArrayList<Integer> KRDens = (ArrayList<Integer>) saveData.getListData(2);
            if (KRNums.size() == 0) {
                nums.add(0);
                dens.add(1);
            }
            else {
                nums.add(sum(KRNums));
                dens.add(sum(KRDens));
            }
        }
    }

    private int sum(ArrayList<Integer> list) {
        int sum = 0;
        for (int i : list) {
            sum += i;
        }
        return sum;
    }

    private void clearSharedPreferences() {
        File sharedPreferenceFile = new File("/data/data/com.example.okrtest/shared_prefs/");
        File[] listFiles = sharedPreferenceFile.listFiles();
        for (File file : listFiles) {
            file.delete();
        }
    }
}
