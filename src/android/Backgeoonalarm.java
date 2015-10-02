// package org.apache.cordova.plugin;
package com.oby.cordova.plugin;

/* class cordova dependency */
import org.apache.cordova.PluginResult;


import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import java.net.MalformedURLException;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import java.util.Calendar;

import android.content.pm.PackageManager;
import android.content.ComponentName;

import android.util.Log;

/**
 * This class echoes a string called from JavaScript.
 */
public class Backgeoonalarm extends CordovaPlugin
{
    private PendingIntent pendingIntent;
    private static final String TAG = "Backgeoonalarm";
    private static final int REPEAT_TIME = 10; // evry 30 sec

    private String convertIntToString(int i){
        return Integer.toString(i);
    }

    @Override
    public boolean execute(String action, final String rawArgs, final CallbackContext callbackContext) throws JSONException {
        // helloworld return OK
        if (action.equals("hello")) {
            threadhelper( new FileOp( ){
                public void run(JSONArray args) throws JSONException, MalformedURLException  {
                    // simple callback
                    callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, args.getString(0)));
                }
            }, rawArgs, callbackContext);
        }

        // initialisation of backgroung geo
        if (action.equals("initbackgroundgeo")) {

            threadhelper( new FileOp( ){
                public void run(JSONArray args) throws JSONException, MalformedURLException  {
                    // launch service
                    Log.v(TAG, "initbackgroundgeo entry");
                    Log.v(TAG, args.toString());

                    // hook on tick alarm system
                    initbackgroundgeo();
                    // callback
                    callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, "initialisation in java sucess"));
                }
            }, rawArgs, callbackContext);

            /*cordova.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    // run the service in detached thread
                    initbackgroundgeo();
                    callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, b));
                }
            });*/
            // return sucess callback after launched thread
            //callbackContext.success();
            //return error callback
            // callbackContext.error("Call configure before calling start");
            return true;
        }
        return false;
    }

    private void hello(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }
    /* wra  pper foir start the alarm tick */
    public void initbackgroundgeo(){
        Log.v(TAG, "initbackgroundgeo function");
        start();
    }

    /* ALARM CLOCK FUNTIONS */
    public void start() {
        Context context = this.cordova.getActivity().getApplicationContext();

        Intent alarmIntent = new Intent(context, AlarmReceiver.class);


        /* Retrieve a PendingIntent that will perform a broadcast */
        boolean alarmUp = (PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_NO_CREATE) != null);

        // if alarm ever defined (oncetime is defined, the app must be uninstall for disabled alarm)
        if (alarmUp){
            Log.v(TAG, "Alarm is ever up.. return true.");
            return;
        }



        pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);

        AlarmManager manager = (AlarmManager) cordova.getActivity().getSystemService(Context.ALARM_SERVICE);
        int interval = REPEAT_TIME*1000;
        
            Log.v(TAG, "Interval for tick is : "+convertIntToString(interval));

        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
        

        Toast.makeText(context, "Alarm Set", Toast.LENGTH_SHORT).show();
    }

    public void cancel() {
        Context context = this.cordova.getActivity().getApplicationContext();

        AlarmManager manager = (AlarmManager) cordova.getActivity().getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
        Toast.makeText(context, "Alarm Canceled", Toast.LENGTH_SHORT).show();
    }

    public void startAt10() {
        AlarmManager manager = (AlarmManager) cordova.getActivity().getSystemService(Context.ALARM_SERVICE);
        int interval = 1000 * 60 * 20;

        /* Set the alarm to start at 10:30 AM */
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 10);
        calendar.set(Calendar.MINUTE, 30);

        /* Repeating on every 20 minutes interval */
        manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                1000 * 60 * 20, pendingIntent);
    }

    private interface FileOp {
        void run(JSONArray args) throws Exception;
    }

    private void threadhelper(final FileOp f, final String rawArgs, final CallbackContext callbackContext){
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    JSONArray args = new JSONArray(rawArgs);
                    f.run(args);
                } catch ( Exception e) {
                    if(e instanceof JSONException ) {
                        callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.JSON_EXCEPTION));
                    } else {
                        e.printStackTrace();
                        callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR));
                    }
                }
            }
        });
    }

}