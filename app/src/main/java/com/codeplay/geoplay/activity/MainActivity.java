package com.codeplay.geoplay.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codeplay.geoplay.R;
import com.codeplay.geoplay.util.PermissionUtil;
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

	int currentFragment = 0;

	private Menu menu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
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
								unHideMenu();
								getSupportFragmentManager().beginTransaction()
										.replace(R.id.container, new VideoListFragment())
										.commit();
								currentFragment = 0;
							}
							return true;

						case R.id.nav_record:
							Intent j = new Intent(MainActivity.this, RecordActivity.class);
							startActivity(j);
							return false;

						case R.id.nav_settings: //1
							if (currentFragment != 1) {
								hideMenu();
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
		}else{
			if (getLocationMode(MainActivity.this) == 0) {
				Toast.makeText(MainActivity.this, "Turn on location to use this app", Toast.LENGTH_SHORT).show();
				startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 2);
			}
		}
	}

	private void unHideMenu(){
//		for (int i = 0; i < menu.size(); i++) {
//			menu.getItem(i).setVisible(true);
//		}
	}

	private void hideMenu() {
//		for (int i = 0; i < menu.size(); i++) {
//			menu.getItem(i).setVisible(false);
//		}
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		getMenuInflater().inflate(R.menu.toobar_menu, menu);
//		this.menu = menu;
//		return super.onCreateOptionsMenu(menu);
//	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == PermissionUtil.PERMISSION_REQUEST_CODE) {
			if (!PermissionUtil.areAllPermissionsGranted(MainActivity.this)) {
				Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
				PermissionUtil.showPermissionsRationale(MainActivity.this);
			}
		} else {
			if (getLocationMode(MainActivity.this) == 0) {
				Toast.makeText(MainActivity.this, "Turn on location to use this app", Toast.LENGTH_SHORT).show();
				startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 2);
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 2){
			if(getLocationMode(MainActivity.this) == 0){
				Toast.makeText(MainActivity.this, "Turn on location to use this app", Toast.LENGTH_SHORT).show();
				startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 2);
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

}