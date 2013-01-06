package org.sharif.ce.firerclient;

import java.util.List;

import org.sharif.ce.firerclient.database.DBGpsPoint;
import org.sharif.ce.firerclient.dataprovider.GpsLocationListener;
import org.sharif.ce.firerclient.dataprovider.IGpsLocationListener;
import org.sharif.ce.firerclient.model.GpsPoint;
import org.sharif.ce.firerclient.network.GpsPointsSender;
import org.sharif.ce.firerclient.network.IGpsPointsSender;

import android.location.LocationManager;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class MainActivity extends Activity 
	implements IGpsLocationListener, IGpsPointsSender {

	public GpsLocationListener locator;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
    }
    
    
    public void getPoint(View view) {
    	locator = new GpsLocationListener(this);
    	LocationManager mlocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

    	mlocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, locator);
    	
    	TextView tx = (TextView)findViewById(R.id.textView2);
    	
    	tx.setText("Loading...");
    	
    }
    
   
	public void dbTest (View view) {
    	TextView tx = (TextView)findViewById(R.id.textView2);
    	
    	tx.setText("db operation");
    	
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
    		
    		Toast.makeText(this.getApplicationContext(), "size of list:" + list.size(), 2).show();
    		
    		for ( int i = 0; i < list.size() ; i++) {
    			GpsPoint p = list.get(i);
    			
    			Toast.makeText(this.getApplicationContext(),i + p.toString(), 2).show();
    		}
    		
    		dbp.close();
    	}
    	
    	
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    public void netTest(View view) {
    	DBGpsPoint dbp = new DBGpsPoint(this.getApplicationContext());
		dbp.open();
		List<GpsPoint> list = dbp.getPointsbyWayId();
		
		GpsPointsSender sender = new GpsPointsSender(list, this);
		
		sender.connect("192.168.1.103", 8001);
		
		dbp.close();
    }

	@Override
	public void newLocation(double lat, double lng, double alt) {
		TextView tx = (TextView)findViewById(R.id.textView1);
		
		tx.setText("gps point : " + lat + " " + lng + " " + alt);
	}
	
	

	@Override
	public void GpsIsTurnOff() {
		locator = null;
		TextView tx = (TextView)findViewById(R.id.textView1);
		
		tx.setText("gps is turn off");
		
	}

	@Override
	public void GpsStateChanged(GpsState newState) {
		TextView tx = (TextView)findViewById(R.id.textView2);
		tx.setText(newState.toString());
	}


	@Override
	public void responseReady(String response) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void errorOccurred(String errorStr) {
		// TODO Auto-generated method stub
		
	}
	
	

    
}
