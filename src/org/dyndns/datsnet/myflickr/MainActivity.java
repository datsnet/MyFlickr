package org.dyndns.datsnet.myflickr;

import java.io.InputStream;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

public class MainActivity extends BaseActivity {
	private static final int REQUEST_GALLERY = 0;
	private Context mContext;
	private ImageView mImageView;
	private final String LOG_TAG = getClass().getSimpleName();


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		this.mContext = this;

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
//		getMenuInflater().inflate(R.menu.activity_main, menu);
////		return true;
//		return super.onCreateOptionsMenu(menu);
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
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (requestCode == REQUEST_GALLERY && resultCode == RESULT_OK) {
			try {
				Intent intent = new Intent();
				intent.setClass(mContext, FlickrActivity.class);
				intent.putExtras(getIntent());
				startActivity(intent);
				finish();
				return;
			} catch (Exception e) {

			}
		}
	}
}
