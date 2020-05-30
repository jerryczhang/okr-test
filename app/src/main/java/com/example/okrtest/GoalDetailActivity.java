package com.example.okrtest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.okrtest.ui.main.Tab1Fragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Objects;

public class GoalDetailActivity extends AppCompatActivity {
    private String goalName;
    private int goalPos;
    private String goalDesc;
    private SharedPreferences sharedPreferences;
    private KRAdapter KRAdapter;
    private RecyclerView KRRecyclerView;

    private ArrayList<String> KRNames = new ArrayList<>();
    private ArrayList<Integer> KRNums = new ArrayList<>();
    private ArrayList<Integer> KRDens = new ArrayList<>();

    private int numKRs;

    private TextView goalDescTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_detail);
        goalDescTextView = (TextView) findViewById(R.id.goalDescTextView);
        sharedPreferences = GoalDetailActivity.this.getPreferences(Context.MODE_PRIVATE);

        goalName = getIntent().getStringExtra(Tab1Fragment.EXTRA_GOAL_NAME);
        goalPos = getIntent().getIntExtra(Tab1Fragment.EXTRA_GOAL_POS, 0);

        ImageView addKR = (ImageView) findViewById(R.id.addKRImageView);
        addKR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Resources res = getResources();
                String title = res.getString(R.string.add_kr_dialog_title);
                String positiveName = res.getString(R.string.add_kr_dialog_positive);
                String negativeName = res.getString(R.string.add_kr_dialog_negative);
                String hint = res.getString(R.string.add_kr_dialog_hint);
                DialogFragment addGoalDialog = new InputTextDialog(title, positiveName, negativeName, hint, new InputTextDialog.textDialogListener() {
                    @Override
                    public void onPositiveInput(String text) {
                        addKR(text);
                    }
                });
                addGoalDialog.show(getSupportFragmentManager(), "add_goal");
            }
        });

        goalDescTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = getString(R.string.change_desc_dialog_title);
                String positiveName = getString(R.string.change_desc_dialog_positive);
                String negativeName = getString(R.string.change_desc_dialog_negative);
                String hint = getString(R.string.change_desc_dialog_hint);
                DialogFragment changeDescDialog = new InputTextDialog(title, positiveName, negativeName, hint, new InputTextDialog.textDialogListener() {
                    @Override
                    public void onPositiveInput(String text) {
                        setGoalDesc(text);
                    }
                });
                changeDescDialog.show(getSupportFragmentManager(), "change_desc");
            }
        });

        KRRecyclerView = (RecyclerView) findViewById(R.id.KRRecyclerView);
        KRRecyclerView.setLayoutManager(new LinearLayoutManager(GoalDetailActivity.this));
        KRRecyclerView.scrollToPosition(0);

        KRAdapter = new KRAdapter(GoalDetailActivity.this, KRNames, new RecyclerClickListener() {
            @Override
            public void onViewClicked(int position) {
            }

            @Override
            public void onItemClicked(final int position, int id) {
                if (id == R.id.deleteKRImageView) {
                    deleteKR(position);
                } else if (id == R.id.KRNameTextView) {
                    String title = getString(R.string.rename_kr_dialog_title);
                    String positiveName = getString(R.string.rename_kr_dialog_positive);
                    String negativeName = getString(R.string.rename_kr_dialog_negative);
                    String hint = getString(R.string.rename_kr_dialog_hint);
                    DialogFragment renameKRDialog = new InputTextDialog(title, positiveName, negativeName, hint, new InputTextDialog.textDialogListener() {
                        @Override
                        public void onPositiveInput(String text) {
                            renameKR(position, text);
                        }
                    });
                    renameKRDialog.show(getSupportFragmentManager(), "rename_kr");
                } else if (id == R.id.editProgButton) {
                    final KRAdapter.ViewHolder v = (KRAdapter.ViewHolder)KRRecyclerView.findViewHolderForAdapterPosition(position);
                    String title = getString(R.string.edit_prog_dialog_title);
                    String positiveName = getString(R.string.edit_prog_dialog_positive);
                    String negativeName = getString(R.string.edit_prog_dialog_negative);
                    assert v != null;
                    int hintNum = v.getProgNum();
                    int hintDen = v.getProgDen();
                    DialogFragment editProgDialog = new InputProgDialog(title, positiveName, negativeName, hintNum, hintDen, new InputProgDialog.progDialogListener() {
                        @Override
                        public void onPositiveInput(int num, int den) {
                            v.setProgress(num, den);
                            v.setKRProgressBar();
                            KRNums.set(position, num);
                            KRDens.set(position, den);
                            saveKRProg(position);
                        }
                    });
                    editProgDialog.show(getSupportFragmentManager(), "add_goal");
                }
            }
        });
        KRRecyclerView.setAdapter(KRAdapter);
        loadKRs();
        loadGoalDetails();
        setTitle(goalName);
        goalDescTextView.setText(goalDesc);
    }

    private void loadGoalDetails() {
        goalDesc = sharedPreferences.getString(getString(R.string.goal_desc) + goalName, getString(R.string.default_goal_desc));
    }

    private void setGoalDesc(String desc) {
        goalDesc = desc;
        goalDescTextView.setText(desc);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.goal_desc) + goalName, desc);
        editor.apply();
    }

    public void addKR(String name) {
        KRNames.add(name);
        KRNums.add(0);
        KRDens.add(100);
        KRAdapter.notifyItemInserted(KRNames.size() - 1);
        ++numKRs;
        saveKRs();
    }

    private void deleteKR(int position) {
        String KRName = KRNames.get(position);
        KRNames.remove(position);
        KRNums.remove(position);
        KRDens.remove(position);
        --numKRs;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(getString(R.string.kr) + goalName + '.' + KRName);
        editor.remove(getString(R.string.kr_prog_num) + goalName + '.' + KRName);
        editor.remove(getString(R.string.kr_prog_den) + goalName + '.' + KRName);
        editor.apply();
        saveKRs();
        KRAdapter.notifyItemRemoved(position);
    }

    private void saveKRs() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(getString(R.string.num_krs) + goalName, numKRs);
        for (int i = 0; i < numKRs; ++i) {
            editor.putString(getString(R.string.kr) + goalName + '.' + i, KRNames.get(i));
        }
        editor.apply();
    }

    private void loadKRs() {
        int defaultNumKRs = 0;
        int defaultNum = 0;
        int defaultDen = 100;
        String defaultKRName = "";
        numKRs = sharedPreferences.getInt(getString(R.string.num_krs) + goalName, defaultNumKRs);
        KRNames.clear();
        for (int i = 0; i < numKRs; ++i) {
            String KRName = sharedPreferences.getString(getString(R.string.kr) + goalName + '.' + i, defaultKRName);
            KRNames.add(KRName);
            KRAdapter.notifyItemInserted(KRNames.size() - 1);
            KRNums.add(sharedPreferences.getInt(getString(R.string.kr_prog_num) + goalName + '.' + KRName, defaultNum));
            KRDens.add(sharedPreferences.getInt(getString(R.string.kr_prog_den) + goalName + '.' + KRName, defaultDen));
        }
        KRAdapter.setKRProg(KRNums, KRDens);
    }

    private void renameKR(int position, String name) {
        String KRName = KRNames.get(position);
        KRNames.set(position, name);
        saveKRProg(position);
        saveKRs();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(getString(R.string.kr_prog_num) + goalName + '.' + KRName);
        editor.remove(getString(R.string.kr_prog_den) + goalName + '.' + KRName);
        editor.apply();
        KRAdapter.notifyItemChanged(position);
    }

    private void saveKRProg(int position) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        KRAdapter.ViewHolder v = (KRAdapter.ViewHolder)KRRecyclerView.findViewHolderForAdapterPosition(position);
        assert v != null;
        String KRName = KRNames.get(position);
        editor.putInt(getString(R.string.kr_prog_num) + goalName + '.' + KRName, v.getProgNum());
        editor.putInt(getString(R.string.kr_prog_den) + goalName + '.' + KRName, v.getProgDen());
        editor.apply();
    }
}
