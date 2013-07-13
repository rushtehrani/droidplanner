package com.droidplanner.widgets.HUD.fpvStream;

import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;


public class FpvOverlay {

	// sample public cam
	// String URL =
	// "http://trackfield.webcam.oregonstate.edu/axis-cgi/mjpg/video.cgi?resolution=800x600&amp%3bdummy=1333689998337";
	public final static String URL = "http://192.168.40.143:81/videostream.cgi?user=admin&pwd=&resolution=32&rate=10";

	public enum DisplayMode {
		SIZE_STANDARD, SIZE_BEST_FIT, SIZE_FULLSCREEN
	}

	public DisplayMode displayMode;
	public MjpegInputStream mIn;
	public Bitmap bm;

	public FpvOverlay(DisplayMode displayMode) {
		this.displayMode = displayMode;
	}

	public void setSource(MjpegInputStream source) {
		Log.d("FPV", "setSource");
		mIn = source;
	}

	public void drawFPV(Canvas canvas, int canvasWidth, int canvasHeight) {
		if (isEnabled()) {
			try {
				bm = mIn.readMjpegFrame();
				Rect destRect = destRect(bm, canvasWidth, canvasHeight);
				canvas.drawBitmap(bm, null, destRect, null);
				bm.recycle();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean isEnabled() {
		return mIn != null;
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
				setSource(result);
			}
		}.execute(URL);
	}
}
