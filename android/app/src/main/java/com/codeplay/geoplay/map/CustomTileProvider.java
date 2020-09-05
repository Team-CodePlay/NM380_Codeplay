package com.codeplay.geoplay.map;

import com.google.android.gms.maps.model.UrlTileProvider;

import java.net.MalformedURLException;
import java.net.URL;

public class CustomTileProvider extends UrlTileProvider {
	private String baseUrl;

	public CustomTileProvider(int i, int i1, String baseUrl) {
		super(i, i1);
		this.baseUrl = baseUrl;
	}

	@Override
	public URL getTileUrl(int i, int i1, int i2) {
		String formattedUrl = baseUrl.replace("${z}", Integer.toString(i2))
				.replace("${x}", Integer.toString(i))
				.replace("${y}", Integer.toString(i1));
		try {
			return new URL(formattedUrl);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new AssertionError(e);
		}
	}
}
