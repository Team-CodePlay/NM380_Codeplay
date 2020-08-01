package com.codeplay.geoplay.model;

import android.util.Log;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "geotag")
public class GeoTag {

	@PrimaryKey(autoGenerate = true)
	public int id;

	@ColumnInfo(name = "video_id")
	public int videoId;

	public Double latitude;
	public Double longitude;

	@ColumnInfo(name = "video_time")
	public Long videoTime;
	public Integer speed;
	public Integer bearing;
	public Long timestamp;

	public GeoTag() {}

	@Ignore
	public GeoTag(Double latitude, Double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}
}
