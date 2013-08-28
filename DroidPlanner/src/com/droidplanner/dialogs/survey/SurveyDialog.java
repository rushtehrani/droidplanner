package com.droidplanner.dialogs.survey;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Spinner;
import android.widget.Toast;

import com.droidplanner.R;
import com.droidplanner.drone.variables.waypoint;
import com.droidplanner.file.IO.CameraInfo;
import com.droidplanner.file.IO.CameraInfoReader;
import com.droidplanner.file.help.CameraInfoLoader;
import com.droidplanner.polygon.Polygon;
import com.droidplanner.survey.SurveyData;
import com.droidplanner.survey.grid.Grid;
import com.droidplanner.survey.grid.GridBuilder;
import com.droidplanner.widgets.SeekBarWithText.SeekBarWithText.OnTextSeekBarChangedListner;
import com.droidplanner.widgets.spinners.SpinnerSelfSelect.OnSpinnerItemSelectedListener;
import com.google.android.gms.maps.model.LatLng;

public abstract class SurveyDialog implements DialogInterface.OnClickListener,
		OnTextSeekBarChangedListner, OnSpinnerItemSelectedListener, OnClickListener {
	public abstract void onPolygonGenerated(List<waypoint> list);

	Polygon polygon;
	private LatLng originPoint;

	public SurveyData surveyData;
	private CameraInfoLoader avaliableCameras;
	public SurveyDialogViews views;
	Grid grid;

	public void generateSurveyDialog(Polygon polygon, double defaultHatchAngle,
			LatLng lastPoint, double defaultAltitude, Context context) {
		this.polygon = polygon;
		this.originPoint = lastPoint;
		views = new SurveyDialogViews(this,context);
		

		avaliableCameras = new CameraInfoLoader(this.views.context);

		if (checkIfPolygonIsValid(polygon)) {
			Toast.makeText(context, "Invalid Polygon", Toast.LENGTH_SHORT)
					.show();
			return;
		}
		

		AlertDialog dialog = views.buildDialog();

		surveyData = new SurveyData(Math.floor(defaultHatchAngle), defaultAltitude);

		views.cameraSpinner.setOnSpinnerItemSelectedListener(this);
		views.updateCameraSpinner(avaliableCameras.getCameraInfoList());

		dialog.show();
	}

	@Override
	public void onSeekBarChanged() {
		surveyData.update(views.angleView.getValue(), views.altitudeView.getValue(),
				views.overlapView.getValue(), views.sidelapView.getValue());
		
		GridBuilder gridBuilder = new GridBuilder(polygon, surveyData, originPoint);
		grid = gridBuilder.generate();				
		
		views.updateViews();		
	}

	private boolean checkIfPolygonIsValid(Polygon polygon) {
		return !polygon.isValid();
	}

	@Override
	public void onClick(DialogInterface arg0, int which) {
		if (which == Dialog.BUTTON_POSITIVE) {
			List<waypoint> result = grid.getWaypoints(surveyData.getAltitude());
			onPolygonGenerated(result);
		}
	}	

	@Override
	public void onSpinnerItemSelected(Spinner spinner, int position, String text) {
		CameraInfo cameraInfo;
		try {
			cameraInfo = avaliableCameras.openFile(text);
		} catch (Exception e) {
			Toast.makeText(views.context,
					views.context.getString(R.string.error_when_opening_file),
					Toast.LENGTH_SHORT).show();
			cameraInfo = CameraInfoReader.getNewMockCameraInfo();
		}
		surveyData.setCameraInfo(cameraInfo);
		views.updateSeekBarsValues(this);
		onSeekBarChanged();
	}
	
	@Override
	public void onClick(View view) {
		if (view.equals(views.innerWPsCheckbox)) {
			surveyData.setInnerWpsState(views.innerWPsCheckbox.isChecked());
		}
		
	}

}
