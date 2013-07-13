package com.droidplanner.widgets.FPV;

import java.io.IOException;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;

public abstract class MjpegStreamBuilder extends AsyncTask<String, Void, MjpegInputStream> {
	private static final String TAG = "FPV";
	public abstract void setSource( MjpegInputStream result);
	
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
		setSource(result);
	}
}
