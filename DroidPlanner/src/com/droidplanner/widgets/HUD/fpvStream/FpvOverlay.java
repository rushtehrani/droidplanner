package com.droidplanner.widgets.HUD.fpvStream;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

import com.droidplanner.widgets.HUD.HUDwidget;


public class FpvOverlay {

	// sample public cam
	// String URL =
	// "http://trackfield.webcam.oregonstate.edu/axis-cgi/mjpg/video.cgi?resolution=800x600&amp%3bdummy=1333689998337";
	public final static String URL = "http://192.168.40.143:81/videostream.cgi?user=admin&pwd=&resolution=32&rate=11";

	public enum DisplayMode {
		SIZE_STANDARD, SIZE_BEST_FIT, SIZE_FULLSCREEN
	}

	public DisplayMode displayMode;
	public MjpegInputStream mIn;
	public Bitmap bm;
	private HUDwidget hudWidget;

	public FpvOverlay(DisplayMode displayMode, HUDwidget huDwidget) {
		this.displayMode = displayMode;
		this.hudWidget = huDwidget;
		Log.d("FPV", "constructor");
	}

	public void onSourceReceived(MjpegInputStream source) {
		Log.d("FPV", "setSource");
		mIn = source;
		new MjpegStreamDecoder(){
			@Override
			protected void onProgressUpdate(Bitmap... values) {
				super.onProgressUpdate(values);
				bm = values[0];
				hudWidget.onDroneUpdate();
			}
			
		}.execute(mIn);
	}

	public void drawFPV(Canvas canvas, int canvasWidth, int canvasHeight) {
		if (isEnabled()) {
				Log.d("FPV", "onDrawFPV");		
				Rect destRect = destRect(bm, canvasWidth, canvasHeight);
				canvas.drawBitmap(bm, null, destRect, null);
		}
	}

	public boolean isEnabled() {
		if (mIn != null) {
			if (bm!=null) {
				return true;				
			}
		}
		return false;
	}

	private Rect destRect(Bitmap picture, int canvasWidth, int canvasHeight) {
		int bmw = picture.getWidth();
		int bmh = picture.getHeight();

		switch (displayMode) {
		default:
		case SIZE_STANDARD:
			return new Rect(-(bmw / 2), -(bmh / 2), (bmw / 2), (bmh / 2));
		case SIZE_BEST_FIT:
			float bmasp = (float) bmw / (float) bmh;
			bmw = canvasWidth;
			bmh = (int) (canvasWidth / bmasp);
			if (bmh > canvasHeight) {
				bmh = canvasHeight;
				bmw = (int) (canvasHeight * bmasp);
			}
			return new Rect(-(bmw / 2), -(bmh / 2), (bmw / 2), (bmh / 2));
		case SIZE_FULLSCREEN:
			return new Rect(-canvasWidth / 2, -canvasHeight / 2,
					canvasWidth / 2, canvasHeight / 2);
		}
	}

	public void startStreaming(String URL) {
		new MjpegStreamBuilder() {
			@Override
			public void setSource(MjpegInputStream result) {
				onSourceReceived(result);
			}
		}.execute(URL);
	}
}
