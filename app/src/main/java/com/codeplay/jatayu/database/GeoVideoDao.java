package com.codeplay.jatayu.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.codeplay.jatayu.model.GeoTag;
import com.codeplay.jatayu.model.GeoVideo;
import com.codeplay.jatayu.model.VideoWithTag;

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
