package com.iamting.audiomanager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LrcHandle {
	private static List<String> mWords = new ArrayList<String>();
	private static List<Integer> mTimeList = new ArrayList<Integer>();

	public static void readLRC(String path) {
		if (path != null) {
			mWords.clear();
			File file = new File(path);
			if (file.exists()) {
				try {
					FileInputStream fileInputStream = new FileInputStream(file);
					InputStreamReader inputStreamReader = new InputStreamReader(
							fileInputStream, "utf-8");
					BufferedReader bufferedReader = new BufferedReader(
							inputStreamReader);
					String s = "";
					while ((s = bufferedReader.readLine()) != null) {
						addTimeToList(s);
						if ((s.indexOf("[ar:") != -1)
								|| (s.indexOf("[ti:") != -1)
								|| (s.indexOf("[by:") != -1)) {
							s = s.substring(s.indexOf(":") + 1, s.indexOf("]"));
						} else {
							String ss = s.substring(s.indexOf("["),
									s.indexOf("]") + 1);
							s = s.replace(ss, "");
						}
						mWords.add(s);
					}

					bufferedReader.close();
					inputStreamReader.close();
					fileInputStream.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					mWords.add("没有歌词文件，赶紧去下载");
				} catch (IOException e) {
					e.printStackTrace();
					mWords.add("没有读取到歌词");
				}
			} else {
				mWords.clear();
				mWords.add("找不到歌词啊");
				mWords.add("by Ting");
			}
		} else {
			mWords.clear();
			mWords.add("自从有了这神器");
			mWords.add("妈妈再也不用担心我唱歌难听了");
			mWords.add("by Ting");
		}
	}

	public static List<String> getWords() {
		return mWords;
	}

	public static List<Integer> getTime() {
		return mTimeList;
	}

	private static int timeHandler(String string) {
		string = string.replace(".", ":");
		String timeData[] = string.split(":");
		int minute = Integer.parseInt(timeData[0]);
		int second = Integer.parseInt(timeData[1]);
		int millisecond = Integer.parseInt(timeData[2]);

		int currentTime = (minute * 60 + second) * 1000 + millisecond * 10;

		return currentTime;
	}

	private static void addTimeToList(String string) {
		Matcher matcher = Pattern.compile(
				"\\[\\d{1,2}:\\d{1,2}([\\.:]\\d{1,2})?\\]").matcher(string);
		if (matcher.find()) {
			String str = matcher.group();
			mTimeList.add(timeHandler(str.substring(1, str.length() - 1)));
		}

	}
}
