package com.codeplay.geoplay.activity;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.CameraX;
import androidx.camera.core.Preview;
import androidx.camera.core.VideoCapture;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.codeplay.geoplay.R;
import com.codeplay.geoplay.camera.AfterSaveCallback;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.util.Timer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class CameraFragment extends Fragment {

	private static final String TAG = "CameraFragment";

	private PreviewView previewView;
	private Button btnResolution;
	private Button btnCameraSwitch;
	private TextView lblTime;

	private VideoCapture videoCapture;
	private File currentFile;

	private Executor videoServiceExecutor;
	private Executor videoTimeServiceExecutor;
	private ScheduledExecutorService timerScheduledExecutor;
	private ScheduledFuture<?> timeFuture;
	private Long startTime;

	private Boolean cameraSwitchAvailable;
	private Integer lensFacing;

	/**
	 * static list of available resolutions
	 */
	private Size[] availableResolutions = new Size[]{
			new Size(1920, 1080), // 16:9
			new Size(1280, 720),
			new Size(854, 480),
			new Size(640, 360),
			new Size(426, 240),
			new Size(1600, 1200), // 4:3
			new Size(1280, 960),
			new Size(800, 600),
			new Size(640, 480),
	};
	private int selectedResolution = 0;

	// TODO: 02-08-2020 add tap to focus


	public CameraFragment(){
		super(R.layout.fragment_camera);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		btnResolution = view.findViewById(R.id.btnResolution);
		btnCameraSwitch = view.findViewById(R.id.btnCameraSwitch);
		lblTime = view.findViewById(R.id.lblTime);
		previewView = view.findViewById(R.id.previewView);

		btnCameraSwitch.setOnClickListener(v -> {
			if (lensFacing == CameraSelector.LENS_FACING_BACK) {
				lensFacing = CameraSelector.LENS_FACING_FRONT;
			} else {
				lensFacing = CameraSelector.LENS_FACING_BACK;
			}
			previewView.post(this::setUpCamera);
			Log.d(TAG, "onViewCreated: Lens changed");
		});

		btnResolution.setOnClickListener(v -> {
			AlertDialog.Builder adb = new AlertDialog.Builder(getContext());
			ListAdapter listAdapter = new ArrayAdapter<>(getContext(), android.R.layout.select_dialog_singlechoice, availableResolutions);
			adb.setSingleChoiceItems(listAdapter, selectedResolution, (dialog, which) -> {
				selectedResolution = which;
				btnResolution.setText(availableResolutions[selectedResolution].toString());
				dialog.dismiss();
				previewView.post(this::setUpCamera);
			});
			adb.setNegativeButton("Cancel", null);
			adb.setTitle("Which one?");
			adb.show();
		});

		videoServiceExecutor = Executors.newSingleThreadExecutor();
		videoTimeServiceExecutor = Executors.newSingleThreadExecutor();
		timerScheduledExecutor = Executors.newSingleThreadScheduledExecutor();

		previewView.post(this::setUpCamera);
		btnResolution.setText(availableResolutions[selectedResolution].toString());

	}

	@SuppressLint("RestrictedApi")
	private void setUpCamera(){

		ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(getContext());

		cameraProviderFuture.addListener(() -> {

			try {
				ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

				if (lensFacing == null) {
					if (CameraX.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) && CameraX.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA)) {
						cameraSwitchAvailable = true;
						btnCameraSwitch.setVisibility(View.VISIBLE);
					} else {
						cameraSwitchAvailable = false;
						btnCameraSwitch.setVisibility(View.GONE);
					}
					lensFacing = CameraSelector.LENS_FACING_BACK;
				}

				Size tempSize;
				if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
					tempSize = availableResolutions[selectedResolution];
				} else {
					tempSize = new Size(
							availableResolutions[selectedResolution].getHeight(),
							availableResolutions[selectedResolution].getWidth()
					);
				}


				CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(lensFacing).build();


				videoCapture = new VideoCapture.Builder()
						.setTargetRotation(previewView.getDisplay().getRotation())
						.setTargetResolution(tempSize)
						.setCameraSelector(cameraSelector)
						.build();

				// sets up the preview
				Preview preview = new Preview.Builder()
						.setTargetRotation(previewView.getDisplay().getRotation())
						.setTargetResolution(tempSize)
						.setCameraSelector(cameraSelector)
						.build();

				// which camera to select rear or front

				cameraProvider.unbindAll();

				Camera camera = cameraProvider.bindToLifecycle(
						getActivity(),
						cameraSelector,
						preview,
						videoCapture
				);

				preview.setSurfaceProvider(previewView.createSurfaceProvider());

				Log.d(TAG, "setUpCamera: Setup complete");
			} catch (Exception e) {
				Log.d(TAG, "setUpCamera: Cannot setup camera");
				Toast.makeText(getContext(), "Cannot start camera: " + e.getMessage(), Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}

		}, ContextCompat.getMainExecutor(getContext()));

		Log.d(TAG, "setUpCamera: Setting up");
	}

	/**
	 * get location for storing the video file
	 * @return
	 */
	private File getMp4Directory(){
		return getContext().getExternalFilesDir("videos");
	}

	/**
	 * get file name .mp4
	 * @return
	 */
	private String getMp4Name(){
		return System.currentTimeMillis() + ".mp4";
	}

	@SuppressLint("RestrictedApi")
	public void startRecording(AfterSaveCallback afterSaveCallback){

		btnCameraSwitch.setVisibility(View.GONE);
		btnResolution.setVisibility(View.GONE);

		File folder = getMp4Directory();
		if(!folder.exists()){
			folder.mkdirs();
		}

		currentFile = new File(folder, getMp4Name());

		videoCapture.startRecording(currentFile, videoServiceExecutor, new VideoCapture.OnVideoSavedCallback() {
			@Override
			public void onVideoSaved(@NonNull File file) {
				Log.i(TAG, "Video file: " + file.toString());
				new Handler(Looper.getMainLooper()).post(
						() -> {
							Log.d(TAG, "onVideoSaved: File totally saved. Running after save");
							afterSaveCallback.afterSave(file);
						}
				);
			}

			@Override
			public void onError(int videoCaptureError, @NonNull String message, @Nullable Throwable cause) {
				Log.i(TAG, "onError: " + message);
				cause.printStackTrace();
				new Handler(Looper.getMainLooper()).post(
						() -> Toast.makeText(getContext(), "onError: " + message, Toast.LENGTH_LONG).show()
				);
			}
		});

		startTime = System.currentTimeMillis();


		timeFuture = timerScheduledExecutor.scheduleAtFixedRate(() -> {
			new Handler(Looper.getMainLooper()).post(() -> {
				long difference = System.currentTimeMillis() - startTime;

				int secs = (int) (difference / 1000);
				int mins = secs / 60;
				secs = secs % 60;
				if (lblTime != null) {
					lblTime.setText(String.format("%02d:%02d", mins, secs));
				}
			});
		}, 0, 1, TimeUnit.SECONDS);

	}

	@SuppressLint("RestrictedApi")
	public void stopRecording(){
		Log.d(TAG, "stopRecording: ");
		videoCapture.stopRecording();
		if(timeFuture != null){
			timeFuture.cancel(true);
		}
		lblTime.setText(String.format("%02d:%02d", 0, 0));
		if (cameraSwitchAvailable) {
			btnCameraSwitch.setVisibility(View.VISIBLE);
		}
		btnResolution.setVisibility(View.VISIBLE);

	}


	/**
	 * for the location callback task. this get's added in the geotag data
	 *
	 * @return current video's time
	 */
	public long getCurrentTime() {
		return System.currentTimeMillis() - startTime;
	}


}
