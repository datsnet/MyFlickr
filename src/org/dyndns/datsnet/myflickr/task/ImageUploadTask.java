package org.dyndns.datsnet.myflickr.task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.dyndns.datsnet.myflickr.helper.FlickrHelper;
import org.xml.sax.SAXException;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;

import com.googlecode.flickrjandroid.FlickrException;
import com.googlecode.flickrjandroid.RequestContext;
import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.uploader.UploadMetaData;
import com.googlecode.flickrjandroid.uploader.Uploader;

public class ImageUploadTask extends AsyncTask<String, Integer, String> {

	private Uploader uploader;;
	private String displayName;
	private Context context;
	private ProgressDialog mProgressDialog;
	private OAuth oauth;

	public ImageUploadTask(String displayName, OAuth oauth, Context context) {
		this.uploader = FlickrHelper.getInstance().getUploader();
		this.displayName = displayName;
		this.context = context;

		this.oauth = oauth;
	}

	@Override
	protected void onPreExecute() {
//		this.dialog = new ProgressDialog(this.context);
//		dialog.setTitle("通信中");
//		dialog.setMessage("Now Loading...");
//		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//		dialog.show();
		super.onPreExecute();
		mProgressDialog = ProgressDialog.show(this.context, "通信中", "Now Loading..."); //$NON-NLS-1$ //$NON-NLS-2$
		mProgressDialog.setCanceledOnTouchOutside(true);
		mProgressDialog.setCancelable(true);
		mProgressDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dlg) {
				ImageUploadTask.this.cancel(true);
			}
		});

	}

	@Override
	protected void onPostExecute(String result) {

//		if (this.dialog.isShowing()) {
//			this.dialog.dismiss();
//		}
		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
		}
		if (this.isCancelled()) {
			result = null;
			return;
		}

		if (result != null) {
			Builder completeDialog = new AlertDialog.Builder(this.context);
			completeDialog.setMessage("アップロードが完了しました");
			completeDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					
				}
			});
			completeDialog.show();

		}

	}

	@Override
	protected String doInBackground(String... params) {
		RequestContext.getRequestContext().setOAuth(oauth);
		File file = new File(params[0]);
		InputStream in = null;
		String result = null;
		try {
			in = new FileInputStream(file);

			if (in != null) {
				UploadMetaData uploadMetaData = new UploadMetaData();
				uploadMetaData.setAsync(true);
				uploadMetaData.setFamilyFlag(true);
				uploadMetaData.setPublicFlag(false);
				uploadMetaData.setFriendFlag(true);
				try {
					result = uploader.upload(displayName, in, uploadMetaData);

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

		return result;
	}



}
