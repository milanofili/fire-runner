package org.sharif.ce.firerclient.model;

import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.string;

/// GPS point model
public class GpsPoint {
	
	private double latitude;
	
	private double longitude;
	
	private double altitude;
	
	private String timeTag;
	
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

	public String getTimeTag() {
		return timeTag;
	}

	public void setTimeTag(String timeTag) {
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
	
	/// Return a json string from list of gps point
	public static String getJson(List<GpsPoint> ps) throws JSONException {
		
		JSONArray points = new JSONArray();
		for (int i =0 ; i < ps.size() ; i++) {
			JSONObject node = new JSONObject();
			node.put("lat", ps.get(i).getLatitude());
			node.put("lng", ps.get(i).getLongitude());
			node.put("alt", ps.get(i).getAltitude());
			node.put("timetag", ps.get(i).getTimeTag());
			node.put("wayid", ps.get(i).getWayId());
			points.put(node);
		}
		
		JSONObject mainObj = new JSONObject();
		mainObj.put("points", points);
		return mainObj.toString();
	}
}
