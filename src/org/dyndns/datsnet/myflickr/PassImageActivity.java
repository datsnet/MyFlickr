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

	@SuppressWarnings("unchecked")
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
		} else if (data.getExtras().get(Intent.EXTRA_STREAM) != null) {
			// ギャラリーから単数選択した場合
			if (data.getExtras().get(Intent.EXTRA_STREAM) != null) {
				imageUriList.add(Uri.parse(data.getExtras().get(Intent.EXTRA_STREAM).toString()));
			} else {
				imageUriList.add(Uri.parse((String) data.getExtras().get(URI_LIST_KEY)));
			}
		} else if (data.getExtras().get(INTENT_SELECT_IMAGE) != null) {
			// 画像選択から選択した場合
			imageUriList = (ArrayList<Uri>) data.getExtras().get(INTENT_SELECT_IMAGE);
		}

		// intent.putExtras(getIntent());
		intent.putExtra(URI_LIST_KEY, imageUriList);
		startActivity(intent);
		finish();
		return;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO 自動生成されたメソッド・スタブ
		super.onActivityResult(requestCode, resultCode, data);

//		if (REQUEST_SELECT_IMAGE == requestCode) {
//			ArrayList<Uri> imageUriList = new ArrayList<Uri>();
//			imageUriList = (ArrayList<Uri>) data.getExtras().get(INTENT_SELECT_IMAGE);
//
//			Intent intent = new Intent();
//			intent.setClass(mContext, FlickrActivity.class);
//			intent.putExtra(URI_LIST_KEY, imageUriList);
//			startActivity(intent);
//			finish();
//			return;
//		}

	}

}
