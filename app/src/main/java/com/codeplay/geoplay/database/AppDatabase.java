package com.codeplay.geoplay.database;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

public abstract class AppDatabase extends RoomDatabase {
	private static AppDatabase instance = null;

	public abstract GeoVideoDao geoVideoDao();

	public static AppDatabase getInstance(Context context){
		if(instance != null) {
			return instance;
		}
		synchronized (context.getApplicationContext()) {
			instance = Room
					.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "geotagging.db")
					.allowMainThreadQueries()
					.build();
			return instance;
		}
	}
}
