package com.example.savevoice;

import java.io.IOException;

import android.app.AlertDialog;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

public class MusicPlayer {
	
	private MediaPlayer mediaPlayer = null;
	
	public void startPlaying(String path) {
		if (mediaPlayer == null) {
			mediaPlayer = new MediaPlayer();
			try {
				mediaPlayer.setDataSource(path);
				mediaPlayer.prepare();
				mediaPlayer.start();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void stopPlaying() {
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
		}
	}

	
	/**
	 * Close the box when the music is end.
	 * @param alertDialog: The dialog box that will be closed.
	 */
	public void setEvent(final AlertDialog alertDialog) {
		if (mediaPlayer != null) {
			mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
				
				@Override
				public void onCompletion(MediaPlayer mp) {
					alertDialog.dismiss();
					stopPlaying();
				}
			});
		}
	}
}
