package com.codeplay.geoplay.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.codeplay.geoplay.R;

/**
 * For location and video recording
 */
public class RecordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
    }
}