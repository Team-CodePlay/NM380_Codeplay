package com.codeplay.geoplay.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codeplay.geoplay.R;
import com.codeplay.geoplay.model.GeoVideo;

import java.io.File;
import java.text.CharacterIterator;
import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class GeoVideoAdapter extends RecyclerView.Adapter<GeoVideoAdapter.GeoVideoViewHolder> implements Filterable {

	private Context context;
	private List<GeoVideo> dataset;
	private List<GeoVideo> datasetFull;
	private OnClickListener onClickListener;
	private OnDeleteListener onDeleteListener;
	private OnUploadListener onUploadListener;


	private static final int detailedVideoCard = 1;
	private static final int largeThumbnailVideoCard = 0;
	public int viewType;

	public GeoVideoAdapter(Context context, List<GeoVideo> dataset, int viewType) {
		this.context = context;
		this.dataset = dataset;
		datasetFull = new ArrayList<>(dataset);
		this.viewType = viewType;
	}

	@Override
	public int getItemViewType(int position) {
		return viewType;
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

	public static String ByteConverter(long bytes) {
		if (-1000 < bytes && bytes < 1000) {
			return bytes + " B";
		}
		CharacterIterator ci = new StringCharacterIterator("KMGTPE");
		while (bytes <= -999_950 || bytes >= 999_950) {
			bytes /= 1000;
			ci.next();
		}
		return String.format("%.1f %cB", bytes / 1000.0, ci.current());
	}


	// TODO: 03-08-2020 maybe something that gives output like 10 secs, 25mins, 1hour 2mins 
	public static String SecondsConverter(long duration) {
		return String.format("%02d:%02d:%02d",
				TimeUnit.SECONDS.toHours(duration),
				TimeUnit.SECONDS.toMinutes(duration) - TimeUnit.HOURS.toMinutes(TimeUnit.SECONDS.toHours(duration)),
				duration - TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(duration) - TimeUnit.HOURS.toMinutes(TimeUnit.SECONDS.toHours(duration))));
	}

	public static String TimestampConverter(Long timestamp) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, yyyy", Locale.US);
		Date resultdate = new Date(timestamp);
		return sdf.format(resultdate);
	}

	@Override
	public void onBindViewHolder(@NonNull GeoVideoViewHolder holder, int position) {
		holder.vidTitle.setText(new File(dataset.get(position).videoPath).getName());
		if (holder.getItemViewType() == detailedVideoCard) {
			holder.date.setText(new File(TimestampConverter(dataset.get(position).videoStartTime)).getName());
			holder.size.setText(new File(ByteConverter(dataset.get(position).size)).getName());
			holder.duration.setText(new File(SecondsConverter(dataset.get(position).duration)).getName());
			if(dataset.get(position).startLocation != null) {
				holder.location.setText(new File((dataset.get(position).startLocation.latitude.toString() + " , " + dataset.get(position).startLocation.longitude.toString())).getName());
			}
		}
		if (dataset.get(position).isUploaded){
			holder.uploaded.setVisibility(View.VISIBLE);
		}
		Glide.with(holder.itemView.getContext())
				.load(Uri.fromFile(new File(dataset.get(position).videoPath)))
				.centerCrop()
				.into(holder.snapshot);

		holder.itemView.setOnClickListener(v -> onClickListener.onClick(dataset.get(position)));
		PopupMenu popup = new PopupMenu(context, holder.optionsIconText);
		popup.inflate(R.menu.video_context);
		popup.setOnMenuItemClickListener(item -> {
			switch (item.getItemId()) {
				case R.id.delete:
					onDeleteListener.onDelete(dataset.get(position));
					return true;
				case R.id.upload:
					onUploadListener.onUpload(dataset.get(position));
					return true;
				default:
					return false;
			}
		});
		//displaying the popup
		holder.itemView.setOnLongClickListener(v -> {
			popup.show();
			return true;
		});
		holder.optionsIconText.setOnClickListener(v -> popup.show());
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
		void onUpload(GeoVideo geoVideo);
	}
	public interface OnClickListener{
		void onClick(GeoVideo geoVideo);
	}

	@Override
	public Filter getFilter() {
		return datasetFilter;
	}

	private Filter datasetFilter = new Filter() {
		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			List<GeoVideo> filteredList = new ArrayList<>();

			if (constraint == null || constraint.length() == 0) {
				filteredList.addAll(datasetFull);
			} else {
				String filterPattern = constraint.toString().toLowerCase().trim();

				for (GeoVideo geoVideo : datasetFull) {
					if ((new File(geoVideo.videoPath).getName()).toLowerCase().contains(filterPattern) ||
							TimestampConverter(geoVideo.videoStartTime).toLowerCase().contains(filterPattern)) {
						filteredList.add(geoVideo);
					}
				}
			}
			FilterResults results = new FilterResults();
			results.values = filteredList;
			return  results;
		}

		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			dataset.clear();
			dataset.addAll((List) results.values);
			notifyDataSetChanged();
		}
	};
}
