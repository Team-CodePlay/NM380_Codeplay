package com.codeplay.geoplay.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codeplay.geoplay.R;
import com.codeplay.geoplay.model.GeoVideo;

import java.util.List;

public class GeoVideoAdapter extends RecyclerView.Adapter<GeoVideoAdapter.GeoVideoViewHolder> {

	private Context context;
	private List<GeoVideo> dataset;
	private OnClickListener onClickListener;
	private OnDeleteListener onDeleteListener;
	private OnUploadListener onUploadListener;


	public GeoVideoAdapter(Context context, List<GeoVideo> dataset) {
		this.context = context;
		this.dataset = dataset;
	}

	private static final int detailedVideoCard = 1;
	private static final int largeThumbnailVideoCard = 0;
	public boolean toggleCard = false;

	@Override
	public int getItemViewType(int position) {
		return toggleCard? largeThumbnailVideoCard: detailedVideoCard;
	}

	@NonNull
	@Override
	public GeoVideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		if (viewType == 1) {
			return new GeoVideoViewHolder(LayoutInflater.from(parent.getContext())
					.inflate(R.layout.row_video_detailed, parent, false));
		}
		return new GeoVideoViewHolder(LayoutInflater.from(parent.getContext())
				.inflate(R.layout.row_video_large_thumbnail, parent, false));
	}

	@Override
	public void onBindViewHolder(@NonNull GeoVideoViewHolder holder, int position) {

	}

	@Override
	public int getItemCount() {
		return dataset.size();
	}

	public static class GeoVideoViewHolder extends RecyclerView.ViewHolder{
		public TextView vidTitle;
		public TextView date;
		public TextView duration;
		public TextView size;
		public TextView location;
		public ImageView snapshot;
		public TextView optionsIconText;
		public ImageView uploaded;

		public GeoVideoViewHolder(@NonNull View itemView) {
			super(itemView);
			vidTitle = itemView.findViewById(R.id.videoTitle);
			snapshot = itemView.findViewById(R.id.snapshot);
			optionsIconText = itemView.findViewById(R.id.options);
			date = itemView.findViewById(R.id.date);
			duration = itemView.findViewById(R.id.duration);
			size = itemView.findViewById(R.id.size);
			location = itemView.findViewById(R.id.location);
			uploaded = itemView.findViewById(R.id.upload_icon);
		}
	}

	public void setOnClickListener(OnClickListener onClickListener) {
		this.onClickListener = onClickListener;
	}

	public void setOnDeleteListener(OnDeleteListener onDeleteListener) {
		this.onDeleteListener = onDeleteListener;
	}

	public void setOnUploadListener(OnUploadListener onUploadListener) {
		this.onUploadListener = onUploadListener;
	}

	public interface OnDeleteListener{
		void onDelete(GeoVideo geoVideo);
	}
	public interface OnUploadListener{
		void onDelete(GeoVideo geoVideo);
	}
	public interface OnClickListener{
		void onDelete(GeoVideo geoVideo);
	}
}
