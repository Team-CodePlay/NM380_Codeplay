package com.codeplay.jatayu.model;

import android.util.Log;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

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

	public GeoTag(JSONObject jsonObject){
		latitude = jsonObject.optDouble("latitude");
		longitude = jsonObject.optDouble("longitude");
		videoTime = jsonObject.optLong("video_time");
		speed = jsonObject.optInt("speed");
		timestamp = jsonObject.optLong("timestamp");
		bearing = jsonObject.optInt("bearing");
	}

	@Ignore
	public JSONObject toJson() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("latitude", latitude);
			jsonObject.put("longitude", longitude);
			jsonObject.put("video_time", videoTime);
			jsonObject.put("speed", speed);
			jsonObject.put("timestamp", timestamp);
			jsonObject.put("bearing", bearing);
		} catch (JSONException err) {
			Log.d("Error", err.toString());
		}
		return jsonObject;
	}

	public LatLng getLatLng() {
		return new LatLng(latitude, longitude);
	}
}
