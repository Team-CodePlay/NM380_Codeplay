package com.codeplay.geoplay.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

	@NonNull
	@Override
	public GeoVideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_video_detailed, parent, false);
		return new GeoVideoViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull GeoVideoViewHolder holder, int position) {

	}

	@Override
	public int getItemCount() {
		return dataset.size();
	}

	public class GeoVideoViewHolder extends RecyclerView.ViewHolder{
		public GeoVideoViewHolder(@NonNull View itemView) {
			super(itemView);
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
