/**
 *
 */
package org.dyndns.datsnet.myflickr.task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.dyndns.datsnet.myflickr.BaseActivity;
import org.dyndns.datsnet.myflickr.FlickrActivity;
import org.dyndns.datsnet.myflickr.dialog.FinishDialog;
import org.dyndns.datsnet.myflickr.helper.FlickrHelper;
import org.dyndns.datsnet.myflickr.utils.StringUtils;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.os.AsyncTask;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.FlickrException;
import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthToken;
import com.googlecode.flickrjandroid.photos.Photo;
import com.googlecode.flickrjandroid.photos.PhotosInterface;

/**
 * @author atsumi
 *
 */
public class PhotoAccessTask extends AsyncTask<String, Integer, ArrayList<String>> {
	private static final Logger logger = LoggerFactory.getLogger(PhotoAccessTask.class);

	private FlickrActivity activity;
	private Context context;
	private OAuth oauth;

	public PhotoAccessTask(FlickrActivity context, OAuth oauth) {
		this.activity = context;
		this.context = context;
		this.oauth = oauth;
	}

	@Override
	protected ArrayList<String> doInBackground(String... photoIds) {

		OAuthToken token = oauth.getToken();
		Flickr flickr = FlickrHelper.getInstance().getFlickrAuthed(token.getOauthToken(),
                token.getOauthTokenSecret());
		String photoId = null;
		ArrayList<String> urlList = new ArrayList<String>();
		PhotosInterface photoInterface = flickr.getPhotosInterface();
		for (int i = 0, photoSize = photoIds.length; i < photoSize;  i++) {
			photoId = photoIds[i];
			Photo photo = null;
			try {
				photo = photoInterface.getPhoto(photoId);
				urlList.add(photo.getUrl());
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			} catch (FlickrException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
		if (!urlList.isEmpty()) {
			return urlList;
		} else {
			return null;
		}

	}

	@Override
	protected void onPostExecute(ArrayList<String> target) {
		if (activity != null) {
			HashMap<String, String> urlList = new HashMap<String, String>();

			// メール
			urlList.put(BaseActivity.KEY_DIALOG_SEND_MAIL, StringUtils.sendForm(target));
			// ブラウザ
			urlList.put(BaseActivity.KEY_DIALOG_SHOW_BROWSER, StringUtils.sendForm(target));
			// クリップボード
			urlList.put(BaseActivity.KEY_DIALOG_COPY_CLIPBOARD, StringUtils.sendForm(target));
			if (target.size() > 1) {
				// ブラウザで閲覧用
				// 複数アップロードの場合遷移先が変わるため
				urlList.put(BaseActivity.KEY_DIALOG_SHOW_BROWSER, StringUtils.photosUrlForm(target.get(0)));
			}

			new FinishDialog(this.context, urlList).show();
		}
	}

}
