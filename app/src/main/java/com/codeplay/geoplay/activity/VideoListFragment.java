package com.codeplay.geoplay.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codeplay.geoplay.R;
import com.codeplay.geoplay.adapter.GeoVideoAdapter;
import com.codeplay.geoplay.database.AppDatabase;
import com.codeplay.geoplay.model.GeoVideo;
import com.codeplay.geoplay.util.GeoTagUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class VideoListFragment extends Fragment {
	private static final String TAG = "VideoListFragment";

	private RecyclerView lstVideos;

	private GeoVideoAdapter videoAdapter;
	List<GeoVideo> videos = new ArrayList<>();

	public VideoListFragment(){
		super(R.layout.fragment_video_list);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		lstVideos = view.findViewById(R.id.lstVideos);
		lstVideos.setLayoutManager(new LinearLayoutManager(getActivity()));

		refresh();
	}

	/**
	 * refreshes the recyclerview
	 */
	private void refresh() {
		getVideoList();

		videoAdapter = new GeoVideoAdapter(getContext(), videos);
		videoAdapter.setOnClickListener(video -> {
			Intent intent = new Intent(getActivity(), PlaybackActivity.class);
			intent.putExtra("geovideo_path", video.videoPath);
			startActivityForResult(intent, 2);
		});
		videoAdapter.setOnDeleteListener(video -> {
			File videoF = new File(video.videoPath);
			videoF.delete();
			AppDatabase.getInstance(getContext()).geoVideoDao().deleteVideoTags(video.id);
			AppDatabase.getInstance(getContext()).geoVideoDao().delete(video);
			refresh();
		});
		videoAdapter.setOnUploadListener(video -> {
			if(video.isUploaded){
				Toast.makeText(getContext(), "Already uploaded: " + video.uploadPath, Toast.LENGTH_SHORT).show();
				return;
			}
			String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
			DatabaseReference videoRef = FirebaseDatabase.getInstance().getReference("videos/"+userId).push();
			StorageReference fileRef = FirebaseStorage.getInstance().getReference("videos/"+userId).child(videoRef.getKey() + ".mp4");

			fileRef.putFile(FileProvider.getUriForFile(getContext(), "com.codeplay.geoplay.provider", new File(video.videoPath)))
					.addOnSuccessListener(taskSnapshot -> {
						Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();

						Map<String, Object> values = GeoTagUtil.getMapFromGeoVideo(getContext(), video.id);
						values.put("video_path", taskSnapshot.getMetadata().getReference().toString());
						values.put("upload_timestamp", System.currentTimeMillis()); // bad practice
						videoRef.setValue(values, (error, ref) -> {
							if (error != null) {
								Toast.makeText(getContext(), "Error:"+error.getDetails(), Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
								video.isUploaded = true;
								video.uploadPath = videoRef.toString();
								AppDatabase.getInstance(getContext()).geoVideoDao().update(video);
							}
						});

					})
					.addOnFailureListener(e -> {
						Toast.makeText(getContext(), "Error:"+e.getMessage(), Toast.LENGTH_SHORT).show();
					});

			Toast.makeText(getContext(), "Uploading...", Toast.LENGTH_SHORT).show();
		});
		lstVideos.setAdapter(videoAdapter);
	}


	/**
	 * Get list of mp4s in app data directory
	 */
	private void getVideoList() {
		videos = AppDatabase.getInstance(getContext()).geoVideoDao().getAll();
	}

	@Override
	public void onResume() {
		super.onResume();
		refresh();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 2) {
			if (resultCode == RESULT_OK) {
				refresh();
			}
		}
	}
}
