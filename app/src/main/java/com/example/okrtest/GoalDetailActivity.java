package com.example.okrtest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.okrtest.ui.main.Tab1Fragment;

public class GoalDetailActivity extends AppCompatActivity {
    String goalName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_detail);
        goalName = getIntent().getStringExtra(Tab1Fragment.EXTRA_GOAL_NAME);
        setTitle(goalName);
    }
}
