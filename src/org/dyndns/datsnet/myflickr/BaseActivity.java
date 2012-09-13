package org.dyndns.datsnet.myflickr;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class BaseActivity extends Activity {
//	protected final String API_KEY = getString(R.string.api_key);
//	protected final String API_SECRET = getString(R.string.api_secret);
//	protected final String LOG_TAG = getString(R.string.app_name); 
	protected final String API_KEY = "e6ed45ec0ed168287b4fa21a4189e26c";
	protected final String API_SECRET = "0ac6960f1389d061";
	protected final String LOG_TAG = "MyFlickr"; 

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
}
