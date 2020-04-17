package com.example.okrtest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public class GoalsAdapter extends RecyclerView.Adapter<GoalsAdapter.ViewHolder> {

    private String[] mGoalNames;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView goalNameTextView;

        public ViewHolder(View view) {
            super(view);
            goalNameTextView = (TextView) view.findViewById(R.id.goalNameTextView);
        }

        public void setGoalNameTextView(String text) {
            goalNameTextView.setText(text);
        }
    }

    public GoalsAdapter(Context context, String[] goalNames) {
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
        holder.setGoalNameTextView(mGoalNames[position]);
    }

    @Override
    public int getItemCount() {
        return mGoalNames.length;
    }
}