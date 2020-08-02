package com.codeplay.geoplay.util;

import com.codeplay.geoplay.model.GeoTag;

import org.jcodec.containers.mp4.boxes.MetaValue;
import org.jcodec.movtool.MetadataEditor;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.codeplay.geoplay.util.GeoTagUtil.getGeoTagJson;
import static com.codeplay.geoplay.util.GeoTagUtil.readFromJsonString;

public class VideoUtil {

	private static final String TAG = "VideoUtil";

	public static void writeMetaData(File videoFile, List<GeoTag> geoTags){
		MetadataEditor mediaMeta = null;
		try {
			mediaMeta = MetadataEditor.createFrom(videoFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Map<String, MetaValue> keyedMeta = mediaMeta.getKeyedMeta();
		if (keyedMeta != null) {
			try {
				keyedMeta.put("locations", MetaValue.createString(getGeoTagJson(geoTags)));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		try {
			mediaMeta.save(true); // may not work if set to false
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static List<GeoTag> readMetaData(String videoPath){
		List<GeoTag> geoTags = new ArrayList<>();
		MetadataEditor mediaMeta = null;
		try {
			mediaMeta = MetadataEditor.createFrom(new File(videoPath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Map<String, MetaValue> keyedMeta = mediaMeta.getKeyedMeta();
		if (keyedMeta != null) {
			for (Map.Entry<String, MetaValue> entry : keyedMeta.entrySet()) {
				if (entry.getKey().equals("locations")) {
					geoTags = readFromJsonString(entry.getValue().getString());
				}
			}
		}
		return geoTags;
	}

}
