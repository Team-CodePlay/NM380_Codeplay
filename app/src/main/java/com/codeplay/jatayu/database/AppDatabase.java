package com.codeplay.jatayu.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.codeplay.jatayu.model.GeoTag;
import com.codeplay.jatayu.model.GeoVideo;

@Database(entities = {GeoVideo.class, GeoTag.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
	private static AppDatabase instance = null;

	public abstract GeoVideoDao geoVideoDao();

	public static AppDatabase getInstance(Context context){
		if(instance != null) {
			return instance;
		}
		synchronized (context.getApplicationContext()) {
			instance = Room
					.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "jatayu.db")
					.allowMainThreadQueries()
					.build();
			return instance;
		}
	}
}
