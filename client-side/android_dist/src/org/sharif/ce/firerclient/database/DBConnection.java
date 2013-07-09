package org.sharif.ce.firerclient.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBConnection extends SQLiteOpenHelper{
	
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "Firer.db";
	
	public static final String GPS_POINT_DATABASE_ID = "_id";
	public static final String GPS_POINT_DATABASE_LAT = "lat";
	public static final String GPS_POINT_DATABASE_LNG = "lng";
	public static final String GPS_POINT_DATABASE_ALT = "alt";
	public static final String GPS_POINT_DATABASE_WAY_ID = "wayId";
	public static final String GPS_POINT_DATABASE_TIME_TAG = "timeTag";
	public static final String GPS_POINT_DATABASE_NAME = "GpsPoint";
	private static final String GPS_POINT_DATABASE_CREATE =
			"create table " + GPS_POINT_DATABASE_NAME 
			+ "(" + GPS_POINT_DATABASE_ID + " integer primary key autoincrement, " 
			+ GPS_POINT_DATABASE_LAT + " real, " + GPS_POINT_DATABASE_LNG 
			+ " real, " + GPS_POINT_DATABASE_ALT +"  real, "
			+ GPS_POINT_DATABASE_WAY_ID + " integer, " 
			+ GPS_POINT_DATABASE_TIME_TAG + " text" + ");";
	
	
	public DBConnection ( Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(GPS_POINT_DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { 
		db.execSQL("DROP TABLE IF EXISTS " + GPS_POINT_DATABASE_NAME );
		onCreate(db);
	} 
	
}
