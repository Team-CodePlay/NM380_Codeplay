package com.codeplay.geoplay.activity;


import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.codeplay.geoplay.R;

public class PreferencesFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_preferences, rootKey);
    }
}
