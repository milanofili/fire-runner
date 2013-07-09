package org.sharif.ce.firerclient.util;

public class CalloriesCalculator {
	
	/**
	 *  Calculate burned calories by distance and weight factors independent to run or 
	 *  walk.
	 * @param distance in kilometer.
	 * @param weight in kilograms.
	 * @return Burned calories.
	 */
	public static double getSimpleCalories( double distance, double weight) {
		// formula used:calories = kilometers x kilograms x 1.036
		return distance * weight * 1.036;
	}
}
