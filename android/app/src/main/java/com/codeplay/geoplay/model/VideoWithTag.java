package com.codeplay.geoplay.model;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class VideoWithTag {
	@Embedded
	public GeoVideo geoVideo;
	@Relation(
			parentColumn = "id",
			entityColumn = "video_id"
	)
	public List<GeoTag> geoTags;
}
