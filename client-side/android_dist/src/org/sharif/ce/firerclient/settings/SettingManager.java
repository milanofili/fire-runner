package org.sharif.ce.firerclient.settings;

import android.app.Activity;
import android.content.SharedPreferences;

public class SettingManager {
	
	public static final String PREF_NAME = "FirerSettings";
	
	public Activity activity;
	
	public SettingManager(Activity activity) {
		this.activity = activity;
	}
	
	public void saveWeight(double weight) {
		SharedPreferences settings = activity.getSharedPreferences(PREF_NAME, 0);
		SharedPreferences.Editor edit = settings.edit();
		
		edit.putFloat("weight", (float)weight);
		edit.commit();
	}
	
	public double getWeight() {
		SharedPreferences settings = activity.getSharedPreferences(PREF_NAME, 0);
		
		return settings.getFloat("weight", (float)80.0);
	}
	
	public void saveLastUploadedWay(int lastId) {
		SharedPreferences settings = activity.getSharedPreferences(PREF_NAME, 0);
		SharedPreferences.Editor edit = settings.edit();
		
		edit.putInt("lastUpload", lastId);
		edit.commit();
	}
	
	public int getLastUploadedWay() {
		SharedPreferences settings = activity.getSharedPreferences(PREF_NAME, 0);
		
		return settings.getInt("lastUpload", 0);
	}
	
	public void saveServerAddress(String address) {
		SharedPreferences settings = activity.getSharedPreferences(PREF_NAME, 0);
		SharedPreferences.Editor edit = settings.edit();
		
		edit.putString("serverAddress", address);
		edit.commit();
	}
	
	public String getServerAddress() {
		SharedPreferences settings = activity.getSharedPreferences(PREF_NAME, 0);
		
		return settings.getString("serverAddress", "10.0.2.2:8080");
	}
}
