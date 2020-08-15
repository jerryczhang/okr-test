package com.example.okrtest.ui.archive;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.okrtest.GoalsAdapter;
import com.example.okrtest.OutputTextDialog;
import com.example.okrtest.R;
import com.example.okrtest.RecyclerClickListener;
import com.example.okrtest.SaveManager;
import com.example.okrtest.SwipeCallback;

import java.util.ArrayList;
import java.util.Objects;

public class ArchiveFragment extends Fragment {
    private SaveManager saveManager;
    private int numArchived;
    private ArrayList<String> archivedNames = new ArrayList<>();
    private GoalsAdapter goalsAdapter;

    private ArrayList<Integer> nums = new ArrayList<>();
    private ArrayList<Integer> dens = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_archive, container, false);
        saveManager = new SaveManager(getContext());
        loadArchived();
        loadProg();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(getContext()));
        boolean isDark = prefs.getBoolean(getString(R.string.dark_mode_key), false);
        if (isDark) {
            root.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.DARK));
        }

        final RecyclerView archivedRecyclerView = (RecyclerView) root.findViewById(R.id.archivedRecyclerView);

        archivedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        archivedRecyclerView.scrollToPosition(0);

        goalsAdapter = new GoalsAdapter(getContext(), archivedNames, new RecyclerClickListener() {
            @Override
            public void onViewClicked(int position) {
            }
            @Override
            public void onItemClicked(final int position, int id) {
            }
        });
        goalsAdapter.setGoalProg(nums, dens);
        goalsAdapter.setHideRename(true);
        archivedRecyclerView.setAdapter(goalsAdapter);

        SwipeCallback swipeCallback = new SwipeCallback(getContext(), SwipeCallback.ARCHIVED, new SwipeCallback.SwipeListener() {
            @Override
            public void onMove(int fromPosition, int toPosition) {
                String goalName = archivedNames.get(fromPosition);
                if (toPosition < fromPosition) {
                    archivedNames.add(toPosition, goalName);
                    archivedNames.remove(fromPosition + 1);
                } else {
                    archivedNames.add(toPosition + 1, goalName);
                    archivedNames.remove(fromPosition);
                }
                saveManager.saveGoals(numArchived, archivedNames, true);
                goalsAdapter.notifyItemMoved(fromPosition, toPosition);
            }

            @Override
            public void onSwipeLeft(final int position) {
                String title = getString(R.string.delete_goal_dialog_title);
                String message = "Delete \"" + archivedNames.get(position) + "\"?";
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
                String title = getString(R.string.unarchive_goal_dialog_title);
                String message = "Unarchive \"" + archivedNames.get(position) + "\"?";
                String positiveName = getString(R.string.unarchive_goal_dialog_positive);
                String negativeName = getString(R.string.unarchive_goal_dialog_negative);
                OutputTextDialog archiveGoalDialog = new OutputTextDialog(title, message, positiveName, negativeName, new OutputTextDialog.OutputTextListener() {
                    @Override
                    public void onPositiveInput() {
                        unarchive(position);
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
        itemTouchHelper.attachToRecyclerView(archivedRecyclerView);

        return root;
    }

    private void loadArchived() {
        SaveManager.SaveData saveData = saveManager.loadGoals(true);
        numArchived = saveData.getNumData();
        archivedNames = (ArrayList<String>)saveData.getListData(0);
    }

    private void deleteGoal(int position) {
        archivedNames.remove(position);
        nums.remove(position);
        dens.remove(position);
        goalsAdapter.notifyItemRemoved(position);
        --numArchived;
        saveManager.deleteGoal(position, true);
        saveManager.saveGoals(numArchived, archivedNames, true);
    }

    private void loadProg() {
        for (String archivedName : archivedNames) {
            SaveManager.SaveData saveData = saveManager.loadKRs(archivedName);
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

    private void unarchive(int position) {
        SaveManager.SaveData saveData = saveManager.loadGoals(false);
        int numGoals = saveData.getNumData();
        ArrayList<String> goalNames = (ArrayList<String>) saveData.getListData(0);
        goalNames.add(archivedNames.get(position));
        archivedNames.remove(position);
        nums.remove(position);
        dens.remove(position);
        ++numGoals;
        --numArchived;
        saveManager.saveGoals(numGoals, goalNames, false);
        saveManager.saveGoals(numArchived, archivedNames, true);
        goalsAdapter.notifyItemRemoved(position);
    }
}
