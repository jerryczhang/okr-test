package com.example.okrtest;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

public class SaveManager {
    private SharedPreferences sharedPreferences;
    private Context c;

    public SaveManager(Context context) {
        c = context;
        sharedPreferences = c.getSharedPreferences("save", Context.MODE_PRIVATE);
    }

    private void refreshSharedPreferences() {
        sharedPreferences = c.getSharedPreferences("save", Context.MODE_PRIVATE);
    }

    public void saveGoals(int numGoals, ArrayList<String> goalNames, boolean archived) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String numString;
        String nameString;
        if (archived) {
            numString = c.getString(R.string.num_archived);
            nameString = c.getString(R.string.archive);
        } else {
            numString = c.getString(R.string.num_goals);
            nameString = c.getString(R.string.goal);
        }
        editor.putInt(numString, numGoals);
        for (int i = 0; i < numGoals; ++i) {
            editor.putString(nameString + i, goalNames.get(i));
        }
        editor.apply();
    }

    public void renameGoal(int position, String oldName, String newName) {
        SaveData KRData = loadKRs(oldName);
        ArrayList<String> KRNames = (ArrayList<String>)KRData.getListData(0);
        ArrayList<Integer> KRNums = (ArrayList<Integer>)KRData.getListData(1);
        ArrayList<Integer> KRDens = (ArrayList<Integer>)KRData.getListData(2);
        String goalDesc = loadGoalDesc(oldName);

        saveKRNames(newName, KRNames);
        for (int i = 0; i < KRNames.size(); ++i) {
            saveKRPRog(newName, KRNames.get(i), KRNums.get(i), KRDens.get(i));
        }
        saveGoalDesc(newName, goalDesc);
        deleteGoal(position, false);
    }

    public SaveData loadGoals(boolean archived) {
        refreshSharedPreferences();
        ArrayList<ArrayList<?>> listHolder = new ArrayList<>();
        int defaultNumGoals = 0;
        String defaultGoalName = "";
        String numString;
        String nameString;
        if (archived) {
            numString = c.getString(R.string.num_archived);
            nameString = c.getString(R.string.archive);
        } else {
            numString = c.getString(R.string.num_goals);
            nameString = c.getString(R.string.goal);
        }
        int numGoals = sharedPreferences.getInt(numString, defaultNumGoals);
        ArrayList<String> goalNames = new ArrayList<>();
        for (int i = 0; i < numGoals; ++i) {
            String goalName = sharedPreferences.getString(nameString + i, defaultGoalName);
            goalNames.add(goalName);
        }
        listHolder.add(goalNames);
        return new SaveData(numGoals, listHolder);
    }

    public void deleteGoal(int position, boolean archived) {
        String goalName;
        if (archived) {
            goalName = ((ArrayList<String>)loadGoals(true).getListData(0)).get(position);
        } else {
            goalName = ((ArrayList<String>)loadGoals(false).getListData(0)).get(position);
        }
        ArrayList<String> KRNames = (ArrayList<String>)loadKRs(goalName).getListData(0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(c.getString(R.string.goal) + position);
        editor.remove(c.getString(R.string.num_krs) + goalName);
        editor.remove(c.getString(R.string.goal_desc) + goalName);
        editor.apply();
        for (int i = 0; i < KRNames.size(); ++i) {
            deleteKR(goalName, i, KRNames.get(i));
        }
    }

    public String loadGoalDesc(String goalName) {
        return sharedPreferences.getString(c.getString(R.string.goal_desc) + goalName, c.getString(R.string.default_goal_desc));
    }

    public void saveGoalDesc(String goalName, String desc) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(c.getString(R.string.goal_desc) + goalName, desc);
        editor.apply();
    }

    public void saveKRNames(String goalName, ArrayList<String> KRNames) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(c.getString(R.string.num_krs) + goalName, KRNames.size());
        for (int i = 0; i < KRNames.size(); ++i) {
            editor.putString(c.getString(R.string.kr) + goalName + '.' + i, KRNames.get(i));
        }
        editor.apply();
    }

    public SaveData loadKRs(String goalName) {
        int defaultNumKRs = 0;
        int defaultNum = 0;
        int defaultDen = 100;
        String defaultKRName = "";
        int numKRs = sharedPreferences.getInt(c.getString(R.string.num_krs) + goalName, defaultNumKRs);
        ArrayList<ArrayList<?>> listHolder = new ArrayList<>();
        ArrayList<String> KRNames = new ArrayList<>();
        ArrayList<Integer> KRNums = new ArrayList<>();
        ArrayList<Integer> KRDens = new ArrayList<>();
        for (int i = 0; i < numKRs; ++i) {
            String KRName = sharedPreferences.getString(c.getString(R.string.kr) + goalName + '.' + i, defaultKRName);
            KRNames.add(KRName);
            KRNums.add(sharedPreferences.getInt(c.getString(R.string.kr_prog_num) + goalName + '.' + KRName, defaultNum));
            KRDens.add(sharedPreferences.getInt(c.getString(R.string.kr_prog_den) + goalName + '.' + KRName, defaultDen));
        }
        listHolder.add(KRNames);
        listHolder.add(KRNums);
        listHolder.add(KRDens);
        return new SaveData(numKRs, listHolder);
    }

    public void deleteKR(String goalName, int position, String KRName) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(c.getString(R.string.kr) + goalName + '.' + position);
        editor.remove(c.getString(R.string.kr_prog_num) + goalName + '.' + KRName);
        editor.remove(c.getString(R.string.kr_prog_den) + goalName + '.' + KRName);
        editor.apply();
    }

    public void saveKRPRog(String goalName, String KRName, int num, int den) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(c.getString(R.string.kr_prog_num) + goalName + '.' + KRName, num);
        editor.putInt(c.getString(R.string.kr_prog_den) + goalName + '.' + KRName, den);
        editor.apply();
    }

    public static class SaveData {
        private int numData;
        private ArrayList<ArrayList<?>> listHolder;

        SaveData(int num, ArrayList<ArrayList<?>> list) {
            numData = num;
            listHolder = list;
        }

        public int getNumData() {
            return numData;
        }

        public ArrayList<?> getListData(int position) {
            return listHolder.get(position);
        }
    }
}
