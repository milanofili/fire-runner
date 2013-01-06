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
	
	public DBGpsPoint (Context context) {
		dbHelper = new DBConnection(context);
	}
	
	public void open() throws SQLException {
	    database = dbHelper.getWritableDatabase();
	}
	
	public void close() {
	    dbHelper.close();
	}

	
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
	
	public GpsPoint cursorToGpsPoint(Cursor cursor) {
		GpsPoint gp = new GpsPoint();
		
		gp.setLatitude(cursor.getDouble(1));
		gp.setLongitude(cursor.getDouble(2));
		gp.setAltitude(cursor.getDouble(3));
		Date dt = new Date(cursor.getString(4));
		gp.setTimeTag(dt);
		gp.setWayId(cursor.getInt(5));
		
		return gp;
	}
	
}
