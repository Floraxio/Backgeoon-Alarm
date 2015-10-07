package com.oby.cordova.plugin;

import java.util.Date;
import android.util.Log;
import org.json.JSONException;
import java.util.Iterator;
import org.json.JSONObject;

public class Customlocation {
    private static final String TAG = "Customlocation";

	private String latitude;
	private String longitude;
	private Date recordedAt;
	private String accuracy;
	private String speed;
	
	private Long id;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public Date getRecordedAt() {
		return recordedAt;
	}
	public void setRecordedAt(Date recordedAt) {
		this.recordedAt = recordedAt;
	}
	public String getAccuracy() {
		return accuracy;
	}
	public void setAccuracy(String accuracy) {
		this.accuracy = accuracy;
	}
	public String getSpeed() {
		return speed;
	}
	public void setSpeed(String speed) {
		this.speed = speed;
	}
	public static Customlocation fromAndroidLocation(android.location.Location originalLocation) {
		Customlocation location = new Customlocation();
		location.setRecordedAt(new Date(originalLocation.getTime()));
		location.setLongitude(String.valueOf(originalLocation.getLongitude()));
		location.setLatitude(String.valueOf(originalLocation.getLatitude()));
		location.setAccuracy(String.valueOf(originalLocation.getAccuracy()));
		location.setSpeed(String.valueOf(originalLocation.getSpeed()));
		
		return location;
	}
}
