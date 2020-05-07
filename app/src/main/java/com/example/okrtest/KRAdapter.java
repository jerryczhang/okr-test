package com.example.okrtest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class KRAdapter extends RecyclerView.Adapter<KRAdapter.ViewHolder> {
    private final RecyclerClickListener listener;
    private ArrayList<String> KRNames;

    public KRAdapter(Context context, ArrayList<String> KRNames, RecyclerClickListener listener) {
        this.KRNames = KRNames;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView goalNameTextView;
        private ImageView deleteGoal;
        private WeakReference<RecyclerClickListener> listenerRef;

        private String KRName;


        public ViewHolder(View view, RecyclerClickListener listener) {
            super(view);
            listenerRef = new WeakReference<RecyclerClickListener>(listener);
            goalNameTextView = (TextView) view.findViewById(R.id.goalNameTextView);
            deleteGoal = (ImageView) view.findViewById(R.id.deleteGoalImageView);

            view.setOnClickListener(this);
            deleteGoal.setOnClickListener(this);
        }

        public void setGoalName(String name) {
            KRName = name;
        }

        public void setGoalNameTextView(String text) {
            goalNameTextView.setText(text);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == deleteGoal.getId()) {
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
        String goalName = KRNames.get(position);
        holder.setGoalName(goalName);
        holder.setGoalNameTextView(goalName);
    }

    @Override
    public int getItemCount() {
        return KRNames.size();
    }
}
