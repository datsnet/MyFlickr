/**
 *
 */
package org.dyndns.datsnet.myflickr;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * @author atsumi
 *
 */
public class PassImageActivity extends BaseActivity {
	private Context mContext;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.mContext = this;

		Intent intent = new Intent();
		intent.setClass(mContext, FlickrActivity.class);
		intent.putExtras(getIntent());
		startActivity(intent);
		finish();
		return;


	}
}
