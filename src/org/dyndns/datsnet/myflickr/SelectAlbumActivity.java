package org.dyndns.datsnet.myflickr;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dyndns.datsnet.myflickr.adapter.SelectAlbumAdapter;
import org.dyndns.datsnet.myflickr.cache.ImageCache;
import org.dyndns.datsnet.myflickr.data.SelectAlbumBindData;
import org.dyndns.datsnet.myflickr.data.SelectImageBindData;
import org.dyndns.datsnet.myflickr.utils.FileUtils;
import org.dyndns.datsnet.myflickr.utils.StringUtils;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

public class SelectAlbumActivity extends BaseActivity {

	private static final String LOG_TAG = SelectAlbumActivity.class.getSimpleName();
	private Context mContext;
	private GridView mGridView = null;

	private ArrayList<SelectImageBindData> selectedDataList = new ArrayList<SelectImageBindData>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_album);
		mContext = this;

		// 画像キャッシュクリア
		ImageCache.clearCache();

		// boolean existThreadPolicy = true;
		// try {
		// Class.forName("android.os.StrictMode$ThreadPolicy");
		// } catch (ClassNotFoundException e) {
		// existThreadPolicy = false;
		// }
		// if (existThreadPolicy) {
		// // StrictModeをOFFに設定
		// StrictMode.setThreadPolicy(new
		// StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
		// }

		// 検索するディレクトリパスをセット
		String searchDir = Environment.getExternalStorageDirectory().getPath();
		List<String> dirList = FileUtils.getFolderPath(Environment.getExternalStorageDirectory().getPath());
//		Collections.sort(dirList, null);
		Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;


//		String[] columns = { MediaColumns.DATA, MediaColumns.DISPLAY_NAME, MediaColumns._ID, MediaColumns.DATA, MediaColumns.DATE_MODIFIED,  MediaStore.Images.ImageColumns.ORIENTATION };
//		String selection = MediaColumns.MIME_TYPE + " = ? AND " + MediaColumns.DATA + " like " + "'" + dirList.get(i) + System.getProperty("file.separator") + "%'" + " AND " + MediaColumns.DATA + "  NOT LIKE ( '" + searchDir + "/android%' )";


		List<SelectAlbumBindData> dataList = new ArrayList<SelectAlbumBindData>();



		for (int i = 0; i < dirList.size(); i++) {

			String[] projection = { MediaColumns.DATA, MediaColumns.DISPLAY_NAME, MediaColumns._ID, MediaColumns.DATE_MODIFIED,  MediaStore.Images.ImageColumns.ORIENTATION };
			String selection = MediaColumns.MIME_TYPE + " = ? AND " + MediaColumns.DATA + " like " + "'" + dirList.get(i) + System.getProperty("file.separator") + "%'" + " AND " + MediaColumns.DATA + "  NOT LIKE ( '" + searchDir + "/android%' )";
			String[] selectionArgs = { "image/jpeg" };
			String sort = MediaColumns.DATE_MODIFIED + " ASC";
			try {
				Cursor cursor = managedQuery(uri, projection, selection, selectionArgs, sort);
				if (cursor != null) {

					boolean isDataPresent = cursor.moveToFirst();
					SelectAlbumBindData data = new SelectAlbumBindData();

					if (isDataPresent) {
						// 画像ディレクトリ名と画像数を設定
						File file = new File(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));

						// 画像を格納しているカレントディレクトリ名を取得する
						// jpgファイルが格納されているディレクトリ名を取得
						String[] strSplitList = dirList.get(i).split(System.getProperty("file.separator"));
						String currentPathOfFileWithoutFileName = null;
						if (strSplitList.length > 1) {
							currentPathOfFileWithoutFileName = strSplitList[strSplitList.length - 1];
						} else {
							currentPathOfFileWithoutFileName = strSplitList[0];
						}
						data.setAlbumPath(file.getAbsolutePath());
						data.setAlbumName(currentPathOfFileWithoutFileName);
						data.setId((int) cursor.getLong(cursor.getColumnIndexOrThrow(MediaColumns._ID)));
						dataList.add(data);


					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		Button doneBtn = (Button) findViewById(R.id.done_btn);
		doneBtn.setOnClickListener(mOnDoneBtnListener);
		mGridView = (GridView) findViewById(R.id.gridView);
		mGridView.setOnItemClickListener(mOnItemClickListener);
		mGridView.setOnItemLongClickListener(mOnItemLongClickListener);
		SelectAlbumAdapter imageAdapter = new SelectAlbumAdapter(mContext, R.layout.item_grid_album_view, dataList);
		mGridView.setAdapter(imageAdapter);
	}

	/**
	 * 画像アイテムを選択した場合
	 */
	protected OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			SelectImageBindData data = (SelectImageBindData) parent.getItemAtPosition(position);
			ViewGroup viewGroup = (ViewGroup) view;
			ImageView selectedThumbView = (ImageView) viewGroup.getChildAt(1);
			if (!data.isSelected()) {
				data.setSelected(true);

				SelectAlbumActivity.this.selectedDataList.add(data);

				selectedThumbView.setImageResource(android.R.drawable.presence_online);
//				selectedThumbView.setImageResource(R.drawable.selected_badge);
			} else {
				data.setSelected(false);
				selectedThumbView.setImageBitmap(null);

				for (int i = 0, size = SelectAlbumActivity.this.selectedDataList.size(); i < size; i++) {
					if (data.getUri().toString().equals(SelectAlbumActivity.this.selectedDataList.get(i).getUri().toString())) {
						SelectAlbumActivity.this.selectedDataList.remove(i);
						break;
					}
				}

			}
		}

	};

	// ロングタップしたとき呼ばれる、拡大表示画面に遷移
	protected OnItemLongClickListener mOnItemLongClickListener = new OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			SelectImageBindData data = (SelectImageBindData) parent.getItemAtPosition(position);
			String path = StringUtils.getPath(mContext, data.getUri());
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.parse("file://" + path), "image/*");
			mContext.startActivity(intent);
			return false;
		}
	};

	private OnClickListener mOnDoneBtnListener = new OnClickListener() {

		@Override
		public void onClick(View view) {

			ArrayList<Uri> uriList = new ArrayList<Uri>();
			int size = SelectAlbumActivity.this.selectedDataList.size();
			if (size <= 0) {
				finish();
				return;
			}
			for (int i = 0; i < size; i++) {
				uriList.add(SelectAlbumActivity.this.selectedDataList.get(i).getUri());
			}
			Toast.makeText(mContext, "選択した画像" + uriList.toString(), Toast.LENGTH_LONG).show();

			Intent intent = new Intent(mContext, PassImageActivity.class);
			intent.putExtra(INTENT_SELECT_IMAGE, uriList);
			startActivityForResult(intent, REQUEST_SELECT_IMAGE);

			finish();

		}
	};

}
