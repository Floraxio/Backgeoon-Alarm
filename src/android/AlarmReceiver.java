package com.oby.cordova.plugin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.app.AlarmManager;
import android.app.PendingIntent;
import java.util.Calendar;

import android.util.Log;


public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";
  	private static final long REPEAT_TIME = 1000 * 30; // evry 30 sec

    @Override
    public void onReceive(Context context, Intent intent) {
		Log.v(TAG, "AlarmReceiver tick.");
		// run the service
		Backservice backservice = new Backservice();
		backservice.launchService();
    }
}