package org.sharif.ce.firerclient.dataprovider;

/// Delegation for gps changing
public interface IGpsLocationListener {
	public enum GpsState
	{
		OUT_SERVICE,
		TEMP_UNAVAILABLE,
		AVAILABLE
	}
	
	/// When new location is changed it will be called
	public void newLocation(double lat, double lng, double alt);
	
	/// When gps is turned off
	public void GpsIsTurnOff();
	
	/// When gps state changed according to enum
	public void GpsStateChanged(GpsState newState);
	
}
