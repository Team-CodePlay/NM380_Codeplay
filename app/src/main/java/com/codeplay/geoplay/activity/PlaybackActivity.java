package com.codeplay.geoplay.activity;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codeplay.geoplay.R;
import com.codeplay.geoplay.map.BingTileProvider;
import com.codeplay.geoplay.map.CustomTileProvider;
import com.codeplay.geoplay.map.OsmTileProvider;
import com.codeplay.geoplay.model.GeoTag;
import com.codeplay.geoplay.util.GeoTagUtil;
import com.codeplay.geoplay.util.TileProviderUtil;
import com.codeplay.geoplay.util.VideoUtil;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * For map and video playback, info and sharing.
 */
public class PlaybackActivity extends AppCompatActivity implements OnMapReadyCallback {

	private static final String TAG = "PlaybackActivity";

	private GoogleMap mMap;
	private PlayerView playerView;
	private ImageView fullscreenButton;
	private TextView lbllatitude;
	private TextView lbllongitude;
	private TextView lbldatetime;
	private TextView lblbearing;
	private TextView lblspeed;

	private SimpleExoPlayer player;

	private Boolean mapUpdatePause = false;
	private ScheduledExecutorService mapUpdateService;
	private ScheduledFuture<?> mapUpdateFuture;

	private ExecutorService backgroundExecutor;

	private String videoPath;
	private List<GeoTag> geoTags;
	private List<LatLng> latLngs;

	Polyline polyline;

	Marker currentLocationMarker;

	boolean exoFullscreen = false;
	boolean exoPortrait = false;
	private boolean exoPlayWhenReady = true;
	private int exoCurrentWindow = 0;
	private long exoPlaybackPosition = 0;
	private int mapUpdateIndex = 0;


	private Player.EventListener exoPlaybackStateListener = new Player.EventListener() {
		@Override
		public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
			if (mapUpdateFuture != null && !mapUpdateFuture.isCancelled()) {
				if (playbackState == Player.STATE_READY && playWhenReady) {
					mapUpdatePause = false;
				} else if (playbackState == Player.STATE_READY) {
					mapUpdatePause = true;
				} else if (playbackState == Player.STATE_IDLE) {
					mapUpdatePause = true;
				} else if (playbackState == Player.STATE_ENDED) {
					mapUpdatePause = true;
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_playback);

		playerView = findViewById(R.id.video_view);

		fullscreenButton = playerView.findViewById(R.id.exo_fullscreen_icon);

		lbllatitude = findViewById(R.id.latitude2);
		lbllongitude = findViewById(R.id.longitude2);
		lbldatetime = findViewById(R.id.date2);
		lblbearing = findViewById(R.id.bearing2);
		lblspeed = findViewById(R.id.speed2);


		fullscreenButton.setOnClickListener(this::toggleFullscreen);

		Bundle bundle = getIntent().getExtras();
		if (bundle != null && bundle.containsKey("geovideo_path")) {
			videoPath = bundle.getString("geovideo_path");
		} else {
			Toast.makeText(PlaybackActivity.this, "Video path not provided", Toast.LENGTH_SHORT).show();
			finish();
		}
		backgroundExecutor = Executors.newSingleThreadExecutor();
		mapUpdateService = Executors.newSingleThreadScheduledExecutor();

		readMetaData();
	}

	private void readMetaData() {
		backgroundExecutor.submit(() -> {
			geoTags = VideoUtil.readMetaData(videoPath);

			new Handler(Looper.getMainLooper()).post(() -> {
				if (!geoTags.isEmpty()) {
					Date date = new Date(geoTags.get(0).timestamp);
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					setTitle(format.format(date));
				}
				setUpMap();
			});
		});
	}

	private void setUpMap() {
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		mMap = googleMap;
		mMap.clear();

		TileProviderUtil.setUpTileProvider(PlaybackActivity.this, mMap);


		if (!geoTags.isEmpty()) {
			mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(geoTags.get(0).getLatLng(), 15f));
		}
		if (player == null)
			initializePlayer();
	}

	private void initializePlayer() {
		player = ExoPlayerFactory.newSimpleInstance(this);
		player.addListener(exoPlaybackStateListener);
		playerView.setPlayer(player);
		Uri uri = Uri.fromFile(new File(videoPath));
		MediaSource mediaSource = buildMediaSource(uri);
		player.setPlayWhenReady(exoPlayWhenReady);
		player.seekTo(exoCurrentWindow, exoPlaybackPosition);
		player.prepare(mediaSource, false, false);
		player.addVideoListener(new VideoListener() {
			@Override
			public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
				exoPortrait = width < height;
			}
		});
		startUpdatingMap();
	}

	public static String TimestampConverter(Long timestamp) {
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss", Locale.US);
		Date resultdate = new Date(timestamp);
		return sdf.format(resultdate);
	}

	private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
		Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
		vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
		Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		vectorDrawable.draw(canvas);
		return BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(bitmap, 100, 100, false));
	}

	/**
	 * start asynctask to update map
	 */
	private void startUpdatingMap() {

		if (!geoTags.isEmpty()) {
			mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(geoTags.get(0).getLatLng(), 17f));
			currentLocationMarker = mMap.addMarker(
					new MarkerOptions().icon(bitmapDescriptorFromVector(PlaybackActivity.this, R.drawable.navigation))
							.zIndex(3)
							.rotation(geoTags.get(0).bearing)
							.position(geoTags.get(0).getLatLng())
			);
			mMap.addCircle(new CircleOptions()
					.radius(10f)
					.fillColor(0xff00aa00)
					.center(geoTags.get(0).getLatLng())
			);
		}

		mMap.addPolyline(new PolylineOptions().visible(true)
				.jointType(JointType.ROUND)
				.width(20)
				.color(0xff96bfff)
				.zIndex(1)
				.addAll(GeoTagUtil.getLatLngFromList(geoTags))
		);


		polyline = mMap.addPolyline(
				new PolylineOptions().visible(true)
						.width(20)
						.zIndex(2)
						.color(0xff0064ff)
		);


		mapUpdateIndex = 0;
		Runnable mapUpdateRunnable = () -> {

			Log.d(TAG, "startUpdatingMap: mapUpdatePause " + mapUpdatePause);

			if (mapUpdatePause) {
				return;
			}

			new Handler(Looper.getMainLooper()).post(() -> {
				long position = player.getCurrentPosition();
				Log.d(TAG, String.format("startUpdatingMap: %d / %d", mapUpdateIndex, geoTags.size()));
				Log.d(TAG, String.format("current postition: %d", (int) position));
				Log.d(TAG, "currentIndex's time " + geoTags.get(mapUpdateIndex).videoTime);
				for (int i = mapUpdateIndex + 1; i < geoTags.size(); i++) {
					if (position > geoTags.get(i).videoTime) {
					} else if (position <= geoTags.get(i).videoTime && position >= geoTags.get(i - 1).videoTime) {
						mapUpdateIndex = i;
						updateMapLocation(geoTags.get(i));
						polyline.setPoints(GeoTagUtil.getLatLngFromList(geoTags.subList(0, i + 1)));
						break;
					} else {
						while (i > 0 && geoTags.get(i - 1).videoTime > position) {
							i--;
						}
						mapUpdateIndex = i;
						updateMapLocation(geoTags.get(i));
						polyline.setPoints(GeoTagUtil.getLatLngFromList(geoTags.subList(0, i + 1)));
						break;
					}
				}
				if (mapUpdateIndex + 1 >= geoTags.size()) {
					mapUpdateIndex = 0;
				}

				lblspeed.setText(String.format("%d km/hr", new Float(geoTags.get(mapUpdateIndex).speed).intValue()));
				lbllatitude.setText(String.format("Lat: %f", geoTags.get(mapUpdateIndex).latitude));
				lbllongitude.setText(String.format("Long: %f", geoTags.get(mapUpdateIndex).longitude));
				lbldatetime.setText(String.format("%s", TimestampConverter(geoTags.get(mapUpdateIndex).timestamp)));
				lblbearing.setText(String.format("Bearing: %d degrees w.r.t. North", geoTags.get(mapUpdateIndex).bearing));

			});
		};

		if (mapUpdateFuture != null) {
			if (!mapUpdateFuture.isCancelled()) {
				mapUpdateFuture.cancel(false);
			}
		}
		mapUpdateFuture = mapUpdateService.scheduleAtFixedRate(
				mapUpdateRunnable,
				0,
				1,
				TimeUnit.SECONDS
		);
	}

	private void updateMapLocation(GeoTag geoTag) {
		Date date = new Date(geoTag.timestamp);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		setTitle(format.format(date));

//		mMap.addMarker(new MarkerOptions().position(geoTags.get(0).getLatLng()).title("A"));
//		mMap.addMarker(new MarkerOptions().position(geoTags.get(geoTags.size() - 1).getLatLng()).title("B"));
//		mMap.addMarker(new MarkerOptions().position(new LatLng(geoTag.latitude, geoTag.longitude)));
//		mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(geoTag.latitude, geoTag.longitude), 15f));
		currentLocationMarker.setPosition(geoTag.getLatLng());
		currentLocationMarker.setRotation(geoTag.bearing);

	}

	private MediaSource buildMediaSource(Uri uri) {
		DataSource.Factory dataSourceFactory =
				new DefaultDataSourceFactory(this, "codeplay-videogeoplay");
		return new ProgressiveMediaSource.Factory(dataSourceFactory)
				.createMediaSource(uri);
	}

	@Override
	public void onStart() {
		super.onStart();
		if (Util.SDK_INT >= 24) {
			if (mMap != null)
				initializePlayer();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (Util.SDK_INT < 24) {
			releasePlayer();
		}
		if (mapUpdateFuture != null) {
			mapUpdatePause = true;
			mapUpdateFuture.cancel(false);
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		if (Util.SDK_INT >= 24) {
			releasePlayer();
		}
		if (mapUpdateFuture != null) {
			mapUpdatePause = true;
			mapUpdateFuture.cancel(false);
		}
	}


	private void releasePlayer() {
		if (player != null) {
			exoPlayWhenReady = player.getPlayWhenReady();
			exoPlaybackPosition = player.getCurrentPosition();
			exoCurrentWindow = player.getCurrentWindowIndex();
			player.release();
			player = null;
		}
	}


	private void toggleFullscreen(View view) {
		if (exoFullscreen) {

			if (mapUpdateFuture != null) {
				mapUpdatePause = true;
			}

			fullscreenButton.setImageDrawable(ContextCompat.getDrawable(PlaybackActivity.this, R.drawable.fullscreen));
			getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
			if (getSupportActionBar() != null) {
				getSupportActionBar().show();
			}

			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) playerView.getLayoutParams();
			params.width = ViewGroup.LayoutParams.MATCH_PARENT;
			params.height = (int) (200 * getApplicationContext().getResources().getDisplayMetrics().density);
			playerView.setLayoutParams(params);
			exoFullscreen = false;
		} else {
			mapUpdatePause = true;
			fullscreenButton.setImageDrawable(ContextCompat.getDrawable(PlaybackActivity.this, R.drawable.fullscreen_exit));
			getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
					| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
					| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
			if (getSupportActionBar() != null) {
				getSupportActionBar().hide();
			}

			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) playerView.getLayoutParams();
			params.width = ViewGroup.LayoutParams.MATCH_PARENT;
			params.height = ViewGroup.LayoutParams.MATCH_PARENT;
			playerView.setLayoutParams(params);
			if (!exoPortrait)
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			exoFullscreen = true;
		}
	}

}