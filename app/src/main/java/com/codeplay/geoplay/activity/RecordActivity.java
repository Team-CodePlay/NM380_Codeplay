package com.codeplay.geoplay.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
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
		btnStart.setOnClickListener(this::startRecording);
		btnStop.setOnClickListener(this::stopRecording);
		cameraFragment = (CameraFragment) getSupportFragmentManager().findFragmentById(R.id.cameraFragment);

		behavior = (LockBottomSheetBehaviour) LockBottomSheetBehaviour.from(findViewById(R.id.bottom_sheet));
		setUpBottomSheet();


		if (PermissionUtil.areAllPermissionsGranted(RecordActivity.this)) {

			SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map);
			mapFragment.getMapAsync(this);

		} else {
			PermissionUtil.requestAllPermissions(RecordActivity.this);
		}


		cameraFragment = (CameraFragment) getSupportFragmentManager().findFragmentById(R.id.cameraFragment);
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		mMap = googleMap;

		// Add a marker in Sydney and move the camera
		LatLng sydney = new LatLng(-34, 151);
		mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
		mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
	}

	private void afterSave(File videoFile) {
		Toast.makeText(RecordActivity.this, "File saved at: " + videoFile.toString(), Toast.LENGTH_SHORT).show();
		Log.d(TAG, "afterSave: Save path: " + videoFile.toString());
	}

	private void startRecording(View view) {
		if (!isRecording) {
			if (view != null) {
				view.setVisibility(View.GONE);
				btnStop.setVisibility(View.VISIBLE);
			}
			cameraFragment.startRecording(this::afterSave);
			isRecording = true;
		}
	}

	private void stopRecording(View view) {
		if (isRecording) {
			if (view != null) {
				view.setVisibility(View.GONE);
				btnStart.setVisibility(View.VISIBLE);
			}
			cameraFragment.stopRecording();
			isRecording = false;
		}
	}

	@Override
	public void onBackPressed() {
		if (isRecording) {
			stopRecording(null);
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
				SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
						.findFragmentById(R.id.map);
				mapFragment.getMapAsync(this);
			}
			// TODO: 01-08-2020 show permission rationale
		}
	}
}