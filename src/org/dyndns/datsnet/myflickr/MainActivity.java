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

		mImageView = (ImageView) findViewById(R.id.filickrImage);

		Uri imageUri = null;

		byte byteArray[] = null;

		Log.v(LOG_TAG, "�N��");
//		try {
//			imageUri = Uri.parse(getIntent().getExtras()
//					.get("android.intent.extra.STREAM").toString());
//			byteArray = getIntent().getExtras().getByteArray(
//					"android.intent.extra.STREAM");
//		}
//
//		catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		if (imageUri != null) {
//			Log.v(LOG_TAG, "�ÖٓIintent����N��");
//			Bitmap bmp = null;
//
//			try {
//				bmp = Media.getBitmap(getContentResolver(), imageUri);
//			}
//
//			catch (FileNotFoundException e) {
//				e.printStackTrace();
//			}
//
//			catch (IOException e) {
//				e.printStackTrace();
//			}
//
//			if (bmp != null) {
//				this.mImageView.setImageBitmap(bmp);
//			}
//
//		}
//
//		else {
//
//			Log.v(LOG_TAG, "���ʂɋN��");
//
//		}

		Intent intent = new Intent();
		intent.setClass(mContext, FlickrActivity.class);
		intent.putExtras(getIntent());
		startActivity(intent);
		finish();
		return;
//		Log.d("API", API_KEY);
//		Log.d("API", API_SECRET);
//
//		try {
//			initialOauth();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (FlickrException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}


		// �M�������[�Ăяo��
		// Intent intent = new Intent();
		// intent.setType("image/*");
		// intent.setAction(Intent.ACTION_GET_CONTENT);
		// startActivityForResult(intent, REQUEST_GALLERY);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		Log.i("MyFlickr", "�摜���H����");
		if (requestCode == REQUEST_GALLERY && resultCode == RESULT_OK) {
			try {
				InputStream in = getContentResolver().openInputStream(
						data.getData());
				Bitmap img = BitmapFactory.decodeStream(in);
				in.close();
				Log.i("MyFlickr", "�摜���H����");

				// �I�������摜��\��
				mImageView.setImageBitmap(img);
			} catch (Exception e) {

			}
		}
	}
}
