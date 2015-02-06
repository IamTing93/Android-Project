package com.iamting.audiomanager;

import java.io.IOException;

import android.media.MediaPlayer;

/**
 * 
 * @author Ting
 *
 */
public class MusicPlayer {
	
	public static MediaPlayer mediaPlayer = null;
	
	/**
	 * Construct a player and play.
	 * @param path: The path of the file that will be played.
	 */
	public static void statrtPlaying(String path) {
		if (mediaPlayer == null)  {
			mediaPlayer = new MediaPlayer();
			try {
				mediaPlayer.setDataSource(path);
				mediaPlayer.prepare();
				mediaPlayer.start();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Stop playing music.
	 */
	public static void stopPlaying() {
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
		}
	}
	
	/**
	 * @param path: The path of the file that will be played.
	 * @param start: Determined whether it plays or not.
	 */
	public static void onPlay(String path, boolean start) {
		if (start && path != null) {
			statrtPlaying(path);
		} else {
			stopPlaying();
		}
	}
	
}
