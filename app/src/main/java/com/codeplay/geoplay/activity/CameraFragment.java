package com.codeplay.geoplay.activity;

import android.os.Bundle;
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
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.codeplay.geoplay.R;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CameraFragment extends Fragment {

	private static final String TAG = "CameraFragment";

	private PreviewView previewView;
	private Button btnResolution;
	private Button btnCameraSwitch;
	private TextView lblTime;

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

	private void setUpCamera(){

		ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(getContext());

		cameraProviderFuture.addListener(() -> {

			try {
				ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

				// sets up the preview
				Preview preview = new Preview.Builder()
						.setTargetRotation(previewView.getDisplay().getRotation())
						.build();

				// which camera to select rear or front
				CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();

				cameraProvider.unbindAll();

				Camera camera = cameraProvider.bindToLifecycle(
						getActivity(),
						cameraSelector,
						preview
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

}
