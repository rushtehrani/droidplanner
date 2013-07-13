package com.droidplanner.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.droidplanner.R;
import com.droidplanner.activitys.SuperActivity;
import com.droidplanner.widgets.HUD.HUDwidget;
import com.droidplanner.widgets.HUD.fpvStream.FpvOverlay;
import com.droidplanner.widgets.HUD.fpvStream.MjpegInputStream;
import com.droidplanner.widgets.HUD.fpvStream.MjpegStreamBuilder;


public class HudFragment extends Fragment{

	private HUDwidget hudWidget;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.hud_fragment, container, false);
		hudWidget = (HUDwidget) view.findViewById(R.id.hudWidget);
		hudWidget.setDrone(((SuperActivity)getActivity()).app.drone);
		hudWidget.onDroneUpdate();
				
		new MjpegStreamBuilder() {
			@Override
			public void setSource(MjpegInputStream result) {
				hudWidget.fpvOverlay.setSource(result);
				Log.d("FPV", "aquireSource");
			}
		}.execute(FpvOverlay.URL);
		return view;
	}
}
