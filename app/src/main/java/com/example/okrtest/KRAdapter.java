package com.example.okrtest;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class KRAdapter extends RecyclerView.Adapter<KRAdapter.ViewHolder> {
    private final RecyclerClickListener listener;
    private ArrayList<String> KRNames;
    private ArrayList<Integer> KRNums;
    private ArrayList<Integer> KRDens;

    private boolean isDark;

    KRAdapter(@NonNull Context context, ArrayList<String> KRNames, RecyclerClickListener listener) {
        this.KRNames = KRNames;
        this.listener = listener;
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        isDark = pref.getBoolean(context.getString(R.string.dark_mode_key), false);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView KRNameTextView;
        private ProgressBar KRProgressBar;
        private ImageView renameKRImageView;
        private WeakReference<RecyclerClickListener> listenerRef;
        private int num;
        private int den;

        ViewHolder(View view, RecyclerClickListener listener, boolean isDark) {
            super(view);
            listenerRef = new WeakReference<RecyclerClickListener>(listener);
            KRNameTextView = (TextView) view.findViewById(R.id.KRNameTextView);
            KRProgressBar = (ProgressBar) view.findViewById(R.id.KRProgressBar);
            renameKRImageView = (ImageView) view.findViewById(R.id.renameKRImageView);

            renameKRImageView.setOnClickListener(this);
            view.setOnClickListener(this);
            if (isDark) {
                KRNameTextView.setTextColor(view.getContext().getColor(R.color.WHITE));
            }
        }

        void setKRNameTextView(String text) {
            KRNameTextView.setText(text);
        }

        void setProgress(int num, int den) {
            this.num = num;
            this.den = den;
        }

        int getProgNum() {
            return num;
        }

        int getProgDen() {
            return den;
        }

        void setKRProgressBar() {
            KRProgressBar.setProgress(num * 100 / den, true);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == renameKRImageView.getId()) {
                listenerRef.get().onItemClicked(getAdapterPosition(), renameKRImageView.getId());
            } else {
                listenerRef.get().onViewClicked(getAdapterPosition());
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.kr_preview, parent, false);
        if (isDark) {
            view.setBackground(parent.getContext().getDrawable(R.drawable.background_dark));
        }
        return new ViewHolder(view, listener, isDark);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String KRName = KRNames.get(position);
        holder.setKRNameTextView(KRName);
        int num = KRNums.get(position);
        int den = KRDens.get(position);
        holder.setProgress(num, den);
        holder.setKRProgressBar();
    }

    @Override
    public int getItemCount() {
        return KRNames.size();
    }

    public void setKRProg(ArrayList<Integer> KRNums, ArrayList<Integer> KRDens) {
        this.KRNums = KRNums;
        this.KRDens = KRDens;
    }
}
