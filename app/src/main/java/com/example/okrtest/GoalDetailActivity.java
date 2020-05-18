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
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.okrtest.ui.main.Tab1Fragment;

import java.util.ArrayList;
import java.util.Objects;

public class GoalDetailActivity extends AppCompatActivity {
    private String goalName;
    private String goalDesc;
    private SharedPreferences sharedPreferences;
    private KRAdapter KRAdapter;

    private ArrayList<String> KRNames = new ArrayList<>();
    private int numKRs;

    private TextView goalDescTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_detail);
        goalDescTextView = (TextView) findViewById(R.id.goalDescTextView);
        sharedPreferences = GoalDetailActivity.this.getPreferences(Context.MODE_PRIVATE);

        goalName = getIntent().getStringExtra(Tab1Fragment.EXTRA_GOAL_NAME);

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

        final RecyclerView KRRecyclerView = (RecyclerView) findViewById(R.id.KRRecyclerView);
        KRRecyclerView.setLayoutManager(new LinearLayoutManager(GoalDetailActivity.this));
        KRRecyclerView.scrollToPosition(0);

        KRAdapter = new KRAdapter(GoalDetailActivity.this, KRNames, new RecyclerClickListener() {
            @Override
            public void onViewClicked(int position) {
            }

            @Override
            public void onItemClicked(int position, int id) {
                if (id == R.id.deleteKRImageView) {
                    deleteKR(position);
                } else if (id == R.id.editProgButton) {
                    final KRAdapter.ViewHolder v = (KRAdapter.ViewHolder)KRRecyclerView.findViewHolderForAdapterPosition(position);
                    String title = getString(R.string.edit_prog_dialog_title);
                    String positiveName = getString(R.string.edit_prog_dialog_positive);
                    String negativeName = getString(R.string.edit_prog_dialog_negative);
                    String hintNum = getString(R.string.edit_prog_dialog_hint_num);
                    String hintDen = getString(R.string.edit_prog_dialog_hint_den);
                    DialogFragment editProgDialog = new InputProgDialog(title, positiveName, negativeName, hintNum, hintDen, new InputProgDialog.progDialogListener() {
                        @Override
                        public void onPositiveInput(int num, int den) {
                            assert v != null;
                            v.setProgress(num, den);
                            v.setKRProgressBar();
                        }
                    });
                    editProgDialog.show(getSupportFragmentManager(), "add_goal");
                }
            }
        });
        KRRecyclerView.setAdapter(KRAdapter);

        loadGoalDetails();
        loadKRs();
        setTitle(goalName);
        goalDescTextView.setText(goalDesc);

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
        KRAdapter.notifyItemInserted(KRNames.size() - 1);
        ++numKRs;
        saveKRs();
    }

    private void deleteKR(int position) {
        KRNames.remove(position);
        KRAdapter.notifyItemRemoved(position);
        --numKRs;
        saveKRs();
    }

    private void saveKRs() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(getString(R.string.num_krs) + goalName, numKRs);
        for (int i = 0; i < numKRs; ++i) {
            editor.putString(getString(R.string.kr) + goalName + i, KRNames.get(i));
        }
        editor.apply();
    }

    private void loadKRs() {
        int defaultNumKRs = 0;
        String defaultKRName = "";
        numKRs = sharedPreferences.getInt(getString(R.string.num_krs) + goalName, defaultNumKRs);
        KRNames.clear();
        for (int i = 0; i < numKRs; ++i) {
            String KRName = sharedPreferences.getString(getString(R.string.kr) + goalName + i, defaultKRName);
            KRNames.add(KRName);
            KRAdapter.notifyItemInserted(KRNames.size() - 1);
        }
    }
}
