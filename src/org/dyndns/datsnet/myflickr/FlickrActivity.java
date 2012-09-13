/**
 * 
 */
package org.dyndns.datsnet.myflickr;

import java.io.IOException;
import java.net.URL;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.FlickrException;
import com.googlecode.flickrjandroid.auth.Permission;
import com.googlecode.flickrjandroid.oauth.OAuthToken;

/**
 * @author atsumi
 *
 */
public class FlickrActivity extends BaseActivity {
	
	private Context mContext;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.mContext = this;
		
		Intent intent = getIntent();
		
		Uri imageUri = null;
		imageUri = Uri.parse(intent.getExtras()
				.get("android.intent.extra.STREAM").toString());
		
	}
	
	public void initialOauth() throws IOException, FlickrException {
		String callBackUrl = "org.dyndns.datsnet.myflickr";
		Flickr f = new Flickr(API_KEY, API_SECRET);
		// get a request token from Flickr
		OAuthToken oauthToken = f.getOAuthInterface().getRequestToken(
				callBackUrl);
		// you should save the request token and token secret to a preference
		// store for later use.
		saveToken(oauthToken);

		// build the Authentication URL with the required permission
		URL oauthUrl = f.getOAuthInterface().buildAuthenticationUrl(
				Permission.WRITE, oauthToken);
		
		 mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri
                 .parse(oauthUrl.toString())));
		
		// redirect user to the genreated URL.
//		redirect(oauthUrl);
	}
	

	public void saveToken(OAuthToken oauthToken) {
		
	}
}
