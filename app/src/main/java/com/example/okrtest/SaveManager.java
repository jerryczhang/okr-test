package com.example.okrtest;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.prefs.Preferences;

public class SaveManager {
    private SharedPreferences sharedPreferences;
    private Context c;

    public SaveManager(Context context) {
        c = context;
        sharedPreferences = c.getSharedPreferences("save", Context.MODE_PRIVATE);
    }

    public void saveGoals(int numGoals, ArrayList<String> goalNames) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(c.getString(R.string.num_goals), numGoals);
        for (int i = 0; i < numGoals; ++i) {
            editor.putString(c.getString(R.string.goal) + i, goalNames.get(i));
        }
        editor.apply();
    }

    public SaveData loadGoals() {
        int defaultNumGoals = 0;
        String defaultGoalName = "";
        int numGoals = sharedPreferences.getInt(c.getString(R.string.num_goals), defaultNumGoals);
        ArrayList<String> goalNames = new ArrayList<>();
        for (int i = 0; i < numGoals; ++i) {
            String goalName = sharedPreferences.getString(c.getString(R.string.goal) + i, defaultGoalName);
            goalNames.add(goalName);
        }
        return new SaveData(numGoals, goalNames);
    }

    public static class SaveData {
        private int numData;
        private ArrayList<String> listData;

        SaveData(int num, ArrayList<String> list) {
            numData = num;
            listData = list;
        }

        public int getNumData() {
            return numData;
        }

        public ArrayList<String> getListData() {
            return listData;
        }
    }
}
