package com.codeplay.geoplay.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codeplay.geoplay.R;
import com.codeplay.geoplay.ui.LockBottomSheetBehaviour;
import com.codeplay.geoplay.util.PermissionUtil;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;
import java.security.Permission;

import static com.codeplay.geoplay.util.BottomSheetUtil.interpolateColor;

/**
 * For location and video recording
 */
public class RecordActivity extends AppCompatActivity implements OnMapReadyCallback {

	private static final String TAG = "RecordActivity";

	private GoogleMap mMap;
	private CameraFragment cameraFragment;
	private FusedLocationProviderClient locationProviderClient;

	private ImageView btnStart;
	private ImageView btnStop;
	private LinearLayout bottomSheet;
	private TextView lblSpeed;
	private LockBottomSheetBehaviour behavior;

	private Boolean isRecording = false;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

		bottomSheet = findViewById(R.id.bottom_sheet);
		lblSpeed = findViewById(R.id.lblSpeed);
		btnStart = findViewById(R.id.btnStart);
		btnStop = findViewById(R.id.btnStop);
		cameraFragment = (CameraFragment) getSupportFragmentManager().findFragmentById(R.id.cameraFragment);

		btnStart.setOnClickListener(v -> {
			v.setVisibility(View.GONE);
			btnStop.setVisibility(View.VISIBLE);
			startRecording();
		});
		btnStop.setOnClickListener(v -> {
			v.setVisibility(View.GONE);
			btnStart.setVisibility(View.VISIBLE);
			stopRecording();
		});

		behavior = (LockBottomSheetBehaviour) LockBottomSheetBehaviour.from(findViewById(R.id.bottom_sheet));
		setUpBottomSheet();


		if (PermissionUtil.areAllPermissionsGranted(RecordActivity.this)) {
			setUpMap();
		} else {
			PermissionUtil.requestAllPermissions(RecordActivity.this);
		}


		cameraFragment = (CameraFragment) getSupportFragmentManager().findFragmentById(R.id.cameraFragment);
	}

	private void setUpMap(){
		locationProviderClient = LocationServices.getFusedLocationProviderClient(RecordActivity.this);
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
	}

	@SuppressLint("MissingPermission")
	@Override
	public void onMapReady(GoogleMap googleMap) {
		mMap = googleMap;

		if (PermissionUtil.areAllPermissionsGranted(RecordActivity.this)) {
			mMap.clear();
			mMap.setMyLocationEnabled(true);

			locationProviderClient.getLastLocation().addOnSuccessListener(location -> {
				if (location != null) {
					mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
							new LatLng(location.getLatitude(), location.getLongitude()),
							15
					));
				}
			});

		}
	}

	private void afterSave(File videoFile) {
		Toast.makeText(RecordActivity.this, "File saved at: " + videoFile.toString(), Toast.LENGTH_SHORT).show();
		Log.d(TAG, "afterSave: Save path: " + videoFile.toString());
	}

	@SuppressLint("MissingPermission")
	private void startRecording() {
		if (!isRecording) {
			cameraFragment.startRecording(this::afterSave);

			locationProviderClient.requestLocationUpdates(
					// TODO: 09-07-2020 change priority according to preferences
					new LocationRequest().setInterval(1000).setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY),
					locationCallback,
					Looper.getMainLooper()
			).addOnCanceledListener(() -> Log.d(TAG, "location: cancelled"));

			isRecording = true;
		}
	}

	private void stopRecording() {
		if (isRecording) {
			locationProviderClient.removeLocationUpdates(locationCallback);

			cameraFragment.stopRecording();
			isRecording = false;
		}
	}

	private LocationCallback locationCallback = new LocationCallback(){
		@Override
		public void onLocationResult(LocationResult locationResult) {
			super.onLocationResult(locationResult);
			Log.d(TAG, "onLocationResult: " + locationResult.getLastLocation().toString());
		}
	};

	@Override
	public void onBackPressed() {
		if (isRecording) {
			stopRecording();
		}
		super.onBackPressed();
	}

	private void setUpBottomSheet() {
		behavior.setAllowUserDragging(false);

		findViewById(R.id.map).setOnTouchListener((v, event) -> {
			switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_UP:
					behavior.setAllowUserDragging(false);
					break;
			}
			v.performClick();
			return true;
		});

		behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
			@Override
			public void onStateChanged(@NonNull View view, int i) {
			}

			@Override
			public void onSlide(@NonNull View view, float v) {
				int colorFrom = Color.TRANSPARENT;
				int colorTo = Color.TRANSPARENT;
				bottomSheet.setBackgroundColor(interpolateColor(v, colorFrom, colorTo));
			}
		});
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if(requestCode == PermissionUtil.PERMISSION_REQUEST_CODE){
			if(PermissionUtil.areAllPermissionsGranted(RecordActivity.this)){
				setUpMap();
			}
			// TODO: 01-08-2020 show permission rationale
		}
	}

	@Override
	protected void onPause() {
		if (isRecording) {
			stopRecording();
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		isRecording = false;
		btnStart.setVisibility(View.VISIBLE);
		btnStop.setVisibility(View.GONE);
	}
}