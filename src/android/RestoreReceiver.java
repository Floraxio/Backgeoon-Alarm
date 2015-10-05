package com.oby.cordova.plugin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.json.JSONObject;

import android.util.Log;

/**
 * This class is triggered upon reboot of the device. It needs to re-register
 * the alarms with the AlarmManager since these alarms are lost in case of
 * reboot.
 */
public class RestoreReceiver extends BroadcastReceiver {
    private static final String TAG = "RestoreReceiver";

    @Override
    public void onReceive (Context context, Intent intent) {
        Log.v(TAG, "RestoreReceiver broadcast Tick.");
        
        // if broadcast is boot completed of android device.. reset functionnality
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            // run the service

        }
    }
}
