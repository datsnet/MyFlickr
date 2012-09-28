package org.dyndns.datsnet.myflickr.adapter;

import org.dyndns.datsnet.myflickr.cache.FileCache;
import org.dyndns.datsnet.myflickr.cache.ImageCache;
import org.dyndns.datsnet.myflickr.data.SelectImageBindData;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

public class GridViewImageLoader implements Runnable {

	private ImageView imageView;
	private ImageView thumbImage;
	private Bitmap image;
	private FileCache fileCache;
	private SelectImageBindData mData;
	private String tag;
	private Context mContext;
	private Bitmap bm;

	public GridViewImageLoader(Context context, ImageView image, ImageView thumbImage, SelectImageBindData data) {
		this.imageView = image;
		this.thumbImage = thumbImage;
		this.fileCache = new FileCache(context);
		this.mData = data;
		this.tag = this.imageView.getTag().toString();
		this.mContext = context;

		this.imageView.setImageBitmap(null);

		this.imageView.invalidate();
		if (this.thumbImage != null) {
			this.thumbImage.setImageBitmap(null);
			this.thumbImage.invalidate();
		}
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
		bm = null;
		String imageTag = GridViewImageLoader.this.imageView.getTag().toString();

		if (imageTag.equals(GridViewImageLoader.this.tag)) {
//			bm = ImageCache.getImage(imageTag);
			if (bm == null) {
				try {
					bm = MediaStore.Images.Thumbnails.getThumbnail(mContext.getApplicationContext().getContentResolver(), GridViewImageLoader.this.mData.getId(), MediaStore.Images.Thumbnails.MICRO_KIND, null);
//					ImageCache.setImage(this.tag, bm);
					Thread.sleep(200);
					Log.i(GridViewImageLoader.class.getSimpleName(), "テスト");
					this.imageView.post(new Runnable() {
						@Override
						public void run() {
							if (GridViewImageLoader.this.imageView.getTag().toString().equals(GridViewImageLoader.this.tag)) {
								// Bitmap bm =
								// MediaStore.Images.Thumbnails.getThumbnail(mContext.getApplicationContext().getContentResolver(),
								// GridViewImageLoader.this.id,
								// MediaStore.Images.Thumbnails.MICRO_KIND, null);

								GridViewImageLoader.this.imageView.setImageBitmap(bm);
//								if (GridViewImageLoader.this.mData.isSelected()) {
//									Log.i(GridViewImageLoader.class.getSimpleName(), "f:" + mData.getUri().getPath());
//
//									if (thumbImage != null) {
//										Log.i(GridViewImageLoader.class.getSimpleName(), "g:" + mData.getUri().getPath());
////										thumbImage.setImageResource(R.drawable.selected_badge);
//										thumbImage.setImageResource(android.R.drawable.presence_online);
//									}
//								}

							}
						}
					});
				} catch (OutOfMemoryError e) {
					bm = null;
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	}

}
