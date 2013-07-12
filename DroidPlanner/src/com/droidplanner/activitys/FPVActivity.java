package com.droidplanner.activitys;


import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.droidplanner.R;
import com.droidplanner.widgets.FPV.MjpegView;

public class FPVActivity extends SuperActivity {

	private MjpegView vv;


	@Override
	int getNavigationItem() {
		return 6;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.fpv);
		
		vv = (MjpegView) findViewById(R.id.mjpegView1);
	}

	public void onPause() {
		super.onPause();
		vv.stopPlayback();
	}

	
}