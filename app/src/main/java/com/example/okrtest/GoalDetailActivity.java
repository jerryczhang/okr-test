package com.example.okrtest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.example.okrtest.ui.main.Tab1Fragment;

import java.util.Objects;

public class GoalDetailActivity extends AppCompatActivity {
    private String goalName;
    private String goalDesc;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_detail);
        TextView goalDescTextView = (TextView) findViewById(R.id.goalDescTextView);
        sharedPreferences = GoalDetailActivity.this.getPreferences(Context.MODE_PRIVATE);

        goalName = getIntent().getStringExtra(Tab1Fragment.EXTRA_GOAL_NAME);
        loadGoalDetails();

        setTitle(goalName);
        goalDescTextView.setText(goalDesc);
    }

    private void loadGoalDetails() {
        goalDesc = sharedPreferences.getString(getString(R.string.goal_desc) + goalName, getString(R.string.default_goal_desc));
    }
}
