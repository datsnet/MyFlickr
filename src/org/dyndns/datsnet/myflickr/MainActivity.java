package org.dyndns.datsnet.myflickr;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends BaseActivity {
	private Context mContext;
	private ImageView mImageView;
	private final String LOG_TAG = getClass().getSimpleName();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		this.mContext = this;

		Button getPictureFromGallery = (Button) findViewById(R.id.get_from_gallery);
		getPictureFromGallery.setOnClickListener(mOnClickGalleryButtonListener);

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
//			intent.setAction(Intent.ACTION_SEND_MULTIPLE);
			startActivityForResult(intent, REQUEST_GALLERY);

		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_GALLERY && resultCode == RESULT_OK) {
			try {
				Intent intent = new Intent();
//				intent.setClass(mContext, FlickrActivity.class);
				intent.setClass(mContext, PassImageActivity.class);
				data.putExtra(URI_LIST_KEY, data.getDataString());
				intent.putExtras(data);
				startActivity(intent);
				finish();
				return;
			} catch (Exception e) {

			}
		}
	}
}
