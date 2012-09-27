package org.dyndns.datsnet.myflickr.data;



public class SelectAlbumBindData extends SelectImageBindData {
	private String albumPath;
	private String albumName;
	public SelectAlbumBindData() {

	}
	/**
	 * @return albumPath
	 */
	public String getAlbumPath() {
		return albumPath;
	}
	/**
	 * @param albumPath セットする albumPath
	 */
	public void setAlbumPath(String albumPath) {
		this.albumPath = albumPath;
	}
	/**
	 * @return albumName
	 */
	public String getAlbumName() {
		return albumName;
	}
	/**
	 * @param albumName セットする albumName
	 */
	public void setAlbumName(String albumName) {
		this.albumName = albumName;
	}


}