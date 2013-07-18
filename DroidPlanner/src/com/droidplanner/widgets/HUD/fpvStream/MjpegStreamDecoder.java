package com.droidplanner.widgets.HUD.fpvStream;

import java.io.IOException;

import android.graphics.Bitmap;
import android.os.AsyncTask;

public class MjpegStreamDecoder extends AsyncTask<MjpegInputStream, Bitmap, String>{

	@Override
	protected String doInBackground(MjpegInputStream... stream) {
		while(true){
		try {
			Bitmap bm = stream[0].readMjpegFrame();
			onProgressUpdate(bm);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
	}
}
