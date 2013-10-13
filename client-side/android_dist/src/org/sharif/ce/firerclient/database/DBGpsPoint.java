package org.sharif.ce.firerclient.database;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.sharif.ce.firerclient.model.GpsPoint;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DBGpsPoint {
	
	private SQLiteDatabase database;
	private DBConnection dbHelper;
	  private String[] allColumns = { DBConnection.GPS_POINT_DATABASE_ID
	      , DBConnection.GPS_POINT_DATABASE_LAT, DBConnection.GPS_POINT_DATABASE_LNG
	      , DBConnection.GPS_POINT_DATABASE_ALT, DBConnection.GPS_POINT_DATABASE_TIME_TAG
	      , DBConnection.GPS_POINT_DATABASE_WAY_ID};
	
	/// Create db gps point from database
	public DBGpsPoint (Context context) {
		dbHelper = new DBConnection(context);
	}
	
	/// Open gps point database
	public void open() throws SQLException {
	    database = dbHelper.getWritableDatabase();
	}
	
	/// Close gps point database
	public void close() {
	    dbHelper.close();
	}

	/**
	 * The function saves the gps point
	 * @param p the point needs to save
	 */
	public void savePoint (GpsPoint p) {
		ContentValues values = new ContentValues();
		values.put(DBConnection.GPS_POINT_DATABASE_LAT, p.getLatitude());
		values.put(DBConnection.GPS_POINT_DATABASE_LNG, p.getLongitude());
		values.put(DBConnection.GPS_POINT_DATABASE_ALT, p.getAltitude());
		Date dt = new Date();
		values.put(DBConnection.GPS_POINT_DATABASE_TIME_TAG, dt.toString());
		values.put(DBConnection.GPS_POINT_DATABASE_WAY_ID, p.getWayId());
		
		database.insert(DBConnection.GPS_POINT_DATABASE_NAME, null, values);
	}
	
	/**
	 * Get latest list of gpspoint
	 * @return
	 */
	public List<GpsPoint> getPointsbyWayId () {
		List<GpsPoint> ret = new ArrayList<GpsPoint>();
		
		Cursor cursor = database.query(DBConnection.GPS_POINT_DATABASE_NAME,
		        allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			GpsPoint gp = cursorToGpsPoint(cursor);
		    ret.add(gp);
		    cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		    
		return ret;
	}
	
	/**
	 * Create gps point from cursor
	 * @param cursor database cursor
	 * @return gpspoint is converted from cursor
	 */
	public GpsPoint cursorToGpsPoint(Cursor cursor) {
		GpsPoint gp = new GpsPoint();
		
		gp.setLatitude(cursor.getDouble(1));
		gp.setLongitude(cursor.getDouble(2));
		gp.setAltitude(cursor.getDouble(3));
		gp.setTimeTag(cursor.getString(4));
		gp.setWayId(cursor.getInt(5));
		
		return gp;
	}
	
	
	
}
