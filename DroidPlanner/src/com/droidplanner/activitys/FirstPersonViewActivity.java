package com.droidplanner.activitys;

import java.io.IOException;
import java.net.URI;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuInflater;

import com.droidplanner.R;
import com.droidplanner.activitys.helpers.SuperActivity;

import com.camera.simplemjpeg.MjpegView;
import com.camera.simplemjpeg.MjpegInputStream;

public class FirstPersonViewActivity extends SuperActivity {
	private static final boolean DEBUG=false;
    private static final String TAG = "MJPEG";

    private MjpegView mjpegview = null;
    String URL;
    
    // for settings (network and resolution)
    private static final int REQUEST_SETTINGS = 0;
    
    private int width = 640;
    private int height = 480;
    
    private String fpv_ip_address = "10.0.1.24";
    private int fpv_port = 8080;
    private String fpv_param = "?action=stream";
    
    private boolean suspending = false;
 
    @Override
	public int getNavigationItem() {
		return 4;
	}
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = getSharedPreferences("SAVED_VALUES", MODE_PRIVATE);
        width = preferences.getInt("width", width);
        height = preferences.getInt("height", height);

        //fpv_ip_address = preferences.getString("pref_server_ip", fpv_ip_address);
        //fpv_port = preferences.getInt("pref_server_port", fpv_port);
        //fpv_param = preferences.getString("pref_server_param", fpv_param);
                
        StringBuilder sb = new StringBuilder();
        String s_http = "http://";
        String s_colon = ":";
        String s_slash = "/";
        sb.append(s_http);
        sb.append(fpv_ip_address);
        sb.append(s_colon);
        sb.append(fpv_port);
        sb.append(s_slash);
        sb.append(fpv_param);
        URL = new String(sb);

        setContentView(R.layout.activity_firstpersonview);
        mjpegview = (MjpegView) findViewById(R.id.mjpegview);  
        if(mjpegview != null){
        	mjpegview.setResolution(width, height);
        }
        new DoRead().execute(URL);
    }

    
    public void onResume() {
    	if(DEBUG) Log.d(TAG,"onResume()");
        super.onResume();
        if(mjpegview!=null){
        	if(suspending){
        		new DoRead().execute(URL);
        		suspending = false;
        	}
        }

    }

    public void onStart() {
    	if(DEBUG) Log.d(TAG,"onStart()");
        super.onStart();
    }
    public void onPause() {
    	if(DEBUG) Log.d(TAG,"onPause()");
        super.onPause();
        if(mjpegview!=null){
        	if(mjpegview.isStreaming()){
		        mjpegview.stopPlayback();
		        suspending = true;
        	}
        }
    }
    public void onStop() {
    	if(DEBUG) Log.d(TAG,"onStop()");
        super.onStop();
    }

    public void onDestroy() {
    	if(DEBUG) Log.d(TAG,"onDestroy()");
    	
    	if(mjpegview!=null){
    		mjpegview.freeCameraMemory();
    	}
    	
        super.onDestroy();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.layout.option_menu, menu);
    	return true;
    }

/*    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    		case R.id.settings:
    			Intent settings_intent = new Intent(FirstPersonViewActivity.this, SettingsActivity.class);
    			settings_intent.putExtra("width", width);
    			settings_intent.putExtra("height", height);
    			settings_intent.putExtra("ip_ad1", ip_ad1);
    			settings_intent.putExtra("ip_ad2", ip_ad2);
    			settings_intent.putExtra("ip_ad3", ip_ad3);
    			settings_intent.putExtra("ip_ad4", ip_ad4);
    			settings_intent.putExtra("ip_port", fpv_port);
    			settings_intent.putExtra("ip_command", fpv_param);
    			startActivityForResult(settings_intent, REQUEST_SETTINGS);
    			return true;
    	}
    	return false;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	switch (requestCode) {
    		case REQUEST_SETTINGS:
    			if (resultCode == Activity.RESULT_OK) {
    				width = data.getIntExtra("width", width);
    				height = data.getIntExtra("height", height);
    				ip_ad1 = data.getIntExtra("ip_ad1", ip_ad1);
    				ip_ad2 = data.getIntExtra("ip_ad2", ip_ad2);
    				ip_ad3 = data.getIntExtra("ip_ad3", ip_ad3);
    				ip_ad4 = data.getIntExtra("ip_ad4", ip_ad4);
    				fpv_port = data.getIntExtra("ip_port", fpv_port);
    				fpv_param = data.getStringExtra("ip_command");

    				if(mv!=null){
    					mv.setResolution(width, height);
    				}
    				SharedPreferences preferences = getSharedPreferences("SAVED_VALUES", MODE_PRIVATE);
    				SharedPreferences.Editor editor = preferences.edit();
    				editor.putInt("width", width);
    				editor.putInt("height", height);
    				editor.putInt("ip_ad1", ip_ad1);
    				editor.putInt("ip_ad2", ip_ad2);
    				editor.putInt("ip_ad3", ip_ad3);
    				editor.putInt("ip_ad4", ip_ad4);
    				editor.putInt("ip_port", fpv_port);
    				editor.putString("ip_command", fpv_param);

    				editor.commit();

    				new RestartApp().execute();
    			}
    			break;
    	}
    }*/

    
    public class DoRead extends AsyncTask<String, Void, MjpegInputStream> {
        protected MjpegInputStream doInBackground(String... url) {
            //TODO: if camera has authentication deal with it and don't just not work
            HttpResponse res = null;
            DefaultHttpClient httpclient = new DefaultHttpClient(); 
            HttpParams httpParams = httpclient.getParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 5*1000);
            Log.d(TAG, "1. Sending http request");
            try {
                res = httpclient.execute(new HttpGet(URI.create(url[0])));
                Log.d(TAG, "2. Request finished, status = " + res.getStatusLine().getStatusCode());
                if(res.getStatusLine().getStatusCode()==401){
                    //You must turn off camera User Access Control before this will work
                    return null;
                }
                return new MjpegInputStream(res.getEntity().getContent());  
            } catch (ClientProtocolException e) {
                e.printStackTrace();
                Log.d(TAG, "Request failed-ClientProtocolException", e);
                //Error connecting to camera
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "Request failed-IOException", e);
                //Error connecting to camera
            }
            return null;
        }

        protected void onPostExecute(MjpegInputStream result) {
            mjpegview.setSource(result);
            if(result!=null) result.setSkip(1);
            mjpegview.setDisplayMode(MjpegView.SIZE_BEST_FIT);
            mjpegview.showFps(false);
        }
    }
    
    public class RestartApp extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... v) {
        	FirstPersonViewActivity.this.finish();
            return null;
        }

        protected void onPostExecute(Void v) {
        	startActivity((new Intent(FirstPersonViewActivity.this, FirstPersonViewActivity.class)));
        }
    }
}
