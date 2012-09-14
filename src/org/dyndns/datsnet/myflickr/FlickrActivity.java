/**
 *
 */
package org.dyndns.datsnet.myflickr;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Locale;

import org.dyndns.datsnet.myflickr.helper.FlickrHelper;
import org.dyndns.datsnet.myflickr.task.ImageUploadTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import android.content.ContentResolver;
import android.content.Context;
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
import com.googlecode.flickrjandroid.auth.Permission;
import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthInterface;
import com.googlecode.flickrjandroid.oauth.OAuthToken;
import com.googlecode.flickrjandroid.people.User;
import com.googlecode.flickrjandroid.uploader.UploadMetaData;
import com.googlecode.flickrjandroid.uploader.Uploader;

/**
 * @author atsumi
 *
 */
public class FlickrActivity extends BaseActivity {

	private Context mContext;
	private static final Logger logger = LoggerFactory.getLogger(FlickrActivity.class);
	public static final String PREFS_NAME = "my-flickr";
	private static final Uri OAUTH_CALLBACK_URI = Uri.parse(FlickrActivity.CALLBACK_SCHEME + "://oauth");
	// public static final String CALLBACK_SCHEME =
	// "org.dyndns.datsnet.myflickr";
	public static final String CALLBACK_SCHEME = "myflickr";
	public static final String KEY_OAUTH_TOKEN = "my-flickr-oauthToken"; //$NON-NLS-1$
	public static final String KEY_TOKEN_SECRET = "my-flickr-tokenSecret"; //$NON-NLS-1$
	public static final String KEY_USER_NAME = "my-flickr-userName"; //$NON-NLS-1$
	public static final String KEY_USER_ID = "my-flickr-userId"; //$NON-NLS-1$
	private static Uri imageUri;
	private static byte byteArray[];

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.mContext = this;

		try {
			imageUri = Uri.parse(getIntent().getExtras().get("android.intent.extra.STREAM").toString());
		}

		catch (Exception e) {
			e.printStackTrace();
		}

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
				String strOauthToken = data[0].substring(data[0].indexOf("=") + 1); //$NON-NLS-1$
				String strOauthVerifier = data[1].substring(data[1].indexOf("=") + 1); //$NON-NLS-1$
				logger.debug("OAuth Token: {}; OAuth Verifier: {}", strOauthToken, strOauthVerifier); //$NON-NLS-1$

				OAuth oauth = getOAuthToken();
				if (oauth != null && oauth.getToken() != null && oauth.getToken().getOauthTokenSecret() != null) {

					String oauthToken = strOauthToken;
					String oauthTokenSecret = oauth.getToken().getOauthTokenSecret();
					String verifier = strOauthVerifier;

					Flickr f = FlickrHelper.getInstance().getFlickr();
					OAuthInterface oauthApi = f.getOAuthInterface();
					try {
						OAuth result = oauthApi.getAccessToken(oauthToken, oauthTokenSecret, verifier);
						onOAuthDone(result);

					} catch (Exception e) {
						logger.error(e.getLocalizedMessage(), e);
					}

				}

			}
		} else if (savedToken != null && savedToken.getUser() != null) {

			Uploader uploader = FlickrHelper.getInstance().getUploader();
//			try {
				if (uploader != null) {

					ContentResolver contentResolver = mContext.getContentResolver();
					String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media.DISPLAY_NAME };
					Cursor cursor = contentResolver.query(imageUri, columns, null, null, null);
					cursor.moveToFirst();
					String filePath = cursor.getString(0);
					String displayName = cursor.getString(1);
					cursor.close();
					logger.info(filePath);

					ImageUploadTask imageUploadTask = new ImageUploadTask(displayName, mContext, RequestContext.getRequestContext().getOAuth());
					imageUploadTask.execute(filePath);


//					File file = new File(filePath);
//					InputStream in = new FileInputStream(file);
//					if (in != null) {
//						UploadMetaData uploadMetaData = new UploadMetaData();
//						uploadMetaData.setAsync(false);
//						uploadMetaData.setFamilyFlag(true);
//						uploadMetaData.setPublicFlag(true);
//						uploader.upload(displayName, in, uploadMetaData);
//						in.close();
//					}

				}

//			} catch (FlickrException e) {
//				// TODO 自動生成された catch ブロック
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO 自動生成された catch ブロック
//				e.printStackTrace();
//			} catch (SAXException e) {
//				// TODO 自動生成された catch ブロック
//				e.printStackTrace();
//			}


		} else {
			try {
				initialOauth();
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			} catch (FlickrException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
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

		RequestContext.getRequestContext().setOAuth(oauth);

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
			// load(result);
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

	public void initialOauth() throws IOException, FlickrException {
		Flickr f = FlickrHelper.getInstance().getFlickr();
		OAuthToken oauthToken = f.getOAuthInterface().getRequestToken(OAUTH_CALLBACK_URI.toString());
		saveToken(oauthToken);

		// build the Authentication URL with the required permission
		URL oauthUrl = f.getOAuthInterface().buildAuthenticationUrl(Permission.WRITE, oauthToken);

		mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(oauthUrl.toString())));
	}

	public void saveToken(OAuthToken oauthToken) {
		logger.debug("save position");
		SharedPreferences sp = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString(KEY_OAUTH_TOKEN, oauthToken.getOauthToken());
		editor.putString(KEY_TOKEN_SECRET, oauthToken.getOauthTokenSecret());
		editor.commit();
	}
}
