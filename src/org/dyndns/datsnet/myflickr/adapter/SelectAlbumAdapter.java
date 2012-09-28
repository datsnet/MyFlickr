package org.dyndns.datsnet.myflickr.adapter;

import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.dyndns.datsnet.myflickr.R;
import org.dyndns.datsnet.myflickr.data.SelectAlbumBindData;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SelectAlbumAdapter extends ArrayAdapter<SelectAlbumBindData> {

	private static final String LOG_TAG = SelectAlbumAdapter.class.getSimpleName();

	private Context mContext;

	private LayoutInflater inflater;

	private int mLayoutId;

	private int mPoolSize = 5;

//	private int mMaxPoolSize = 5;
	private int mMaxPoolSize = 128;

	private long mKeepAliveTime = 5;

	private ThreadPoolExecutor mThreadPool = null;

	private LiloBlockingDeque<Runnable> mQueue = null;

	public SelectAlbumAdapter(Context context, int layoutId, List<SelectAlbumBindData> data) {
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
			holder.thumbImageView = (ImageView) convertView.findViewById(R.id.thumbImage);
			holder.thumbTitle = (TextView) convertView.findViewById(R.id.thumbTitle);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		SelectAlbumBindData data = getItem(position);
		// holder.textView.setText(data.title);
		holder.thumbImageView.setImageBitmap(null);
		holder.thumbImageView.setTag(data.getAlbumPath().hashCode());
		holder.thumbImageView.invalidate();

		holder.thumbTitle.setText(data.getAlbumName());

		GridViewImageLoader imageLoader = new GridViewImageLoader(this.mContext, holder.thumbImageView, null, data);
//		mQueue.add(imageLoader);
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
		ImageView thumbImageView;
		TextView thumbTitle;
	}
	public class LiloBlockingDeque<E> extends LinkedBlockingDeque<E> {

		private static final long serialVersionUID = -4854985351588039351L;

		// private static final int QUEUE_SIZE = 15;
		private static final int QUEUE_SIZE = 100;

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
