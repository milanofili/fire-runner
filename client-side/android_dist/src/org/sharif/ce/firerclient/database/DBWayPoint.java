package org.sharif.ce.firerclient.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

public class DBWayPoint {
	
	private SQLiteDatabase database;
	private Context context;
	private DBConnection dbHelper;
	private String[] wayColumn = {};
	
    public DBWayPoint (Context context) {
    	this.context = context;
		dbHelper = new DBConnection(context);
	}
	
	public void open() throws SQLException {
	    database = dbHelper.getWritableDatabase();
	}
	
	public void close() {
	    dbHelper.close();
	}
	
	public long getNewWayId() {
		String query = String.format("SELECT %s from %s order by %s DESC limit 1", 
				DBConnection.GPS_POINT_DATABASE_WAY_ID, DBConnection.GPS_POINT_DATABASE_NAME, DBConnection.GPS_POINT_DATABASE_WAY_ID);
		Cursor c = database.rawQuery(query, wayColumn);
		if (c != null && c.moveToFirst()) {
		    return c.getLong(0) + 1; //The 0 is the column index, we only have 1 column, so the index is 0
		}
		return 1;
	}
}
