package com.codeplay.geoplay.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import com.codeplay.geoplay.AppClass;
import com.codeplay.geoplay.R;
import com.codeplay.geoplay.util.PermissionUtil;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Holds two fragments. VideoListFragment and SettingsFragment.
 */
public class MainActivity extends ActivityBase {

	BottomNavigationView bottomNavigation;

	RelativeLayout progressCard;
	TextView lblVideoTitle;
	TextView lblVideoSize;
	TextView lblUploadCount;
	ProgressBar progressBar;
	Toolbar myToolbar;

	int currentFragment = 0;

	private Menu menu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		if (AppClass.getSP().getBoolean("NIGHT_MODE", false)) {
			AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
		} else {
			AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
		}

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		myToolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(myToolbar);

		progressCard = findViewById(R.id.progress_card);
		lblVideoTitle = findViewById(R.id.lblVideoTitle);
		lblVideoSize = findViewById(R.id.lblVideoSize);
		lblUploadCount = findViewById(R.id.lblUploadCount);
		progressBar = findViewById(R.id.progressBar);

		bottomNavigation = findViewById(R.id.bottom_navigation);
		BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
				menuItem -> {
					switch (menuItem.getItemId()) {
						case R.id.nav_library:
							if (currentFragment != 0) {
								getSupportFragmentManager().beginTransaction()
										.replace(R.id.container, new VideoListFragment())
										.commit();
								currentFragment = 0;
							}
							return true;

						case R.id.nav_record:
							if (getLocationMode(MainActivity.this) == 0) {
								Toast.makeText(MainActivity.this, "Turn on location services to record geo tagged videos. High accuracy preferred.", Toast.LENGTH_SHORT).show();
								startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
							} else {
								Intent j = new Intent(MainActivity.this, RecordActivity.class);
								startActivity(j);
							}
							return false;

						case R.id.nav_settings: //1
							if (currentFragment != 1) {
								getSupportFragmentManager().beginTransaction()
										.replace(R.id.container, new PreferencesFragment())
										.commit();
								currentFragment = 1;
							}
							return true;
					}
					return false;
				};

		bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
		bottomNavigation.setSelectedItemId(R.id.nav_library);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, new VideoListFragment())
				.commit();
		currentFragment = 0;


		if (!PermissionUtil.areAllPermissionsGranted(MainActivity.this)) {
			PermissionUtil.requestAllPermissions(MainActivity.this);
		}
		showOnBoarding();
	}


	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == PermissionUtil.PERMISSION_REQUEST_CODE) {
			if (!PermissionUtil.areAllPermissionsGranted(MainActivity.this)) {
				Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
				PermissionUtil.showPermissionsRationale(MainActivity.this);
			}
		}
	}


	public int getLocationMode(Context context) {
		try {
			return Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
		} catch (Settings.SettingNotFoundException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private void showOnBoarding() {

		final TapTargetSequence sequence = new TapTargetSequence(this)
				.targets(
						TapTarget.forView(findViewById(R.id.dummyView), "Welcome to GeoPlay!",
								"This is a small tutorial to help you get started.")
								.outerCircleAlpha(0.96f)
								.titleTextSize(30)
								.titleTextColor(R.color.colorAccent)
								.descriptionTextSize(20)
								.descriptionTextColor(android.R.color.white)
								.dimColor(android.R.color.black)
								.outerCircleColor(R.color.colorPrimaryDark)
								.targetCircleColor(R.color.colorPrimaryLight)
								.drawShadow(true)
								.cancelable(false)
								.targetRadius(30)
								.transparentTarget(false)
								.id(1),
						TapTarget.forView(findViewById(R.id.nav_library), "Video Library",
								"All recorded videos will be available here. You can play, upload to cloud, or delete these videos.")
								.outerCircleAlpha(0.96f)
								.titleTextSize(30)
								.titleTextColor(R.color.colorAccent)
								.descriptionTextSize(20)
								.descriptionTextColor(android.R.color.white)
								.dimColor(android.R.color.black)
								.outerCircleColor(R.color.colorPrimaryDark)
								.targetCircleColor(R.color.colorPrimaryLight)
								.drawShadow(true)
								.cancelable(false)
								.targetRadius(30)
								.transparentTarget(true)
								.id(2),
						TapTarget.forView(findViewById(R.id.nav_record), "Record Videos",
								"Click here to record new geo tagged videos.")
								.outerCircleAlpha(0.96f)
								.titleTextSize(30)
								.titleTextColor(R.color.colorAccent)
								.descriptionTextSize(20)
								.descriptionTextColor(android.R.color.white)
								.dimColor(android.R.color.black)
								.outerCircleColor(R.color.colorPrimaryDark)
								.targetCircleColor(R.color.colorPrimaryLight)
								.drawShadow(true)
								.cancelable(false)
								.targetRadius(30)
								.transparentTarget(true)
								.id(3),
						TapTarget.forView(findViewById(R.id.nav_settings), "Settings",
								"There are many useful settings here. Tinker around with the different app settings and find whats best for you!")
								.outerCircleAlpha(0.96f)
								.titleTextSize(30)
								.titleTextColor(R.color.colorAccent)
								.descriptionTextSize(20)
								.descriptionTextColor(android.R.color.white)
								.dimColor(android.R.color.black)
								.outerCircleColor(R.color.colorPrimaryDark)
								.targetCircleColor(R.color.colorPrimaryLight)
								.drawShadow(true)
								.targetRadius(30)
								.cancelable(false)
								.transparentTarget(true)
								.id(4),
						TapTarget.forToolbarOverflow(myToolbar, "Toggle View",
								" This toggles the view between detailed video cards and large thumbnail video cards.")
								.outerCircleAlpha(0.96f)
								.titleTextSize(30)
								.titleTextColor(R.color.colorAccent)
								.descriptionTextSize(20)
								.descriptionTextColor(android.R.color.white)
								.dimColor(android.R.color.black)
								.outerCircleColor(R.color.colorPrimaryDark)
								.targetCircleColor(R.color.colorPrimaryLight)
								.drawShadow(true)
								.targetRadius(30)
								.cancelable(false)
								.transparentTarget(true)
								.id(5),
						TapTarget.forView(findViewById(R.id.dummySearch), "Search",
								"You can search through your recorded videos here. Use terms from the video title and date.")
								.outerCircleAlpha(0.96f)
								.titleTextSize(30)
								.titleTextColor(R.color.colorAccent)
								.descriptionTextSize(20)
								.descriptionTextColor(android.R.color.white)
								.dimColor(android.R.color.black)
								.outerCircleColor(R.color.colorPrimaryDark)
								.targetCircleColor(R.color.colorPrimaryLight)
								.drawShadow(true)
								.targetRadius(30)
								.cancelable(false)
								.transparentTarget(true)
								.id(6),
						TapTarget.forView(findViewById(R.id.nav_record), "Great!",
								"Lets move to the Record Screen. Click the record button to begin")
								.outerCircleAlpha(0.96f)
								.titleTextSize(30)
								.titleTextColor(R.color.colorAccent)
								.descriptionTextSize(20)
								.descriptionTextColor(android.R.color.white)
								.dimColor(android.R.color.black)
								.outerCircleColor(R.color.colorPrimaryDark)
								.targetCircleColor(R.color.colorPrimaryLight)
								.drawShadow(true)
								.cancelable(false)
								.targetRadius(30)
								.transparentTarget(true)
								.id(7)
				).listener(new TapTargetSequence.Listener() {
					@Override
					public void onSequenceFinish() {
						AppClass.getSP().edit().putBoolean("SHOW_TUTORIAL_1", false).apply();
						Intent j = new Intent(MainActivity.this, RecordActivity.class);
						startActivity(j);
					}

					@Override
					public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {

					}

					@Override
					public void onSequenceCanceled(TapTarget lastTarget) {

					}
				});
		if (AppClass.getSP().getBoolean("SHOW_TUTORIAL_1", true)) {
			sequence.start();
		}
	}

}