package org.dyndns.datsnet.myflickr.task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.dyndns.datsnet.myflickr.BaseActivity;
import org.dyndns.datsnet.myflickr.FlickrActivity;
import org.dyndns.datsnet.myflickr.helper.FlickrHelper;
import org.xml.sax.SAXException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.googlecode.flickrjandroid.FlickrException;
import com.googlecode.flickrjandroid.RequestContext;
import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.uploader.UploadMetaData;
import com.googlecode.flickrjandroid.uploader.Uploader;

public class ImageUploadTask extends AsyncTask<Void, Integer, ArrayList<String>> {

	private Uploader uploader;;
	private Context context;
	private ProgressDialog mProgressDialog;
	private OAuth oauth;
	private FlickrActivity activity;
	private ArrayList<File> files;

	public ImageUploadTask(ArrayList<File> files, OAuth oauth, Context context) {
		this.files = files;
		this.oauth = oauth;
		this.uploader = FlickrHelper.getInstance().getUploader();
		this.context = context;
		this.activity = (FlickrActivity) context;
	}
    @Override
    protected void onProgressUpdate(Integer... progress) {
//    	mProgressDialog.setProgress(progress[0]);
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
		mProgressDialog.setMessage("Now updating...");
		mProgressDialog.setCanceledOnTouchOutside(true);
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
			this.activity.uploadDone(this.oauth, results);

		}

	}

	@Override
	protected ArrayList<String> doInBackground(Void... params) {
		RequestContext.getRequestContext().setOAuth(oauth);
		int count = 0;
		ArrayList<String> results = new ArrayList<String>();
		UploadMetaData uploadMetaData = setMetaUploadData();

//		int fileSize = 0;
//		for (int i = 0, listSize = this.files.size(); i < listSize; i++) {
//			fileSize += this.files.get(i).length();
//		}

		for (File targetFile : this.files) {
			String fileName = targetFile.getName();
			InputStream in = null;
			try {
				in = new FileInputStream(targetFile);

				if (in != null) {

					try {
						String result = uploader.upload(fileName, in, uploadMetaData);
						results.add(result);

					} catch (IOException e) {
						// TODO 自動生成された catch ブロック
						e.printStackTrace();
					} catch (FlickrException e) {
						// TODO 自動生成された catch ブロック
						e.printStackTrace();
					} catch (SAXException e) {
						// TODO 自動生成された catch ブロック
						e.printStackTrace();
					}
				}
				in.close();

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}

			// プログレスダイアログ更新
			int progress = 100 / (this.files.size() + count);
			Log.i("progress", String.valueOf(progress));
			publishProgress(progress);


			count++;
		}

		publishProgress(100);
		return results;
	}

	/**
	 * @return
	 */
	private UploadMetaData setMetaUploadData() {
		SharedPreferences sp = this.context.getSharedPreferences(BaseActivity.PREFS_NAME, Context.MODE_PRIVATE);
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
