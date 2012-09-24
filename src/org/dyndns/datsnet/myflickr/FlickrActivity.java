/**
 *
 */
package org.dyndns.datsnet.myflickr;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.dyndns.datsnet.myflickr.dialog.FinishDialog;
import org.dyndns.datsnet.myflickr.entity.ImageListEntity;
import org.dyndns.datsnet.myflickr.helper.FlickrHelper;
import org.dyndns.datsnet.myflickr.task.GetOAuthTokenTask;
import org.dyndns.datsnet.myflickr.task.ImageUploadTask;
import org.dyndns.datsnet.myflickr.task.OAuthTask;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.FlickrException;
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
	private static final Logger logger = LoggerFactory.getLogger(FlickrActivity.class);
	private static final Uri OAUTH_CALLBACK_URI = Uri.parse(FlickrActivity.CALLBACK_SCHEME + "://oauth");
	// public static final String CALLBACK_SCHEME =
	// "org.dyndns.datsnet.myflickr";
	public static final String CALLBACK_SCHEME = "myflickr";
	public static final String KEY_OAUTH_TOKEN = "my-flickr-oauthToken"; //$NON-NLS-1$
	public static final String KEY_TOKEN_SECRET = "my-flickr-tokenSecret"; //$NON-NLS-1$
	public static final String KEY_USER_NAME = "my-flickr-userName"; //$NON-NLS-1$
	public static final String KEY_USER_ID = "my-flickr-userId"; //$NON-NLS-1$

	private static Uri mImageUri;
	private static ArrayList<Uri> mImageUriList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.mContext = this;
		setContentView(R.layout.activity_flickr);

		try {
//			ImageListEntity.getInstance();
			// mImageUri =
			// Uri.parse(getIntent().getExtras().get("android.intent.extra.STREAM").toString());
			// mImageUri = Uri.parse(getIntent().getStringExtra(URI_LIST_KEY));
//			mImageUriList = (ArrayList<Uri>) getIntent().getExtras().get(URI_LIST_KEY);
			ImageListEntity imageListEntity = ImageListEntity.getInstance();
			if (ImageListEntity.getInstance().getmImageUriList() == null) {
				imageListEntity.setmImageUriList((ArrayList<Uri>) getIntent().getExtras().get(URI_LIST_KEY));
			}
		}

		catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this, "画像を取得できませんでした。", Toast.LENGTH_LONG).show();
			return;
		}

		OAuth oauth = getOAuthToken();
		// 認証情報が得られない場合OAuth認証ページへ
//		if (oauth == null || oauth.getUser() == null) {
		Intent intent = getIntent();
		String scheme = intent.getScheme();
		if (scheme == null && oauth != null) {
			setUpload(mContext);
		} else if (scheme == null || oauth == null) {
//		if (oauth.getToken().getOauthTokenSecret() == null || oauth.getUser() == null) {
			OAuthTask task = new OAuthTask(this);
			task.execute();
		}

	}

	private void load(OAuth oauth) {
		if (oauth != null) {
			// new LoadUserTask(this, userIcon).execute(oauth);
			// new LoadPhotostreamTask(this, listView).execute(oauth);
		}
	}

    @Override
    protected void onNewIntent(Intent intent) {
            //this is very important, otherwise you would get a null Scheme in the onResume later on.
            setIntent(intent);
    }

	@Override
	public void onResume() {
		super.onResume();
		Intent intent = getIntent();
		String scheme = intent.getScheme();
		OAuth savedToken = getOAuthToken();
		if (CALLBACK_SCHEME.equals(scheme) && (savedToken == null || savedToken.getUser() == null)) {
			Uri uri = intent.getData();
			String query = uri.getQuery();
			logger.debug("Returned Query: {}", query); //$NON-NLS-1$
			String[] data = query.split("&"); //$NON-NLS-1$
			if (data != null && data.length == 2) {
				String oauthToken = data[0].substring(data[0].indexOf("=") + 1); //$NON-NLS-1$
				String oauthVerifier = data[1].substring(data[1].indexOf("=") + 1); //$NON-NLS-1$
				logger.debug("OAuth Token: {}; OAuth Verifier: {}", oauthToken, oauthVerifier); //$NON-NLS-1$

				OAuth oauth = getOAuthToken();

				if (oauth != null && oauth.getToken() != null && oauth.getToken().getOauthTokenSecret() != null) {

					GetOAuthTokenTask task = new GetOAuthTokenTask(this);
					task.execute(oauthToken, oauth.getToken().getOauthTokenSecret(), oauthVerifier);

				}
			}

			// } else if (savedToken != null && savedToken.getUser() != null) {
			//
			// setUpload(mContext);

		} else {
			// try {
			// initialOauth();
			// } catch (IOException e) {
			// e.printStackTrace();
			// } catch (FlickrException e) {
			// e.printStackTrace();
			// }
		}

	}

	/**
	 * アップロードを開始する
	 */
	public void setUpload(Context context) {
		Uploader uploader = FlickrHelper.getInstance().getUploader();
		if (uploader != null) {

			ContentResolver contentResolver = mContext.getContentResolver();
			String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media.DISPLAY_NAME };

			ImageListEntity imageListEntity = ImageListEntity.getInstance();
			ArrayList<Uri> imageUriList = imageListEntity.getmImageUriList();
			int arrSize = imageListEntity.getmImageUriList().size();
			String filePaths[] = new String[arrSize];
			String fileNames[] = new String[arrSize];


			int count = 0;
			ArrayList<File> targetFiles = new ArrayList<File>();
			for (Uri imageUri : imageUriList) {
				Cursor cursor = contentResolver.query(imageUri, columns, null, null, null);
				cursor.moveToFirst();
				targetFiles.add(new File(cursor.getString(0)));
				fileNames[count] = cursor.getString(1);
				cursor.close();
				count++;
			}

			ImageUploadTask imageUploadTask = new ImageUploadTask(targetFiles, getOAuthToken(), context);
			imageUploadTask.execute();

		}
	}

	public OAuth getOAuthToken() {
		// Restore preferences
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
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
		logger.debug("Retrieved token from preference store: oauth token={}, and token secret={}", oauthTokenString, tokenSecret); //$NON-NLS-1$
		return oauth;
	}

	public void onOAuthDone(OAuth result) {
		if (result == null) {
			Toast.makeText(this, "Authorization failed", //$NON-NLS-1$
			Toast.LENGTH_LONG).show();
		} else {
			User user = result.getUser();
			OAuthToken token = result.getToken();
			if (user == null || user.getId() == null || token == null || token.getOauthToken() == null || token.getOauthTokenSecret() == null) {
				Toast.makeText(this, "Authorization failed", //$NON-NLS-1$
				Toast.LENGTH_LONG).show();
				return;
			}
			String message = String.format(Locale.US, "Authorization Succeed: user=%s, userId=%s, oauthToken=%s, tokenSecret=%s", //$NON-NLS-1$
					user.getUsername(), user.getId(), token.getOauthToken(), token.getOauthTokenSecret());
			Toast.makeText(this, message, Toast.LENGTH_LONG).show();
			saveOAuthToken(user.getUsername(), user.getId(), token.getOauthToken(), token.getOauthTokenSecret());

			RequestContext.getRequestContext().setOAuth(result);
		}
	}

	public void saveOAuthToken(String userName, String userId, String token, String tokenSecret) {
		logger.debug("Saving userName=%s, userId=%s, oauth token={}, and token secret={}", new String[] { userName, userId, token, tokenSecret }); //$NON-NLS-1$
		SharedPreferences sp = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString(KEY_OAUTH_TOKEN, token);
		editor.putString(KEY_TOKEN_SECRET, tokenSecret);
		editor.putString(KEY_USER_NAME, userName);
		editor.putString(KEY_USER_ID, userId);
		editor.commit();
	}

	/**
	 * アップロード完了時に呼ばれる
	 */
	public void uploadDone(OAuth oauth, ArrayList<String> photoIds) {

//		new FinishDialog(mContext, "タイトル", "本文").show();
		// 選択した画像を初期化
		ImageListEntity.getInstance().initialize();

		OAuthToken token = oauth.getToken();
		Flickr flickr = FlickrHelper.getInstance().getFlickrAuthed(token.getOauthToken(),
                token.getOauthTokenSecret());
		String photoId = null;
		if (photoIds.size() == 1) {
			photoId = photoIds.get(0);
		}
		String url = null;
		try {
			url = flickr.getPhotosInterface().getPhoto(photoId).getLargeUrl();
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
		ArrayList<String> urlList = new ArrayList<String>();
		urlList.add(url);
		new FinishDialog(mContext, urlList).show();

//		Builder completeDialog = new AlertDialog.Builder(mContext);
//		completeDialog.setMessage("アップロードが完了しました");

//
//		completeDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				finish();
//			}
//		});
//		completeDialog.show();
	}

}
