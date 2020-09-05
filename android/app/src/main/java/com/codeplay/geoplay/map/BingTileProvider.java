package com.codeplay.geoplay.map;

import com.google.android.gms.maps.model.UrlTileProvider;

import java.net.MalformedURLException;
import java.net.URL;

public class BingTileProvider extends UrlTileProvider {

	public BingTileProvider(int i, int i1) {
		super(i, i1);
	}

	public static String TileXYToQuadKey(int tileX, int tileY, int levelOfDetail)
	{
		StringBuilder quadKey = new StringBuilder();
		for (int i = levelOfDetail; i > 0; i--)
		{
			char digit = '0';
			int mask = 1 << (i - 1);
			if ((tileX & mask) != 0)
			{
				digit++;
			}
			if ((tileY & mask) != 0)
			{
				digit++;
				digit++;
			}
			quadKey.append(digit);
		}
		return quadKey.toString();
	}


	@Override
	public URL getTileUrl(int i, int i1, int i2) {
		String urlFormat = "http://ak.dynamic.t1.tiles.virtualearth.net/comp/ch/%s.png?mkt=en-US&it=G,L&shading=hill&og=1043&n=z";
		try {
			URL url = new URL(String.format(urlFormat, TileXYToQuadKey(i, i1, i2)));
			return url;
		} catch (MalformedURLException e) {
			throw new AssertionError(e);
		}
	}
}
