package com.codeplay.geoplay.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codeplay.geoplay.AppClass;
import com.codeplay.geoplay.R;
import com.codeplay.geoplay.database.AppDatabase;
import com.codeplay.geoplay.map.BingTileProvider;
import com.codeplay.geoplay.map.OsmTileProvider;
import com.codeplay.geoplay.model.GeoTag;
import com.codeplay.geoplay.model.GeoVideo;
import com.codeplay.geoplay.ui.LockBottomSheetBehaviour;
import com.codeplay.geoplay.util.GeoTagUtil;
import com.codeplay.geoplay.util.PermissionUtil;
import com.codeplay.geoplay.util.TileProviderUtil;
import com.codeplay.geoplay.util.VideoUtil;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
	private ImageView rcdIndicator;
	private TextView lbllatitude;
	private TextView lbllongitude;
	private TextView lbldatetime;
	private TextView lblbearing;
	private LockBottomSheetBehaviour behavior;

	private Boolean isRecording = false;

	private ExecutorService backgoundExecutor;
	private Future backgoundExecutorFuture;
	List<GeoTag> geoTags = new ArrayList<>();
	private Polyline polyline;
	private Long startTime;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		SharedPreferences.Editor editor = prefs.edit();

		final TapTargetSequence sequence = new TapTargetSequence(this)
				.targets(
						TapTarget.forView(findViewById(R.id.btnStart), "Start / Stop Record",
								"Click here to start or stop recording videos")
								.outerCircleAlpha(0.96f)
								.titleTextSize(30)
								.titleTextColor(R.color.colorAccent)
								.descriptionTextSize(20)
								.descriptionTextColor(android.R.color.white)
								.dimColor(android.R.color.black)
								.outerCircleColor(R.color.colorPrimaryDark)
								.targetCircleColor(R.color.colorPrimaryLight)
								.drawShadow(true)
								.cancelable(true)
								.targetRadius(40)
								.transparentTarget(true)
								.id(1),
						TapTarget.forView(findViewById(R.id.btm_sheet_header), "Map",
								"The map is under this tab. You can slide it up to view it.")
								.outerCircleAlpha(0.96f)
								.titleTextSize(30)
								.titleTextColor(R.color.colorAccent)
								.descriptionTextSize(20)
								.descriptionTextColor(android.R.color.white)
								.dimColor(android.R.color.black)
								.outerCircleColor(R.color.colorPrimaryDark)
								.targetCircleColor(R.color.colorPrimaryLight)
								.drawShadow(true)
								.cancelable(true)
								.targetRadius(50)
								.transparentTarget(true)
								.id(2),
						TapTarget.forView(findViewById(R.id.dummyResolution), "Resolution",
								"Use this to change the resolution before recording a video.")
								.outerCircleAlpha(0.96f)
								.titleTextSize(30)
								.titleTextColor(R.color.colorAccent)
								.descriptionTextSize(20)
								.descriptionTextColor(android.R.color.white)
								.dimColor(android.R.color.black)
								.outerCircleColor(R.color.colorPrimaryDark)
								.targetCircleColor(R.color.colorPrimaryLight)
								.drawShadow(true)
								.cancelable(true)
								.targetRadius(40)
								.transparentTarget(true)
								.id(3),
						TapTarget.forView(findViewById(R.id.dummyCameraSwitch), "Switch Camera",
								"Use this to switch to the front or back camera when needed.")
								.outerCircleAlpha(0.96f)
								.titleTextSize(30)
								.titleTextColor(R.color.colorAccent)
								.descriptionTextSize(20)
								.descriptionTextColor(android.R.color.white)
								.dimColor(android.R.color.black)
								.outerCircleColor(R.color.colorPrimaryDark)
								.targetCircleColor(R.color.colorPrimaryLight)
								.drawShadow(true)
								.cancelable(true)
								.targetRadius(40)
								.transparentTarget(true)
								.id(4),
						TapTarget.forView(findViewById(R.id.dummyPlaceholder), "Congratulations!",
								"You have completed the tutorial of the basic functions.")
								.outerCircleAlpha(0.96f)
								.titleTextSize(30)
								.titleTextColor(R.color.colorAccent)
								.descriptionTextSize(20)
								.descriptionTextColor(android.R.color.white)
								.dimColor(android.R.color.black)
								.outerCircleColor(R.color.colorPrimaryDark)
								.targetCircleColor(R.color.colorPrimaryLight)
								.drawShadow(true)
								.cancelable(true)
								.targetRadius(50)
								.transparentTarget(false)
								.id(5)
				).listener(new TapTargetSequence.Listener() {
					@Override
					public void onSequenceFinish() {
						editor.putBoolean("SHOW_TUTORIAL_2", false);
						editor.apply();
						Intent j = new Intent(RecordActivity.this, MainActivity.class);
						startActivity(j);
					}

					@Override
					public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {

					}

					@Override
					public void onSequenceCanceled(TapTarget lastTarget) {

					}
				});
		if (prefs.getBoolean("SHOW_TUTORIAL_2", true)){
			sequence.start();
		}

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

		bottomSheet = findViewById(R.id.bottom_sheet);
		lblSpeed = findViewById(R.id.lblSpeed);
		btnStart = findViewById(R.id.btnStart);
		btnStop = findViewById(R.id.btnStop);
		rcdIndicator = findViewById(R.id.recording_indicator);
		lbllatitude = findViewById(R.id.latitude);
		lbllongitude = findViewById(R.id.longitude);
		lbldatetime = findViewById(R.id.date);
		lblbearing = findViewById(R.id.bearing);
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
		backgoundExecutor = Executors.newSingleThreadExecutor();
	}

	private void setUpMap() {
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

			TileProviderUtil.setUpTileProvider(RecordActivity.this, mMap);

			mMap.setMyLocationEnabled(true);
			polyline = mMap.addPolyline(new PolylineOptions().visible(true)
					.jointType(JointType.ROUND)
					.zIndex(1)
					.width(10));

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


	private void saveMetaData(File videoFile) {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

		Toast.makeText(RecordActivity.this, "File saved at: " + videoFile.toString(), Toast.LENGTH_SHORT).show();
		Log.d(TAG, "afterSave: Save path: " + videoFile.toString());

		backgoundExecutorFuture = backgoundExecutor.submit(() -> {
			VideoUtil.writeMetaData(videoFile, geoTags);


			GeoVideo geoVideo = new GeoVideo();
			geoVideo.videoPath = videoFile.getPath();

			geoVideo.videoStartTime = startTime;
			geoVideo.duration = (int) (System.currentTimeMillis() - startTime) / 1000;
			geoVideo.size = videoFile.length();
			if (!geoTags.isEmpty()) {
				geoVideo.startLocation = new GeoVideo.Location(
						geoTags.get(0).latitude,
						geoTags.get(0).longitude
				);
				geoVideo.endLocation = new GeoVideo.Location(
						geoTags.get(geoTags.size() - 1).latitude,
						geoTags.get(geoTags.size() - 1).longitude
				);
			}
			int videoId = (int) AppDatabase.getInstance(RecordActivity.this).geoVideoDao().insert(geoVideo);

			for (int i = 0; i < geoTags.size(); i++) {
				geoTags.get(i).videoId = videoId;
			}
			if (!geoTags.isEmpty())
				AppDatabase.getInstance(RecordActivity.this).geoVideoDao().insertTags(geoTags);
		});

	}

	@SuppressLint("MissingPermission")
	private void startRecording() {
		if (!isRecording) {
			lblSpeed.setVisibility(View.VISIBLE);
			rcdIndicator.setVisibility(View.VISIBLE);
			lbllongitude.setVisibility(View.VISIBLE);
			lbllatitude.setVisibility(View.VISIBLE);
			lbldatetime.setVisibility(View.VISIBLE);
			lblbearing.setVisibility(View.VISIBLE);
			int currentOrientation = getResources().getConfiguration().orientation;
			if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
			} else {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
			}

			geoTags.clear();
			polyline.setPoints(new ArrayList<>());

			startTime = System.currentTimeMillis();

			cameraFragment.startRecording(this::saveMetaData);

			int locationAccuracy;

			switch(AppClass.getSP().getString("location_accuracy", "medium")){
				case "high":
					locationAccuracy = LocationRequest.PRIORITY_HIGH_ACCURACY;
					break;
				case "low":
					locationAccuracy = LocationRequest.PRIORITY_LOW_POWER;
					break;
				case "medium":
				default:
					locationAccuracy = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
					break;
			}

			locationProviderClient.requestLocationUpdates(
					new LocationRequest().setInterval(1000).setPriority(locationAccuracy),
					locationCallback,
					Looper.getMainLooper()
			).addOnCanceledListener(() -> Log.d(TAG, "location: cancelled"));

			isRecording = true;
		}
	}

	private void stopRecording() {
		if (isRecording) {
			lblSpeed.setVisibility(View.GONE);
			rcdIndicator.setVisibility(View.GONE);
			lbllongitude.setVisibility(View.GONE);
			lbllatitude.setVisibility(View.GONE);
			lbldatetime.setVisibility(View.GONE);
			lblbearing.setVisibility(View.GONE);
			locationProviderClient.removeLocationUpdates(locationCallback);
			cameraFragment.stopRecording();
			isRecording = false;
			setResult(RESULT_OK);
		}
	}

	public static String TimestampConverter(Long timestamp) {
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss", Locale.US);
		Date resultdate = new Date(timestamp);
		return sdf.format(resultdate);
	}

	private LocationCallback locationCallback = new LocationCallback() {
		@Override
		public void onLocationResult(LocationResult locationResult) {
			super.onLocationResult(locationResult);
			Location location = locationResult.getLastLocation();
			LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

			GeoTag geoTag = new GeoTag();
			geoTag.latitude = latLng.latitude;
			geoTag.longitude = latLng.longitude;
			geoTag.bearing = (int) location.getBearing();
			geoTag.videoTime = cameraFragment.getCurrentTime();
			geoTag.timestamp = System.currentTimeMillis();

			if (!geoTags.isEmpty()) {
				float[] results = new float[1];
				Location.distanceBetween(
						geoTags.get(geoTags.size() - 1).latitude,
						geoTags.get(geoTags.size() - 1).longitude,
						latLng.latitude,
						latLng.longitude,
						results);
				geoTag.speed = (int) (location.getSpeed() * 18 / 5);
				lblSpeed.setText(String.format("%d km/hr", new Float(geoTag.speed).intValue()));
				lbllatitude.setText(String.format("Lat: %f", geoTag.latitude));
				lbllongitude.setText(String.format("Long: %f", geoTag.longitude));
				lbldatetime.setText(String.format("%s", TimestampConverter(geoTag.timestamp)));
				lblbearing.setText(String.format("Bearing: %d degrees w.r.t. North", geoTag.bearing));

			}

			// update the map with the new location
//			mMap.addCircle(new CircleOptions().center(latLng).visible(true).fillColor(Color.BLACK).radius(4));

			geoTags.add(geoTag);
			polyline.setPoints(GeoTagUtil.getLatLngFromList(geoTags));
		}
	};

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
		if (requestCode == PermissionUtil.PERMISSION_REQUEST_CODE) {
			if (PermissionUtil.areAllPermissionsGranted(RecordActivity.this)) {
				setUpMap();
			} else {
				Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
				PermissionUtil.showPermissionsRationale(RecordActivity.this);
			}
		}
	}

	@Override
	public void onBackPressed() {
		if (isRecording) {
			stopRecording();
		}
		super.onBackPressed();
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