package com.droidplanner.activitys;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.MediaController;
import android.widget.VideoView;

import com.droidplanner.R;

public class FPVActivity extends SuperActivity {

	@Override
	int getNavigationItem() {
		return 6;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		VideoView vv = new VideoView(this);
        setContentView(vv);        
        vv.setVideoPath("http://192.168.40.125:8080/");
        vv.setMediaController(new MediaController(this));        

	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
			default:
			return super.onMenuItemSelected(featureId, item);
		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_fpv, menu);
		return super.onCreateOptionsMenu(menu);
	}
		
}