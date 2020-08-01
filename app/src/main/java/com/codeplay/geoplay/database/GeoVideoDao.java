package com.codeplay.geoplay.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.codeplay.geoplay.model.GeoTag;
import com.codeplay.geoplay.model.GeoVideo;
import com.codeplay.geoplay.model.VideoWithTag;

import java.util.List;

@Dao
public interface GeoVideoDao {
	@Query("SELECT * FROM geovideo")
	List<GeoVideo> getAll();

	@Insert
	long insert(GeoVideo geoVideo);

	@Insert
	void insertTags(List<GeoTag> geoTags);

	@Update
	void update(GeoVideo geoVideo);

	@Delete
	void delete(GeoVideo geoVideo);

	@Query("DELETE FROM geotag WHERE video_id = :videoId")
	void deleteVideoTags(int videoId);

	@Transaction
	@Query("SELECT * FROM geovideo WHERE id = :id")
	VideoWithTag getVideosWithTags(int id);

}
