package com.example.notebook;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.security.auth.PrivateCredentialPermission;

import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.notebook.db.NoteDB;

public class AtyEditNote extends ListActivity {

	public static final String EXTRA_NOTE_ID = "noteId";
	public static final String EXTRA_NOTE_NAME = "noteName";
	public static final String EXTRA_NOTE_CONTENT = "noteContent";
	public static final int REQUEST_CODE_GET_PICTURE = 1;
	public static final int REQUEST_CODE_GET_VIDEO = 2;

	private int noteId = -1;
	private EditText etName, etContent;
	private MediaAdapter adapter = null;
	private String currentPath = null;
	private NoteDB db = null;
	private SQLiteDatabase dbRead = null;
	private SQLiteDatabase dbWrite = null;
	private OnClickListener btnClickHandler = new OnClickListener() {

		@Override
		public void onClick(View v) {
			
			Intent intent = null;
			File file = null;
			
			switch (v.getId()) {
			case R.id.btnAddPhoto:
				
				intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				file = new File(getMediaDir(), System.currentTimeMillis() + ".jpg");
				
				if (!file.exists()) {
					try {
						file.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				currentPath = file.getAbsolutePath();
				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
				startActivityForResult(intent, REQUEST_CODE_GET_PICTURE);
				break;

			case R.id.btnAddVideo:

				intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
				file = new File(getMediaDir(), System.currentTimeMillis() + ".mp4");
				
				if (!file.exists()) {
					try {
						file.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				currentPath = file.getAbsolutePath();
				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
				startActivityForResult(intent, REQUEST_CODE_GET_VIDEO);
				break;

			case R.id.btnSave:
				saveMedia(saveNote());
				setResult(RESULT_OK);
				finish();
				break;

			case R.id.btnCancel:
				setResult(RESULT_CANCELED);
				finish();
				break;

			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aty_add_note);

		db = new NoteDB(this);
		dbRead = db.getReadableDatabase();
		dbWrite = db.getWritableDatabase();

		adapter = new MediaAdapter(this);
		setListAdapter(adapter);

		etName = (EditText) findViewById(R.id.etName);
		etContent = (EditText) findViewById(R.id.etContent);
		noteId = getIntent().getIntExtra(EXTRA_NOTE_ID, -1);

		if (noteId > -1) {
			etName.setText(getIntent().getStringExtra(EXTRA_NOTE_NAME));
			etContent.setText(getIntent().getStringExtra(EXTRA_NOTE_CONTENT));

			Cursor cursor = dbRead.query(NoteDB.TABLE_NAME_MEDIA, null,
					NoteDB.COLUMN_NAME_MEDIA_OWNER_NOTE_ID + "=?",
					new String[] { noteId + "" }, null, null, null);
			while (cursor.moveToNext()) {
				adapter.add(new MedialistCellData(cursor.getString(cursor
						.getColumnIndex(NoteDB.COLUMN_NAME_MEDIA_PATH)), cursor
						.getInt(cursor.getColumnIndex(NoteDB.COLUMN_NAME_ID))));
			}
			adapter.notifyDataSetChanged();
		}

		findViewById(R.id.btnSave).setOnClickListener(btnClickHandler);
		findViewById(R.id.btnCancel).setOnClickListener(btnClickHandler);
		findViewById(R.id.btnAddPhoto).setOnClickListener(btnClickHandler);
		findViewById(R.id.btnAddVideo).setOnClickListener(btnClickHandler);

	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		MedialistCellData data = adapter.getItem(position);
		Intent intent = null;
		switch (data.type) {
		case MediaType.PICTURE:
			intent = new Intent(this, AtyPhotoViewer.class);
			intent.putExtra(AtyPhotoViewer.EXTRA_PATH, data.path);
			startActivity(intent);
			break;
		
		case MediaType.VIDEO:
			intent = new Intent(this, AtyVideoViewer.class);
			intent.putExtra(AtyVideoViewer.EXTRA_PATH, data.path);
			startActivity(intent);
			break;
			
		default:
			break;
		}
	}

	public void saveMedia(int noteId) {
		MedialistCellData data = null;
		ContentValues cv = null;
		for (int i = 0; i < adapter.getCount(); i++) {
			data = adapter.getItem(i);

			if (data.id <= -1) {
				cv = new ContentValues();
				cv.put(NoteDB.COLUMN_NAME_MEDIA_PATH, data.path);
				cv.put(NoteDB.COLUMN_NAME_MEDIA_OWNER_NOTE_ID, noteId);
				dbWrite.insert(NoteDB.TABLE_NAME_MEDIA, null, cv);
			}
		}
	}
	
	public File getMediaDir() {
		File dir = new File(Environment.getExternalStorageDirectory(), "NoteMedia");
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir;
	}

	public int saveNote() {
		ContentValues cv = new ContentValues();
		cv.put(NoteDB.COLUMN_NAME_NOTE_NAME, etName.getText().toString());
		cv.put(NoteDB.COLUMN_NAME_NOTE_CONTENT, etContent.getText().toString());
		cv.put(NoteDB.COLUMN_NAME_NOTE_DATE, new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss").format(new Date()));

		if (noteId > -1) {
			dbWrite.update(NoteDB.TABLE_NAME_NOTE, cv, NoteDB.COLUMN_NAME_ID
					+ "=?", new String[] { noteId + "" });
			return noteId;
		} else {
			return (int) dbWrite.insert(NoteDB.TABLE_NAME_NOTE, null, cv);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE_GET_PICTURE:
		case REQUEST_CODE_GET_VIDEO:
			if (resultCode == RESULT_OK) {
				adapter.add(new MedialistCellData(currentPath));
				adapter.notifyDataSetChanged();
			}
			break;

		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		dbRead.close();
		dbWrite.close();
		super.onDestroy();
	}

	static class MediaAdapter extends BaseAdapter {

		private Context context = null;
		private List<MedialistCellData> list = new ArrayList<AtyEditNote.MedialistCellData>();

		public MediaAdapter(Context context) {
			this.context = context;
		}

		public void add(MedialistCellData data) {
			list.add(data);
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public MedialistCellData getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.media_list_cell, null);
			}
			MedialistCellData data = getItem(position);

			ImageView ivIcon = (ImageView) convertView
					.findViewById(R.id.ivIcon);
			TextView tvPath = (TextView) convertView.findViewById(R.id.tvPath);

			ivIcon.setImageResource(data.iconId);
			tvPath.setText(data.path);
			return convertView;
		}
	}

	static class MedialistCellData {

		public MedialistCellData(String path) {
			this.path = path;
			if (path.endsWith(".jpg")) {
				iconId = R.drawable.ic_launcher;
				type = MediaType.PICTURE;
			} else if (path.endsWith("mp4")) {
				iconId = R.drawable.ic_launcher;
				type = MediaType.VIDEO;
			}
		}

		public MedialistCellData(String path, int id) {
			this(path);
			this.id = id;
		}
		
		int type = 0;
		int id = -1;
		String path = "";
		int iconId = R.drawable.ic_launcher;
	}
	
	static class MediaType {
		static final int PICTURE = 1;
		static final int VIDEO = 2;
	}
}
