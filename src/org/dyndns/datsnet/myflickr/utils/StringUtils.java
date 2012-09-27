/**
 * BitmapUtils.java
 */
package org.dyndns.datsnet.myflickr.utils;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

public class StringUtils {

	public static String photosUrlForm(String url) {
		int position = url.lastIndexOf("/");
		String result = url.substring(0, position);
		return result;
	}

	public static String sendForm(ArrayList<String> targetList) {
		String result = "";
		for (int i = 0, size = targetList.size(); i < size; i++) {
			if (size == 0) {
				break;
			}
			result += (size == i) ? targetList.get(i) : targetList.get(i) + "\n";
		}

		return result;
	}

	/**
	 *  UriからPathへの変換処理
	 * @param uri
	 * @return String  
	 */
	public static String getPath(Context context, Uri uri) {
		ContentResolver contentResolver = context.getContentResolver();
		String[] columns = { MediaStore.Images.Media.DATA };
		Cursor cursor = contentResolver.query(uri, columns, null, null, null);
		cursor.moveToFirst();
		String path = cursor.getString(0);
		cursor.close();
		return path;
	}

}
