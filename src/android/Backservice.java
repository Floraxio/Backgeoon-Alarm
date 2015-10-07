package com.oby.cordova.plugin;


import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;

import android.app.PendingIntent;
import android.app.NotificationManager;
import android.app.Notification;
import android.widget.Toast;
import android.os.SystemClock;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.ArrayList;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.Map;

import android.util.Log;
 
public class Backservice extends Service implements LocationListener {
    private static final String TAG = "Backservice";
    protected boolean isRunning;
    protected Thread backgroundThread;

    protected int NOTIFICATION = 666;
    protected NotificationManager mNM;
    protected Context mContext;
    //private final Context mContext;
 
    // flag for GPS status
    boolean isGPSEnabled = false;
 
    // flag for network status
    boolean isNetworkEnabled = false;
 
    // flag for GPS status
    boolean canGetLocation = false;
 
    Location location; // location
    double latitude; // latitude
    double longitude; // longitude
 
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
 
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
 
    // Declaring a Location Manager
    protected LocationManager locationManager;
 
    /*public Backservice(Context context) {
        Log.v(TAG, "Backservice constructor");
        this.mContext = context;
    }*/
 
    public Location getLocation() {
        try {
            //locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
            locationManager = (LocationManager) this.getApplicationContext().getSystemService(LOCATION_SERVICE);
            // getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            // getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                this.canGetLocation = true;
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d(TAG, "Network");
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d(TAG, "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.v(TAG, location.toString());
        return location;
    }
     
    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     * */
    public void stopUsingGPS(){
        if(locationManager != null){
            locationManager.removeUpdates(Backservice.this);
        }
    }
    /**
     * Function to get latitude
     * */
    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }
        return latitude;
    }
    /**
     * Function to get longitude
     * */
    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }
        return longitude;
    }
    /**
     * Function to check GPS/wifi enabled
     * @return boolean
     * */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }


    @Override
    public void onLocationChanged(Location location) {
    }
    @Override
    public void onProviderDisabled(String provider) {
    }
    @Override
    public void onProviderEnabled(String provider) {
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
    @Override
    public void onDestroy() {
        this.isRunning = false;
        // Cancel the persistent notification.
        mNM.cancel(NOTIFICATION);
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();

    }
    @Override
    public void onCreate() {
        this.isRunning = false;
        this.backgroundThread = new Thread(myTask);
        this.mContext = this;

        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        // Display a notification about us starting.  We put an icon in the status bar.
        showNotification();
        // get location on create for have the context inside (and send http request in the thread)
        getLocation();

        Toast.makeText(this, "The new Service was Created", Toast.LENGTH_LONG).show();

    }
    /**
     * Show a notification while this service is running.
     */
    private void showNotification() {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = "MyEvent - Backservice";
        // The PendingIntent to launch our activity if the user selects this notification
        // Build a Notification required for running service in foreground.
        Intent main = new Intent(this, Backgeoonalarm.class);
        main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, main,  PendingIntent.FLAG_UPDATE_CURRENT);
        // Set the info for the views that show in the notification panel.
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(android.R.drawable.ic_menu_mylocation)  // the status icon
                .setTicker(text)  // the status text
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentTitle("Geocode..")  // the label of the entry
                .setContentText(text)  // the contents of the entry
                .setContentIntent(pendingIntent)  // The intent to send when the entry is clicked
                .build();
        // Send the notification to system.
        mNM.notify(NOTIFICATION, notification);
    }
    /* old && deprecated api */
    @Override
    public void onStart(Intent intent, int startId) {
        // For time consuming an long tasks you can launch a new thread here...
        handleStart(intent, startId);
    }
    /* new api function */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleStart(intent, startId);
        return START_NOT_STICKY;
    }
    /* Master entry of the service */
    public int handleStart(Intent intent, int startId){
        Log.v(TAG, "handleStart Service.");

        if(!this.isRunning) {
            Log.v(TAG, "different de - isRunning - relaunch action.");

            this.isRunning = true;
            //start the process
            this.backgroundThread.start();
        } else {
            Log.v(TAG, "ever - isRunning.");
        }

        Toast.makeText(this, " handleStart", Toast.LENGTH_SHORT).show();

        return START_STICKY;
    }
    /* Master task of the service */
    private Runnable myTask = new Runnable() {
        public void run() {
            // Do something here
            Log.v(TAG, "Runnable run thread entry.");
            // get the background localisation
            Log.v(TAG, "location : "+location.toString());

            // add it to the local litesql database
            Sqlitelocation sqlitelocation = new Sqlitelocation(mContext);
            sqlitelocation.addLocation(location);
            
            // send to the server the full object
            // si android is online send it to server
            if (isNetworkOnline()){
                Log.v(TAG, "device is online !!! send to the server the multiple infos ");
                // get all location in an arrayList
                ArrayList allLocations = sqlitelocation.getAllLocations();
                    Log.v(TAG, "get all customlocations.. ");
                    Log.v(TAG, allLocations.toString());
                // prepare && send the request to the server
                Httprequests httprequests = new Httprequests();
                // ArrayList<String> listdata = new ArrayList<String>();
                JSONArray jsonArray = new JSONArray();
                for(Object customlocation : allLocations){
                    //JSONObject obj = new JSONObject();
                    //obj.put()
                    jsonArray.put(customlocation);
                }
                    Log.v(TAG, "transform to jsonarray ");
                    Log.v(TAG, jsonArray.toString());
                
                // create the jsonObject from map
                JSONObject jsonObjSend = new JSONObject();
                try {
                    jsonObjSend.put("data", jsonArray);
                    jsonObjSend.put("token", "xxxxxxxxxxxxxxxxxxxxxxxxx");
                    jsonObjSend.put("id", "yyyyyyyyyyyy");

                } catch (Exception e)
                {
                    // More about HTTP exception handling in another tutorial.
                    // For now we just print the stack trace.
                    e.printStackTrace();
                }
                // prepare with variale values
                String urlTo = "http://192.168.1.28/pp_simple_rest_full/v1/backgroundgeolocation";
                JSONObject jSONObjectReceive = httprequests.SendHttpPost(urlTo, jsonObjSend);
                if (jSONObjectReceive != null){
                    Log.v(TAG, "get return http");
                    Log.v(TAG, jSONObjectReceive.toString());
                } else {
                    // nothing to do -> server is not properly config for return json
                    Log.v(TAG, "return http is NULL");
                }
            } else {
                Log.v(TAG, "device NOT online !!! ");

            }
            
            // create a delay for observe the background precesses if too quick
            // SystemClock.sleep(10000); // sleep 10 sec

            // internal stopping the thread.
            stopSelf();
        }
    };
    public boolean isNetworkOnline() {
        boolean status=false;
        try{
            ConnectivityManager cm = (ConnectivityManager) this.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getNetworkInfo(0);
            if (netInfo != null && netInfo.getState()==NetworkInfo.State.CONNECTED) {
                status= true;
            }else {
                netInfo = cm.getNetworkInfo(1);
                if(netInfo!=null && netInfo.getState()==NetworkInfo.State.CONNECTED)
                    status= true;
            }
        }catch(Exception e){
            e.printStackTrace();  
            return false;
        }
        return status;
    }
}