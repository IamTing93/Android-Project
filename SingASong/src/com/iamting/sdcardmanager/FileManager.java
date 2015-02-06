package com.iamting.sdcardmanager;

import java.io.File;
import java.util.ArrayList;

public class FileManager {
	public static final String DIR_PATH = SDCardManager.getExternalPath()
			+ "/SingSongs";
	public static final String RECORD_DIR_PATH = DIR_PATH + "/records";
	public static final String MUSIC_DIR_PATH = DIR_PATH + "/musics";
	public static final String LYRIC_DIR_PATH = DIR_PATH + "/lyrics";

	public FileManager() {
		// Build all directories in request.
		buildDir(DIR_PATH);
		buildDir(RECORD_DIR_PATH);
		buildDir(MUSIC_DIR_PATH);
		buildDir(LYRIC_DIR_PATH);
	}

	/**
	 * Build directory for the given path.
	 * @param dirPath: The specific directory path.
	 * @return Whether the directory is builded.
	 */
	private boolean buildDir(String dirPath) {
		File dir = new File(dirPath);
		if (dir.exists()) {
			return false;
		}
		System.out.println("This function builds a directory");
		dir.mkdirs();
		return true;
	}

	/**
	 * @param path: The path of the directory. 
	 * @return A list that contains all files' path.
	 */
	public ArrayList<String> getAllFilesPath(String path) {
		File dir = new File(path);
		ArrayList<String> lists = new ArrayList<String>();
		File[] files = dir.listFiles();
		for (File file : files) {
			lists.add(file.getAbsolutePath());
		}
		return lists;
	}
	
	public boolean deleteFile(String path) {
		File file = new File(path);
		if (!file.exists()) {
			return false;
		}
		file.delete();
		return true;
	}
}
