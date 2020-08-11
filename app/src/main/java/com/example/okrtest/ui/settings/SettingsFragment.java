package com.example.okrtest.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.okrtest.MainActivity;
import com.example.okrtest.OutputTextDialog;
import com.example.okrtest.R;

import java.io.File;

public class SettingsFragment extends Fragment {
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_settings, container, false);
        super.onCreate(savedInstanceState);
        PrefFragment prefFragment = new SettingsFragment.PrefFragment();
        getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, prefFragment)
                .commit();
        Preference button = prefFragment.findPreference(getString(R.string.clear_sp_key));
        assert button != null;
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                String title = getString(R.string.delete_save_data_dialog_title);
                String message = getString(R.string.delete_goal_dialog_text);
                String positiveName = getString(R.string.delete_save_data_dialog_positive);
                String negativeName = getString(R.string.delete_save_data_dialog_negative);
                OutputTextDialog deleteSaveDataDialog = new OutputTextDialog(title, message, positiveName, negativeName, new OutputTextDialog.OutputTextListener() {
                    @Override
                    public void onPositiveInput() {
                        clearSharedPreferences();
                    }

                    @Override
                    public void onNegativeInput() {
                    }
                });
                deleteSaveDataDialog.show(getParentFragmentManager(), "delete_save_data");
                return true;
            }
        });
        return root;
    }

    public static class PrefFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }

    private void clearSharedPreferences() {
        File sharedPreferenceFile = new File("/data/data/com.example.okrtest/shared_prefs/");
        File[] listFiles = sharedPreferenceFile.listFiles();
        for (File file : listFiles) {
            file.delete();
        }
    }
}

