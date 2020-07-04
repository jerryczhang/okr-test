package com.example.okrtest;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.okrtest.ui.main.Tab1Fragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class GoalsAdapter extends RecyclerView.Adapter<GoalsAdapter.ViewHolder> {

    private final RecyclerClickListener listener;
    private ArrayList<String> goalNames;

    public GoalsAdapter(Context context, ArrayList<String> goalNames, RecyclerClickListener listener) {
        this.goalNames = goalNames;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView goalNameTextView;
        private WeakReference<RecyclerClickListener> listenerRef;

        private String goalName;


        public ViewHolder(View view, RecyclerClickListener listener) {
            super(view);
            listenerRef = new WeakReference<RecyclerClickListener>(listener);
            goalNameTextView = (TextView) view.findViewById(R.id.goalNameTextView);

            view.setOnClickListener(this);
            goalNameTextView.setOnClickListener(this);
        }

        public void setGoalName(String name) {
            goalName = name;
        }

        public void setGoalNameTextView(String text) {
            goalNameTextView.setText(text);
        }

        @Override
        public void onClick(View v) {
            listenerRef.get().onViewClicked(getAdapterPosition());
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.goal_preview, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String goalName = goalNames.get(position);
        holder.setGoalName(goalName);
        holder.setGoalNameTextView(goalName);
    }

    @Override
    public int getItemCount() {
        return goalNames.size();
    }
}
