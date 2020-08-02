package com.codeplay.geoplay.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

import com.codeplay.geoplay.R;
import com.codeplay.geoplay.util.PermissionUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Holds two fragments. VideoListFragment and SettingsFragment.
 */
public class MainActivity extends AppCompatActivity {

	BottomNavigationView bottomNavigation;

	int currentFragment = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(myToolbar);

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
							Intent j = new Intent(MainActivity.this, RecordActivity.class);
							startActivity(j);
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
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toobar_menu, menu);
        return true;
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
}