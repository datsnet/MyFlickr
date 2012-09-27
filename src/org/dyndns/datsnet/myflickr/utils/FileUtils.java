/**
 * FileUtils.java
 * 2012/05/17 i-kugyon
 */
package org.dyndns.datsnet.myflickr.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import org.dyndns.datsnet.myflickr.R;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * ファイルやディレクトリに関するUtils。
 *
 * @author i-kugyon
 */
public class FileUtils {

	private static final String LOG_TAG = FileUtils.class.getSimpleName();

	/**
	 * 対象のディレクトリ内であるJPG画像Fileを渡す。
	 *
	 * @param dir 対象のディレクトリ
	 * @return 画像Fileのリスト
	 */
	public static List<File> getImageFileOnlyInFolder(Context context, final String dir) {
		// 画像パスのリストを作成する。
		List<File> imageFileList = new ArrayList<File>();

		// 対象のディレクトリのFileを取得
		File targetDir = new File(dir);

		if (targetDir.exists() && targetDir.isDirectory()) {
			// 対象のディレクトリが存在している場合
			// 対象のディレクトリのサブファイルを取得する。
			String extensionJpegLower = "jpg";
			File[] imageFiles = targetDir.listFiles(getFileRegexFilter(extensionJpegLower));

			for (File imageFile : imageFiles) {
				imageFileList.add(imageFile);
			}
		}

		return imageFileList;
	}

	/**
	 * 対象のディレクトリ内であるFileを渡す。
	 *
	 * @param dir 対象のディレクトリ
	 * @return Fileのリスト
	 */
	public static List<File> getFileOnlyInFolder(final String dir) {
		// パスのリストを作成する。
		List<File> fileList = new ArrayList<File>();

		// 対象のディレクトリのFileを取得
		File targetDir = new File(dir);

		if (targetDir.exists() && targetDir.isDirectory()) {
			// 対象のディレクトリが存在している場合
			// 対象のディレクトリのサブファイルを取得する。
			File[] files = targetDir.listFiles(getNotDirFileFilter());

			for (File imageFile : files) {
				fileList.add(imageFile);
			}
		}

		return fileList;
	}

	/**
	 * 対象のディレクトリ内であるJPG画像のパスのリストを渡す。
	 *
	 * @param dir 対象のディレクトリ
	 * @return 画像パスのリスト
	 */
	public static List<String> getImagePathOnlyInFolder(Context context, final String dir) {
		// 画像パスのリストを作成する。
		List<String> imagePathList = new ArrayList<String>();

		List<File> imageFiles = getImageFileOnlyInFolder(context, dir);

		for (File imageFile : imageFiles) {
			imagePathList.add(imageFile.getAbsolutePath());
		}

		return imagePathList;
	}

	/**
	 * 対象のディレクトリ内にあるディレクトリFileを返す
	 *
	 * @param dir 対象のディレクトリ
	 * @return list<String>
	 */
	public static List<File> getFolder(String dir) {
		// パスのリストを作成する。
		List<File> dirList = new ArrayList<File>();

		// 対象のディレクトリのFileを取得
		File targetDir = new File(dir);

		if (targetDir.exists() && targetDir.isDirectory()) {
			// 対象のディレクトリが存在している場合
			// 対象のディレクトリのサブファイルを取得する。
			File[] files = targetDir.listFiles(getDirOnlyFilter());

			for (File imageFile : files) {
				dirList.add(imageFile);
			}
		}

		return dirList;

	}

	/**
	 * 対象のディレクトリ内にあるディレクトリを返す
	 *
	 * @param dir 対象のディレクトリ
	 * @return list<String>
	 */
	public static List<String> getFolderPath(String dir) {
		// パスのリストを作成する。
		List<String> dirList = new ArrayList<String>();

		List<File> dirFiles = getFolder(dir);
		for (File dirFile : dirFiles) {
			dirList.add(dirFile.getAbsolutePath());
		}

		return dirList;

	}

	/**
	 * UriからPathへの変換処理
	 *
	 * @param uri
	 * @return String
	 */
	public static String convertPathFromUriToString(Context context, Uri uri) {
		ContentResolver contentResolver = context.getContentResolver();
		String[] columns = { MediaStore.Images.Media.DATA };
		Cursor cursor = contentResolver.query(uri, columns, null, null, null);
		cursor.moveToFirst();
		String path = cursor.getString(0);
		cursor.close();
		return path;
	}

	/**
	 * 親フォルダからサブフォルダまで全部削除する。
	 *
	 * @param fileOrDirectory 親フォルダ
	 */
	public static void deleteRecursive(File fileOrDirectory) {
		if (fileOrDirectory.isDirectory()) {
			for (File child : fileOrDirectory.listFiles()) {
				deleteRecursive(child);
			}
		}

		fileOrDirectory.delete();
	}

	/**
	 * リストに格納されたファイルパス名のファイルを部削除する。
	 *
	 * @param fileOrDirectory 親フォルダ
	 */
	public static void deleteAllListedFiles(List<String> fileList) {
		for (int i = 0; i < fileList.size(); i++) {
			String strTargetPath = fileList.get(i);
			File targetFile = new File(strTargetPath);
			if (targetFile.isFile() && targetFile.exists()) {
				targetFile.delete();
			}
		}
	}

	public static void renameFile(String oldFileName, String newFileNmae) {
		File targetFile = new File(oldFileName);
		if (targetFile.exists() && targetFile.isFile()) {
			File newFile = new File(newFileNmae);
			if (newFile.exists()) {
				newFile.delete();
			}
			targetFile.renameTo(new File(newFileNmae));
		}
	}


	/**
	 * ファイルのコピーを生成する
	 * @param srcFilePath 元ファイルパス（ファイル名含む絶対パス）
	 * @param dstFilePath 生成先ファイルパス（ファイル名含む絶対パス）
	 * @throws IOException
	 */
	public static boolean copyFile(String srcFilePath, String dstFilePath) throws IOException {
		FileChannel iChannel = new FileInputStream(srcFilePath).getChannel();
		FileChannel oChannel = new FileOutputStream(dstFilePath).getChannel();
		iChannel.transferTo(0, iChannel.size(), oChannel);
		iChannel.close();
		oChannel.close();
		return true;
	}

	/**
	 * ファイルを削除する。
	 *
	 * @param filePath ファイルパス
	 */
	public static void deleteFile(String filePath) {
		File targetFile = new File(filePath);
		if (targetFile.isFile() && targetFile.exists()) {
			targetFile.delete();
		}
	}

	/**
	 * フォルダにあるファイルのみを全部削除する。
	 *
	 * @param dirPath フォルダパス
	 */
	public static void deleteFiles(String dirPath) {
		// 対象のディレクトリのFileを取得
		File targetDir = new File(dirPath);

		if (targetDir.exists() && targetDir.isDirectory()) {
			// 対象のディレクトリが存在している場合
			// 対象のディレクトリのサブファイルを削除する。
			File[] files = targetDir.listFiles();

			for (File file : files) {
				if (file.isFile()) {
					file.delete();
				}
			}
		}

	}

	private static FilenameFilter getFileRegexFilter(String extension) {
		final String extension_ = extension;
		return new FilenameFilter() {
			public boolean accept(File file, String name) {
				boolean ret = name.toLowerCase().endsWith(extension_);
				return ret;
			}
		};
	}

	private static FileFilter getNotDirFileFilter() {
		return new FileFilter() {
			public boolean accept(File file) {
				return file.isFile();
			}
		};
	}

	private static FileFilter getDirOnlyFilter() {
		return new FileFilter() {
			public boolean accept(File file) {
				return file.isDirectory();
			}
		};
	}
}
