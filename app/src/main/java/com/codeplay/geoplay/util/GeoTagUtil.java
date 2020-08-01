package com.codeplay.geoplay.util;

import com.codeplay.geoplay.model.GeoTag;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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



}
