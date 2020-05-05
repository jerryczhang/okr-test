package com.example.okrtest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.okrtest.ui.main.Tab1Fragment;

import java.util.Objects;

public class GoalDetailActivity extends AppCompatActivity {
    private String goalName;
    private String goalDesc;
    private SharedPreferences sharedPreferences;

    private TextView goalDescTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_detail);
        goalDescTextView = (TextView) findViewById(R.id.goalDescTextView);
        sharedPreferences = GoalDetailActivity.this.getPreferences(Context.MODE_PRIVATE);

        goalName = getIntent().getStringExtra(Tab1Fragment.EXTRA_GOAL_NAME);
        loadGoalDetails();

        setTitle(goalName);
        goalDescTextView.setText(goalDesc);
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
}
