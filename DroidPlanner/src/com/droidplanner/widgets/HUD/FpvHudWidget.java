package com.droidplanner.widgets.HUD;

import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;

import com.droidplanner.widgets.FPV.MjpegInputStream;

public class FpvHudWidget extends HUDwidget {
	public enum DisplayMode {
		SIZE_STANDARD,SIZE_BEST_FIT,SIZE_FULLSCREEN
	}
	
    private MjpegInputStream mIn = null; 
    private Bitmap bm;
    private Paint p = new Paint();
	private DisplayMode displayMode = DisplayMode.SIZE_BEST_FIT;

	public FpvHudWidget(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		if (drone == null){
			return;
		}
		
		// clear screen
		canvas.drawColor(Color.rgb(20, 20, 20));
		canvas.translate(width / 2, attHeightPx / 2 + yawHeightPx);		// set center of HUD excluding YAW area

		//from now on each drawing routine has to undo all applied transformations, clippings, etc by itself
		//this will improve performance because not every routine applies that stuff, so general save and restore 
		//is not necessary
		drawPitch(canvas);
		drawRoll(canvas);
		drawYaw(canvas);
		drawPlane(canvas);
		drawRightScroller(canvas);
		drawLeftScroller(canvas);
		drawAttitudeInfoText(canvas);
		drawFailsafe(canvas);
	}
	
	@Override
	protected void drawPitch(Canvas canvas) {
		double pitch = drone.getPitch();
		double roll = drone.getRoll();
		
		if (hudDebug) {
			pitch = hudDebugPitch;
			roll = hudDebugRoll;
		}
		
		int pitchOffsetPx = (int) (pitch * pitchPixPerDegree);
		int rollTriangleBottom = -attHeightPx / 2 + rollTopOffsetPx / 2 + rollTopOffsetPx;
		

		// Draw the background
		try {
			if (mIn != null) {
				bm = mIn.readMjpegFrame();
                Rect destRect = destRect(bm);
				canvas.drawBitmap(bm, null, destRect, p);				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		canvas.rotate(-(int) roll);
		
		//canvas.drawRect(-width, pitchOffsetPx, width, 2 * height /* Go plenty low */, ground);
		//canvas.drawRect(-width, -2 * height /* Go plenty high */, width, pitchOffsetPx, sky);
		canvas.drawLine(-width, pitchOffsetPx, width, pitchOffsetPx, whiteThinTics);
		
		// Draw roll triangle
		Path arrow = new Path();
		int tempOffset = Math.round(plane.getStrokeWidth() + whiteBorder.getStrokeWidth() / 2);
		arrow.moveTo(0, -attHeightPx / 2 + rollTopOffsetPx + tempOffset);
		arrow.lineTo(0 - rollTopOffsetPx / 3, rollTriangleBottom + tempOffset);
		arrow.lineTo(0 + rollTopOffsetPx / 3, rollTriangleBottom + tempOffset);
		arrow.close();
		canvas.drawPath(arrow, plane);
		
		// Draw gauge
		int yPos;
		for (int i = -180; i <= 180; i += 5) {
			yPos = Math.round(-i * pitchPixPerDegree + pitchOffsetPx);
			if ((yPos < -rollTriangleBottom) && (yPos > rollTriangleBottom) && (yPos != pitchOffsetPx)) {
				if (i % 2 == 0) {
					canvas.drawLine(-pitchScaleWideHalfWidth, yPos, pitchScaleWideHalfWidth, yPos, whiteThinTics);
					canvas.drawText(i + "", -pitchScaleWideHalfWidth - pitchScaleTextXOffset, yPos - pitchTextCenterOffsetPx, pitchText);
				}
				else
					canvas.drawLine(-pitchScaleNarrowHalfWidth, yPos, pitchScaleNarrowHalfWidth, yPos, whiteThinTics);
			}
		}

		canvas.rotate((int) roll);
	}
	
    private Rect destRect(Bitmap picture) {
    	int bmw = picture.getWidth();
		int bmh = picture.getHeight();
		switch (displayMode) {
		default:
		case SIZE_STANDARD:
			return new Rect(-(bmw / 2), -(bmh / 2),(bmw / 2), (bmh / 2));
		case SIZE_BEST_FIT:
			float bmasp = (float) bmw / (float) bmh;
			bmw = width;
			bmh = (int) (width / bmasp);
			if (bmh > attHeightPx) {
				bmh = attHeightPx;
				bmw = (int) (attHeightPx * bmasp);
			}
			return new Rect(-(bmw / 2), -(bmh / 2),(bmw / 2), (bmh / 2));
		case SIZE_FULLSCREEN:
			return new Rect(-width/2, -attHeightPx/2, width/2, attHeightPx/2);
		}
	}

	public void setSource(MjpegInputStream source) {
		Log.d("FPV", "setSource");
		mIn = source;		
	}

}
