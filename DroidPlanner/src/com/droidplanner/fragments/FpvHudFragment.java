package com.droidplanner.fragments;

import java.io.IOException;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.droidplanner.R;
import com.droidplanner.activitys.SuperActivity;
import com.droidplanner.widgets.FPV.MjpegInputStream;
import com.droidplanner.widgets.HUD.FpvHudWidget;

public class FpvHudFragment extends Fragment {

	// sample public cam
	// String URL =
	// "http://trackfield.webcam.oregonstate.edu/axis-cgi/mjpg/video.cgi?resolution=800x600&amp%3bdummy=1333689998337";
	String URL = "http://192.168.40.143:81/videostream.cgi?user=admin&pwd=&resolution=32&rate=10";
	
	private FpvHudWidget fpvHudWidget;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fpv_hud_fragment, container,false);
		fpvHudWidget = (FpvHudWidget) view.findViewById(R.id.fpvHudWidget);
		fpvHudWidget.setDrone(((SuperActivity)getActivity()).app.drone);
		fpvHudWidget.onDroneUpdate();
		new DoRead().execute(URL);

		return view;
	}

	

	public class DoRead extends AsyncTask<String, Void, MjpegInputStream> {
		private static final String TAG = "FPV";

		protected MjpegInputStream doInBackground(String... url) {
			// TODO: if camera has authentication deal with it and don't just
			// not work
			HttpResponse res = null;
			DefaultHttpClient httpclient = new DefaultHttpClient();
			Log.d(TAG, "1. Sending http request");
			try {
				res = httpclient.execute(new HttpGet(URI.create(url[0])));
				Log.d(TAG, "2. Request finished, status = "
						+ res.getStatusLine().getStatusCode());
				if (res.getStatusLine().getStatusCode() == 401) {
					// You must turn off camera User Access Control before this
					// will work
					return null;
				}
				return new MjpegInputStream(res.getEntity().getContent());
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				Log.d(TAG, "Request failed-ClientProtocolException", e);
				// Error connecting to camera
			} catch (IOException e) {
				e.printStackTrace();
				Log.d(TAG, "Request failed-IOException", e);
				// Error connecting to camera
			}

			return null;
		}

		protected void onPostExecute(MjpegInputStream result) {
			fpvHudWidget.setSource(result);
		}
	}
}
