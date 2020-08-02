package com.codeplay.jatayu.util;

import android.content.Context;

import com.codeplay.jatayu.database.AppDatabase;
import com.codeplay.jatayu.model.GeoTag;
import com.codeplay.jatayu.model.VideoWithTag;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeoTagUtil {

	/**
	 * returns json from list of geo tags
	 * @param geoTags
	 * @return
	 * @throws JSONException
	 */
	public static String getGeoTagJson(List<GeoTag> geoTags) throws JSONException {
		JSONArray array = getJsonArrayFromList(geoTags);
		JSONObject temp = new JSONObject();
		temp.put("locations", array);
		return temp.toString();
	}

	/**
	 * Converts GeoTag list to JSON array
	 * @param geoTags
	 * @return
	 */
	public static JSONArray getJsonArrayFromList(List<GeoTag> geoTags) {
		JSONArray array = new JSONArray();
		for (GeoTag t : geoTags) {
			array.put(t.toJson());
		}
		return array;
	}



	/**
	 * returns list of GeoTags from json
	 * @param jsonString
	 * @return
	 */
	public static List<GeoTag> readFromJsonString(String jsonString) {
		try {
			JSONObject object = new JSONObject(jsonString);
			JSONArray locations = object.getJSONArray("locations");
			List<GeoTag> geoTags = new ArrayList<>();
			for (int i = 0; i < locations.length(); i++) {
				geoTags.add(new GeoTag(locations.optJSONObject(i)));
			}
			Collections.sort(geoTags, (o1, o2) -> Long.compare(o1.videoTime, o2.videoTime));
			return geoTags;
		} catch (JSONException e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	/**
	 * Converts GeoTag list to LatLng (used for Polyline)
	 * @param geoTags
	 * @return
	 */
	public static List<LatLng> getLatLngFromList(List<GeoTag> geoTags) {
		List<LatLng> latLngs = new ArrayList<>();
		for (GeoTag t : geoTags) {
			latLngs.add(t.getLatLng());
		}
		return latLngs;
	}

	public static Map<String, Object> getMapFromGeoTag(GeoTag geoTag) {
		Map<String, Object> geoTagMap = new HashMap<>();
		geoTagMap.put("lat", geoTag.latitude);
		geoTagMap.put("lng", geoTag.longitude);
		geoTagMap.put("video_time", geoTag.videoTime);
		geoTagMap.put("speed", geoTag.speed);
		geoTagMap.put("bearing", geoTag.bearing);
		geoTagMap.put("timestamp", geoTag.timestamp);
		return geoTagMap;
	}

	public static Map<String, Object> getMapFromGeoVideo(Context context, int videoId) {
		Map<String, Object> videoMap = new HashMap<>();
		VideoWithTag videoWithTags = AppDatabase.getInstance(context).geoVideoDao().getVideosWithTags(videoId);

		Map<String, Object> startLoc = new HashMap<>();
		startLoc.put("lat", videoWithTags.geoVideo.startLocation.latitude);
		startLoc.put("lng", videoWithTags.geoVideo.startLocation.longitude);
		videoMap.put("start_location", startLoc);

		Map<String, Object> endLoc = new HashMap<>();
		endLoc.put("lat", videoWithTags.geoVideo.endLocation.latitude);
		endLoc.put("lng", videoWithTags.geoVideo.endLocation.longitude);
		videoMap.put("end_location", endLoc);

		videoMap.put("duration", videoWithTags.geoVideo.duration);
		videoMap.put("size", videoWithTags.geoVideo.size);
		videoMap.put("video_start_time", videoWithTags.geoVideo.videoStartTime);

		List<Object> geoTags = new ArrayList<>();
		for (GeoTag geoTag :
				videoWithTags.geoTags) {
			geoTags.add(getMapFromGeoTag(geoTag));
		}
		videoMap.put("geotags", geoTags);

		return videoMap;
	}


}
