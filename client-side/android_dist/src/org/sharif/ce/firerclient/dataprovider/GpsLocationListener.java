package org.sharif.ce.firerclient.dataprovider;

import org.sharif.ce.firerclient.dataprovider.IGpsLocationListener;
import org.sharif.ce.firerclient.dataprovider.IGpsLocationListener.*;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.location.*;


public class GpsLocationListener implements LocationListener {
	
	private IGpsLocationListener delegate;
	
	public GpsLocationListener ( IGpsLocationListener listener ) {
		setDelegate(listener);
	}
	
	public void setDelegate(IGpsLocationListener delegate) {
		this.delegate = delegate;
	}
	
	public IGpsLocationListener getDelegate() {
		return delegate;
	}

	@Override
	public void onLocationChanged(Location loc) {
		delegate.newLocation(loc.getLatitude(), loc.getLongitude(), loc.getAltitude());
	}

	@Override
	public void onProviderDisabled(String arg0) {
		delegate.GpsIsTurnOff();
	}

	@Override
	public void onProviderEnabled(String arg0) {
		
	}

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
