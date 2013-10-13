package org.sharif.ce.firerclient.dataprovider;

import org.sharif.ce.firerclient.dataprovider.IGpsLocationListener;
import org.sharif.ce.firerclient.dataprovider.IGpsLocationListener.*;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.location.*;


/// Get updates from location handler
public class GpsLocationListener implements LocationListener {
	
	private IGpsLocationListener delegate;
	
	/// Create location handler with a delegate
	public GpsLocationListener ( IGpsLocationListener listener ) {
		setDelegate(listener);
	}
	
	/// Change delegate by input delegate
	public void setDelegate(IGpsLocationListener delegate) {
		this.delegate = delegate;
	}
	
	/// Return current delegate
	public IGpsLocationListener getDelegate() {
		return delegate;
	}

	/// It will be called when location is changed
	@Override
	public void onLocationChanged(Location loc) {
		delegate.newLocation(loc.getLatitude(), loc.getLongitude(), loc.getAltitude());
	}

	/// It will be called when gps is disabled
	@Override
	public void onProviderDisabled(String arg0) {
		delegate.GpsIsTurnOff();
	}

	/// It will be called when gps is enabled
	@Override
	public void onProviderEnabled(String arg0) {
		
	}

	/// It will be called when status of gps changed
	@Override
	public void onStatusChanged(String provider, int state, Bundle extra) {
		switch (state) {
		case LocationProvider.OUT_OF_SERVICE:
			delegate.GpsStateChanged(GpsState.OUT_SERVICE);
			break;
		case LocationProvider.TEMPORARILY_UNAVAILABLE:
			delegate.GpsStateChanged(GpsState.TEMP_UNAVAILABLE);
			break;
		case LocationProvider.AVAILABLE:
			delegate.GpsStateChanged(GpsState.AVAILABLE);
			break;
		}
	}
	
}
