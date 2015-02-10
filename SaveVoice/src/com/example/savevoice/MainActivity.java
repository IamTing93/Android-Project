package com.example.savevoice;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	private Button btnPlayAndStopButton = null;
	private ListView lvShowRecord = null;

	private ArrayAdapter<String> adapter = null;
	private List<String> list = null;
	private MusicPlayer player = null;
	private Intent intent = null;
	private MusicRecorder recorder = null;
	private FileManager fileManager = null;
	private SecondService secondService = null;

	// This is used to update the UI when the data changes.
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			List<String> lists = (List<String>) msg.obj;
			int len = lists.size();
			adapter.clear();
			for (int i = len - 1; i >= 0; i--) {
				String str = lists.get(i).split("/")[lists.get(i).split("/").length - 1]
						.split("\\.")[0];
				adapter.add(str);
				adapter.notifyDataSetChanged();
				
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();
		startService();
		setEvent();

	}

	// Initialize the variables.
	private void init() {
		player = new MusicPlayer();
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1);
		btnPlayAndStopButton = (Button) findViewById(R.id.btnPlayAndStop);
		lvShowRecord = (ListView) findViewById(R.id.lvShowRecord);
		btnPlayAndStopButton.getBackground().setAlpha(120);

		// Update the UI.
		fileManager = new FileManager();
		list = fileManager.getAllFilesPath(FileManager.DIR_PATH);
		lvShowRecord.setAdapter(adapter);
		Message msg = new Message();
		msg.obj = list;
		handler.sendMessage(msg);
	}

	private void startService() {
		intent = new Intent(MainActivity.this, SecondService.class);
		bindService(intent, sc, Context.BIND_AUTO_CREATE);
	}

	// Set the service connection.
	private ServiceConnection sc = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
		}

		@Override
		public void onServiceConnected(ComponentName arg0, IBinder arg1) {
			Log.v("Connection", "connected");
			secondService = ((SecondService.SecondServiceBinder) arg1).getService();
			recorder = secondService.getRecorder();
		}
	};

	private void setEvent() {
		btnPlayAndStopButton.setOnClickListener(this);

		// If click item, it will play the record.
		lvShowRecord.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, final View view,
					final int position, long id) {
				if (recorder.getIsBuild()) {
					Toast.makeText(MainActivity.this, "录音中，别乱按",
							Toast.LENGTH_LONG).show();
				} else {
					new AlertDialog.Builder(MainActivity.this)
							.setTitle("提示")
							.setMessage("确定播放?")
							.setPositiveButton("确定",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											String path = FileManager.DIR_PATH + "/" + ((TextView) view).getText() + ".3gp";
											AlertDialog alertDialog = new AlertDialog.Builder(
													MainActivity.this)
													.setPositiveButton(
															"停止",
															new DialogInterface.OnClickListener() {

																@Override
																public void onClick(
																		DialogInterface dialog,
																		int which) {
																	player.stopPlaying();
																}
															}).create();
											player.startPlaying(path);
											player.setEvent(alertDialog);
											alertDialog.show();
										}

									}).setNegativeButton("取消", null).create()
							.show();
				}
			}
		});

		// If click item long, it will delete the file.
		lvShowRecord.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				final int posTmp = position;
				if (!recorder.getIsBuild()) {
					new AlertDialog.Builder(MainActivity.this)
							.setTitle("提示")
							.setMessage("确定删除？")
							.setPositiveButton("确定",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											String path = list.get(list.size() - 1 - posTmp);
											Log.v("Delete File:", path);
											fileManager.deleteFile(path);
											list.remove(list.size() - 1 - posTmp);
											Message msg = new Message();
											msg.obj = list;
											handler.sendMessage(msg);
										}
									}).setNegativeButton("取消", null).create()
							.show();
				} else {
					Toast.makeText(MainActivity.this, "录音中，别乱按",
							Toast.LENGTH_LONG).show();
				}
				return true;
			}
		});
	}

	/**
	 * Determine whether it plays or stops. And update the UI.
	 */
	@Override
	public void onClick(View v) {

		if (!recorder.getIsBuild()) {
			
			if (player != null) {
				player.stopPlaying();
			}
			
			String path = getPath();
			btnPlayAndStopButton.setText("停止录音");
			btnPlayAndStopButton.setBackgroundResource(R.drawable.btn_style_stop);
			recorder.startRecording(path);
			list.add(path);
			Message msg = new Message();
			msg.obj = list;
			handler.sendMessage(msg);
		} else {
			btnPlayAndStopButton.setText("开始录音");
			btnPlayAndStopButton.setBackgroundResource(R.drawable.btn_style_start);
			recorder.stopRecording();
		}
	}

	/**
	 * Generate the file path.
	 * 
	 * @return The path of the file that will be builded.
	 */
	@SuppressLint("SimpleDateFormat")
	private String getPath() {
		String path = FileManager.DIR_PATH;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
		Date currentDate = new Date(System.currentTimeMillis());
		String str = formatter.format(currentDate);
		return path + "/Recorder" + str + ".3gp";
	}

	// Unbind the service if it quits.
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(sc);
		player.stopPlaying();
	}

	long lastTime = 0;

	@Override
	public void onBackPressed() {
		
		long currentTime = System.currentTimeMillis();
		if (currentTime - lastTime > 3000) {
			Toast.makeText(MainActivity.this, "再按一次返回键退出", Toast.LENGTH_SHORT)
					.show();
			lastTime = System.currentTimeMillis();
		} else {
			finish();
		}
	}
}
