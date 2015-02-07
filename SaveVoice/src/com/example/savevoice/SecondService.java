package com.example.savevoice;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;


public class SecondService extends Service {

	private MusicRecorder recorder = null;
	
	@Override
	public IBinder onBind(Intent intent) {
		Log.v("onBind", "onBind");
		return new SecondServiceBinder();
	}
	
	@Override
	public void onCreate() {
		recorder = new MusicRecorder();
		super.onCreate();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		recorder.stopRecording();
		Log.v("ServiceDestroy", "onDestroy");
	}
	
	public class SecondServiceBinder extends Binder {
		SecondService getService() {
			return SecondService.this;
		}
	}
	
	public MusicRecorder getRecorder() {
		return recorder;
	}
}