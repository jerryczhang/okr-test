package com.example.okrtest;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;


import org.w3c.dom.Text;

import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class GoalsAdapter extends RecyclerView.Adapter<GoalsAdapter.ViewHolder> {

    private final RecyclerClickListener listener;
    private ArrayList<String> goalNames;
    private ArrayList<Integer> goalNums;
    private ArrayList<Integer> goalDens;

    public GoalsAdapter(Context context, ArrayList<String> goalNames, RecyclerClickListener listener) {
        this.goalNames = goalNames;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ProgressBar goalProgressBar;
        private TextView goalNameTextView;
        private ImageView renameGoalImageView;
        private WeakReference<RecyclerClickListener> listenerRef;

        private String goalName;
        private int num;
        private int den;

        public ViewHolder(View view, RecyclerClickListener listener) {
            super(view);
            goalProgressBar = view.findViewById(R.id.goalProgressBar);
            goalNameTextView = view.findViewById(R.id.goalNameTextView);
            renameGoalImageView = view.findViewById(R.id.renameGoalImageView);
            listenerRef = new WeakReference<RecyclerClickListener>(listener);
            view.setOnClickListener(this);
            renameGoalImageView.setOnClickListener(this);
        }

        public void setGoalName(String name) {
            goalName = name;
        }

        public void setGoalNameTextView(String text) {
            goalNameTextView.setText(text);
        }

        public void setProgress(int num, int den) {
            this.num = num;
            this.den = den;
        }

        public void setGoalProgressBar() {
            goalProgressBar.setProgress(num * 100 / den);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == renameGoalImageView.getId()) {
                listenerRef.get().onItemClicked(getAdapterPosition(), renameGoalImageView.getId());
            } else {
                listenerRef.get().onViewClicked(getAdapterPosition());
            }
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
        holder.setProgress(goalNums.get(position), goalDens.get(position));
        holder.setGoalProgressBar();
    }

    @Override
    public int getItemCount() {
        return goalNames.size();
    }

    public void setGoalProg(ArrayList<Integer> nums, ArrayList<Integer> dens) {
        goalNums = nums;
        goalDens = dens;
    }
}
