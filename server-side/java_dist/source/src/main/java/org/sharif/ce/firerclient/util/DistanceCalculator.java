package org.sharif.ce.firerclient.util;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

public class DistanceCalculator {
	/**
	 *  Return distance between two point in Killometer.
	 * @param lat1 Latitude of first point.
	 * @param lng1 Longitude of first point.
	 * @param alt1
	 * @param lat2 Latitude of second point.
	 * @param lng2 Longitude of second point.
	 * @param alt2
	 * @return
	 */
	public static double getDistance(double lat1, double lng1, double alt1,
			double lat2, double lng2, double alt2) {
		LatLng p1 = new LatLng(lat1, lng1);
		LatLng p2 = new LatLng(lat2, lng2);
		return LatLngTool.distance(p1, p2, LengthUnit.KILOMETER);
	}

}
