package com.codeplay.geoplay.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

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

		videoServiceExecutor = Executors.newSingleThreadExecutor();
		videoTimeServiceExecutor = Executors.newSingleThreadExecutor();

		previewView.post(this::setUpCamera);

	}

	@SuppressLint("RestrictedApi")
	private void setUpCamera(){

		ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(getContext());

		cameraProviderFuture.addListener(() -> {

			try {
				ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

				CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();

				videoCapture = new VideoCapture.Builder()
						.setTargetRotation(previewView.getDisplay().getRotation())
						.setCameraSelector(cameraSelector)
						.build();

				// sets up the preview
				Preview preview = new Preview.Builder()
						.setTargetRotation(previewView.getDisplay().getRotation())
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

	}

	@SuppressLint("RestrictedApi")
	public void stopRecording(){
		Log.d(TAG, "stopRecording: ");
		videoCapture.stopRecording();
	}

}
