package org.dyndns.datsnet.myflickr;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends BaseActivity {
	private Context mContext;
	private final String LOG_TAG = getClass().getSimpleName();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		this.mContext = this;

		Button getPictureFromGallery = (Button) findViewById(R.id.get_from_gallery);
		Button getPictureFromCustomGallery = (Button) findViewById(R.id.get_from_custom_gallery);
		Button settingBtn = (Button) findViewById(R.id.setting_button);
		getPictureFromGallery.setOnClickListener(mOnClickGalleryButtonListener);
		getPictureFromCustomGallery.setOnClickListener(mOnClickCusomGalleryButtonListener);
		settingBtn.setOnClickListener(mOnClickSettingButtonListener);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
		// getMenuInflater().inflate(R.menu.activity_main, menu);
		// // return true;
		// return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.menu_settings:
			startSettingMenu();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private OnClickListener mOnClickGalleryButtonListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// ギャラリーに遷移
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(intent, REQUEST_GALLERY);

		}
	};

	private OnClickListener mOnClickCusomGalleryButtonListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// ギャラリーに遷移
			Intent intent = new Intent(mContext, SelectImageGalleryActivity.class);
			startActivity(intent);

			startActivityForResult(intent, REQUEST_GALLERY);

		}
	};

	private OnClickListener mOnClickSettingButtonListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// 設定メニューへ
			startSettingMenu();

		}
	};
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_GALLERY && resultCode == RESULT_OK) {
			try {
				Intent intent = new Intent();
				// intent.setClass(mContext, FlickrActivity.class);
				intent.setClass(mContext, PassImageActivity.class);
				data.putExtra(URI_LIST_KEY, data.getDataString());
				intent.putExtras(data);
				startActivity(intent);
//				finish();
				return;
			} catch (Exception e) {

			}
		}
	}
}
