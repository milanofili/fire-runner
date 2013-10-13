package org.sharif.ce.firerclient;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.ChoiceFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.net.ssl.ManagerFactoryParameters;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.sharif.ce.firerclient.database.DBGpsPoint;
import org.sharif.ce.firerclient.database.DBWayPoint;
import org.sharif.ce.firerclient.dataprovider.GpsLocationListener;
import org.sharif.ce.firerclient.dataprovider.IGpsLocationListener;
import org.sharif.ce.firerclient.model.GpsPoint;
import org.sharif.ce.firerclient.network.GpsPointsSender;
import org.sharif.ce.firerclient.network.IGpsPointsSender;
import org.sharif.ce.firerclient.settings.SettingManager;
import org.sharif.ce.firerclient.util.CalloriesCalculator;
import org.sharif.ce.firerclient.util.DistanceCalculator;
import org.sharif.ce.firerclient.util.LoadingToast;

import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * God page to all activities from ui view
 * @author milano
 */
public class MainActivity extends Activity implements IGpsLocationListener,
		IGpsPointsSender {

	/// Listener for gps location changes
	public GpsLocationListener locator;

	/// Total computed distance on latest start
	public double totalDistances;

    /// Total computed callories on latest start
	public double totalCallories;

    /// Last Latitude
	public double lastLat;

    /// Last longitude
	public double lastLng;

    /// Last altitude
	public double lastAlt;

    /// Check is started or not
	public boolean isStarted;

    /// First location before 
	public boolean firstLoc;

    /// Keep last way id
	public long lastWayId;

    /// Weight 
	public double weight = 70.0;

    /// Location manager for testing purpose
	LocationManager mlocManager;

    /// Progress dialog
	public ProgressDialog dialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		prepareUIforStopping();

		updateWeight();
	}

	public void updateWeight() {
		SettingManager manager = new SettingManager(this);
		weight = manager.getWeight();
	}

	public void prepareUIforStarting() {
		isStarted = true;

		// start stop button update
		Button startBT = (Button) findViewById(R.id.startButton);
		startBT.setEnabled(false);
		Button stopBT = (Button) findViewById(R.id.stopButton);
		stopBT.setEnabled(true);

		// Choronometer update
		Chronometer ch = (Chronometer) findViewById(R.id.elapsedTimeChronometer);
		ch.setBase(SystemClock.elapsedRealtime());
		ch.start();

		totalDistances = 0;
		totalCallories = 0;

		updateCaloriesUI();
		updateDistanceUI();

		// update
		DBWayPoint dbWay = new DBWayPoint(this.getApplicationContext());
		dbWay.open();

		lastWayId = dbWay.getNewWayId();

		dbWay.close();

	}

    /// Update callories format by total computed callories
	public void updateCaloriesUI() {
		TextView view = (TextView) findViewById(R.id.calloriesTextView);
		view.setText(String.format("Calories: %.3f cal", totalCallories));
	}

    /// Update distance format by last computed callories
	public void updateDistanceUI() {
		TextView view = (TextView) findViewById(R.id.distanceTextView);
		view.setText(String.format("Distances: %.3f km", totalDistances));
	}

    /// Pre-steps before stopping
	public void prepareUIforStopping() {
		isStarted = false;
		Button stopBT = (Button) findViewById(R.id.stopButton);
		stopBT.setEnabled(false);
		Button startBT = (Button) findViewById(R.id.startButton);
		startBT.setEnabled(true);

		// Chronometer update
		Chronometer ch = (Chronometer) findViewById(R.id.elapsedTimeChronometer);
		ch.stop();
	}

    /// It will be called before creation menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!isStarted) {
			MenuInflater menuInflater = getMenuInflater();
			menuInflater.inflate(R.layout.main_menu, menu);
			return true;
		}
		return true;
	}

    /// It will be called after an item is selected on menu
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_settings: {
			setContentView(R.layout.settings);
			EditText et = (EditText) findViewById(R.id.addressEditText);
			SettingManager sm = new SettingManager(this);
			et.setText(String.valueOf(sm.getServerAddress()));
			break;
		}
		case R.id.menu_parameters: {
			setContentView(R.layout.parameters);
			EditText et = (EditText) findViewById(R.id.weightEditText);
			SettingManager sm = new SettingManager(this);
			et.setText(String.valueOf(sm.getWeight()));
			break;
		}
		}
		return true;
	}

    /// Convert http entity to string
	protected String getASCIIContentFromEntity(HttpEntity entity)
			throws IllegalStateException, IOException {

		InputStream in = entity.getContent();

		StringBuffer out = new StringBuffer();
		int n = 1;
		while (n > 0) {
			byte[] b = new byte[4096];

			n = in.read(b);

			if (n > 0)
				out.append(new String(b, 0, n));

		}

		return out.toString();

	}

    //~~~~~~~~~~~ test ~~~~~~~~~~~~//
	public void testClick(View view) {
		TextView et = (TextView) findViewById(R.id.logview);
		
		DBGpsPoint dbp = new DBGpsPoint(getApplicationContext());
		dbp.open();
		List<GpsPoint> list = dbp.getPointsbyWayId();

		dbp.close();
		try {
			et.append(GpsPoint.getJson(list));
		} catch (JSONException e) {
			et.append("json exception");
		}
		
	}
    
    /// Request to location for new location
	public void getPoint(View view) {
		locator = new GpsLocationListener(this);
		LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
				locator);

	}

    /// Upload remained data
	public void uploadRemainedData(View view) {
		dialog = ProgressDialog.show(this, "",
				"Loading. Please wait to upload...", true);

		DBGpsPoint dbp = new DBGpsPoint(getApplicationContext());
		dbp.open();
		List<GpsPoint> list = dbp.getPointsbyWayId();

		dbp.close();
		
		SettingManager manager = new SettingManager(this);
		
		try {
			new LongRunningGetIO(this, GpsPoint.getJson(list), manager.getWeight(), "testid", 
					manager.getServerAddress()).execute();
		} catch (JSONException e) {
			Toast.makeText(getApplicationContext(), "test app", Toast.LENGTH_SHORT);
		}
	}

    //// It will be called when start button clicked
	public void startButtonClicked(View view) {
		
		dialog = ProgressDialog.show(this, "",
				"Loading. Please wait for stable GPS signal...", true);

		locator = new GpsLocationListener(this);
		mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000,
				2, locator);

		firstLoc = true;
	}

    /// It will be called when stop button clicked
	public void stopButtonClicked(View view) {
		prepareUIforStopping();
		locator = null;
		mlocManager = null;
	}

    /// It will be called when test button is clicked
	public void testButtonClicked(View view) {
		prepareUIforStopping();
		setContentView(R.layout.test_layout);
	}

    /// Return main activity 
    /// Use this when you want to check using this
	public MainActivity getThis() {
		return this;
	}

    //// Load all preferences
	public void loadPreferences() {
		updateWeight();
	}

    /// Save all preferences
	public void savePreferences(boolean isSetting) {
		if (!isSetting) {
			EditText et = (EditText) findViewById(R.id.weightEditText);
			String textofEt = et.getText().toString();

			SettingManager sm = new SettingManager(this);
			sm.saveWeight(Double.valueOf(textofEt));
		} else {
			EditText et = (EditText) findViewById(R.id.addressEditText);
			String textofEt = et.getText().toString();

			SettingManager sm = new SettingManager(this);
			sm.saveServerAddress(textofEt);
		}
	}

    /// It will be called when back button is clicked on settings menu 
	public void backClicked(View view) {
		if (view.getId() == R.id.backMainActivityfromSetting) {
			savePreferences(true);
		} else {
			savePreferences(false);
		}
		loadPreferences();
		setContentView(R.layout.activity_main);
		Button stopBT = (Button) findViewById(R.id.stopButton);
		stopBT.setEnabled(false);
	}

    /// It will be called when we have new location
	@Override
	public void newLocation(double lat, double lng, double alt) {
		if (firstLoc) {
			// setup dialog
			dialog.cancel();
			// add lat and lng and alt to last point
			lastLat = lat;
			lastLng = lng;
			lastAlt = alt;

			firstLoc = false;
			prepareUIforStarting();
		} else {
			// update
			double dist = DistanceCalculator.getDistance(lastLat, lastLng,
					lastAlt, lat, lng, alt);
			lastLat = lat;
			lastLng = lng;
			lastAlt = alt;
			totalDistances += dist;

			double cal = CalloriesCalculator.getSimpleCalories(dist, weight);
			totalCallories += cal;

			// update ui
			updateCaloriesUI();
			updateDistanceUI();

			// save on db
			DBGpsPoint dbp = new DBGpsPoint(this.getApplicationContext());

			dbp.open();

			GpsPoint gp = new GpsPoint();
			gp.setAltitude(lastAlt);
			gp.setLatitude(lastLat);
			gp.setLongitude(lastLng);
			gp.setWayId((int) lastWayId);

			dbp.savePoint(gp);
			dbp.close();

		}
	}

    /// Call it to show user the gps is off
	@Override
	public void GpsIsTurnOff() {
		dialog.cancel();
		Toast.makeText(this.getApplicationContext(),
				"Gps is turn off. Please turn it on and try again.",
				Toast.LENGTH_LONG).show();
	}
    
    
	@Override
	public void responseReady(String response) {
		dialog.cancel();

		Toast.makeText(this.getApplicationContext(),
				"successful synch with server.", Toast.LENGTH_LONG).show();

	}

    /// Call it when an error is occurred
	@Override
	public void errorOccurred(String errorStr) {
		dialog.cancel();

		Toast.makeText(this.getApplicationContext(), errorStr,
				Toast.LENGTH_LONG).show();
	}

    /// Gps state is changed
	@Override
	public void GpsStateChanged(GpsState newState) {
		// TODO Auto-generated method stub

	}
    
    /// We use this to send json request to server
	private class LongRunningGetIO extends AsyncTask<Void, Void, String> {
		
		private String json;
		private String userid;
		private double weight;
		private String url;
		private MainActivity main;
		
		public LongRunningGetIO(MainActivity active, String json, double weight, String userid, String url) {
			this.json = json;
			this.weight = weight;
			this.userid = userid;
			this.url = url;
			main = active;
		}
		
		protected String getASCIIContentFromEntity(HttpEntity entity)
				throws IllegalStateException, IOException {

			InputStream in = entity.getContent();

			StringBuffer out = new StringBuffer();
			int n = 1;
			while (n > 0) {
				byte[] b = new byte[4096];

				n = in.read(b);

				if (n > 0)
					out.append(new String(b, 0, n));

			}

			return out.toString();

		}

		@Override
		protected String doInBackground(Void... params) {

			try {
				HttpClient httpClient = new DefaultHttpClient();
				HttpContext localContext = new BasicHttpContext();

				HttpPost httpPost = new HttpPost(
						"http://"  + this.url+"/FireRunnerBackends/andservice/andservices/" + userid + 
						"/" + this.weight);
				
				StringEntity sentity;

				sentity = new StringEntity(json);

				sentity.setContentType("application/json");
				httpPost.setEntity(sentity);

				String text = null;

				HttpResponse response = httpClient.execute(httpPost,
						localContext);

				HttpEntity entity = response.getEntity();

				text = getASCIIContentFromEntity(entity);

				return text;

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return e.getMessage();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				return e.getMessage();
			} catch (IOException e) {
				e.printStackTrace();
				return e.getMessage();
			}

		}

		@Override
		protected void onPostExecute(String result) {

			main.dialog.dismiss();
			
			if (result != null) {
				
				if (result.contains("Saved")) {
					Toast.makeText(main.getApplicationContext(), "Succefully Saved", Toast.LENGTH_SHORT).show();
				}
				else {
					Toast.makeText(main.getApplicationContext(), result, Toast.LENGTH_SHORT).show();
				}
			}
			else {
				Toast.makeText(main.getApplicationContext(), "Unknown error", Toast.LENGTH_SHORT).show();
			}
			
		}


	} // end of inner class



} // end of out class
