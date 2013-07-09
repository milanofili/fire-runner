package org.sharif.ce.firerclient.model;

import java.util.Date;

import android.R.string;

public class GpsPoint {
	
	private double latitude;
	
	private double longitude;
	
	private double altitude;
	
	private Date timeTag;
	
	private int wayId;

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getAltitude() {
		return altitude;
	}

	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}

	public Date getTimeTag() {
		return timeTag;
	}

	public void setTimeTag(Date timeTag) {
		this.timeTag = timeTag;
	}

	public int getWayId() {
		return wayId;
	}

	public void setWayId(int wayId) {
		this.wayId = wayId;
	}
	
	
	public String toString() {
		return "lat: " + latitude + "| long: " + longitude + "| alt: " + altitude
				+ "| wayid:" + wayId + " in time:" + timeTag.toString();
	}
}
