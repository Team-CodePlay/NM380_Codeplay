package com.codeplay.geoplay.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.codeplay.geoplay.R;

/**
 * For map and video playback, info and sharing.
 */
public class PlaybackActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_playback);
	}
}