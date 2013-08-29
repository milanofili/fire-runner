package org.sharif.ce.firerunnerbackends;

import java.util.ArrayList;
import java.util.List;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;


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
        
        public double weight;
	
	public static List<GpsPoint> parseJson(String str) throws JSONException {
            List<GpsPoint> ret = new ArrayList<GpsPoint>();
                    
            JSONObject pobj = new JSONObject(str);
        
            JSONArray points = pobj.getJSONArray("points");
        
            for (int i =0 ; i < points.length(); i++) {
                GpsPoint ge = new GpsPoint();
                ge.setLatitude(points.getJSONObject(i).getDouble("lat"));
                ge.setLongitude(points.getJSONObject(i).getDouble("lng"));
                ge.setWayId(points.getJSONObject(i).getInt("wayid"));
                ge.setTimeTag(points.getJSONObject(i).getString("timetag"));
                ge.setAltitude(points.getJSONObject(i).getDouble("alt"));
               
                ret.add(ge);
            }
            return ret;
	}
}
