package org.dyndns.datsnet.myflickr.task;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import org.dyndns.datsnet.myflickr.BaseActivity;
import org.dyndns.datsnet.myflickr.FlickrActivity;
import org.dyndns.datsnet.myflickr.helper.FlickrHelper;
import org.dyndns.datsnet.myflickr.utils.BitmapUtils;
import org.xml.sax.SAXException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.os.AsyncTask;
import android.util.Log;

import com.googlecode.flickrjandroid.FlickrException;
import com.googlecode.flickrjandroid.RequestContext;
import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.uploader.UploadMetaData;
import com.googlecode.flickrjandroid.uploader.Uploader;

public class ImageUploadTask extends
		AsyncTask<Void, Integer, ArrayList<String>> {

	private Uploader uploader;;
	private Context context;
	private ProgressDialog mProgressDialog;
	private OAuth oauth;
	private FlickrActivity activity;
	private ArrayList<File> files;
	private boolean isFailed = false;

	public ImageUploadTask(ArrayList<File> files, OAuth oauth, Context context) {
		this.files = files;
		this.oauth = oauth;
		this.uploader = FlickrHelper.getInstance().getUploader();
		this.context = context;
		this.activity = (FlickrActivity) context;
	}

	@Override
	protected void onProgressUpdate(Integer... progress) {
		// mProgressDialog.setProgress(progress[0]);
		mProgressDialog.incrementProgressBy(progress[0]);
	}

	@Override
	protected void onPreExecute() {
		// this.dialog = new ProgressDialog(this.context);
		// dialog.setTitle("通信中");
		// dialog.setMessage("Now Loading...");
		// dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		// dialog.show();
		super.onPreExecute();
		//		mProgressDialog = ProgressDialog.show(this.context, "通信中", "Now Loading..."); //$NON-NLS-1$ //$NON-NLS-2$

		mProgressDialog = new ProgressDialog(this.context);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setIndeterminate(false);

		mProgressDialog.setTitle("通信中");
		mProgressDialog.setMessage("Now uploading...");
//		mProgressDialog.setCanceledOnTouchOutside(true);
		mProgressDialog.setCancelable(true);
		mProgressDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dlg) {
				ImageUploadTask.this.cancel(true);
			}
		});

		mProgressDialog.show();

	}

	@Override
	protected void onPostExecute(ArrayList<String> results) {

		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
		}
		if (this.isCancelled()) {
			results = null;
			return;
		}

		if (results != null) {
			if (results.size() == 0) {
				// １つもアップロードできなかった場合
				this.activity.uploadFailed();
				return;
			} else if(isFailed) {
				// エラーがあった場合
				this.activity.finishActivityWidthDialog(results.get(0));
				return;
			}
			this.activity.uploadDone(this.oauth, results);
		}

	}

	@Override
	protected ArrayList<String> doInBackground(Void... params) {
		RequestContext.getRequestContext().setOAuth(oauth);
		int count = 0;
		ArrayList<String> results = new ArrayList<String>();
		UploadMetaData uploadMetaData = setMetaUploadData();

		// int fileSize = 0;
		// for (int i = 0, listSize = this.files.size(); i < listSize; i++) {
		// fileSize += this.files.get(i).length();
		// }

		try {
			for (File targetFile : this.files) {
				// リサイズ
				String dst = "tmp.jpg";
				OutputStream output = this.activity.openFileOutput(dst,
						Context.MODE_PRIVATE);
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				BitmapFactory.decodeFile(targetFile.getAbsolutePath(), options);
				options.inSampleSize = BitmapUtils.calculateInSampleSize(options, context);
				options.inJustDecodeBounds = false;
				Bitmap bm = BitmapFactory.decodeFile(targetFile.getAbsolutePath(), options);
				bm.compress(CompressFormat.JPEG, 100, output);
				bm.recycle();
				
				
				String fileName = targetFile.getName();
				InputStream in = null;
//				in = new FileInputStream(targetFile);
				in = this.activity.openFileInput(dst);

				if (in != null) {

					String result = uploader.upload(fileName, in,
							uploadMetaData);
					results.add(result);

				}
				in.close();

				// プログレスダイアログ更新
				int progress = 100 / (this.files.size() + count);
				Log.i("progress", String.valueOf(progress));
				publishProgress(progress);

				count++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (FlickrException e) {
			results.add(e.getErrorMessage());
			isFailed = true;
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		publishProgress(100);
		return results;
	}

	/**
	 * @return
	 */
	private UploadMetaData setMetaUploadData() {
		SharedPreferences sp = this.context.getSharedPreferences(
				BaseActivity.PREFS_NAME, Context.MODE_PRIVATE);
		int releaseValue = sp.getInt(BaseActivity.Release_FLAG_PREFERENCE, 0);
		UploadMetaData uploadMetaData = new UploadMetaData();

		// 公開設定
		switch (releaseValue) {
		case 0:
			// パブリック
			uploadMetaData.setPublicFlag(true);
			uploadMetaData.setFamilyFlag(true);
			uploadMetaData.setFriendFlag(true);
			break;
		case 1:
			// 非公開
			uploadMetaData.setPublicFlag(false);
			uploadMetaData.setFamilyFlag(false);
			uploadMetaData.setFriendFlag(false);
			break;
		case 2:
			// 家族
			uploadMetaData.setPublicFlag(false);
			uploadMetaData.setFamilyFlag(true);
			uploadMetaData.setFriendFlag(false);
			break;
		case 3:
			// 友達
			uploadMetaData.setPublicFlag(false);
			uploadMetaData.setFamilyFlag(false);
			uploadMetaData.setFriendFlag(true);
			break;
		case 4:
			uploadMetaData.setPublicFlag(false);
			uploadMetaData.setFamilyFlag(true);
			uploadMetaData.setFriendFlag(true);
			break;

		default:
			break;
		}

		return uploadMetaData;
	}

}
