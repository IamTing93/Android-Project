package com.example.savevoice;


import java.io.File;
import java.util.ArrayList;

public class FileManager {
	public static final String DIR_PATH = SDCardManager.getExternalPath() + "/SaveVoices";
	public FileManager() {
		// Build all directories in request.
		buildDir(DIR_PATH);
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
	
	/**
	 * Delete the specified file.
	 * @param path: The path of the file.
	 * @return True if success.
	 */
	public boolean deleteFile(String path) {
		File file = new File(path);
		if (!file.exists()) {
			return false;
		}
		file.delete();
		return true;
	}
}
