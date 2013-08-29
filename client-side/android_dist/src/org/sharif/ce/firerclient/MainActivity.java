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

public class MainActivity extends Activity implements IGpsLocationListener,
		IGpsPointsSender {

	public GpsLocationListener locator;
	public double totalDistances;
	public double totalCallories;

	public double lastLat;
	public double lastLng;
	public double lastAlt;
	public boolean isStarted;
	public boolean firstLoc;
	public long lastWayId;

	public double weight = 70.0;
	LocationManager mlocManager;

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

	public void updateCaloriesUI() {
		TextView view = (TextView) findViewById(R.id.calloriesTextView);
		view.setText(String.format("Calories: %.3f cal", totalCallories));
	}

	public void updateDistanceUI() {
		TextView view = (TextView) findViewById(R.id.distanceTextView);
		view.setText(String.format("Distances: %.3f km", totalDistances));
	}

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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!isStarted) {
			MenuInflater menuInflater = getMenuInflater();
			menuInflater.inflate(R.layout.main_menu, menu);
			return true;
		}
		return true;
	}

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

	public void getPoint(View view) {
		locator = new GpsLocationListener(this);
		LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
				locator);

	}

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

	public void startButtonClicked(View view) {
		
		dialog = ProgressDialog.show(this, "",
				"Loading. Please wait for stable GPS signal...", true);

		locator = new GpsLocationListener(this);
		mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000,
				2, locator);

		firstLoc = true;
	}

	public void stopButtonClicked(View view) {
		prepareUIforStopping();
		locator = null;
		mlocManager = null;
	}

	public void testButtonClicked(View view) {
		prepareUIforStopping();
		setContentView(R.layout.test_layout);
	}

	public void dbTest(View view) {
		{
			DBGpsPoint dbp = new DBGpsPoint(this.getApplicationContext());

			dbp.open();

			GpsPoint gp = new GpsPoint();
			gp.setAltitude(1.2);
			gp.setLatitude(1.3);
			gp.setLongitude(1.4);
			gp.setWayId(1);

			dbp.savePoint(gp);
			dbp.close();
		}

		{
			DBGpsPoint dbp = new DBGpsPoint(this.getApplicationContext());
			dbp.open();
			List<GpsPoint> list = dbp.getPointsbyWayId();

			Toast.makeText(this.getApplicationContext(),
					"size of list:" + list.size(), 2).show();

			for (int i = 0; i < list.size(); i++) {
				GpsPoint p = list.get(i);

				Toast.makeText(this.getApplicationContext(), i + p.toString(),
						2).show();
			}

			dbp.close();
		}

	}

	public void netTest(View view) {
		new Thread(new Runnable() {
			public void run() {
				DBGpsPoint dbp = new DBGpsPoint(getApplicationContext());
				dbp.open();
				List<GpsPoint> list = dbp.getPointsbyWayId();

				GpsPointsSender sender = new GpsPointsSender(list, getThis());

				sender.connect("192.168.1.2", 10000);

				dbp.close();
			}
		}).start();

	}

	public MainActivity getThis() {
		return this;
	}

	public void loadPreferences() {
		updateWeight();
	}

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

	@Override
	public void errorOccurred(String errorStr) {
		dialog.cancel();

		Toast.makeText(this.getApplicationContext(), errorStr,
				Toast.LENGTH_LONG).show();
	}

	@Override
	public void GpsStateChanged(GpsState newState) {
		// TODO Auto-generated method stub

	}

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

	}
}
