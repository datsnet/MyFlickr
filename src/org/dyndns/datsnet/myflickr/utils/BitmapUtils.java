/**
 * BitmapUtils.java
 */
package org.dyndns.datsnet.myflickr.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.view.Display;
import android.view.WindowManager;

public class BitmapUtils {


	/**
	 * 画面サイズに合わせたinSampleSizeを返す
	 * @param options
	 * @param context
	 * @return inSampleSize
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options, Context context) {

		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display disp = wm.getDefaultDisplay();
		int reqWidth = disp.getWidth();
		int reqHeight = disp.getHeight();

		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}
		}
		return inSampleSize;
	}

	/**
	 * 画像ローテーション処理を行いビットマップを返す
	 * @param bm
	 * @param rotation
	 * @param isReflected 反転するかどうか
	 * @return bm
	 */
	public static Bitmap rotateBitmap(Bitmap bm, int orientation, boolean isReflected) {
		// 画像ローテーション処理

		Matrix matrix = new Matrix();
		if (isReflected) {
			// 反転処理をする
			matrix.setScale(-1.0f, -1.0f);
		}
		if (orientation != 0) {
			matrix.postRotate(orientation);
			bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
		}
		return bm;

	}

}
