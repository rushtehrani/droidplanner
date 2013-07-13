package com.droidplanner.fragments;



import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.droidplanner.R;
import com.droidplanner.activitys.SuperActivity;
import com.droidplanner.widgets.FPV.MjpegInputStream;
import com.droidplanner.widgets.FPV.MjpegStreamBuilder;
import com.droidplanner.widgets.HUD.FpvHudWidget;
import com.droidplanner.widgets.HUD.FpvOverlay;

public class FpvHudFragment extends Fragment {

	private FpvHudWidget fpvHudWidget;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fpv_hud_fragment, container,false);
		fpvHudWidget = (FpvHudWidget) view.findViewById(R.id.fpvHudWidget);
		fpvHudWidget.setDrone(((SuperActivity)getActivity()).app.drone);
		fpvHudWidget.onDroneUpdate();
		
		new MjpegStreamBuilder() {
			@Override
			public void setSource(MjpegInputStream result) {
				fpvHudWidget.fpvOverlay.setSource(result);
				Log.d("FPV", "aquireSource");
			}
		}.execute(FpvOverlay.URL);

		return view;
	}
}
