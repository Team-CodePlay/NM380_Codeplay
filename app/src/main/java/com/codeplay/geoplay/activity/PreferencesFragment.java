package com.codeplay.geoplay.activity;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreferenceDialogFragmentCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.codeplay.geoplay.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

public class PreferencesFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_preferences, rootKey);

        findPreference("logout").setOnPreferenceClickListener(preference -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            return true;
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SwitchPreferenceCompat display_mode = findPreference("display_mode");
        SwitchPreferenceCompat customMap = findPreference("custom_map_switch");
        EditTextPreference customMapStringInput = findPreference("custom_map_string_input");
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            getActivity().setTheme(R.style.AppTheme_AppBarOverlay);
            System.out.println("ON");
            display_mode.setChecked(true);
        } else {
            getActivity().setTheme(R.style.AppTheme);
            System.out.println("OFF");
            display_mode.setChecked(false);

        }
        display_mode.setOnPreferenceChangeListener((preference, newValue) -> {
            if (display_mode.isChecked()) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                return false;
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                return true;
            }
        });


        customMap.setOnPreferenceChangeListener((preference, newValue) -> {
            if(customMap.isChecked()) {
                customMapStringInput.setVisible(false);
                customMap.setChecked(true);
            } else {
                customMapStringInput.setVisible(true);
                customMap.setChecked(false);
            }
            return true;
        });

        // Language selector
        Preference language = findPreference("language");
        language.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                getActivity().finish();
                startActivity(getActivity().getIntent());
                return true;
            }
        });

    }
}
