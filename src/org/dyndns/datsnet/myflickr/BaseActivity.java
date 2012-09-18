package org.dyndns.datsnet.myflickr;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class BaseActivity extends Activity {
	// protected final String API_KEY = getString(R.string.api_key);
	// protected final String API_SECRET = getString(R.string.api_secret);
	// protected final String LOG_TAG = getString(R.string.app_name);
	// protected final String API_KEY = "e6ed45ec0ed168287b4fa21a4189e26c";
	// protected final String API_SECRET = "0ac6960f1389d061";
	public static final String PREFS_NAME = "my-flickr";
	protected final String LOG_TAG = "MyFlickr";
	protected AlertDialog mAlertDialog;

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
//		return true;
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.menu_settings:
	        startSettingMenu();
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}

	/**
	 * ダイアログ表示状態を取得する。
	 * 
	 * @return ダイアログ表示状態(true：表示中、false：非表示)
	 */
	protected boolean isDialogShowing() {
		if (this.mAlertDialog != null && this.mAlertDialog.isShowing()) {
			return true;
		}
		return false;
	}

	/**
	 * 標準ダイアログを表示する。
	 * 
	 * @param message
	 *            表示メッセージ
	 */
	public void showDefaultDialog(final String message) {
		if (isDialogShowing()) {
			// ダイアログの多重起動防止
			return;
		}
		// リソースからメッセージやボタンのラベルを取得する。
		String doneBtn = "OK";

		// ダイアログを設定して、表示する。
		this.mAlertDialog = new AlertDialog.Builder(this).setMessage(message)
				.setCancelable(false)
				.setPositiveButton(doneBtn, mCloseBtnOnClickListener).create();
		this.mAlertDialog.show();
	}

	/**
	 * ダイアログ「閉じる」ボタン押下時のイベントリスナー。
	 */
	private OnClickListener mCloseBtnOnClickListener = new OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			// ダイアログを閉じる。
			dialog.dismiss();
		}
	};

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onStart() {
		if (this.mAlertDialog != null && this.mAlertDialog.isShowing()) {
			this.mAlertDialog.hide();
			this.mAlertDialog.show();
		}
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
	}

	protected void startSettingMenu() {
		Intent intent = new Intent();
		intent.setClass(this, SettingActivity.class);
		startActivity(intent);
	}
}
