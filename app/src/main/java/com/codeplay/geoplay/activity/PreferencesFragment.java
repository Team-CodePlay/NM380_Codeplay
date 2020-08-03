package com.codeplay.geoplay.activity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.ListPreferenceDialogFragmentCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

import com.codeplay.geoplay.AppClass;
import com.codeplay.geoplay.R;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.app.Activity.RESULT_OK;

public class PreferencesFragment extends PreferenceFragmentCompat {
	private Preference storageLocation;
	private SwitchPreferenceCompat display_mode;
	private SwitchPreferenceCompat customMap;
	private EditTextPreference customMapStringInput;
	private ListPreference mapList;
	private Preference language;

	@Override
	public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
		setPreferencesFromResource(R.xml.settings_preferences, rootKey);

		storageLocation = findPreference("storage_location");

		customMap = findPreference("custom_map_switch");
		customMapStringInput = findPreference("custom_map_string_input");

		display_mode = findPreference("display_mode");

		mapList = findPreference("choose_map");
		language = findPreference("language");

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
		SharedPreferences.Editor editor = prefs.edit();

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
				editor.putBoolean("NIGHT_MODE", false);
				editor.apply();
				Intent intent = getActivity().getIntent();
				getActivity().finish();
				intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(intent);
				return false;
			} else {
				editor.putBoolean("NIGHT_MODE", true);
				editor.apply();
				Intent intent = getActivity().getIntent();
				getActivity().finish();
				intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(intent);
				return true;
			}
		});



		customMap.setOnPreferenceChangeListener((preference, newValue) -> {
			if (customMap.isChecked()) {
				customMapStringInput.setEnabled(false);
				mapList.setEnabled(true);
			} else {
				customMapStringInput.setEnabled(true);
				mapList.setEnabled(false);
			}
			return true;
		});

		if (PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("custom_map_switch", false)) {
			customMapStringInput.setEnabled(true);
			mapList.setEnabled(false);
		} else {
			customMapStringInput.setEnabled(false);
			mapList.setEnabled(true);
		}
		customMapStringInput.setDialogMessage("Format: http://url.com/{z}/{x}/{y}.jpeg");
		customMapStringInput.setOnPreferenceChangeListener((preference, newValue) -> {
			String newUrl = (String) newValue;
			String urlRegex = "^(https?|ftp|file):\\/\\/[-a-zA-Z0-9+&{}$@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
			Pattern pattern = Pattern.compile(urlRegex);//. represents single character
			Matcher matcher = pattern.matcher(newUrl);
			if (matcher.matches()) {
				if (newUrl.contains("${x}") && newUrl.contains("${y}") && newUrl.contains("${z}")) {
					return true;
				} else {
					Toast.makeText(getContext(), "URL does not contain ${x}, ${y} and ${z}", Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(getContext(), "Invalid URL", Toast.LENGTH_SHORT).show();
			}
			return false;
		});


		// Language selector
		language.setOnPreferenceChangeListener((preference, newValue) -> {
			getActivity().finish();
			startActivity(getActivity().getIntent());
			return true;
		});


		findPreference("logout").setOnPreferenceClickListener(preference -> {
			FirebaseAuth.getInstance().signOut();
			Intent intent = new Intent(getContext(), LoginActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(intent);
			return true;
		});

		findPreference("help").setOnPreferenceClickListener(preference -> {
			// TODO: 03-08-2020 add help
			return true;
		});

		findPreference("about").setOnPreferenceClickListener(preference -> {
			// TODO: 03-08-2020 add some more text here
			AlertDialog adb = new AlertDialog.Builder(getContext())
					.setTitle("About the app")
					.setMessage("Created by Team Codeplay")
					.setPositiveButton("Ok", null)
					.create();
			adb.show();
			return true;
		});
	}

}
