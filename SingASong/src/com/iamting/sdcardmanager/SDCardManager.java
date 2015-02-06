package com.iamting.sdcardmanager;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.os.Environment;

public class SDCardManager {
	
	/**
	 * 摘自网上，获取外部sd卡路径
	 * 网址：http://www.cnblogs.com/littlepanpc/p/3868369.html
	 * 这个内容需要学习!!
	 * @return sd卡的路径
	 */
	public static String getExternalPath() {
		List<String> paths = new ArrayList<String>();
		String extFileStatus = Environment.getExternalStorageState();
		File extFile = Environment.getExternalStorageDirectory();
		if (extFileStatus.equals(Environment.MEDIA_MOUNTED) && extFile.exists() && extFile.isDirectory() && extFile.canWrite()) {
			paths.add(extFile.getAbsolutePath());
		}
		Runtime runtime = Runtime.getRuntime();
		try {
			Process process = runtime.exec("mount");
			InputStream is = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			int mountPathIndex = 1;
			while ((line = br.readLine()) != null) {
				if ((!line.contains("fat") && !line.contains("fuse") && !line.contains("storage")) || line.contains("secure") || line.contains("asec") || line.contains("firmware") || line.contains("shell") || line.contains("obb") || line.contains("legacy") || line.contains("data")) {
					continue;
				}
				String[] parts = line.split(" ");
				int length = parts.length;
				if (mountPathIndex >= length) {
					continue;
				}
				String mountPath = parts[mountPathIndex];
				if (!mountPath.contains("/") || mountPath.contains("data") || mountPath.contains("Data")) {
					continue;
				}
				File mountRoot = new File(mountPath);
				if (!mountRoot.exists() || !mountRoot.isDirectory() || !mountRoot.canWrite()) {
					continue;
				}
				boolean equalsToPrimarySD = mountPath.equals(extFile.getAbsoluteFile());
				if (equalsToPrimarySD) {
					continue;
				}
				paths.add(mountPath);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (paths.size() > 1) {
			return paths.get(paths.size() - 1);
		}
		return paths.get(0);
	}
	
	/**
	 * 获取内置sd卡的路径
	 * @return 内置sd卡的路径
	 */
	public static String getInternalPath() {
		return Environment.getExternalStorageDirectory().getAbsolutePath();
	}
}
