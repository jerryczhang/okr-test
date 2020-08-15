package com.example.okrtest.ui.friends;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.example.okrtest.R;

import java.util.Objects;

public class FriendsFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_friends, container, false);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(getContext()));
        boolean isDark = prefs.getBoolean(getString(R.string.dark_mode_key), false);
        if (isDark) {
            root.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.DARK));
        }

        final TextView textView = root.findViewById(R.id.text_gallery);
        return root;
    }
}
