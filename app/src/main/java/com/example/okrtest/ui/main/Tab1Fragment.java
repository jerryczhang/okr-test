package com.example.okrtest.ui.main;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.example.okrtest.ItemAdapter;
import com.example.okrtest.R;

public class Tab1Fragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_tab1, container, false);

        Resources res = getResources();
        String[] goalNames = res.getStringArray(R.array.goal_names);

        RecyclerView goalsRecyclerView = (RecyclerView) root.findViewById(R.id.goalsRecyclerView);
        if (this.getContext() != null) {
            ItemAdapter itemAdapter = new ItemAdapter(this.getContext(), goalNames);
            goalsRecyclerView.setAdapter(itemAdapter);
        }
        return root;
    }
}
