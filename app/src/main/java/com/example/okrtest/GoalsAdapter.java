package com.example.okrtest;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GoalsAdapter extends RecyclerView.Adapter<GoalsAdapter.ViewHolder> {

    private ArrayList<String> mGoalNames;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView goalNameTextView;

        private final Context context;

        public static final String EXTRA_GOAL_NAME = "com.example.okrtest.GOAL_NAME";
        private String goalName;

        public ViewHolder(View view) {
            super(view);
            goalNameTextView = (TextView) view.findViewById(R.id.goalNameTextView);

            context = view.getContext();
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, GoalDetailActivity.class);
                    intent.putExtra(EXTRA_GOAL_NAME, goalName);
                    context.startActivity(intent);
                }
            });
        }

        public void setGoalName(String name) {
            goalName = name;
        }

        public void setGoalNameTextView(String text) {
            goalNameTextView.setText(text);
        }
    }

    public GoalsAdapter(Context context, ArrayList<String> goalNames) {
        mGoalNames = goalNames;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.goal_preview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String goalName = mGoalNames.get(position);
        holder.setGoalName(goalName);
        holder.setGoalNameTextView(goalName);
    }

    @Override
    public int getItemCount() {
        return mGoalNames.size();
    }
}
