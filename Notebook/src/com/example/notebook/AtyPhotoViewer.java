package com.example.notebook;

import java.io.File;

import android.app.Activity;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

public class AtyPhotoViewer extends Activity {

	public static final String EXTRA_PATH = "path";

	private ImageView iv = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		iv = new ImageView(this);
		setContentView(iv);
		
		String path = getIntent().getStringExtra(EXTRA_PATH);
		
		if (path != null) {
			iv.setImageURI(Uri.fromFile(new File(path)));
		} else {
			finish();
		}
	}

}
