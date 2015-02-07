package com.example.savevoice;

import java.io.IOException;

import android.media.MediaRecorder;

public class MusicRecorder {

	private MediaRecorder mediaRecorder = null;
	// Note whether the recorder is builded.
	private boolean isBuild = false;

	public boolean startRecording(String path) {
		if (mediaRecorder == null) {
			mediaRecorder = new MediaRecorder();
			mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			mediaRecorder.setOutputFile(path);
			mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			try {
				mediaRecorder.prepare();
				mediaRecorder.start();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			isBuild = true;
			return true;
		} else {
			return false;
		}
	}
	
	public void stopRecording() {
		if (mediaRecorder != null) {
			mediaRecorder.stop();
			mediaRecorder.release();
			mediaRecorder = null;
			isBuild = false;
		}
	}
	
	public boolean getIsBuild() {
		return isBuild;
	}
}
