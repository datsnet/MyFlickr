package org.dyndns.datsnet.myflickr.data;

import android.net.Uri;


public class SelectImageBindData {
	private int id;
	private boolean isSelected = false;
	private Uri uri;
	public SelectImageBindData() {

	}
	/**
	 * @return id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id セットする id
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return isSelected
	 */
	public boolean isSelected() {
		return isSelected;
	}
	/**
	 * @param isSelected セットする isSelected
	 */
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	/**
	 * @return uri
	 */
	public Uri getUri() {
		return uri;
	}
	/**
	 * @param uri セットする uri
	 */
	public void setUri(Uri uri) {
		this.uri = uri;
	}


}