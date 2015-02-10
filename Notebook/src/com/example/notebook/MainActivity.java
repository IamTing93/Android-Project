package com.example.notebook;

import com.example.notebook.db.NoteDB;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MainActivity extends ListActivity {

	public static final int REQUEST_CODE_ADD_NOTE = 1;
	public static final int REQUEST_CODE_EDIT_NOTE = 2;
	
	private SimpleCursorAdapter adapter = null;
	private NoteDB db = null;
	private SQLiteDatabase dbRead = null;
	
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		db = new NoteDB(this);
		dbRead = db.getReadableDatabase();
		
		adapter = new SimpleCursorAdapter(this, R.layout.notes_list_cell, null, new String[]{NoteDB.COLUMN_NAME_NOTE_NAME, NoteDB.COLUMN_NAME_NOTE_DATE}, new int[]{R.id.tvName, R.id.tvDate});
		
		setListAdapter(adapter);
		
		refreshNoteListView();
		
		findViewById(R.id.btnAddNote).setOnClickListener(btnAddNote_clickHandler);
	}

	public void refreshNoteListView() {
		adapter.changeCursor(dbRead.query(NoteDB.TABLE_NAME_NOTE, null, null, null, null, null, null));
	}
	
	private OnClickListener btnAddNote_clickHandler = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			startActivityForResult(new Intent(MainActivity.this, AtyEditNote.class), REQUEST_CODE_ADD_NOTE);
		}
	};
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE_ADD_NOTE:
		case REQUEST_CODE_EDIT_NOTE:
			if (resultCode == Activity.RESULT_OK) {
				refreshNoteListView();
			}
			break;
			

		default:
			break;
		}
	};
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		Cursor cursor = adapter.getCursor();
		cursor.moveToPosition(position);
		
		Intent intent = new Intent(MainActivity.this, AtyEditNote.class);
		intent.putExtra(AtyEditNote.EXTRA_NOTE_ID, cursor.getInt(cursor.getColumnIndex(NoteDB.COLUMN_NAME_ID)));
		intent.putExtra(AtyEditNote.EXTRA_NOTE_NAME, cursor.getString(cursor.getColumnIndex(NoteDB.COLUMN_NAME_NOTE_NAME)));
		intent.putExtra(AtyEditNote.EXTRA_NOTE_CONTENT, cursor.getString(cursor.getColumnIndex(NoteDB.COLUMN_NAME_NOTE_CONTENT)));
		startActivityForResult(intent, REQUEST_CODE_EDIT_NOTE);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
