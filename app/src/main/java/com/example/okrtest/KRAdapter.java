package com.example.okrtest;

import android.content.Context;
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

    KRAdapter(Context context, ArrayList<String> KRNames, RecyclerClickListener listener) {
        this.KRNames = KRNames;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView KRNameTextView;
        private ImageView deleteKR;
        private ProgressBar KRProgressBar;
        private WeakReference<RecyclerClickListener> listenerRef;

        ViewHolder(View view, RecyclerClickListener listener) {
            super(view);
            listenerRef = new WeakReference<RecyclerClickListener>(listener);
            KRNameTextView = (TextView) view.findViewById(R.id.KRNameTextView);
            deleteKR = (ImageView) view.findViewById(R.id.deleteKRImageView);
            KRProgressBar = (ProgressBar) view.findViewById(R.id.KRProgressBar);

            view.setOnClickListener(this);
            deleteKR.setOnClickListener(this);

            final EditText progressNumEditText = (EditText) view.findViewById(R.id.progressNumEditText);
            final EditText progressDenEditText = (EditText) view.findViewById(R.id.progressDenEditText);
            Button saveProgButton = (Button) view.findViewById(R.id.saveProgButton);
            saveProgButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int num = Integer.parseInt(progressNumEditText.getText().toString());
                    int den = Integer.parseInt(progressDenEditText.getText().toString());
                    setKRProgressBar(num, den);
                }
            });
        }

        void setKRNameTextView(String text) {
            KRNameTextView.setText(text);
        }

        void setKRProgressBar(int num, int den) {
            KRProgressBar.setProgress(num * 100 / den, true);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == deleteKR.getId()) {
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
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String KRName = KRNames.get(position);
        holder.setKRNameTextView(KRName);
    }

    @Override
    public int getItemCount() {
        return KRNames.size();
    }
}
