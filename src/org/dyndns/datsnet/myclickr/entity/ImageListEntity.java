package org.dyndns.datsnet.myclickr.entity;

import java.util.ArrayList;

import android.net.Uri;

public class ImageListEntity {
	private ArrayList<Uri> mImageUriList;
	private static ImageListEntity instance;

	private ImageListEntity () {

	}

	public static ImageListEntity getInstance() {
		synchronized (ImageListEntity.class) {
			if (instance == null) {
				instance = new ImageListEntity();
			}
		}
		return instance;
	}

	public void initialize() {
		instance = null;
	}

	public ArrayList<Uri> getmImageUriList() {
		return instance.mImageUriList;
	}

	public void setmImageUriList(ArrayList<Uri> imageUriList) {
		instance.mImageUriList = imageUriList;
	}
}
