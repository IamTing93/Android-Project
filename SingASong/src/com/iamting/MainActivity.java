package com.iamting;

/**
 * Reference: http://www.it165.net/pro/html/201406/16223.html
 */
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.singer.R;
import com.iamting.audiomanager.LrcHandle;
import com.iamting.audiomanager.MusicPlayer;
import com.iamting.audiomanager.MusicRecorder;
import com.iamting.audiomanager.WordView;
import com.iamting.sdcardmanager.FileManager;

public class MainActivity extends FragmentActivity implements
		OnGestureListener, OnClickListener {

	private FileManager fileManager = null;
	private String musicPath = null;
	private String LRCPath = null;
	private WordView mWordView = null;
	private List<Integer> mTimeList = null;

	public static Fragment[] fragments = null;
	public static LinearLayout[] linearLayouts = null;
	public static TextView[] textViews = null;
	public static GestureDetector detector = null;
	public int MARK = 0;
	private final int MIN_DISTANCE = 300;

	private List<String> list = null;
	private ArrayAdapter<String> adapter = null;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			adapter.clear();
			list = (List<String>) msg.obj;
			for (String item : list) {
				String[] tmp = item.split("/");
				adapter.add(tmp[tmp.length - 1]);
			}
			adapter.notifyDataSetChanged();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		fileManager = new FileManager();

		setFrament();
		setLinearLayouts();
		initView();
		detector = new GestureDetector(this, this);
	}

	private void initView() {
		setTextView();
		setBtnEvent();
		setListView(1, FileManager.MUSIC_DIR_PATH);
		setListView(2, FileManager.RECORD_DIR_PATH);
	}

	private void setFrament() {
		fragments = new Fragment[3];
		fragments[0] = getSupportFragmentManager().findFragmentById(
				R.id.fragLRC);
		fragments[1] = getSupportFragmentManager().findFragmentById(
				R.id.fragMusic);
		fragments[2] = getSupportFragmentManager().findFragmentById(
				R.id.fragRecord);

		getSupportFragmentManager().beginTransaction().hide(fragments[0])
				.hide(fragments[1]).hide(fragments[2]).show(fragments[0])
				.commit();

	}

	private void setLinearLayouts() {
		linearLayouts = new LinearLayout[3];
		linearLayouts[0] = (LinearLayout) findViewById(R.id.lay1);
		linearLayouts[1] = (LinearLayout) findViewById(R.id.lay2);
		linearLayouts[2] = (LinearLayout) findViewById(R.id.lay3);
	}

	private void setTextView() {
		textViews = new TextView[3];
		textViews[0] = (TextView) findViewById(R.id.fragtxt1);
		textViews[1] = (TextView) findViewById(R.id.fragtxt2);
		textViews[2] = (TextView) findViewById(R.id.fragtxt3);
		mWordView = (WordView) fragments[0].getActivity().findViewById(
				R.id.tvDispLRC);
		textViews[0]
				.setTextColor(getResources().getColor(R.color.lightseablue));
	}

	private void setBtnEvent() {
		fragments[0].getActivity().findViewById(R.id.btnStart)
				.setOnClickListener(this);
		fragments[0].getActivity().findViewById(R.id.btnStop)
				.setOnClickListener(this);
	}

	private void setListView(int index, String path) {
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1);
		list = fileManager.getAllFilesPath(path);
		for (String item : list) {
			String[] tmp = item.split("/");
			adapter.add(tmp[tmp.length - 1].split("\\.")[0]);
		}
		int id = (index == 1) ? R.id.listMusic : R.id.listRecord;
		ListView lv = (ListView) fragments[index].getActivity()
				.findViewById(id);
		lv.setAdapter(adapter);

		switch (index) {
		case 1:
			lv.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, final View view,
						int position, long id) {
					new AlertDialog.Builder(MainActivity.this)
							.setMessage("你想...")
							.setPositiveButton("唱歌",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											getSupportFragmentManager()
													.beginTransaction()
													.hide(fragments[0])
													.hide(fragments[1])
													.hide(fragments[2])
													.setTransition(
															FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
													.show(fragments[0])
													.commit();
											textViews[0]
													.setTextColor(getResources()
															.getColor(
																	R.color.lightseablue));
											textViews[1]
													.setTextColor(getResources()
															.getColor(
																	R.color.black));
											MARK = 0;
											String name = (String) ((TextView) view)
													.getText();
											String recordPath = FileManager.RECORD_DIR_PATH
													+ "/" + name + ".3gp";
											musicPath = FileManager.MUSIC_DIR_PATH
													+ "/" + name + ".mp3";
											MusicRecorder
													.startRecording(recordPath);
											MusicPlayer
													.statrtPlaying(musicPath);
											LRCPath = FileManager.LYRIC_DIR_PATH
													+ "/" + name + ".lrc";
											LrcHandle.readLRC(LRCPath);
											showLRC();

											list.add(recordPath);
											Message msg = new Message();
											msg.obj = list;
											handler.sendMessage(msg);
										}
									})
							.setNeutralButton("听歌",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											getSupportFragmentManager()
													.beginTransaction()
													.hide(fragments[0])
													.hide(fragments[1])
													.hide(fragments[2])
													.setTransition(
															FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
													.show(fragments[0])
													.commit();
											textViews[0]
													.setTextColor(getResources()
															.getColor(
																	R.color.lightseablue));
											textViews[1]
													.setTextColor(getResources()
															.getColor(
																	R.color.black));
											MARK = 0;
											musicPath = FileManager.MUSIC_DIR_PATH
													+ "/"
													+ ((TextView) view)
															.getText() + ".mp3";
											LRCPath = FileManager.LYRIC_DIR_PATH
													+ "/"
													+ ((TextView) view)
															.getText() + ".lrc";
											LrcHandle.readLRC(LRCPath);
											MusicPlayer.onPlay(musicPath, true);
											showLRC();
										}
									}).setNegativeButton("没事", null).create()
							.show();
				}
			});
			break;

		case 2:
			lv.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, final View view,
						final int position, long id) {
					new AlertDialog.Builder(MainActivity.this)
							.setMessage("你想...")
							.setNeutralButton("毁灭之",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											String name = (String) ((TextView) view)
													.getText();
											String recordPath = FileManager.RECORD_DIR_PATH
													+ "/" + name + ".3gp";
											fileManager.deleteFile(recordPath);
											list.remove(position);
											Message msg = new Message();
											msg.obj = list;
											handler.sendMessage(msg);
										}
									})
							.setPositiveButton("听歌",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											getSupportFragmentManager()
													.beginTransaction()
													.hide(fragments[0])
													.hide(fragments[1])
													.hide(fragments[2])
													.setTransition(
															FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
													.show(fragments[0])
													.commit();
											textViews[0]
													.setTextColor(getResources()
															.getColor(
																	R.color.lightseablue));
											textViews[2]
													.setTextColor(getResources()
															.getColor(
																	R.color.black));
											MARK = 0;
											musicPath = FileManager.RECORD_DIR_PATH
													+ "/"
													+ ((TextView) view)
															.getText() + ".mp3";
											MusicPlayer.onPlay(musicPath, true);
											LRCPath = FileManager.LYRIC_DIR_PATH
													+ "/"
													+ ((TextView) view)
															.getText() + ".lrc";
											LrcHandle.readLRC(LRCPath);
											showLRC();
										}
									}).setNegativeButton("没事", null).create()
							.show();

				}
			});
			break;
		}
	}

	public void layoutOnclick(View v) {
		resetlaybg();
		switch (v.getId()) {
		case R.id.lay1:
			getSupportFragmentManager().beginTransaction().hide(fragments[0])
					.hide(fragments[1]).hide(fragments[2])
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
					.show(fragments[0]).commit();
			MARK = 0;
			textViews[0].setTextColor(getResources().getColor(
					R.color.lightseablue));
			break;

		case R.id.lay2:
			getSupportFragmentManager().beginTransaction().hide(fragments[0])
					.hide(fragments[1]).hide(fragments[2])
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
					.show(fragments[1]).commit();
			MARK = 1;
			textViews[1].setTextColor(getResources().getColor(
					R.color.lightseablue));
			break;

		case R.id.lay3:
			getSupportFragmentManager().beginTransaction().hide(fragments[0])
					.hide(fragments[1]).hide(fragments[2])
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
					.show(fragments[2]).commit();
			MARK = 2;
			textViews[2].setTextColor(getResources().getColor(
					R.color.lightseablue));
			break;
		}
	}

	private void resetlaybg() {
		for (int i = 0; i < 3; i++) {
			textViews[i].setTextColor(getResources().getColor(R.color.black));
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return detector.onTouchEvent(event);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {

	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		resetlaybg();
		if (MARK == 0) {
			if (e1.getX() > e2.getX() + MIN_DISTANCE) {
				getSupportFragmentManager()
						.beginTransaction()
						.hide(fragments[0])
						.hide(fragments[1])
						.hide(fragments[2])
						.setTransition(
								FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
						.show(fragments[1]).commit();
				MARK = 1;
				textViews[1].setTextColor(getResources().getColor(
						R.color.lightseablue));
			} else {
				textViews[0].setTextColor(getResources().getColor(
						R.color.lightseablue));
			}
		} else if (MARK == 1) {
			if (e1.getX() > e2.getX() + MIN_DISTANCE) {
				getSupportFragmentManager()
						.beginTransaction()
						.hide(fragments[0])
						.hide(fragments[1])
						.hide(fragments[2])
						.setTransition(
								FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
						.show(fragments[2]).commit();
				MARK = 2;
				textViews[2].setTextColor(getResources().getColor(
						R.color.lightseablue));
			} else if (e2.getX() > e1.getX() + MIN_DISTANCE) {
				getSupportFragmentManager()
						.beginTransaction()
						.hide(fragments[0])
						.hide(fragments[1])
						.hide(fragments[2])
						.setTransition(
								FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
						.show(fragments[0]).commit();
				MARK = 0;
				textViews[0].setTextColor(getResources().getColor(
						R.color.lightseablue));
			} else {
				textViews[1].setTextColor(getResources().getColor(
						R.color.lightseablue));
			}
		} else if (MARK == 2) {
			if (e2.getX() > e1.getX() + MIN_DISTANCE) {
				getSupportFragmentManager()
						.beginTransaction()
						.hide(fragments[0])
						.hide(fragments[1])
						.hide(fragments[2])
						.setTransition(
								FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
						.show(fragments[1]).commit();
				MARK = 1;
				textViews[1].setTextColor(getResources().getColor(
						R.color.lightseablue));
			} else {
				textViews[2].setTextColor(getResources().getColor(
						R.color.lightseablue));
			}
		}
		return false;
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btnStart:
			if (musicPath != null) {
				LrcHandle.readLRC(LRCPath);
				showLRC();
				MusicPlayer.statrtPlaying(musicPath);
			} else {
				Toast.makeText(MainActivity.this, "先选择你要唱的歌...",
						Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.btnStop:
			MusicPlayer.stopPlaying();
			MusicRecorder.stopRecordering();
			Toast.makeText(MainActivity.this, "跪了不要问点解，因为本程序猿也跪了",
					Toast.LENGTH_SHORT).show();
			break;
		}
	}

	private void showLRC() {
		mTimeList = LrcHandle.getTime();
		mWordView.setLrc(LRCPath);
		final Handler handler2 = new Handler();
		new Thread(new Runnable() {

			int i = 0;

			@Override
			public void run() {
				while (MusicPlayer.mediaPlayer != null && mTimeList.size() > 0
						&& MusicPlayer.mediaPlayer.isPlaying()) {
					handler2.post(new Runnable() {

						@Override
						public void run() {
							mWordView.invalidate();
						}
					});
					try {
						Thread.sleep(mTimeList.get(i + 1) - mTimeList.get(i));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					i++;
					if (i == mTimeList.size() - 1) {
						MusicPlayer.stopPlaying();
						break;
					}
				}
			}
		}).start();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		MusicPlayer.stopPlaying();
		MusicRecorder.stopRecordering();
	}

}
