package org.dyndns.datsnet.myflickr;

import java.util.ArrayList;

import org.dyndns.datsnet.myflickr.adapter.SelectImageAdapter;
import org.dyndns.datsnet.myflickr.data.SelectImageBindData;
import org.dyndns.datsnet.myflickr.utils.StringUtils;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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

public class SelectImageActivity extends BaseActivity {

	private static final String LOG_TAG = SelectImageActivity.class.getSimpleName();

	private static final int SEARCH_LIMIT_CNT = 8;

	private Context mContext;

	private static final String QUERY_URL = "https://ajax.googleapis.com/ajax/services/search/images?v=1.0&rsz=" + SEARCH_LIMIT_CNT + "&q=";

	private GridView mGridView = null;

	private SelectImageAdapter mAdapter = null;

	private ArrayList<SelectImageBindData> selectedDataList = new ArrayList<SelectImageBindData>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_image);
		mContext = this;

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

		final String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID };
		final String sort = MediaColumns.DATE_MODIFIED + " ASC";
		Cursor cursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, sort);
		int image_column_index = cursor.getColumnIndex(MediaStore.Images.Media._ID);
		int imageListSize = cursor.getCount();
		int[] idArr = new int[imageListSize];
		ArrayList<SelectImageBindData> bindList = new ArrayList<SelectImageBindData>();
		for (int i = 0; i < imageListSize; i++) {
			cursor.moveToPosition(i);
			SelectImageBindData bindData = new SelectImageBindData();
			String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
			Uri uriPath = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cursor.getLong(cursor.getColumnIndexOrThrow(MediaColumns._ID)));
			int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);

			bindData.setId(cursor.getInt(image_column_index));
			bindData.setUri(uriPath);
			bindList.add(bindData);

			// thumbnails[i] = MediaStore.Images.Thumbnails.getThumbnail(
			// getApplicationContext().getContentResolver(), id,
			// MediaStore.Images.Thumbnails.MICRO_KIND, null);
			// arrPath[i]= imagecursor.getString(dataColumnIndex);
		}
		Button doneBtn = (Button) findViewById(R.id.done_btn);
		doneBtn.setOnClickListener(mOnDoneBtnListener);
		mGridView = (GridView) findViewById(R.id.gridView);
		mGridView.setOnItemClickListener(mOnItemClickListener);
		mGridView.setOnItemLongClickListener(mOnItemLongClickListener);
		SelectImageAdapter imageAdapter = new SelectImageAdapter(mContext, R.layout.item_grid_view, bindList);
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

				SelectImageActivity.this.selectedDataList.add(data);

				selectedThumbView.setImageResource(android.R.drawable.presence_online);
			} else {
				data.setSelected(false);
				selectedThumbView.setImageBitmap(null);

				for (int i = 0, size = SelectImageActivity.this.selectedDataList.size(); i < size; i++) {
					if (data.getUri().toString().equals(SelectImageActivity.this.selectedDataList.get(i).getUri().toString())) {
						SelectImageActivity.this.selectedDataList.remove(i);
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
			int size = SelectImageActivity.this.selectedDataList.size();
			if (size <= 0) {
				finish();
				return;
			}
			for (int i = 0; i < size; i++) {
				uriList.add(SelectImageActivity.this.selectedDataList.get(i).getUri());
			}
			Toast.makeText(mContext, "選択した画像" + uriList.toString(), Toast.LENGTH_LONG).show();

			Intent intent = new Intent(mContext, PassImageActivity.class);
			intent.putExtra(INTENT_SELECT_IMAGE, uriList);
			startActivityForResult(intent, REQUEST_SELECT_IMAGE);

			finish();

		}
	};

}
