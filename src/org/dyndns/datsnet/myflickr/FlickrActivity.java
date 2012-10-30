/**
 *
 */
package org.dyndns.datsnet.myflickr;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import org.dyndns.datsnet.myflickr.entity.ImageListEntity;
import org.dyndns.datsnet.myflickr.helper.FlickrHelper;
import org.dyndns.datsnet.myflickr.task.GetOAuthTokenTask;
import org.dyndns.datsnet.myflickr.task.ImageUploadTask;
import org.dyndns.datsnet.myflickr.task.OAuthTask;
import org.dyndns.datsnet.myflickr.task.PhotoAccessTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import com.googlecode.flickrjandroid.RequestContext;
import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthToken;
import com.googlecode.flickrjandroid.people.User;
import com.googlecode.flickrjandroid.uploader.Uploader;

/**
 * @author atsumi
 * 
 */
public class FlickrActivity extends BaseActivity {

	private Context mContext;
	private static final Logger logger = LoggerFactory
			.getLogger(FlickrActivity.class);
	// public static final String CALLBACK_SCHEME =
	// "org.dyndns.datsnet.myflickr";
	public static final String CALLBACK_SCHEME = "myflickr";
	public static final String KEY_OAUTH_TOKEN = "my-flickr-oauthToken"; //$NON-NLS-1$
	public static final String KEY_TOKEN_SECRET = "my-flickr-tokenSecret"; //$NON-NLS-1$
	public static final String KEY_USER_NAME = "my-flickr-userName"; //$NON-NLS-1$
	public static final String KEY_USER_ID = "my-flickr-userId"; //$NON-NLS-1$

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.mContext = this;
		setContentView(R.layout.activity_flickr);

		try {
			ImageListEntity imageListEntity = ImageListEntity.getInstance();
			if (ImageListEntity.getInstance().getmImageUriList() == null) {
				if (getIntent().getExtras().get(URI_LIST_KEY) instanceof ArrayList) {
					imageListEntity
							.setmImageUriList((ArrayList<Uri>) getIntent()
									.getExtras().get(URI_LIST_KEY));
				} else {
					throw new Exception();
				}
			}
		}

		catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this, "画像を取得できませんでした。", Toast.LENGTH_LONG).show();
			return;
		}

		OAuth oauth = getOAuthToken();
		// 認証情報が得られない場合OAuth認証ページへ
		Intent intent = getIntent();
		String scheme = intent.getScheme();
		if (scheme == null && oauth != null) {
			setUpload(mContext);
		} else if (scheme == null || oauth == null) {
			OAuthTask task = new OAuthTask(this);
			task.execute();
		}

	}

	@Override
	protected void onNewIntent(Intent intent) {
		// this is very important, otherwise you would get a null Scheme in the
		// onResume later on.
		setIntent(intent);
	}

	@Override
	public void onResume() {
		super.onResume();
		Intent intent = getIntent();
		String scheme = intent.getScheme();
		OAuth savedToken = getOAuthToken();
		if (CALLBACK_SCHEME.equals(scheme)
				&& (savedToken == null || savedToken.getUser() == null)) {
			Uri uri = intent.getData();
			String query = uri.getQuery();
			logger.debug("Returned Query: {}", query); //$NON-NLS-1$
			String[] data = query.split("&"); //$NON-NLS-1$
			if (data != null && data.length == 2) {
				String oauthToken = data[0].substring(data[0].indexOf("=") + 1); //$NON-NLS-1$
				String oauthVerifier = data[1]
						.substring(data[1].indexOf("=") + 1); //$NON-NLS-1$
				logger.debug(
						"OAuth Token: {}; OAuth Verifier: {}", oauthToken, oauthVerifier); //$NON-NLS-1$

				OAuth oauth = getOAuthToken();

				if (oauth != null && oauth.getToken() != null
						&& oauth.getToken().getOauthTokenSecret() != null) {

					GetOAuthTokenTask task = new GetOAuthTokenTask(this);
					task.execute(oauthToken, oauth.getToken()
							.getOauthTokenSecret(), oauthVerifier);

				}
			}

		}

	}

	/**
	 * アップロードを開始する
	 */
	public void setUpload(Context context) {
		Uploader uploader = FlickrHelper.getInstance().getUploader();
		if (uploader != null) {

			ContentResolver contentResolver = mContext.getContentResolver();
			String[] columns = { MediaStore.Images.Media.DATA,
					MediaStore.Images.Media.DISPLAY_NAME };

			ImageListEntity imageListEntity = ImageListEntity.getInstance();
			ArrayList<Uri> imageUriList = imageListEntity.getmImageUriList();
			if (imageUriList != null) {
				int arrSize = imageUriList.size();
				String filePaths[] = new String[arrSize];
				String fileNames[] = new String[arrSize];

				int count = 0;
				ArrayList<File> targetFiles = new ArrayList<File>();
				for (Uri imageUri : imageUriList) {
					Cursor cursor = contentResolver.query(imageUri, columns,
							null, null, null);
					cursor.moveToFirst();
					targetFiles.add(new File(cursor.getString(0)));
					fileNames[count] = cursor.getString(1);
					cursor.close();
					count++;
				}

				ImageUploadTask imageUploadTask = new ImageUploadTask(
						targetFiles, getOAuthToken(), context);
				imageUploadTask.execute();
			} else {
				showDefaultDialog("失敗しました", null);
			}

		}
	}

	public OAuth getOAuthToken() {
		// Restore preferences
		SharedPreferences settings = getSharedPreferences(PREFS_NAME,
				Context.MODE_PRIVATE);
		String oauthTokenString = settings.getString(KEY_OAUTH_TOKEN, null);
		String tokenSecret = settings.getString(KEY_TOKEN_SECRET, null);
		if (oauthTokenString == null && tokenSecret == null) {
			logger.warn("No oauth token retrieved"); //$NON-NLS-1$
			return null;
		}
		OAuth oauth = new OAuth();
		String userName = settings.getString(KEY_USER_NAME, null);
		String userId = settings.getString(KEY_USER_ID, null);
		if (userId != null) {
			User user = new User();
			user.setUsername(userName);
			user.setId(userId);
			oauth.setUser(user);
		}
		OAuthToken oauthToken = new OAuthToken();
		oauth.setToken(oauthToken);
		oauthToken.setOauthToken(oauthTokenString);
		oauthToken.setOauthTokenSecret(tokenSecret);
		logger.debug(
				"Retrieved token from preference store: oauth token={}, and token secret={}", oauthTokenString, tokenSecret); //$NON-NLS-1$
		return oauth;
	}

	public void onOAuthDone(OAuth result) {
		if (result == null) {
			Toast.makeText(this, "Authorization failed", //$NON-NLS-1$
					Toast.LENGTH_LONG).show();
		} else {
			User user = result.getUser();
			OAuthToken token = result.getToken();
			if (user == null || user.getId() == null || token == null
					|| token.getOauthToken() == null
					|| token.getOauthTokenSecret() == null) {
				Toast.makeText(this, "Authorization failed", //$NON-NLS-1$
						Toast.LENGTH_LONG).show();
				return;
			}
			String message = String
					.format(Locale.US,
							"Authorization Succeed: user=%s, userId=%s, oauthToken=%s, tokenSecret=%s", //$NON-NLS-1$
							user.getUsername(), user.getId(),
							token.getOauthToken(), token.getOauthTokenSecret());
			Toast.makeText(this, message, Toast.LENGTH_LONG).show();
			saveOAuthToken(user.getUsername(), user.getId(),
					token.getOauthToken(), token.getOauthTokenSecret());

			RequestContext.getRequestContext().setOAuth(result);
		}
	}

	public void saveOAuthToken(String userName, String userId, String token,
			String tokenSecret) {
		logger.debug(
				"Saving userName=%s, userId=%s, oauth token={}, and token secret={}", new String[] { userName, userId, token, tokenSecret }); //$NON-NLS-1$
		SharedPreferences sp = getSharedPreferences(PREFS_NAME,
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString(KEY_OAUTH_TOKEN, token);
		editor.putString(KEY_TOKEN_SECRET, tokenSecret);
		editor.putString(KEY_USER_NAME, userName);
		editor.putString(KEY_USER_ID, userId);
		editor.commit();
	}

	/**
	 * アップロードに失敗した場合
	 */
	public void uploadFailed() {
		// 選択した画像を初期化
		ImageListEntity.getInstance().initialize();
		showDefaultDialog(getString(R.string.warning_failed_a_part_of_image),
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						// ダイアログを閉じる。
						dialog.dismiss();
						finish();
					}
				});
	}

	/**
	 * アップロード完了時に呼ばれる
	 */
	public void uploadDone(OAuth oauth, ArrayList<String> photoIds) {

		// 選択した画像を初期化
		ImageListEntity.getInstance().initialize();

		PhotoAccessTask task = new PhotoAccessTask(this, oauth);
		task.execute(photoIds.toArray(new String[0]));
	}

	public void finishActivityWidthDialog(String message) {
		showDefaultDialog(message, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				finish();
			}
		});
	}

}
