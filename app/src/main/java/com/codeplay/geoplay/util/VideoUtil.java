package com.codeplay.geoplay.util;

import android.util.Log;

import com.codeplay.geoplay.model.GeoTag;

import org.jcodec.containers.mp4.boxes.MetaValue;
import org.jcodec.movtool.MetadataEditor;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.codeplay.geoplay.util.GeoTagUtil.getGeoTagJson;

public class VideoUtil {

	public static void writeMetaData(File videoFile, List<GeoTag> geoTags){
		MetadataEditor mediaMeta = null;
		try {
			mediaMeta = MetadataEditor.createFrom(videoFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Map<String, MetaValue> keyedMeta = mediaMeta.getKeyedMeta();
		if (keyedMeta != null) {
			System.out.println("Keyed metadata:");
			for (Map.Entry<String, MetaValue> entry : keyedMeta.entrySet()) {
				System.out.println(entry.getKey() + ": " + entry.getValue());
			}
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

}
