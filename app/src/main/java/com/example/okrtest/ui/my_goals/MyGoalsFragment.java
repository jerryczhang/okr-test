package com.example.okrtest.ui.my_goals;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.okrtest.GoalDetailActivity;
import com.example.okrtest.GoalsAdapter;
import com.example.okrtest.InputTextDialog;
import com.example.okrtest.MainActivity;
import com.example.okrtest.OutputTextDialog;
import com.example.okrtest.R;
import com.example.okrtest.RecyclerClickListener;
import com.example.okrtest.SaveManager;
import com.example.okrtest.SwipeCallback;

import java.io.File;
import java.util.ArrayList;

public class MyGoalsFragment extends Fragment {
    private int numGoals;
    private ArrayList<String> goalNames = new ArrayList<>();
    private GoalsAdapter goalsAdapter;
    private SaveManager saveManager;

    private int numArchived;
    private ArrayList<String> archivedNames = new ArrayList<>();

    private ArrayList<Integer> nums = new ArrayList<>();
    private ArrayList<Integer> dens = new ArrayList<>();

    public static final String EXTRA_GOAL_NAME = "com.example.okrtest.GOAL_NAME";


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_my_goals, container, false);
        saveManager = new SaveManager(getContext());
        loadGoals();
        loadArchived();

        final RecyclerView goalsRecyclerView = (RecyclerView) root.findViewById(R.id.goalsRecyclerView);
        Button addGoal = (Button) root.findViewById(R.id.addGoalButton);

        goalsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        goalsRecyclerView.scrollToPosition(0);

        goalsAdapter = new GoalsAdapter(getContext(), goalNames, new RecyclerClickListener() {
            @Override
            public void onViewClicked(int position) {
                Intent intent = new Intent(getContext(), GoalDetailActivity.class);
                intent.putExtra(EXTRA_GOAL_NAME, goalNames.get(position));
                startActivity(intent);
            }
            @Override
            public void onItemClicked(final int position, int id) {
                if (id == R.id.renameGoalImageView) {
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
                    goalsAdapter.notifyItemChanged(position);
                }
            }
        });
        goalsRecyclerView.setAdapter(goalsAdapter);
        loadProg();
        goalsAdapter.setGoalProg(nums, dens);

        SwipeCallback swipeCallback = new SwipeCallback(getContext(), SwipeCallback.DEFAULT , new SwipeCallback.SwipeListener() {
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
                saveManager.saveGoals(numGoals, goalNames, false);
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
                deleteGoalDialog.show(getParentFragmentManager(), "delete_goal");
                goalsAdapter.notifyItemChanged(position);
            }

            @Override
            public void onSwipeRight(final int position) {
                String title = getString(R.string.archive_goal_dialog_title);
                String message = "Archive \"" + goalNames.get(position) + "\"?";
                String positiveName = getString(R.string.archive_goal_dialog_positive);
                String negativeName = getString(R.string.archive_goal_dialog_negative);
                OutputTextDialog archiveGoalDialog = new OutputTextDialog(title, message, positiveName, negativeName, new OutputTextDialog.OutputTextListener() {
                    @Override
                    public void onPositiveInput() {
                        archive(position);
                    }

                    @Override
                    public void onNegativeInput() {
                    }
                });
                archiveGoalDialog.show(getParentFragmentManager(), "archive_goal");
                goalsAdapter.notifyItemChanged(position);
            }
        } );
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
                            KRExistsDialog.show(getParentFragmentManager(), "goal_exists");
                        } else if (archivedNames.contains(text)) {
                            String title = getString(R.string.goal_exists_dialog_title);
                            String message = getString(R.string.goal_exists_archive_dialog_text);
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

    private void addGoal(String name) {
        goalNames.add(name);
        nums.add(0);
        dens.add(1);
        goalsAdapter.setGoalProg(nums, dens);
        goalsAdapter.notifyItemInserted(goalNames.size() - 1);
        ++numGoals;
        saveManager.saveGoals(numGoals, goalNames, false);
    }

    private void archive(int position) {
        String name = goalNames.get(position);
        archivedNames.add(name);
        ++numArchived;
        goalNames.remove(position);
        nums.remove(position);
        dens.remove(position);
        goalsAdapter.notifyItemRemoved(position);
        --numGoals;
        saveManager.saveGoals(numGoals, goalNames, false);
        saveManager.saveGoals(numArchived, archivedNames, true);
    }

    private void deleteGoal(int position) {
        goalNames.remove(position);
        nums.remove(position);
        dens.remove(position);
        goalsAdapter.notifyItemRemoved(position);
        --numGoals;
        saveManager.deleteGoal(position, false);
        saveManager.saveGoals(numGoals, goalNames, false);
    }

    private void renameGoal(int position, String name) {
        String oldName = goalNames.get(position);
        goalNames.set(position, name);
        saveManager.renameGoal(position, oldName, name);
        saveManager.saveGoals(numGoals, goalNames, false);
        goalsAdapter.notifyItemChanged(position);
    }

    private void loadGoals() {
        SaveManager.SaveData saveData = saveManager.loadGoals(false);
        numGoals = saveData.getNumData();
        goalNames = (ArrayList<String>)saveData.getListData(0);
    }

    private void loadArchived() {
        SaveManager.SaveData saveData = saveManager.loadGoals(true);
        numArchived = saveData.getNumData();
        archivedNames = (ArrayList<String>)saveData.getListData(0);
        Log.d("test_archive", archivedNames.toString());
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
        Intent intent = new Intent(getContext(), MainActivity.class);
        startActivity(intent);
    }
}
