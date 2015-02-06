package com.iamting.audiomanager;

import java.io.IOException;

import android.media.MediaRecorder;
 	/**
 	 * 
 	 * @author Ting
 	 * This is the audio recorder.
 	 */
public class MusicRecorder {
	
	private static MediaRecorder mediaRecorder = null;
	
	/**
	 * Define a recorder and start to record.
	 * @param path: The path of the file that will be builded.
	 */
	public static void startRecording(String path) {
		if (mediaRecorder == null) {
		mediaRecorder = new MediaRecorder();
		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		mediaRecorder.setOutputFile(path);
		mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		try {
			mediaRecorder.prepare();
		} catch (IllegalStateException | IOException e) {
			e.printStackTrace();
		}
		mediaRecorder.start();
		}
	}
	
	/**
	 * Stop recording. Note that it will release the recourse.
	 */
	public static void stopRecordering() {
		if (mediaRecorder != null) {
			mediaRecorder.stop();
			mediaRecorder.release();
			mediaRecorder = null;
		}
	}
	
	/**
	 * This interface decides which operation should be token.
	 * @param path: The path of the file that will be builded.
	 * @param start: Whether it will start to record or not.
	 * @return 
	 */
	public static void onRecord(String path, boolean start) {
		if (start && path != null) {
			startRecording(path);
		} else {
			stopRecordering();
		}
	}
}
