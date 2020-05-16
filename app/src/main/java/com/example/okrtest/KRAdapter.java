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
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class KRAdapter extends RecyclerView.Adapter<KRAdapter.ViewHolder> {
    private final RecyclerClickListener listener;
    private ArrayList<String> KRNames;
    private SharedPreferences sharedPreferences;

    KRAdapter(@NonNull Context context, ArrayList<String> KRNames, RecyclerClickListener listener) {
        this.KRNames = KRNames;
        this.listener = listener;
        sharedPreferences = context.getSharedPreferences("KRPref", Context.MODE_PRIVATE);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView KRNameTextView;
        private ImageView deleteKR;
        private ProgressBar KRProgressBar;
        private WeakReference<RecyclerClickListener> listenerRef;
        private SharedPreferences sharedPreferences;
        private EditText progressNumEditText;
        private EditText progressDenEditText;
        private int num;
        private int den;

        ViewHolder(View view, RecyclerClickListener listener, SharedPreferences sharedPreferences) {
            super(view);
            this.sharedPreferences = sharedPreferences;
            listenerRef = new WeakReference<RecyclerClickListener>(listener);
            KRNameTextView = (TextView) view.findViewById(R.id.KRNameTextView);
            deleteKR = (ImageView) view.findViewById(R.id.deleteKRImageView);
            KRProgressBar = (ProgressBar) view.findViewById(R.id.KRProgressBar);

            view.setOnClickListener(this);
            deleteKR.setOnClickListener(this);
            KRProgressBar.setOnClickListener(this);

            progressNumEditText = (EditText) view.findViewById(R.id.progressNumEditText);
            progressDenEditText = (EditText) view.findViewById(R.id.progressDenEditText);
            Button saveProgButton = (Button) view.findViewById(R.id.saveProgButton);
            saveProgButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setKRProgressBar();
                    saveProgress();
                }
            });
        }

        void setKRNameTextView(String text) {
            KRNameTextView.setText(text);
        }

        void setKRProgressBar() {
            num = Integer.parseInt(progressNumEditText.getText().toString());
            den = Integer.parseInt(progressDenEditText.getText().toString());
            KRProgressBar.setProgress(num * 100 / den, true);
        }

        private void saveProgress() {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Resources res = itemView.getResources();
            String KRName = KRNameTextView.getText().toString();
            editor.putInt(res.getString(R.string.kr_prog_num) + KRName, num);
            editor.putInt(res.getString(R.string.kr_prog_den) + KRName, den);
            editor.apply();
        }

        private void loadProgress() {
            int defaultNum = 0;
            int defaultDen = 100;
            Resources res = itemView.getResources();
            String KRName = KRNameTextView.getText().toString();
            num = sharedPreferences.getInt(res.getString(R.string.kr_prog_num) + KRName, defaultNum);
            den = sharedPreferences.getInt(res.getString(R.string.kr_prog_den) + KRName, defaultDen);
            progressNumEditText.setText(String.valueOf(num));
            progressDenEditText.setText(String.valueOf(den));
            setKRProgressBar();
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == deleteKR.getId() || v.getId() == KRProgressBar.getId()) {
                listenerRef.get().onItemClicked(getAdapterPosition(), v.getId());
            } else {
                listenerRef.get().onViewClicked(getAdapterPosition());
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.kr_preview, parent, false);
        return new ViewHolder(view, listener, sharedPreferences);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String KRName = KRNames.get(position);
        holder.setKRNameTextView(KRName);
        holder.loadProgress();
    }

    @Override
    public int getItemCount() {
        return KRNames.size();
    }
}
