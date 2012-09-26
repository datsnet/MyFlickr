package org.dyndns.datsnet.myflickr.adapter;

import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.dyndns.datsnet.myflickr.R;
import org.dyndns.datsnet.myflickr.cache.FileCache;
import org.dyndns.datsnet.myflickr.data.SelectImageBindData;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

public class SelectImageAdapter extends ArrayAdapter<SelectImageBindData> {

	private static final String LOG_TAG = SelectImageAdapter.class.getSimpleName();

	private Context mContext;

	private LayoutInflater inflater;

	private int mLayoutId;

	private int mPoolSize = 2;

	private int mMaxPoolSize = 5;

	private long mKeepAliveTime = 3;

	private ThreadPoolExecutor mThreadPool = null;

	private LiloBlockingDeque<Runnable> mQueue = null;

	// public SelectImageAdapter(Context context, int layoutId, List<SelectImageBindData>
	// objects) {
	// super(context, 0, objects);
	//
	// this.mContext = context;
	// this.inflater = (LayoutInflater)
	// context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	// this.mLayoutId = layoutId;
	//
	// mQueue = new LiloBlockingDeque<Runnable>();
	// mThreadPool = new ThreadPoolExecutor(mPoolSize, mMaxPoolSize,
	// mKeepAliveTime, TimeUnit.SECONDS, mQueue);
	// }

	public SelectImageAdapter(Context context, int layoutId, List<SelectImageBindData> data) {
		super(context, 0, data);

		this.mContext = context;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mLayoutId = layoutId;

		mQueue = new LiloBlockingDeque<Runnable>();
		mThreadPool = new ThreadPoolExecutor(mPoolSize, mMaxPoolSize, mKeepAliveTime, TimeUnit.SECONDS, mQueue);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(mLayoutId, parent, false);
			holder = new ViewHolder();
			holder.imageView = (ImageView) convertView.findViewById(R.id.thumbImage);
//			holder.imageView.setOnClickListener(new OnClickListener() {
//
//				public void onClick(View view) {
//					// TODO Auto-generated method stub
////					int id = v.getId();
////					Intent intent = new Intent();
////					intent.setAction(Intent.ACTION_VIEW);
////					intent.setDataAndType(Uri.parse("file://" + arrPath[id]), "image/*");
////					startActivity(intent);
//					SelectImageBindData data = getItem(position);
//					if (!data.isSelected) {
//						holder.thumbImageView.setImageResource(android.R.drawable.presence_online);
//					} else {
//						holder.thumbImageView.setImageBitmap(null);
//					}
//
//				}
//			});
//			holder.imageView.setOnLongClickListener(new OnLongClickListener() {
//
//				@Override
//				public boolean onLongClick(View view) {
//					int id = view.getId();
//					Intent intent = new Intent();
//					intent.setAction(Intent.ACTION_VIEW);
////					intent.setDataAndType(Uri.parse("file://" + arrPath[id]), "image/*");
//					mContext.startActivity(intent);
//					return false;
//				}
//			});
			holder.thumbImageView = (ImageView) convertView.findViewById(R.id.select_thumb);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		SelectImageBindData data = getItem(position);
		// holder.textView.setText(data.title);
		holder.imageView.setImageBitmap(null);
		holder.imageView.setTag(position);
		holder.imageView.invalidate();
		holder.thumbImageView.setImageBitmap(null);
		holder.thumbImageView.invalidate();
		GridViewImageLoader imageLoader = new GridViewImageLoader(this.mContext, holder.imageView, holder.thumbImageView, data);
		// mQueue.add(imageLoader);
		this.mThreadPool.execute(imageLoader);
		Log.i(LOG_TAG, "Task count.." + this.mQueue.size());
		convertView.invalidate();

		return convertView;
	}

	public void clearImageQueue() {
		mQueue.clear();
	}

	public void startDisplayImage() {
	}

	static class ViewHolder {
		ImageView imageView;
		ImageView thumbImageView;
	}

	private class GridViewImageLoader implements Runnable {

		private ImageView imageView;
		private ImageView thumbImage;
		private String url;
		private Bitmap image;
		private FileCache fileCache;
		private int id;
		private String tag;
		private boolean isSelected;

		public GridViewImageLoader(Context context, ImageView image, ImageView thumbImage, SelectImageBindData data) {
			this.imageView = image;
			this.thumbImage = thumbImage;
			this.url = url;
			this.fileCache = new FileCache(context);
			this.id = data.getId();
			this.isSelected = data.isSelected();
			this.tag = this.imageView.getTag().toString();

			this.imageView.setImageBitmap(null);
			this.imageView.invalidate();
			this.thumbImage.setImageBitmap(null);
			this.thumbImage.invalidate();
		}

		@Override
		public void run() {
			// キャッシュから取り出す
			// this.imageView.setImageResource(R.drawable.ic_launcher);
			// if (this.imageView.getTag().equals(this.url.hashCode())) {
			// File file = this.fileCache.getFile(this.url);
			//
			// this.image = DownloadImageHttpClient.getBitmap(file, this.url);
			// // ImageCache.setImage(this.url, this.image);
			// this.imageView.post(new Runnable() {
			// @Override
			// public void run() {
			// if
			// (GridViewImageLoader.this.imageView.getTag().equals(GridViewImageLoader.this.url.hashCode()))
			// {
			// GridViewImageLoader.this.imageView.setImageBitmap(GridViewImageLoader.this.image);
			// }
			// }
			// });
			// }
			// this.image = DownloadImageHttpClient.getBitmap(file, this.url);
			// ImageCache.setImage(this.url, this.image);
			this.imageView.post(new Runnable() {
				@Override
				public void run() {
					if (GridViewImageLoader.this.imageView.getTag().toString().equals(GridViewImageLoader.this.tag)) {
						Bitmap bm = MediaStore.Images.Thumbnails.getThumbnail(mContext.getApplicationContext().getContentResolver(), GridViewImageLoader.this.id, MediaStore.Images.Thumbnails.MICRO_KIND, null);
						GridViewImageLoader.this.imageView.setImageBitmap(bm);

						if (GridViewImageLoader.this.isSelected) {
							thumbImage.setImageResource(android.R.drawable.presence_online);
						}

					}
				}
			});

		}
	}

	public class LiloBlockingDeque<E> extends LinkedBlockingDeque<E> {

		private static final long serialVersionUID = -4854985351588039351L;

		private static final int QUEUE_SIZE = 15;

		@Override
		public boolean offer(E e) {
			// override to put objects at the front of the list
			while (this.size() >= QUEUE_SIZE) {
				super.removeLast();
			}
			return super.offerFirst(e);
		}

		@Override
		public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
			// override to put objects at the front of the list
			while (this.size() >= QUEUE_SIZE) {
				super.removeLast();
			}
			return super.offerFirst(e, timeout, unit);
		}

		@Override
		public boolean add(E e) {
			// override to put objects at the front of the list
			while (this.size() >= QUEUE_SIZE) {
				super.removeLast();
			}
			return super.offerFirst(e);
		}

		@Override
		public void put(E e) throws InterruptedException {
			// override to put objects at the front of the list
			while (this.size() >= QUEUE_SIZE) {
				super.removeLast();
			}
			super.putFirst(e);
		}
	}

}