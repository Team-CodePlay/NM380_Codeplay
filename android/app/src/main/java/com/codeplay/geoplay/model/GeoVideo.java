package com.codeplay.geoplay.model;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "geovideo")
public class GeoVideo {
	@PrimaryKey(autoGenerate = true)
	public int id;

	@ColumnInfo(name="video_path")
	public String videoPath;

	@Embedded(prefix = "start_location_")
	public Location startLocation;

	@Embedded(prefix = "end_location_")
	public Location endLocation;

	public Integer duration;

	/**
	 * in bytes
	 */
	public Long size;

	@ColumnInfo(name = "video_start_time")
	public Long videoStartTime;

	@ColumnInfo(name = "is_uploaded")
	public Boolean isUploaded = false;

	@ColumnInfo(name = "upload_path")
	public String uploadPath;

	public static class Location {
		public Double latitude;
		public Double longitude;

		@Ignore
		public Location() {
		}

		public Location(Double latitude, Double longitude) {
			this.latitude = latitude;
			this.longitude = longitude;
		}
	}

}
