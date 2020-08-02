package com.codeplay.jatayu.util;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionUtil {

	public static int PERMISSION_REQUEST_CODE = 10;
	public static String[] REQUIRED_PERMISSIONS = new String[]{
			Manifest.permission.READ_EXTERNAL_STORAGE,
			Manifest.permission.WRITE_EXTERNAL_STORAGE,
			Manifest.permission.CAMERA,
			Manifest.permission.RECORD_AUDIO,
			Manifest.permission.INTERNET,
			Manifest.permission.ACCESS_FINE_LOCATION,
			Manifest.permission.ACCESS_COARSE_LOCATION,
	};

	public static boolean areAllPermissionsGranted(Context context){
		for (String requiredPermission : REQUIRED_PERMISSIONS) {
			if (ContextCompat.checkSelfPermission(context.getApplicationContext(), requiredPermission) != PackageManager.PERMISSION_GRANTED) {
				return false;
			}
		}
		return true;
	}

	/**
	 * request all permissions
	 * @param activity
	 */
	public static void requestAllPermissions(Activity activity){
		ActivityCompat.requestPermissions(activity, REQUIRED_PERMISSIONS, PERMISSION_REQUEST_CODE);
	}

	public static void showPermissionsRationale(Activity activity) {
		AlertDialog.Builder adb = new AlertDialog.Builder(activity)
				.setTitle("Grant all permissions")
				.setMessage("You would need to grant all permissions to be able to use this app")
				.setPositiveButton("Ok", (dialog, which) -> {
					requestAllPermissions(activity);
				})
				.setCancelable(false)
				.setNegativeButton("No", (dialog, which) -> {
					activity.finish();
				});
		adb.create().show();
	}
}
