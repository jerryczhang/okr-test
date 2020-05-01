package com.example.okrtest;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.okrtest.ui.main.Tab1Fragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class GoalsAdapter extends RecyclerView.Adapter<GoalsAdapter.ViewHolder> {

    private final ClickListener listener;
    private ArrayList<String> goalNames;

    public GoalsAdapter(Context context, ArrayList<String> goalNames, ClickListener listener) {
        this.goalNames = goalNames;
        this.listener = listener;
    }

    public interface ClickListener {
        void onViewClicked(int position);
        void onItemClicked(int position, int id);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView goalNameTextView;
        private Button deleteGoalButton;
        private WeakReference<ClickListener> listenerRef;

        private final Context context;

        private String goalName;


        public ViewHolder(View view, ClickListener listener) {
            super(view);
            listenerRef = new WeakReference<ClickListener>(listener);
            goalNameTextView = (TextView) view.findViewById(R.id.goalNameTextView);
            deleteGoalButton = (Button) view.findViewById(R.id.deleteGoalButton);

            context = view.getContext();
            view.setOnClickListener(this);
            deleteGoalButton.setOnClickListener(this);
        }

        public void setGoalName(String name) {
            goalName = name;
        }

        public void setGoalNameTextView(String text) {
            goalNameTextView.setText(text);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == deleteGoalButton.getId()) {
                listenerRef.get().onItemClicked(getAdapterPosition(), v.getId());
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
    }

    @Override
    public int getItemCount() {
        return goalNames.size();
    }
}
