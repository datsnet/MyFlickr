/**
 *
 */
package org.dyndns.datsnet.myflickr;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;

/**
 * @author atsumi
 *
 */
public class PassImageActivity extends BaseActivity {
	private Context mContext;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.mContext = this;

		Intent intent = new Intent();
		intent.setClass(mContext, FlickrActivity.class);
		Intent data = getIntent();

//		String requiredUri = (data.getExtras().get("android.intent.extra.STREAM")) != null ? data.getExtras().get("android.intent.extra.STREAM").toString() : null;
		// String imageUri = (requiredUri) != null ? requiredUri :
		// data.getStringExtra("uri");
		ArrayList<Uri> imageUriList = new ArrayList<Uri>();

		// ギャラリーから直接複数選択した場合
		if (data.getExtras().get(Intent.EXTRA_STREAM) != null && data.getParcelableArrayListExtra(Intent.EXTRA_STREAM) != null) {
			ArrayList<Parcelable> dataList = data.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
			for (Parcelable parcel : dataList) {
				imageUriList.add((Uri) parcel);
			}
		} else {
			// ギャラリーから単数選択した場合
			if (data.getExtras().get(Intent.EXTRA_STREAM) != null) {
				imageUriList.add(Uri.parse(data.getExtras().get(Intent.EXTRA_STREAM).toString()));
			} else {
				imageUriList.add(Uri.parse((String) data.getExtras().get(URI_LIST_KEY)));
			}
		}

		// intent.putExtras(getIntent());
		intent.putExtra(URI_LIST_KEY, imageUriList);
		startActivity(intent);
		finish();
		return;

	}
}
