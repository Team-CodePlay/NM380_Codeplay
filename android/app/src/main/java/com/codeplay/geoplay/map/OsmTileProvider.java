package com.codeplay.geoplay.map;

import com.google.android.gms.maps.model.UrlTileProvider;

import java.net.MalformedURLException;
import java.net.URL;

public class OsmTileProvider extends UrlTileProvider {


	public OsmTileProvider(int i, int i1) {
		super(i, i1);
	}

	@Override
	public URL getTileUrl(int i, int i1, int i2) {
		String urlFormat = "https://a.tile.openstreetmap.org/%d/%d/%d.png";
		try {
			return new URL(String.format(urlFormat, i2, i, i1));
		} catch (MalformedURLException e) {
			throw new AssertionError(e);
		}
	}
}
