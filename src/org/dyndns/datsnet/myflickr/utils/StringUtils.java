/**
 * BitmapUtils.java
 */
package org.dyndns.datsnet.myflickr.utils;

import java.util.ArrayList;

public class StringUtils {


	public static String photosUrlForm(String url) {
		int position = url.lastIndexOf("/");
		String result = url.substring(0, position);
		return result;
	}

	public static String sendForm(ArrayList<String> targetList) {
		String result = "";
		for (int i = 0, size = targetList.size(); i < size; i++) {
			result += (size == i) ? targetList.get(i) : targetList.get(i) + "\n";
		}

		return result;
	}

}
