/**
 *
 */
package org.dyndns.datsnet.myflickr.dialog;

import java.util.HashMap;

import org.dyndns.datsnet.myflickr.BaseActivity;
import org.dyndns.datsnet.myflickr.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.ClipboardManager;
import android.view.View;
import android.widget.Button;

/**
 * @author user1
 *
 */
public class FinishDialog extends Dialog {

	private Context mContext;

	/** ボタンビュー */
	private Button mMailBtn;
	private Button mBrowserBtn;
	private Button mLinkCopyBtn;
	private Button mCloseBtn;

	/**
	 * コンストラクタ
	 *
	 * @param context
	 */
	public FinishDialog(Context context, final HashMap<String, String> target) {
		super(context, R.style.Theme_FinishDialog);

		// レイアウトを決定
		setContentView(R.layout.finish_dialog);

		mContext = context;
		mMailBtn = (Button) findViewById(R.id.mail_btn);
		mBrowserBtn = (Button) findViewById(R.id.browser_btn);
		mLinkCopyBtn = (Button) findViewById(R.id.link_copy_btn);
		mCloseBtn = (Button) findViewById(R.id.close_btn);


		// ボタンビューにOnClickListenerを登録
		mBrowserBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dismiss();
				mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(target.get(BaseActivity.KEY_DIALOG_SHOW_BROWSER))));
				((Activity) mContext).finish();
			}
		});
		mMailBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				dismiss();
				Intent mailIntent = new Intent();
				mailIntent.setAction( Intent.ACTION_SENDTO );
				mailIntent.setData(Uri.parse("mailto:" + ""));
				mailIntent.putExtra(Intent.EXTRA_TEXT, target.get(BaseActivity.KEY_DIALOG_SEND_MAIL));
				mContext.startActivity(mailIntent);
				((Activity) mContext).finish();

			}
		});

		mLinkCopyBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				dismiss();
				ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Activity.CLIPBOARD_SERVICE);
				cm.setText(target.get(BaseActivity.KEY_DIALOG_COPY_CLIPBOARD));
				((Activity) mContext).finish();
			}
		});

		mCloseBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				dismiss();
				((Activity) mContext).finish();
			}
		});
	}
}
