package com.codeplay.geoplay.util;

import android.content.Context;

import androidx.preference.PreferenceManager;

import com.codeplay.geoplay.map.BingTileProvider;
import com.codeplay.geoplay.map.CustomTileProvider;
import com.codeplay.geoplay.map.OsmTileProvider;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;

public class TileProviderUtil {

	public static void setUpTileProvider(Context context, GoogleMap mMap){
		if (PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).getBoolean("custom_map_switch", false)) {
			String customTileUrl = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).getString("custom_map_string_input", "");
			if (!customTileUrl.isEmpty()) {
				mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
				TileOverlay tileOverlay1 = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(new CustomTileProvider(256, 256, customTileUrl)));
				tileOverlay1.setZIndex(-1);
			}
		} else {
			switch (PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext())
					.getString("choose_map", "google")) {
				case "google":
					break;
				case "bing":
					mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
					TileOverlay tileOverlay1 = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(new BingTileProvider(256, 256)));
					tileOverlay1.setZIndex(-1);
					break;
				case "osm":
					mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
					TileOverlay tileOverlay2 = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(new OsmTileProvider(256, 256)));
					tileOverlay2.setZIndex(-1);
					break;
				default:
					break;
			}
		}
	}

}
