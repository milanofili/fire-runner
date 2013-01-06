package org.sharif.ce.firerclient.dataprovider;

public interface IGpsLocationListener {
	public enum GpsState
	{
		OUT_SERVICE,
		TEMP_UNAVAILABLE,
		AVAILABLE
	}
	
	public void newLocation(double lat, double lng, double alt);
	
	public void GpsIsTurnOff();
	
	public void GpsStateChanged(GpsState newState);
	
}
