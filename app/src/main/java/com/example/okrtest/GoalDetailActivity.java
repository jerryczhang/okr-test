package com.example.okrtest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class GoalDetailActivity extends AppCompatActivity {
    String goalName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_detail);
        goalName = getIntent().getStringExtra(GoalsAdapter.ViewHolder.EXTRA_GOAL_NAME);
        setTitle(goalName);
    }
}
