package com.codeplay.geoplay.activity;


import android.content.Intent;
import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.codeplay.geoplay.R;
import com.google.firebase.auth.FirebaseAuth;

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
}
