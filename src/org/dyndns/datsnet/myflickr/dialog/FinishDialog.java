/**
 *
 */
package org.dyndns.datsnet.myflickr.dialog;

import java.util.ArrayList;

import org.dyndns.datsnet.myflickr.R;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

	/**
	 * コンストラクタ
	 *
	 * @param context
	 */
	public FinishDialog(Context context, ArrayList<String> urlList) {
		super(context, R.style.Theme_FinishDialog);

		// レイアウトを決定
		setContentView(R.layout.finish_dialog);

		mContext = context;
		mMailBtn = (Button) findViewById(R.id.mail_btn);
		mBrowserBtn = (Button) findViewById(R.id.browser_btn);
		mLinkCopyBtn = (Button) findViewById(R.id.link_copy_btn);

		// ボタンビューにOnClickListenerを登録
		mBrowserBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dismiss();
				mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://m.flickr.com/#/photos/datsnet/8019102369/")));
			}
		});
	}
}
